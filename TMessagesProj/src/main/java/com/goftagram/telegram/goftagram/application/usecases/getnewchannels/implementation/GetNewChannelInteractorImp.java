package com.goftagram.telegram.goftagram.application.usecases.getnewchannels.implementation;

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
import com.goftagram.telegram.goftagram.application.usecases.getnewchannels.contract.GetNewChannelInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getnewchannels.contract.GetNewChannelInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getnewchannels.contract.GetNewChannelRequest;
import com.goftagram.telegram.goftagram.application.usecases.getnewchannels.contract.GetNewChannelResponse;
import com.goftagram.telegram.goftagram.application.usecases.getnewchannels.contract.GetNewChannelTaskCallback;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.network.api.message.FetchNewChannelMessage;
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
public class GetNewChannelInteractorImp implements
        GetNewChannelInteractor,
        GetNewChannelTaskCallback,
        GetCategoryInteractorCallback {

    private final String LOG_TAG = LogUtils.makeLogTag(GetNewChannelInteractorImp.class.getSimpleName());
    private static final boolean DEBUG = true;

    private boolean mHasPendingRequest;
    private static GetNewChannelInteractorImp sGetNewChannelInteractorImp;
    private Context mContext;
    private static final Object sLock = new Object();

    private ConcurrentMap<Integer, GetNewChannelInteractorCallback> mCallbackMap;
    private ConcurrentMap<Integer, GetNewChannelRequest> mPendingRequest;


    private GetNewChannelInteractorImp(Context context) {
        mContext = context.getApplicationContext();
        mCallbackMap = new ConcurrentHashMap<>();
        mPendingRequest = new ConcurrentHashMap<>();
        mHasPendingRequest = false;
        EventBus.getDefault().register(this);
    }

    public static synchronized GetNewChannelInteractor getInstance(Context context) {
        if (sGetNewChannelInteractorImp == null) {
            sGetNewChannelInteractorImp = new GetNewChannelInteractorImp(context);
        }
        return sGetNewChannelInteractorImp;
    }

    @Override
    public void getAsync(GetNewChannelRequest request, GetNewChannelInteractorCallback callback) {

        LogUtils.LOGI(LOG_TAG,"GetNewChannel request id = " + request.mRequestId);
            mCallbackMap.put(request.mRequestId, callback);
            mPendingRequest.put(request.mRequestId, request);
            Task task = new GetNewChannelTask(mContext, request, this);
            TaskManager.getInstance().execute(task);

    }

    @Override
    public void onHit(GetNewChannelRequest request, NameValueDataHolder dataHolder) {
        synchronized (sLock) {
            LogUtils.LOGI(LOG_TAG, "GetNewChannel onHit request id = " + request.mRequestId);
            GetNewChannelInteractorCallback callback = mCallbackMap.get(request.mRequestId);
            if(callback == null){
                mCallbackMap.remove(request.mRequestId);
                mPendingRequest.remove(request.mRequestId);
                return;
            }
            GetNewChannelResponse responseModel = new GetNewChannelResponse();
            responseModel.mMessage = "Succuss";
            responseModel.mState = GetNewChannelResponse.SUCCESS;
            responseModel.mRequestId = request.mRequestId;
            responseModel.mTotalServerItems = dataHolder.getInt(KEY_TOTAL_CHANNELS);
            mCallbackMap.remove(request.mRequestId);
            mPendingRequest.remove(request.mRequestId);
            callback.onResponse(responseModel);
        }
    }

    @Override
    public void onMiss(GetNewChannelRequest request, NameValueDataHolder dataHolder) {
        synchronized (sLock) {
            final boolean isCategoryEmpty = dataHolder.getBoolean(KEY_IS_CATEGORY_EMPTY);
            LogUtils.LOGI(LOG_TAG, "GetNewChannel onMiss request id isCategoryEmpty " +
                    request.mRequestId + " " + isCategoryEmpty);
            String token = UserController.getInstance(mContext).getToken();
            if (isCategoryEmpty) {
                if (!mHasPendingRequest) {
                    GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
                    getCategoryRequest.mRequestId = request.mRequestId;
                    GetCategoryInteractorImp.getInstance(mContext).getAsync(
                            getCategoryRequest, this
                    );
                    mHasPendingRequest = true;
                }

            } else {
                if (!mHasPendingRequest) {
                    ServiceHelper.getInstance(mContext)
                            .fetchNewChannels(
                                    request.mRequestId,
                                    token
                            );
                    mHasPendingRequest = true;
                }
            }
        }
    }



    @Override
    public void onResponse(GetCategoryResponse response) {
        synchronized (sLock) {
            mHasPendingRequest = false;
            LogUtils.LOGI(LOG_TAG, "GetNewChannel onResponse");
            if (response.mState == GetCategoryResponse.SUCCESS) {

                for (Integer i : mPendingRequest.keySet()) {
                    Task task = new GetNewChannelTask(
                            mContext,
                            mPendingRequest.get(i),
                            this
                    );
                    TaskManager.getInstance().execute(task);
                }

            } else {
                notifyObservers(response.mMessage, GetNewChannelResponse.FAIL,-1);
            }
        }
    }

    private void notifyObservers(String message,int state,int totalItem){
        synchronized (sLock) {
            for (Integer pendingRequest : mPendingRequest.keySet()) {

                GetNewChannelInteractorCallback callback = mCallbackMap.get(pendingRequest);
                if(callback == null)continue;
                GetNewChannelResponse responseModel = new GetNewChannelResponse();
                responseModel.mMessage = message;
                responseModel.mState = state;
                responseModel.mRequestId = pendingRequest;
                responseModel.mTotalServerItems = totalItem;
                callback.onResponse(responseModel);
            }
            mCallbackMap.clear();
            mPendingRequest.clear();
        }

    }

    public void onEventAsync(FetchNewChannelMessage networkMessage) {

        synchronized (sLock) {

            mHasPendingRequest = false;
            int transactionId = networkMessage.mTransactionId;
            String rawResponse = networkMessage.mRawResponse;
            int statusCode = networkMessage.mStatusCode;

            TelegramChannelParser.TelegramChannelListParserResponse response = null;

            if ( statusCode == HttpManager.SOCKET_TIME_OUT) {

                notifyObservers(
                        mContext.getString(R.string.io_error),
                        GetNewChannelResponse.FAIL,
                        -1);
                return;

            } else {
                try {
                    response = TelegramChannelParser.listParser(rawResponse);

                } catch (JSONException exp) {
                    exp.printStackTrace();
                    notifyObservers(
                            mContext.getString(R.string.server_unknown_format),
                            GetNewChannelResponse.FAIL,
                            -1
                    );
                    return;
                }
                if (statusCode == HttpManager.OK) {

                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_SUCCESS) {

                        if (response.mTelegramChannelList.size() > 0) {

                            Uri newTelegramChannelUri = GoftagramContract
                                    .NewTelegramChannelEntry
                                    .buildTelegramChannelList();

                            Uri telegramChannelUri = GoftagramContract.TelegramChannelEntry
                                    .buildTelegramChannel(SQLiteDatabase.CONFLICT_IGNORE);


                            ContentValues[] telegramChannelCvs = TelegramChannel.TelegramChannelsToContentValueArray(
                                    response.mTelegramChannelList
                            );

                            int totalChannels = response.mTelegramChannelList.size();
                            ContentValues[] telegramChannelIds = new ContentValues[totalChannels];


                            List<Category> categoryList = new ArrayList<>();


                            for (int i = 0; i < response.mTelegramChannelList.size(); ++i) {

                                String serverId = String.valueOf(
                                        response.mTelegramChannelList.get(i).getServerId()
                                );
                                telegramChannelIds[i] = new ContentValues();
                                telegramChannelIds[i].put(
                                        GoftagramContract.NewTelegramChannelEntry.COLUMN_SERVER_ID,
                                        serverId
                                );

                                categoryList.add(response.mTelegramChannelList.get(i).getCategory());
                            }


                            mContext.getContentResolver().bulkInsert(telegramChannelUri,
                                    telegramChannelCvs
                            );
                            mContext.getContentResolver().bulkInsert(newTelegramChannelUri,
                                    telegramChannelIds
                            );
                        }
                        notifyObservers(
                                response.mMessage,
                                GetNewChannelResponse.SUCCESS,
                                response.mTelegramChannelList.size()
                        );
                    } else {
                        notifyObservers(response.mMessage, GetNewChannelResponse.FAIL, -1);
                    }
                } else {
                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_EXPIRE) {
                        UserController.getInstance(mContext).updateMainLogIn(false);
                        UserController.getInstance(mContext).doLoginAsync(transactionId);
                    } else if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_FAIL) {
                        notifyObservers(response.mMessage, GetNewChannelResponse.FAIL, -1);
                    }
                }
            }

        }
    }



    public void onEventAsync(UserController.UserLogInUiEvent event) {
        synchronized (sLock) {
            if (!mHasPendingRequest) {

                if (mPendingRequest.size() > 0 && mPendingRequest.get(event.getMessageId())!=null) {
                    Integer pendingRequest = mPendingRequest.get(event.getMessageId()).mRequestId;

                    ServiceHelper.getInstance(mContext)
                            .fetchNewChannels(
                                    pendingRequest,
                                    event.getToken());
                    mHasPendingRequest = true;
                }
            }
        }
    }

}
