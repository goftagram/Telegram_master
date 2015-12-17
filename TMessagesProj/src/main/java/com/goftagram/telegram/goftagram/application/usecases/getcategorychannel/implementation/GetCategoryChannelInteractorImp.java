package com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.implementation;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.goftagram.telegram.goftagram.application.model.Category;
import com.goftagram.telegram.goftagram.application.model.TelegramChannel;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryRequest;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryResponse;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.implementation.GetCategoryInteractorImp;
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract.GetCategoryChannelInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract.GetCategoryChannelInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract.GetCategoryChannelRequest;
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract.GetCategoryChannelResponse;
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract.GetCategoryChannelTaskCallback;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.network.api.message.FetchChannelsByCategoryNetworkMessage;
import com.goftagram.telegram.goftagram.parser.TelegramChannelParser;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.taskmanager.Task;
import com.goftagram.telegram.goftagram.taskmanager.TaskManager;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.messenger.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.greenrobot.event.EventBus;

/**
 * Created by WORK on 10/30/2015.
 */
public class GetCategoryChannelInteractorImp implements
        GetCategoryChannelInteractor,
        GetCategoryChannelTaskCallback,
        GetCategoryInteractorCallback {


    private final String LOG_TAG = LogUtils.makeLogTag(GetCategoryChannelInteractorImp.class.getSimpleName());


    private boolean mHasPendingGetCategoryRequest;
    private boolean mHasPendingLogInRequest;

    private static GetCategoryChannelInteractorImp sGetCategoryChannelInteractorImp;
    private Context mContext;
    private static final Object sLock = new Object();

    private ConcurrentMap<Integer, GetCategoryChannelInteractorCallback> mCallbackMap;
    private ConcurrentMap<Integer, GetCategoryChannelRequest> mPendingRequest;
    private ConcurrentMap<Integer, String> mPendingStateRequest;

    private GetCategoryChannelInteractorImp(Context context) {
        mContext = context.getApplicationContext();
        mCallbackMap = new ConcurrentHashMap<>();
        mPendingRequest = new ConcurrentHashMap<>();
        mPendingStateRequest = new ConcurrentHashMap<>();
        mHasPendingGetCategoryRequest = false;
        EventBus.getDefault().register(this);
    }

    public static synchronized GetCategoryChannelInteractor getInstance(Context context) {
        if (sGetCategoryChannelInteractorImp == null) {
            sGetCategoryChannelInteractorImp = new GetCategoryChannelInteractorImp(context);
        }
        return sGetCategoryChannelInteractorImp;
    }


    @Override
    public void getAsync(GetCategoryChannelRequest request, GetCategoryChannelInteractorCallback callback) {

        mCallbackMap.put(request.mRequestId, callback);
        mPendingRequest.put(request.mRequestId, request);
        Task task = new GetCategoryChannelTask(mContext, request, this);
        TaskManager.getInstance().execute(task);

    }


    @Override
    public void onHit(GetCategoryChannelRequest request, NameValueDataHolder dataHolder) {
        synchronized (sLock) {

            notifyObserver(
                    request.mRequestId,
                    "Success",
                    GetCategoryChannelResponse.SUCCESS,
                    dataHolder.getInt(KEY_TOTAL_CHANNELS)
            );

        }
    }

    @Override
    public void onMiss(GetCategoryChannelRequest request, NameValueDataHolder dataHolder) {

        synchronized (sLock) {
            final boolean isCategoryEmpty = dataHolder.getBoolean(KEY_IS_CATEGORY_EMPTY);
            String token = UserController.getInstance(mContext).getToken();
            if (isCategoryEmpty) {
                if (!mHasPendingGetCategoryRequest) {

                    GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
                    getCategoryRequest.mRequestId = request.mRequestId;
                    GetCategoryInteractorImp.getInstance(mContext).getAsync(getCategoryRequest, this);
                    mHasPendingGetCategoryRequest = true;
                }

            } else {

                if (!mHasPendingGetCategoryRequest) {
                    ServiceHelper
                            .getInstance(mContext)
                            .fetchChannelByCategory(
                                    request.mRequestId,
                                    token,
                                    request.mCategoryId,
                                    request.mServerPageRequest
                            );
                }
            }
        }
    }


    private void notifyObservers(String message, int state, int totalItem) {
        synchronized (sLock) {
            List<Integer> pendingRequestId = new ArrayList<>(mPendingRequest.keySet());
            for (Integer i : pendingRequestId) {
                notifyObserver(i, message, state, totalItem);
            }
        }
    }


    private void notifyObserver(int requestId, String message, int state, int totalItem) {
        synchronized (sLock) {
            GetCategoryChannelInteractorCallback callback = mCallbackMap.get(requestId);
            if(callback == null){
                mCallbackMap.remove(requestId);
                mPendingRequest.remove(requestId);
                return;
            }
            GetCategoryChannelResponse responseModel = new GetCategoryChannelResponse();
            responseModel.mMessage = message;
            responseModel.mState = state;
            responseModel.mRequestId = requestId;
            responseModel.mTotalServerItems = totalItem;
            mCallbackMap.remove(requestId);
            mPendingRequest.remove(requestId);
            callback.onResponse(responseModel);
        }

    }


    @Override
    public void onResponse(GetCategoryResponse response) {
        synchronized (sLock) {
            mHasPendingGetCategoryRequest = false;
            if (response.mState == GetCategoryResponse.SUCCESS) {
                for (Integer i : mPendingRequest.keySet()) {
                    Task task = new GetCategoryChannelTask(
                            mContext,
                            mPendingRequest.get(i),
                            this
                    );
                    TaskManager.getInstance().execute(task);
                }
            } else {
                notifyObservers(response.mMessage, GetCategoryChannelResponse.FAIL, -1);
            }
        }
    }


    public void onEventAsync(FetchChannelsByCategoryNetworkMessage networkMessage) {

        synchronized (sLock) {

            int transactionId = networkMessage.mTransactionId;
            String rawResponse = networkMessage.mRawResponse;
            String categoryId = networkMessage.mCategoryId;
            int statusCode = networkMessage.mStatusCode;
            int page = networkMessage.mPage;


            TelegramChannelParser.TelegramChannelListParserResponse response = null;

            if (statusCode == HttpManager.SOCKET_TIME_OUT) {

                notifyObservers(
                        mContext.getString(R.string.io_error),
                        GetCategoryChannelResponse.FAIL,
                        -1
                );

                return;

            } else {


                try {
                    response = TelegramChannelParser.listParser(rawResponse);
                } catch (JSONException exp) {

                    exp.printStackTrace();

                    notifyObserver(
                            transactionId,
                            mContext.getString(R.string.server_unknown_format),
                            GetCategoryChannelResponse.FAIL
                            , -1
                    );
                    return;
                }

                if (statusCode == HttpManager.OK) {

                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_SUCCESS) {

                        int totalPage = 0;
                        int perPage = 0;

                        if (response.mTelegramChannelList.size() > 0) {

                            Uri telegramChannelUri = GoftagramContract.TelegramChannelEntry
                                    .buildTelegramChannel(
                                            SQLiteDatabase.CONFLICT_REPLACE
                                    );

                            ContentValues[] cvs = TelegramChannel.TelegramChannelsToContentValueArray(
                                    response.mTelegramChannelList
                            );

                            for(int i = 0 ; i< cvs.length;i++){
                                cvs[i].put(GoftagramContract.TelegramChannelEntry.COLUMN_RANK_IN_CATEGORY, ((page-1)+i));
                            }

                            ContentValues categoryCvs = Category.CategoryToContentValues(
                                    response.mTelegramChannelList.get(0).getCategory()
                            );
                            Uri updateUri =
                                    GoftagramContract
                                            .CategoryEntry
                                            .buildCategoryUriWithConflictFlag(
                                                    SQLiteDatabase.CONFLICT_IGNORE
                                            );

                            if (response.mHasPagination) {

                                totalPage = response.mTotal;
                                perPage = response.mPerPage;

                                categoryCvs.put(
                                        GoftagramContract.CategoryEntry.COLUMN_PER_PAGE_ITEM, perPage
                                );
                                categoryCvs.put(
                                        GoftagramContract.CategoryEntry.COLUMN_TOTAL_CHANNELS, totalPage
                                );
                            }

                            categoryCvs.put(
                                    GoftagramContract.CategoryEntry.COLUMN_UPDATED, System.currentTimeMillis()
                            );
                            categoryCvs.put(
                                    GoftagramContract.CategoryEntry.COLUMN_STATE, GoftagramContract.SYNC_STATE_IDLE
                            );

                            mContext.getContentResolver().update(
                                    updateUri, categoryCvs,
                                    GoftagramContract.CategoryEntry.COLUMN_SERVER_ID + " = ? ",
                                    new String[]{categoryId});

                            mContext.getContentResolver().bulkInsert(telegramChannelUri, cvs);
                        }

                        notifyObserver(
                                transactionId,
                                response.mMessage,
                                GetCategoryChannelResponse.SUCCESS,
                                totalPage
                        );

                    } else {
                        notifyObserver(transactionId, response.mMessage, GetCategoryChannelResponse.FAIL, -1);
                    }

                } else {

                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_EXPIRE) {

                        mHasPendingLogInRequest = true;
                        UserController.getInstance(mContext).updateMainLogIn(false);
                        UserController.getInstance(mContext).doLoginAsync(transactionId);

                    } else if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_FAIL) {

                        notifyObserver(transactionId, response.mMessage, GetCategoryChannelResponse.FAIL, -1);

                    }
                }
            }
        }
    }


    public void onEventAsync(UserController.UserLogInUiEvent event) {
        synchronized (sLock) {
             if (mHasPendingLogInRequest) {
                if (mPendingRequest.size() > 0 && mPendingRequest.get(event.getMessageId()) != null) {
                    GetCategoryChannelRequest request = mPendingRequest.get(event.getMessageId());
                    ServiceHelper.getInstance(mContext)
                            .fetchChannelByCategory(
                                    request.mRequestId,
                                    event.getToken(),
                                    request.mCategoryId,
                                    request.mServerPageRequest);
                    mHasPendingLogInRequest = false;
                }
            }
        }
    }

}
