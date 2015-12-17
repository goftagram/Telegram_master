package com.goftagram.telegram.goftagram.application.usecases.reportcomment.implementation;

import android.content.Context;

import com.goftagram.telegram.goftagram.application.usecases.reportcomment.contract.ReportCommentInteractor;
import com.goftagram.telegram.goftagram.application.usecases.reportcomment.contract.ReportCommentInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.reportcomment.contract.ReportCommentRequest;
import com.goftagram.telegram.goftagram.application.usecases.reportcomment.contract.ReportCommentResponse;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.network.api.message.ReportCommentMessage;
import com.goftagram.telegram.goftagram.parser.CommentParser;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.messenger.R;

import org.json.JSONException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.greenrobot.event.EventBus;

/**
 * Created by WORK on 10/30/2015.
 */
public class ReportCommentInteractorImp implements ReportCommentInteractor {

    private final String LOG_TAG = LogUtils.makeLogTag(ReportCommentInteractorImp.class.getSimpleName());


    private static ReportCommentInteractorImp sReportCommentInteractorImp;
    private Context mContext;
    private static final Object sLock = new Object();

    private ConcurrentMap<Integer, ReportCommentRequest> mPendingRequest;
    private ConcurrentMap<Integer, ReportCommentInteractorCallback> mCallbackMap;


    private boolean mHasPendingRequest;

    private ReportCommentInteractorImp(Context context) {
        mContext = context.getApplicationContext();
        mCallbackMap = new ConcurrentHashMap<>();
        mPendingRequest = new ConcurrentHashMap<>();
        EventBus.getDefault().register(this);
        mHasPendingRequest = false;
    }

    public static synchronized ReportCommentInteractor getInstance(Context context) {
        if (sReportCommentInteractorImp == null) {
            sReportCommentInteractorImp = new ReportCommentInteractorImp(context);
        }
        return sReportCommentInteractorImp;
    }


    @Override
    public void postAsync(ReportCommentRequest request, ReportCommentInteractorCallback callback) {

        mCallbackMap.put(request.mRequestId, callback);
        mPendingRequest.put(request.mRequestId, request);
        String token = UserController.getInstance(mContext).getToken();
        ServiceHelper
                .getInstance(mContext)
                .reportComment(
                        request.mRequestId,
                        request.mReportText,
                        request.mCommentServerId,
                        token
                );


    }


    public void onEventAsync(ReportCommentMessage networkMessage) {

        synchronized (sLock) {

            mHasPendingRequest = false;
            int transactionId = networkMessage.mTransactionId;
            String rawResponse = networkMessage.mRawResponse;
            int statusCode = networkMessage.mStatusCode;
            CommentParser.ReportCommentResponse response = null;

            if(statusCode  == HttpManager.SOCKET_TIME_OUT){

                notifyObservers(
                        mContext.getString(R.string.io_error),
                        ReportCommentResponse.FAIL
                );
                return;

            }else {

                try {
                    response = CommentParser.reportCommentParser(rawResponse);
                } catch (JSONException exp) {

                    exp.printStackTrace();

                    notifyObservers(
                            mContext.getString(R.string.server_unknown_format),
                            ReportCommentResponse.FAIL
                    );
                    return;
                }

                if (statusCode == HttpManager.OK) {

                    if (response.mStatusCode == CommentParser.STATUS_CODE_SUCCESS) {
                        notifyObservers(response.mMessage, ReportCommentResponse.SUCCESS);
                        return;
                    } else {
                        notifyObservers(response.mMessage, ReportCommentResponse.FAIL);
                        return;
                    }
                } else {

                    if (response.mStatusCode == CommentParser.STATUS_CODE_EXPIRE) {

                        UserController.getInstance(mContext).updateMainLogIn(false);
                        UserController.getInstance(mContext).doLoginAsync(transactionId);


                    } else if (response.mStatusCode == CommentParser.STATUS_CODE_FAIL) {
                        notifyObservers(response.mMessage, ReportCommentResponse.FAIL);
                        return;
                    }
                }
            }
        }
    }


    private void notifyObservers(String message, int state) {
        synchronized (sLock) {
            for (Integer pendingRequest : mPendingRequest.keySet()) {

                ReportCommentInteractorCallback callback = mCallbackMap.get(pendingRequest);
                if(callback == null)continue;
                ReportCommentResponse responseModel = new ReportCommentResponse();
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
                    String commentId = mPendingRequest.get(event.getMessageId()).mCommentServerId;

                    ServiceHelper.getInstance(mContext).reportComment(
                            pendingRequest, "", commentId,event.getToken()
                    );
                    mHasPendingRequest = true;
                }
            }
        }
    }
}
