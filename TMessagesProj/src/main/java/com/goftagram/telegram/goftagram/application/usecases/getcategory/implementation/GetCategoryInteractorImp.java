package com.goftagram.telegram.goftagram.application.usecases.getcategory.implementation;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.goftagram.telegram.goftagram.application.model.Category;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryRequest;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryResponse;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryTaskCallback;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.network.api.message.FetchCategoryNetworkMessage;
import com.goftagram.telegram.goftagram.parser.CategoryParser;
import com.goftagram.telegram.goftagram.parser.TelegramChannelParser;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.taskmanager.Task;
import com.goftagram.telegram.goftagram.taskmanager.TaskManager;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.messenger.R;

import org.json.JSONException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.greenrobot.event.EventBus;

/**
 * Created by WORK on 10/30/2015.
 */
public class GetCategoryInteractorImp implements
        GetCategoryInteractor,
        GetCategoryTaskCallback {

    private final String LOG_TAG = LogUtils.makeLogTag(GetCategoryInteractorImp.class.getSimpleName());


    private static GetCategoryInteractorImp sGetCategoryInteractorImp;
    private Context mContext;
    private static final Object sLock = new Object();

    private ConcurrentMap<Integer, GetCategoryRequest> mPendingRequest;
    private ConcurrentMap<Integer, GetCategoryInteractorCallback> mCallbackMap;

    private ConcurrentMap<Integer, String> mPendingRequestState;
    private String mToken;

    private boolean mHasPendingRequest;

    private GetCategoryInteractorImp(Context context) {
        mContext = context.getApplicationContext();
        mCallbackMap = new ConcurrentHashMap<>();
        mPendingRequest = new ConcurrentHashMap<>();
        mPendingRequestState = new ConcurrentHashMap<>();
        EventBus.getDefault().register(this);
        mHasPendingRequest = false;
    }

    public static synchronized GetCategoryInteractor getInstance(Context context) {
        if (sGetCategoryInteractorImp == null) {
            sGetCategoryInteractorImp = new GetCategoryInteractorImp(context);
        }
        return sGetCategoryInteractorImp;
    }


    @Override
    public void getAsync(GetCategoryRequest request, GetCategoryInteractorCallback callback) {

        LogUtils.LOGI(LOG_TAG,"GetCategory  request id = " + request.mRequestId);
        mCallbackMap.put(request.mRequestId, callback);
        mPendingRequest.put(request.mRequestId, request);
        Task task = new GetCategoryTask(mContext, request, this);
        TaskManager.getInstance().execute(task);

    }

    @Override
    public void onHit(GetCategoryRequest request, NameValueDataHolder dataHolder) {
        synchronized (sLock) {

            GetCategoryInteractorCallback callback = mCallbackMap.get(request.mRequestId);
            LogUtils.LOGI(LOG_TAG,"GetCategory  callback onHit = " + callback);
            if (callback == null) {
                mCallbackMap.remove(request.mRequestId);
                mPendingRequest.remove(request.mRequestId);
                return;
            }

            GetCategoryResponse responseModel = new GetCategoryResponse();
            responseModel.mMessage = "Succuss";
            responseModel.mState = GetCategoryResponse.SUCCESS;
            responseModel.mRequestId = request.mRequestId;


            mCallbackMap.remove(request.mRequestId);
            mPendingRequest.remove(request.mRequestId);

            callback.onResponse(responseModel);


        }
    }

    @Override
    public void onMiss(GetCategoryRequest request, NameValueDataHolder dataHolder) {
        synchronized (sLock) {
            if (!mHasPendingRequest) {
                String token = UserController.getInstance(mContext).getToken();
                ServiceHelper.getInstance(mContext).fetchCategoryList(request.mRequestId, token);
                mHasPendingRequest = true;
            }
        }
    }

    public void onEventAsync(FetchCategoryNetworkMessage networkMessage) {

        synchronized (sLock) {

            mHasPendingRequest = false;
            int transactionId = networkMessage.mTransactionId;
            String rawResponse = networkMessage.mRawResponse;
            int statusCode = networkMessage.mStatusCode;
            CategoryParser.CategoryParserResponse response = null;

            if (statusCode == HttpManager.SOCKET_TIME_OUT) {

                notifyObservers(
                        mContext.getString(R.string.io_error),
                        GetCategoryResponse.FAIL,
                        0
                );
                return;

            } else {
                try {
                    response = CategoryParser.listParser(rawResponse);


                } catch (JSONException exp) {

                    exp.printStackTrace();

                    notifyObservers(
                            mContext.getString(R.string.server_unknown_format),
                            GetCategoryResponse.FAIL,
                            0
                    );
                    return;
                }

                if (statusCode == HttpManager.OK) {

                    if (response.mStatusCode == CategoryParser.STATUS_CODE_SUCCESS) {


                        Uri insertUri = GoftagramContract.CategoryEntry
                                .buildCategoryList();

                        ContentValues[] categoryContentValue = Category.CategoryToContentValueArray(
                                response.mCategories
                        );
                        long updatedTime = System.currentTimeMillis();
                        String state = GoftagramContract.SYNC_STATE_IDLE;
                        for (ContentValues value : categoryContentValue) {
                            value.put(GoftagramContract.CategoryEntry.COLUMN_UPDATED, updatedTime);
                            value.put(GoftagramContract.CategoryEntry.COLUMN_STATE, state);
                        }

                        int totalItems = 0;
                        if (response.mCategories.size() > 0) {
                            mContext.getContentResolver().bulkInsert(insertUri, categoryContentValue);
                            totalItems = response.mCategories.size();
                        }

                        notifyObservers(response.mMessage, GetCategoryResponse.SUCCESS, totalItems);

                        return;

                    } else {

                        notifyObservers(response.mMessage, GetCategoryResponse.FAIL, 0);

                        return;

                    }

                } else {

                    if (response.mStatusCode == CategoryParser.STATUS_CODE_EXPIRE) {

                        UserController.getInstance(mContext).updateMainLogIn(false);
                        UserController.getInstance(mContext).doLoginAsync(transactionId);


                    } else if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_FAIL) {
                        notifyObservers(response.mMessage, GetCategoryResponse.FAIL, 0);
                        return;
                    }
                }
            }
        }
    }


    private void notifyObservers(String message, int state, int totalItems) {
        synchronized (sLock) {
            for (Integer pendingRequest : mPendingRequest.keySet()) {

                GetCategoryInteractorCallback callback = mCallbackMap.get(pendingRequest);
                LogUtils.LOGI(LOG_TAG,"GetCategory  notifyObservers = " + callback);

                if (callback == null) continue;
                GetCategoryResponse responseModel = new GetCategoryResponse();
                responseModel.mMessage = message;
                responseModel.mState = state;
                responseModel.mTotalServerItems = totalItems;
                responseModel.mRequestId = pendingRequest;
                LogUtils.LOGI(LOG_TAG,"GetCategory  mRequestId = " +  responseModel.mRequestId);
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
