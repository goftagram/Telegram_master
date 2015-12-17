package com.goftagram.telegram.goftagram.application.usecases.reportchannel.implementation;

import android.content.Context;

import com.goftagram.telegram.goftagram.application.usecases.reportchannel.contract.ReportChannelInteractor;
import com.goftagram.telegram.goftagram.application.usecases.reportchannel.contract.ReportChannelInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.reportchannel.contract.ReportChannelRequest;
import com.goftagram.telegram.goftagram.application.usecases.reportchannel.contract.ReportChannelResponse;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.network.api.message.ReportChannelMessage;
import com.goftagram.telegram.goftagram.parser.TelegramChannelParser;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.messenger.R;

import org.json.JSONException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.greenrobot.event.EventBus;

/**
 * Created by WORK on 10/30/2015.
 */
public class ReportChannelInteractorImp implements ReportChannelInteractor {

    private final String LOG_TAG = LogUtils.makeLogTag(ReportChannelInteractorImp.class.getSimpleName());


    private static ReportChannelInteractorImp sReportChannelInteractorImp;
    private Context mContext;
    private static final Object sLock = new Object();

    private ConcurrentMap<Integer, ReportChannelRequest> mPendingRequest;
    private ConcurrentMap<Integer, ReportChannelInteractorCallback> mCallbackMap;


    private boolean mHasPendingRequest;

    private ReportChannelInteractorImp(Context context) {
        mContext = context.getApplicationContext();
        mCallbackMap = new ConcurrentHashMap<>();
        mPendingRequest = new ConcurrentHashMap<>();
        EventBus.getDefault().register(this);
        mHasPendingRequest = false;
    }

    public static synchronized ReportChannelInteractor getInstance(Context context) {
        if (sReportChannelInteractorImp == null) {
            sReportChannelInteractorImp = new ReportChannelInteractorImp(context);
        }
        return sReportChannelInteractorImp;
    }


    @Override
    public void postAsync(ReportChannelRequest request, ReportChannelInteractorCallback callback) {

        mCallbackMap.put(request.mRequestId, callback);
        mPendingRequest.put(request.mRequestId, request);
        String token = UserController.getInstance(mContext).getToken();
        ServiceHelper
                .getInstance(mContext)
                .reportChannel(
                        request.mRequestId,
                        request.mReportText,
                        request.mTelegramChannelId,
                        token
                );


    }


    public void onEventAsync(ReportChannelMessage networkMessage) {

        synchronized (sLock) {
            mHasPendingRequest = false;
            int transactionId = networkMessage.mTransactionId;
            String rawResponse = networkMessage.mRawResponse;
            int statusCode = networkMessage.mStatusCode;
            TelegramChannelParser.TelegramChannelReportParserResponse response = null;

            if(statusCode  == HttpManager.SOCKET_TIME_OUT){

                notifyObservers(
                        mContext.getString(R.string.io_error),
                        ReportChannelResponse.FAIL
                );
                return;

            }else {

                try {
                    response = TelegramChannelParser.reportParser(rawResponse);
                } catch (JSONException exp) {

                    exp.printStackTrace();

                    notifyObservers(
                            mContext.getString(R.string.server_unknown_format),
                            ReportChannelResponse.FAIL
                    );
                    return;
                }

                if (statusCode == HttpManager.OK) {

                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_SUCCESS) {
                        notifyObservers(response.mMessage, ReportChannelResponse.SUCCESS);
                        return;
                    } else {
                        notifyObservers(response.mMessage, ReportChannelResponse.FAIL);
                        return;
                    }
                } else {

                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_EXPIRE) {

                        UserController.getInstance(mContext).updateMainLogIn(false);
                        UserController.getInstance(mContext).doLoginAsync(transactionId);


                    } else if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_FAIL) {
                        notifyObservers(response.mMessage, ReportChannelResponse.FAIL);
                        return;
                    }
                }
            }
        }
    }


    private void notifyObservers(String message, int state) {
        synchronized (sLock) {
            for (Integer pendingRequest : mPendingRequest.keySet()) {

                ReportChannelInteractorCallback callback = mCallbackMap.get(pendingRequest);
                if(callback == null)continue;
                ReportChannelResponse responseModel = new ReportChannelResponse();
                responseModel.mMessage = message;
                responseModel.mState = state;
                responseModel.mRequestId = pendingRequest;
                callback.onResponse(responseModel);
            }
            mCallbackMap.clear();
            mPendingRequest.clear();
        }

    }

    public void onEventAsync(UserController.UserLogInUiEvent event) {
        synchronized (sLock) {
            if (!mHasPendingRequest) {
                if (mPendingRequest.size() > 0 && mPendingRequest.get(event.getMessageId()) != null) {

                    Integer pendingRequest = mPendingRequest.get(event.getMessageId()).mRequestId;
                    String channelId = mPendingRequest.get(event.getMessageId()).mTelegramChannelId;
                    ServiceHelper.getInstance(mContext).reportChannel(
                            pendingRequest, "", event.getToken(),channelId
                    );
                    mHasPendingRequest = true;
                }
            }
        }
    }


}
