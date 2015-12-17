package com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.implementation;

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
import com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.contract.GetChannelByTagInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.contract.GetChannelByTagInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.contract.GetChannelByTagRequest;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.contract.GetChannelByTagResponse;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.contract.GetChannelByTagTaskCallback;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.network.api.message.FetchChannelsByTagNetworkMessage;
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
public class GetChannelByTagInteractorImp implements
        GetChannelByTagInteractor,
        GetChannelByTagTaskCallback,
        GetCategoryInteractorCallback {



    private final String LOG_TAG = LogUtils.makeLogTag(GetChannelByTagInteractorImp.class.getSimpleName());



    private boolean mHasPendingGetCategoryRequest;
    private boolean mHasPendingLogInRequest;

    private static GetChannelByTagInteractorImp sGetChannelByTagInteractorImp;
    private Context mContext;
    private static final Object sLock = new Object();

    private ConcurrentMap<Integer, GetChannelByTagInteractorCallback> mCallbackMap;
    private ConcurrentMap<Integer, GetChannelByTagRequest> mPendingRequest;


    private GetChannelByTagInteractorImp(Context context) {
        mContext = context.getApplicationContext();
        mCallbackMap = new ConcurrentHashMap<>();
        mPendingRequest = new ConcurrentHashMap<>();
        mHasPendingGetCategoryRequest = false;
        EventBus.getDefault().register(this);
    }

    public static synchronized GetChannelByTagInteractor getInstance(Context context) {
        if (sGetChannelByTagInteractorImp == null) {
            sGetChannelByTagInteractorImp = new GetChannelByTagInteractorImp(context);
        }
        return sGetChannelByTagInteractorImp;
    }


    @Override
    public void getAsync(GetChannelByTagRequest request, GetChannelByTagInteractorCallback callback) {

            mCallbackMap.put(request.mRequestId, callback);
            mPendingRequest.put(request.mRequestId, request);
            Task task = new GetChannelByTagTask(mContext, request, this);
            TaskManager.getInstance().execute(task);

    }


    @Override
    public void onHit(GetChannelByTagRequest request, NameValueDataHolder dataHolder) {
        synchronized (sLock) {

            notifyObserver(
                    request.mRequestId,
                    "Success",
                    GetChannelByTagResponse.SUCCESS,
                    dataHolder.getInt(KEY_TOTAL_CHANNELS)
            );

        }
    }

    @Override
    public void onMiss(GetChannelByTagRequest request, NameValueDataHolder dataHolder) {

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
                            .fetchChannelByTag(
                                    request.mRequestId,
                                    token,
                                    request.mTagId,
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
            GetChannelByTagInteractorCallback callback = mCallbackMap.get(requestId);
            if(callback == null){
                mCallbackMap.remove(requestId);
                mPendingRequest.remove(requestId);
                return;
            }
            GetChannelByTagResponse responseModel = new GetChannelByTagResponse();
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
                    Task task = new GetChannelByTagTask(
                            mContext,
                            mPendingRequest.get(i),
                            this
                    );
                    TaskManager.getInstance().execute(task);
                }
            } else {
                notifyObservers(response.mMessage, GetChannelByTagResponse.FAIL, -1);
            }
        }
    }



    public void onEventAsync(FetchChannelsByTagNetworkMessage networkMessage) {

        synchronized (sLock) {

            int transactionId = networkMessage.mTransactionId;
            String rawResponse = networkMessage.mRawResponse;
            String tagId = networkMessage.mTagId;
            int statusCode = networkMessage.mStatusCode;
            int page = networkMessage.mPage;


            TelegramChannelParser.TelegramChannelListParserResponse response = null;
            if(statusCode == HttpManager.SOCKET_TIME_OUT){

                notifyObservers(
                        mContext.getString(R.string.io_error),
                        GetChannelByTagResponse.FAIL,
                        -1);
                return;

            }else {

                try {
                    response = TelegramChannelParser.listParser(rawResponse);

                } catch (JSONException exp) {

                    exp.printStackTrace();
                    notifyObserver(transactionId,
                            mContext.getString(R.string.server_unknown_format),
                            GetChannelByTagResponse.FAIL,
                            -1
                    );


                    return;


                }

                if (statusCode == HttpManager.OK) {

                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_SUCCESS) {

                        int totalPage = 0;


                        if (response.mTelegramChannelList.size() > 0) {

                            Uri telegramChannelUri = GoftagramContract.TelegramChannelEntry
                                    .buildTelegramChannel(
                                            SQLiteDatabase.CONFLICT_IGNORE
                                    );

                            ContentValues[] cvs = TelegramChannel.TelegramChannelsToContentValueArray(
                                    response.mTelegramChannelList
                            );

                            ContentValues tagChannelContentValues = new ContentValues();

                            Uri tagChannelUri = GoftagramContract.TagEntry.buildTagUri();

                            if (response.mHasPagination) {

                                totalPage = response.mTotal;

                                tagChannelContentValues.put(
                                        GoftagramContract.TagEntry.COLUMN_TOTAL_TELEGRAM_CHANNEL, totalPage
                                );
                            }

                            tagChannelContentValues.put(
                                    GoftagramContract.CategoryEntry.COLUMN_UPDATED, System.currentTimeMillis()
                            );
                            tagChannelContentValues.put(
                                    GoftagramContract.CategoryEntry.COLUMN_STATE, GoftagramContract.SYNC_STATE_IDLE
                            );

                            int rowsUpdated = mContext.getContentResolver().update(
                                    tagChannelUri, tagChannelContentValues,
                                    GoftagramContract.TagEntry.COLUMN_SERVER_ID + " = ? ",
                                    new String[]{tagId});


                            ContentValues[] telegramChannelOfTag
                                    = new ContentValues[response.mTelegramChannelList.size()];

                            for (int i = 0; i < telegramChannelOfTag.length; ++i) {

                                ContentValues insertCvs = new ContentValues();

                                insertCvs.put(
                                        GoftagramContract.TelegramChannelTagEntry
                                                .COLUMN_TELEGRAM_CHANNEL_SERVER_ID,
                                        response.mTelegramChannelList.get(i).getServerId()
                                );
                                insertCvs.put(
                                        GoftagramContract.TelegramChannelTagEntry
                                                .COLUMN_TAG_SERVER_ID, tagId

                                );
                                telegramChannelOfTag[i] = insertCvs;
                            }

                            Uri telegramChannelOfTagUri =
                                    GoftagramContract
                                            .TelegramChannelTagEntry
                                            .buildTelegramChannelTagUri(
                                                    SQLiteDatabase.CONFLICT_IGNORE
                                            );
                            mContext.getContentResolver()
                                    .bulkInsert(telegramChannelOfTagUri, telegramChannelOfTag);


                            mContext.getContentResolver().bulkInsert(telegramChannelUri, cvs);


                        }

                        notifyObserver(
                                transactionId,
                                response.mMessage,
                                GetChannelByTagResponse.SUCCESS,
                                totalPage
                        );

                    } else {
                        notifyObserver(transactionId, response.mMessage, GetChannelByTagResponse.FAIL, -1);
                    }

                } else {

                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_EXPIRE) {

                        mHasPendingLogInRequest = true;
                        UserController.getInstance(mContext).updateMainLogIn(false);
                        UserController.getInstance(mContext).doLoginAsync(transactionId);

                    } else if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_FAIL) {

                        notifyObserver(transactionId, response.mMessage, GetChannelByTagResponse.FAIL, -1);

                    }
                }
            }
        }
    }


    public void onEventAsync(UserController.UserLogInUiEvent event) {
        synchronized (sLock) {
             if (mHasPendingLogInRequest) {
                if (mPendingRequest.size() > 0  && mPendingRequest.get(event.getMessageId())!=null) {
                    GetChannelByTagRequest request = mPendingRequest.get(event.getMessageId());
                    ServiceHelper.getInstance(mContext)
                            .fetchChannelByCategory(
                                    request.mRequestId,
                                    event.getToken(),
                                    request.mTagId,
                                    request.mServerPageRequest);
                    mHasPendingLogInRequest = false;
                }
            }
        }
    }

}
