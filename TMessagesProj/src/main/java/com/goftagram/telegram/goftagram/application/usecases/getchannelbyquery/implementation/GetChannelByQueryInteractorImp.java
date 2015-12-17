package com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.implementation;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.goftagram.telegram.goftagram.application.model.TelegramChannel;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryRequest;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryResponse;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.implementation.GetCategoryInteractorImp;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract.GetChannelByQueryInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract.GetChannelByQueryInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract.GetChannelByQueryRequest;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract.GetChannelByQueryResponse;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract.GetChannelByQueryTaskCallback;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.network.api.message.FetchChannelsByQueryNetworkMessage;
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
public class GetChannelByQueryInteractorImp implements
        GetChannelByQueryInteractor,
        GetChannelByQueryTaskCallback,
        GetCategoryInteractorCallback {



    private final String LOG_TAG = LogUtils.makeLogTag(GetChannelByQueryInteractorImp.class.getSimpleName());



    private boolean mHasPendingGetCategoryRequest;
    private boolean mHasPendingLogInRequest;

    private static GetChannelByQueryInteractorImp sGetChannelByQueryInteractorImp;
    private Context mContext;
    private static final Object sLock = new Object();

    private ConcurrentMap<Integer, GetChannelByQueryInteractorCallback> mCallbackMap;
    private ConcurrentMap<Integer, GetChannelByQueryRequest> mPendingRequest;


    private GetChannelByQueryInteractorImp(Context context) {
        mContext = context.getApplicationContext();
        mCallbackMap = new ConcurrentHashMap<>();
        mPendingRequest = new ConcurrentHashMap<>();
        mHasPendingGetCategoryRequest = false;
        EventBus.getDefault().register(this);
    }

    public static synchronized GetChannelByQueryInteractor getInstance(Context context) {
        if (sGetChannelByQueryInteractorImp == null) {
            sGetChannelByQueryInteractorImp = new GetChannelByQueryInteractorImp(context);
        }
        return sGetChannelByQueryInteractorImp;
    }


    @Override
    public void getAsync(GetChannelByQueryRequest request, GetChannelByQueryInteractorCallback callback) {

            mCallbackMap.put(request.mRequestId, callback);
            mPendingRequest.put(request.mRequestId, request);
            Task task = new GetChannelByQueryTask(mContext, request, this);
            TaskManager.getInstance().execute(task);

    }


    @Override
    public void onHit(GetChannelByQueryRequest request, NameValueDataHolder dataHolder) {
        synchronized (sLock) {

            notifyObserver(
                    request.mRequestId,
                    "Success",
                    GetChannelByQueryResponse.SUCCESS,
                    dataHolder.getInt(KEY_TOTAL_CHANNELS)
            );

        }
    }

    @Override
    public void onMiss(GetChannelByQueryRequest request, NameValueDataHolder dataHolder) {

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
                            .fetchChannelByQuery(
                                    request.mRequestId,
                                    token,
                                    request.mQuery,
                                    request.mServerPageRequest
                            );
                }
            }
        }
    }


    private void notifyObservers( String message,int state,int totalItem){
        synchronized (sLock) {
            List<Integer> pendingRequestId = new ArrayList<>(mPendingRequest.keySet());
            for (Integer i : pendingRequestId) {
                notifyObserver(i, message, state, totalItem);
            }
        }
    }


    private void notifyObserver(int requestId, String message,int state,int totalItem){
        synchronized (sLock) {
            GetChannelByQueryInteractorCallback callback = mCallbackMap.get(requestId);
            if(callback == null){
                mCallbackMap.remove(requestId);
                mPendingRequest.remove(requestId);
                return;
            }
            GetChannelByQueryResponse responseModel = new GetChannelByQueryResponse();
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
                    Task task = new GetChannelByQueryTask(
                            mContext,
                            mPendingRequest.get(i),
                            this
                    );
                    TaskManager.getInstance().execute(task);
                }
            } else {
                notifyObservers(response.mMessage, GetChannelByQueryResponse.FAIL, -1);
            }
        }
    }



    public void onEventAsync(FetchChannelsByQueryNetworkMessage networkMessage) {

        synchronized (sLock) {

            int transactionId = networkMessage.mTransactionId;
            String rawResponse = networkMessage.mRawResponse;
            String query = networkMessage.mQuery;
            int statusCode = networkMessage.mStatusCode;
            int page = networkMessage.mPage;


            TelegramChannelParser.TelegramChannelListParserResponse response = null;

            if(statusCode ==  HttpManager.SOCKET_TIME_OUT){

                notifyObservers(
                        mContext.getString(R.string.io_error),
                        GetChannelByQueryResponse.FAIL,
                        -1);
                return;

            }else {

                try {
                    response = TelegramChannelParser.listParser(rawResponse);

                } catch (JSONException exp) {

                    exp.printStackTrace();
                    notifyObserver(transactionId,
                            mContext.getString(R.string.server_unknown_format),
                            GetChannelByQueryResponse.FAIL,
                            -1
                    );


                    return;


                }

                if (statusCode == HttpManager.OK) {

                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_SUCCESS) {

                        int total = 0;


                        if (response.mTelegramChannelList.size() > 0) {

                            Uri telegramChannelUri = GoftagramContract.TelegramChannelEntry
                                    .buildTelegramChannel(
                                            SQLiteDatabase.CONFLICT_IGNORE
                                    );

                            ContentValues[] cvs = TelegramChannel.TelegramChannelsToContentValueArray(
                                    response.mTelegramChannelList
                            );

                            ContentValues queryTelegramChannelContentValues = new ContentValues();

                            Uri queryChannelUri = GoftagramContract.SearchedQueryEntry.buildSearchedQueryUri();

                            if (response.mHasPagination) {

                                total = response.mTotal;

                                queryTelegramChannelContentValues.put(
                                        GoftagramContract.SearchedQueryEntry.COLUMN_TOTAL_TELEGRAM_CHANNEL, total
                                );
                            }

                            queryTelegramChannelContentValues.put(
                                    GoftagramContract.SearchedQueryEntry.COLUMN_UPDATED, System.currentTimeMillis()
                            );
                            queryTelegramChannelContentValues.put(
                                    GoftagramContract.SearchedQueryEntry.COLUMN_STATE, GoftagramContract.SYNC_STATE_IDLE
                            );


                            ContentValues[] telegramChannelOfQuery
                                    = new ContentValues[response.mTelegramChannelList.size()];

                            for (int i = 0; i < telegramChannelOfQuery.length; ++i) {

                                ContentValues insertCvs = new ContentValues();

                                insertCvs.put(
                                        GoftagramContract.TelegramChannelSearchedQueryEntry
                                                .COLUMN_TELEGRAM_CHANNEL_SERVER_ID,
                                        response.mTelegramChannelList.get(i).getServerId()
                                );
                                insertCvs.put(
                                        GoftagramContract.TelegramChannelSearchedQueryEntry
                                                .COLUMN_SEARCHED_QUERY, query

                                );
                                telegramChannelOfQuery[i] = insertCvs;
                            }

                            Uri searchedQueryTelegramChannel =
                                    GoftagramContract
                                            .SearchedQueryEntry
                                            .buildTelegramChannelOfSearchedQueryUri(
                                                    SQLiteDatabase.CONFLICT_IGNORE
                                            );

                            mContext.getContentResolver()
                                    .bulkInsert(searchedQueryTelegramChannel, telegramChannelOfQuery);

                            int rowsUpdated = mContext.getContentResolver().update(
                                    queryChannelUri, queryTelegramChannelContentValues,
                                    GoftagramContract.SearchedQueryEntry.COLUMN_QUERY + " = ? ",
                                    new String[]{query}
                            );

                            mContext.getContentResolver().bulkInsert(telegramChannelUri, cvs);

                        }

                        notifyObserver(
                                transactionId,
                                response.mMessage,
                                GetChannelByQueryResponse.SUCCESS,
                                total
                        );

                    } else {
                        notifyObserver(transactionId, response.mMessage, GetChannelByQueryResponse.FAIL, -1);
                    }

                } else {

                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_EXPIRE) {

                        mHasPendingLogInRequest = true;
                        UserController.getInstance(mContext).updateMainLogIn(false);
                        UserController.getInstance(mContext).doLoginAsync(transactionId);

                    } else if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_FAIL) {

                        notifyObserver(transactionId, response.mMessage, GetChannelByQueryResponse.FAIL, -1);

                    }
                }
            }
        }
    }


    public void onEventAsync(UserController.UserLogInUiEvent event) {
        synchronized (sLock) {
           if (mHasPendingLogInRequest) {
                if (mPendingRequest.size() > 0  && mPendingRequest.get(event.getMessageId())!=null) {
                    GetChannelByQueryRequest request = mPendingRequest.get(event.getMessageId());
                    ServiceHelper.getInstance(mContext)
                            .fetchChannelByQuery(
                                    request.mRequestId,
                                    event.getToken(),
                                    request.mQuery,
                                    request.mServerPageRequest);
                    mHasPendingLogInRequest = false;
                }
            }
        }
    }

}
