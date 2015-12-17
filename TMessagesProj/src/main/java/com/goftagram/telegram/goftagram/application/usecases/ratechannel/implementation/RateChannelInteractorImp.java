package com.goftagram.telegram.goftagram.application.usecases.ratechannel.implementation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.goftagram.telegram.goftagram.application.model.TelegramChannel;
import com.goftagram.telegram.goftagram.application.usecases.ratechannel.contract.RateChannelInteractor;
import com.goftagram.telegram.goftagram.application.usecases.ratechannel.contract.RateChannelInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.ratechannel.contract.RateChannelRequest;
import com.goftagram.telegram.goftagram.application.usecases.ratechannel.contract.RateChannelResponse;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.network.api.message.RateChannelMessage;
import com.goftagram.telegram.goftagram.parser.CategoryParser;
import com.goftagram.telegram.goftagram.parser.TelegramChannelParser;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.messenger.R;

import org.json.JSONException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.greenrobot.event.EventBus;

/**
 * Created by WORK on 10/30/2015.
 */
public class RateChannelInteractorImp implements RateChannelInteractor {

    private final String LOG_TAG = LogUtils.makeLogTag(RateChannelInteractorImp.class.getSimpleName());


    private static RateChannelInteractorImp sRateChannelInteractorImp;
    private Context mContext;
    private static final Object sLock = new Object();

    private ConcurrentMap<Integer, RateChannelRequest> mPendingRequest;
    private ConcurrentMap<Integer, RateChannelInteractorCallback> mCallbackMap;


    private boolean mHasPendingRequest;

    private RateChannelInteractorImp(Context context) {
        mContext = context.getApplicationContext();
        mCallbackMap = new ConcurrentHashMap<>();
        mPendingRequest = new ConcurrentHashMap<>();
        EventBus.getDefault().register(this);
        mHasPendingRequest = false;
    }

    public static synchronized RateChannelInteractor getInstance(Context context) {
        if (sRateChannelInteractorImp == null) {
            sRateChannelInteractorImp = new RateChannelInteractorImp(context);
        }
        return sRateChannelInteractorImp;
    }


    @Override
    public void postAsync(RateChannelRequest request, RateChannelInteractorCallback callback) {

        mCallbackMap.put(request.mRequestId, callback);
        mPendingRequest.put(request.mRequestId, request);
        String token = UserController.getInstance(mContext).getToken();
        ServiceHelper
                .getInstance(mContext)
                .rateChannel(
                        request.mRequestId,
                        request.mRate,
                        request.mTelegramChannelId,
                        token
                );


    }


    public void onEventAsync(RateChannelMessage networkMessage) {

        synchronized (sLock) {

            mHasPendingRequest = false;
            int transactionId = networkMessage.mTransactionId;
            String rawResponse = networkMessage.mRawResponse;
            int statusCode = networkMessage.mStatusCode;
            int rate = networkMessage.mRate;
            String telegramChannel = networkMessage.mTelegramChannelId;
            TelegramChannelParser.TelegramChannelRateParserResponse response = null;

            if (statusCode == HttpManager.SOCKET_TIME_OUT) {

                notifyObservers(
                        mContext.getString(R.string.io_error),
                        RateChannelResponse.FAIL,
                        -1);
                return;

            } else {
                try {
                    response = TelegramChannelParser.rateParser(rawResponse);
                } catch (JSONException exp) {

                    exp.printStackTrace();

                    notifyObservers(
                            mContext.getString(R.string.server_unknown_format),
                            RateChannelResponse.FAIL,
                            -1
                    );
                    return;
                }

                if (statusCode == HttpManager.OK) {

                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_SUCCESS) {


                        Uri telegramChannelUri = GoftagramContract
                                .TelegramChannelEntry
                                .buildTelegramChannelDetail();

                        Cursor telegramChannelCursor = mContext.getContentResolver().query(
                                telegramChannelUri,
                                null,
                                GoftagramContract.TelegramChannelEntry.COLUMN_SERVER_ID + " = ?",
                                new String[]{telegramChannel},
                                null
                        );

                        if(telegramChannelCursor.moveToFirst()){
                            TelegramChannel updatedTelegramChannel =
                                    TelegramChannel.cursorToTelegramChannel(telegramChannelCursor);

                            ContentValues cv = new ContentValues();

                            cv.put(GoftagramContract.TelegramChannelEntry.COLUMN_RATE, response.mRate);

                            cv.put(GoftagramContract.TelegramChannelEntry.COLUMN_STAR_1, response.mStar_1);
                            cv.put(GoftagramContract.TelegramChannelEntry.COLUMN_STAR_2, response.mStar_2);
                            cv.put(GoftagramContract.TelegramChannelEntry.COLUMN_STAR_3, response.mStar_3);
                            cv.put(GoftagramContract.TelegramChannelEntry.COLUMN_STAR_4, response.mStar_4);
                            cv.put(GoftagramContract.TelegramChannelEntry.COLUMN_STAR_5, response.mStar_5);


                            mContext.getContentResolver().update(
                                    telegramChannelUri,
                                    cv,
                                    GoftagramContract.TelegramChannelEntry.COLUMN_SERVER_ID + " = ?",
                                    new String[]{telegramChannel});
                        }

                        notifyObservers(response.mMessage, RateChannelResponse.SUCCESS, response.mRate);
                        return;
                    } else {
                        notifyObservers(response.mMessage, RateChannelResponse.FAIL, -1);
                        return;
                    }
                } else {

                    if (response.mStatusCode == CategoryParser.STATUS_CODE_EXPIRE) {

                        UserController.getInstance(mContext).updateMainLogIn(false);
                        UserController.getInstance(mContext).doLoginAsync(transactionId);


                    } else if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_FAIL) {
                        notifyObservers(response.mMessage, RateChannelResponse.FAIL, -1);
                        return;
                    }
                }
            }
        }
    }


    private void notifyObservers(String message, int state,float rate) {
        synchronized (sLock) {
            for (Integer pendingRequest : mPendingRequest.keySet()) {

                RateChannelInteractorCallback callback = mCallbackMap.get(pendingRequest);
                if(callback == null)continue;
                RateChannelResponse responseModel = new RateChannelResponse();
                responseModel.mMessage = message;
                responseModel.mState = state;
                responseModel.mRequestId = pendingRequest;
                responseModel.mRate = rate;
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
                    ServiceHelper.getInstance(mContext).fetchCategoryList(
                            pendingRequest, event.getToken()
                    );
                    mHasPendingRequest = true;
                }
            }
        }
    }
}
