package com.goftagram.telegram.goftagram.application.usecases.addcommentforchannel.implementation;

import android.content.Context;

import com.goftagram.telegram.goftagram.application.usecases.addcommentforchannel.contract.AddCommentInteractor;
import com.goftagram.telegram.goftagram.application.usecases.addcommentforchannel.contract.AddCommentInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.addcommentforchannel.contract.AddCommentRequest;
import com.goftagram.telegram.goftagram.application.usecases.addcommentforchannel.contract.AddCommentResponse;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.network.api.message.AddCommentMessage;
import com.goftagram.telegram.goftagram.parser.CategoryParser;
import com.goftagram.telegram.goftagram.parser.CommentParser;
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
public class AddCommentInteractorImp implements AddCommentInteractor {

    private final String LOG_TAG = LogUtils.makeLogTag(AddCommentInteractorImp.class.getSimpleName());


    private static AddCommentInteractorImp sAddCommentInteractorImp;
    private Context mContext;
    private static final Object sLock = new Object();

    private ConcurrentMap<Integer, AddCommentRequest> mPendingRequest;
    private ConcurrentMap<Integer, AddCommentInteractorCallback> mCallbackMap;


    private boolean mHasPendingRequest;

    private AddCommentInteractorImp(Context context) {
        mContext = context.getApplicationContext();
        mCallbackMap = new ConcurrentHashMap<>();
        mPendingRequest = new ConcurrentHashMap<>();
        EventBus.getDefault().register(this);
        mHasPendingRequest = false;
    }

    public static synchronized AddCommentInteractor getInstance(Context context) {
        if (sAddCommentInteractorImp == null) {
            sAddCommentInteractorImp = new AddCommentInteractorImp(context);
        }
        return sAddCommentInteractorImp;
    }


    @Override
    public void postAsync(AddCommentRequest request, AddCommentInteractorCallback callback) {

        mCallbackMap.put(request.mRequestId, callback);
        mPendingRequest.put(request.mRequestId, request);
        String token = UserController.getInstance(mContext).getToken();
        ServiceHelper
                .getInstance(mContext)
                .addComment(
                        request.mRequestId,
                        request.mComment,
                        request.mTelegramChannelId,
                        token
                );


    }


    public void onEventAsync(AddCommentMessage networkMessage) {

        synchronized (sLock) {

            mHasPendingRequest = false;
            int transactionId = networkMessage.mTransactionId;
            String rawResponse = networkMessage.mRawResponse;
            int statusCode = networkMessage.mStatusCode;
            CommentParser.AddCommentResponse response = null;

            if(statusCode  == HttpManager.SOCKET_TIME_OUT){

                notifyObservers(
                        mContext.getString(R.string.io_error),
                        AddCommentResponse.FAIL
                );
                return;

            }else {

                try {
                    response = CommentParser.addCommentParser(rawResponse);
                } catch (JSONException exp) {

                    exp.printStackTrace();

                    notifyObservers(
                            mContext.getString(R.string.server_unknown_format),
                            AddCommentResponse.FAIL
                    );
                    return;
                }

                if (statusCode == HttpManager.OK) {

                    if (response.mStatusCode == CategoryParser.STATUS_CODE_SUCCESS) {
                        notifyObservers(response.mMessage, AddCommentResponse.SUCCESS);
                        return;
                    } else {
                        notifyObservers(response.mMessage, AddCommentResponse.FAIL);
                        return;
                    }
                } else {

                    if (response.mStatusCode == CategoryParser.STATUS_CODE_EXPIRE) {

                        UserController.getInstance(mContext).updateMainLogIn(false);
                        UserController.getInstance(mContext).doLoginAsync(transactionId);


                    } else if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_FAIL) {
                        notifyObservers(response.mMessage, AddCommentResponse.FAIL);
                        return;
                    }
                }
            }
        }
    }


    private void notifyObservers(String message, int state) {
        synchronized (sLock) {
            for (Integer pendingRequest : mPendingRequest.keySet()) {

                AddCommentInteractorCallback callback = mCallbackMap.get(pendingRequest);
                if (callback == null) continue;
                AddCommentResponse responseModel = new AddCommentResponse();
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
                    ServiceHelper.getInstance(mContext).fetchCategoryList(
                            pendingRequest, event.getToken()
                    );

//                    ServiceHelper
//                            .getInstance(mContext)
//                            .addComment(
//                                    request.mRequestId,
//                                    request.mComment,
//                                    request.mTelegramChannelId,
//                                    event.getToken()
//                            );
                    mHasPendingRequest = true;
                }
            }
        }
    }


}
