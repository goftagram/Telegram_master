package com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.implementation;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.goftagram.telegram.goftagram.application.model.Comment;
import com.goftagram.telegram.goftagram.application.model.Tag;
import com.goftagram.telegram.goftagram.application.model.TelegramChannel;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetRequest;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryRequest;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryResponse;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.implementation.GetCategoryInteractorImp;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.ChannelMetaDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataRequest;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataResponse;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataTaskCallback;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.network.api.message.FetchChannelMetaDataMessage;
import com.goftagram.telegram.goftagram.parser.TelegramChannelParser;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.taskmanager.Task;
import com.goftagram.telegram.goftagram.taskmanager.TaskManager;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.messenger.R;

import org.json.JSONException;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.greenrobot.event.EventBus;

/**
 * Created by WORK on 10/30/2015.
 */
public class GetChannelMetaDataInteractorImp implements
        GetChannelMetaDataInteractor,
        GetChannelMetaDataTaskCallback,
        GetCategoryInteractorCallback {

    private final String LOG_TAG = LogUtils.makeLogTag(GetChannelMetaDataInteractorImp.class.getSimpleName());




    private int  mState = STATE_IDLE;


    private boolean mHasPendingRequest;
    private static GetChannelMetaDataInteractorImp sGetChannelMetaDataInteractorImp;
    private Context mContext;
    private static final Object sLock = new Object();

    private ConcurrentMap<Integer, GetChannelMetaDataInteractorCallback> mCallbackMap;
    private ConcurrentMap<Integer, GetChannelMetaDataRequest> mRequestQueue;


    private GetChannelMetaDataInteractorImp(Context context) {
        mContext = context.getApplicationContext();
        mCallbackMap = new ConcurrentHashMap<>();
        mRequestQueue = new ConcurrentHashMap<>();
        mHasPendingRequest = false;
        EventBus.getDefault().register(this);
    }

    public static synchronized GetChannelMetaDataInteractor getInstance(Context context) {
        if (sGetChannelMetaDataInteractorImp == null) {
            sGetChannelMetaDataInteractorImp = new GetChannelMetaDataInteractorImp(context);
        }
        return sGetChannelMetaDataInteractorImp;
    }


    @Override
    public void getAsync(GetChannelMetaDataRequest request, GetChannelMetaDataInteractorCallback callback) {

            request.mState = AbsGetRequest.PRE_PROCESSING_REQUEST;
            mCallbackMap.put(request.mRequestId, callback);
            mRequestQueue.put(request.mRequestId, request);
            request.mState = AbsGetRequest.READING_LOCAL_DATA_SOURCE;
            Task task = new GetChannelMetaDataTask(mContext, request, this);
            TaskManager.getInstance().execute(task);

    }


    @Override
    public void onHit(GetChannelMetaDataRequest request, ChannelMetaDataHolder dataHolder) {
        synchronized (sLock) {

            GetChannelMetaDataInteractorCallback callback = mCallbackMap.get(request.mRequestId);
            if(callback == null){
                mCallbackMap.remove(request.mRequestId);
                mRequestQueue.remove(request.mRequestId);
                return;
            }
            GetChannelMetaDataResponse responseModel = new GetChannelMetaDataResponse();
            responseModel.mMessage = "Succuss";
            responseModel.mState = GetChannelMetaDataResponse.SUCCESS;
            responseModel.mRequestId = request.mRequestId;

            responseModel.mHasMoreComment               = dataHolder.getBoolean(KEY_HAS_MORE_COMMENTS);
            responseModel.mRelatedTelegramChannelList   = dataHolder.getList(KEY_RELATED_TELEGRAM_CHANNEL_LIST);
            responseModel.mTagList                      = dataHolder.getList(KEY_TAG_LIST);
            responseModel.mLocalCommentList             = dataHolder.getList(KEY_LOCAL_COMMENT_LIST);
            responseModel.mDownloadedCommentList        = dataHolder.getList(KEY_DOWNLOADED_COMMENT_LIST);
            responseModel.mTelegramChannel              = dataHolder.getTelegramChannel(KEY_TELEGRAM_CHANNEL);

            mCallbackMap.remove(request.mRequestId);
            mRequestQueue.remove(request.mRequestId);
            callback.onResponse(responseModel);
        }
    }

    @Override
    public void onMiss(GetChannelMetaDataRequest request, ChannelMetaDataHolder dataHolder) {

        synchronized (sLock) {
            final boolean isCategoryEmpty = dataHolder.getBoolean(KEY_IS_CATEGORY_EMPTY);
            String token = UserController.getInstance(mContext).getToken();
            if (isCategoryEmpty) {
                if (!mHasPendingRequest) {

                    GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
                    getCategoryRequest.mRequestId = request.mRequestId;
                    GetCategoryInteractorImp.getInstance(mContext).getAsync(getCategoryRequest, this);
                    mHasPendingRequest = true;
                }

            } else {

                if (!mHasPendingRequest) {
                    ServiceHelper
                            .getInstance(mContext)
                            .fetchChannelMetaData(
                                    request.mRequestId,
                                    token,
                                    request.mTelegramChannelId,
                                    request.mServerPageRequest
                            );
                }
            }
        }
    }


    @Override
    public void onResponse(GetCategoryResponse response) {
        synchronized (sLock) {
            mHasPendingRequest = false;
            if (response.mState == GetCategoryResponse.SUCCESS) {
                for (Integer i : mRequestQueue.keySet()) {
                    Task task = new GetChannelMetaDataTask(
                            mContext,
                            mRequestQueue.get(i),
                            this
                    );
                    TaskManager.getInstance().execute(task);
                }

            } else {

                notifyObservers(response.mMessage, GetChannelMetaDataResponse.FAIL, null);
            }
        }
    }

    private void notifyObservers(String message, int state, NameValueDataHolder dataHolder) {

        synchronized (sLock) {

            for (Integer pendingRequest : mRequestQueue.keySet()) {

                GetChannelMetaDataInteractorCallback callback = mCallbackMap.get(pendingRequest);
                if(callback == null)continue;

                GetChannelMetaDataResponse responseModel = new GetChannelMetaDataResponse();
                responseModel.mMessage = message;
                responseModel.mState = state;
                responseModel.mRequestId = pendingRequest;

                if (dataHolder != null) {
                    responseModel.mHasMoreComment
                            = dataHolder.getBoolean(KEY_HAS_MORE_COMMENTS);
                    responseModel.mRelatedTelegramChannelList
                            = dataHolder.getList(KEY_RELATED_TELEGRAM_CHANNEL_LIST);
                    responseModel.mTagList
                            = dataHolder.getList(KEY_TAG_LIST);
                    responseModel.mLocalCommentList
                            = dataHolder.getList(KEY_LOCAL_COMMENT_LIST);
                    responseModel.mDownloadedCommentList
                            = dataHolder.getList(KEY_DOWNLOADED_COMMENT_LIST);
                }

                callback.onResponse(responseModel);
            }
            mCallbackMap.clear();
            mRequestQueue.clear();
        }
    }

    public void onEventAsync(FetchChannelMetaDataMessage networkMessage) {

        synchronized (sLock) {

            mHasPendingRequest = false;
            int transactionId = networkMessage.mTransactionId;
            String rawResponse = networkMessage.mRawResponse;
            String telegramChannelServerId = networkMessage.mTelegramChannelServerId;
            int statusCode = networkMessage.mStatusCode;
            int page = networkMessage.mPage;

            TelegramChannelParser.TelegramChannelMetaDataParserResponse response = null;

            if (statusCode ==  HttpManager.SOCKET_TIME_OUT) {

                notifyObservers(
                        mContext.getString(R.string.io_error),
                        GetChannelMetaDataResponse.FAIL,
                        null);
                return;

            } else {
                try {
                    response = TelegramChannelParser.metaParser(rawResponse);

                } catch (JSONException exp) {

                    exp.printStackTrace();
                    notifyObservers(mContext.getString(R.string.server_unknown_format),
                            GetChannelMetaDataResponse.FAIL,
                            null
                    );


                    return;


                }

                if (statusCode == HttpManager.OK) {

                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_SUCCESS) {

                        int totalComments = 0;
                        ChannelMetaDataHolder nameValueDataHolder = new ChannelMetaDataHolder();
                        nameValueDataHolder.put(KEY_TELEGRAM_CHANNEL,response.mTelegramChannel);

                        if (page == 1) {

                            Uri telegramChannelMetaDataUri =
                                    GoftagramContract
                                            .TelegramChannelEntry
                                            .buildTelegramChannelDetail();


                            ContentValues telegramChannelCvs =
                                    TelegramChannel.TelegramChannelToContentValues(
                                            response.mTelegramChannel
                                    );

                            long updatedTime = System.currentTimeMillis();

                            String state = GoftagramContract.SYNC_STATE_IDLE;
                            telegramChannelCvs.put(GoftagramContract.TelegramChannelEntry.COLUMN_UPDATED, updatedTime);
                            telegramChannelCvs.put(GoftagramContract.TelegramChannelEntry.COLUMN_STATE, state);

                            int rowUpdated = mContext.getContentResolver().update(
                                    telegramChannelMetaDataUri,
                                    telegramChannelCvs,
                                    GoftagramContract.TelegramChannelEntry.COLUMN_SERVER_ID +" = ? ",
                                    new String[]{response.mTelegramChannel.getServerId()}
                            );

                            if(rowUpdated <= 0){
                                mContext.getContentResolver().insert(
                                        telegramChannelMetaDataUri,
                                        telegramChannelCvs
                                );
                            }

                            if (response.mTelegramChannel.getRelatedChannel() != null) {

                                TelegramChannel[] relatedTelegramChannel =
                                        response.mTelegramChannel.getRelatedChannel();

                                ContentValues[] cvs = TelegramChannel.TelegramChannelsToContentValueArray(
                                        relatedTelegramChannel);

                                Uri bulkInsert = GoftagramContract.TelegramChannelEntry.buildTelegramChannel(
                                        SQLiteDatabase.CONFLICT_IGNORE);

                                mContext.getContentResolver().bulkInsert(bulkInsert, cvs);


                                Uri telegramChannelTelegramChannelTable =
                                        GoftagramContract.TelegramChannelTelegramChannelEntry
                                                .buildTelegramChannelTelegramChannelUri(
                                                        SQLiteDatabase.CONFLICT_IGNORE
                                                );

                                ContentValues[] telegramChannelTelegramChannelContVals =
                                        new ContentValues[relatedTelegramChannel.length];


                                for (int i = 0; i < relatedTelegramChannel.length; ++i) {
                                    ContentValues insertCvs = new ContentValues();
                                    insertCvs.put(
                                            GoftagramContract.TelegramChannelTelegramChannelEntry.COLUMN_TELEGRAM_CHANNEL_ID_1,
                                            telegramChannelServerId);
                                    insertCvs.put(
                                            GoftagramContract.TelegramChannelTelegramChannelEntry.COLUMN_TELEGRAM_CHANNEL_ID_2,
                                            relatedTelegramChannel[i].getServerId());
                                    telegramChannelTelegramChannelContVals[i] = insertCvs;
                                }

                                mContext.getContentResolver().bulkInsert(
                                        telegramChannelTelegramChannelTable,
                                        telegramChannelTelegramChannelContVals
                                );

                                nameValueDataHolder.put(KEY_RELATED_TELEGRAM_CHANNEL_LIST,
                                        Arrays.asList(response.mTelegramChannel.getRelatedChannel()));

                            }

                            if (response.mTelegramChannel.getTags() != null &&
                                    response.mTelegramChannel.getTags().length > 0) {
                                Tag[] tags = response.mTelegramChannel.getTags();
                                Tag.insertTagArray(tags, mContext);

                                Uri updateTelegramChannelTagTable =
                                        GoftagramContract
                                                .TelegramChannelEntry
                                                .buildTelegramChannelTagUri();


                                ContentValues[] telegramChannelTagContVals =
                                        new ContentValues[tags.length];


                                for (int i = 0; i < tags.length; ++i) {
                                    ContentValues insertCvs = new ContentValues();
                                    insertCvs.put(
                                            GoftagramContract.TelegramChannelTagEntry.COLUMN_TELEGRAM_CHANNEL_SERVER_ID,
                                            telegramChannelServerId);
                                    insertCvs.put(
                                            GoftagramContract.TelegramChannelTagEntry.COLUMN_TAG_SERVER_ID,
                                            tags[i].getServerId());
                                    telegramChannelTagContVals[i] = insertCvs;
                                }

                                mContext.getContentResolver().bulkInsert(
                                        updateTelegramChannelTagTable,
                                        telegramChannelTagContVals
                                );

                                nameValueDataHolder.put(KEY_TAG_LIST,
                                        Arrays.asList(response.mTelegramChannel.getTags()));
                            }

                        }

                        int hasReadAllComments = 1;

                        if (response.mTelegramChannel.getComment() != null &&
                                response.mTelegramChannel.getComment().length > 0) {
                            Comment[] comments = response.mTelegramChannel.getComment();
                            Comment.insertCommentArray(comments, mContext);
                            hasReadAllComments = 0;
                        }

                        ContentValues cv = new ContentValues();

                        cv.put(
                                GoftagramContract.TelegramChannelEntry.COLUMN_HAS_READ_ALL_COMMENTS,
                                hasReadAllComments
                        );

                        mContext.getContentResolver().update(
                                GoftagramContract
                                        .TelegramChannelEntry
                                        .buildTelegramChannelDetail(),
                                cv,
                                GoftagramContract.TelegramChannelEntry.COLUMN_SERVER_ID + " = ? ",
                                new String[]{telegramChannelServerId}
                        );


                        LogUtils.LOGI(LOG_TAG, "hasReadAllComments: " + hasReadAllComments);



                        nameValueDataHolder.put(KEY_HAS_MORE_COMMENTS, (hasReadAllComments == 0));

                        nameValueDataHolder.put(KEY_DOWNLOADED_COMMENT_LIST,
                                Arrays.asList(response.mTelegramChannel.getComment()));

                        notifyObservers(
                                response.mMessage,
                                GetChannelMetaDataResponse.SUCCESS,
                                nameValueDataHolder
                        );




                    } else {
                        notifyObservers(response.mMessage, GetChannelMetaDataResponse.FAIL, null);
                    }

                } else {

                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_EXPIRE) {

                        UserController.getInstance(mContext).updateMainLogIn(false);
                        UserController.getInstance(mContext).doLoginAsync(transactionId);

                    } else if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_FAIL) {

                        notifyObservers(response.mMessage, GetChannelMetaDataResponse.FAIL, null);

                    }
                }

            }
        }

    }


    public void onEventAsync(UserController.UserLogInUiEvent event) {
        synchronized (sLock) {
            if (!mHasPendingRequest) {
                if (mRequestQueue.size() > 0 && mRequestQueue.get(event.getMessageId()) != null) {
                    GetChannelMetaDataRequest request = mRequestQueue.get(event.getMessageId());
                    ServiceHelper.getInstance(mContext)
                            .fetchChannelMetaData(
                                    request.mRequestId,
                                    event.getToken(),
                                    request.mTelegramChannelId,
                                    request.mServerPageRequest);
                    mHasPendingRequest = true;
                }
            }
        }
    }

}
