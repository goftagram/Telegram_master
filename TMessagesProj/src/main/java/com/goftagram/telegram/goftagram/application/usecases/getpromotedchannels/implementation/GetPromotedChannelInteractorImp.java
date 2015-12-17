package com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.implementation;

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
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract.GetCategoryChannelResponse;
import com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.contract.GetPromotedChannelInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.contract.GetPromotedChannelInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.contract.GetPromotedChannelRequest;
import com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.contract.GetPromotedChannelResponse;
import com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.contract.GetPromotedChannelTaskCallback;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.network.api.message.FetchPromotedChannelMessage;
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
public class GetPromotedChannelInteractorImp implements
        GetPromotedChannelInteractor,
        GetPromotedChannelTaskCallback,
        GetCategoryInteractorCallback {

    private final String LOG_TAG = LogUtils.makeLogTag(GetPromotedChannelInteractorImp.class.getSimpleName());

    private boolean mHasPendingGetCategoryRequest;
    private boolean mHasPendingLogInRequest;

    private static GetPromotedChannelInteractorImp sGetPromotedChannelInteractorImp;
    private Context mContext;
    private static final Object sLock = new Object();

    private ConcurrentMap<Integer, GetPromotedChannelInteractorCallback> mCallbackMap;
    private ConcurrentMap<Integer, GetPromotedChannelRequest> mPendingRequest;


    private GetPromotedChannelInteractorImp(Context context) {
        mContext = context.getApplicationContext();
        mCallbackMap = new ConcurrentHashMap<>();
        mPendingRequest = new ConcurrentHashMap<>();
        mHasPendingGetCategoryRequest = false;
        mHasPendingLogInRequest = false;
        EventBus.getDefault().register(this);
    }

    public static synchronized GetPromotedChannelInteractor getInstance(Context context) {
        if (sGetPromotedChannelInteractorImp == null) {
            sGetPromotedChannelInteractorImp = new GetPromotedChannelInteractorImp(context);
        }
        return sGetPromotedChannelInteractorImp;
    }

    @Override
    public void getAsync(GetPromotedChannelRequest request, GetPromotedChannelInteractorCallback callback) {

        LogUtils.LOGI(LOG_TAG, "GetPromotedChannel getAsync request id = " + request.mRequestId);
        mCallbackMap.put(request.mRequestId, callback);
        mPendingRequest.put(request.mRequestId, request);
        Task task = new GetPromotedChannelTask(mContext, request, this);
        TaskManager.getInstance().execute(task);

    }

    @Override
    public void onHit(GetPromotedChannelRequest request, NameValueDataHolder dataHolder) {
        synchronized (sLock) {
            LogUtils.LOGI(LOG_TAG, "GetPromotedChannel onHit request id = " + request.mRequestId);
            notifyObserver(
                    request.mRequestId,
                    "Success",
                    GetCategoryChannelResponse.SUCCESS,
                    dataHolder.getInt(KEY_TOTAL_CHANNELS)
            );
        }
    }

    @Override
    public void onMiss(GetPromotedChannelRequest request, NameValueDataHolder dataHolder) {
        synchronized (sLock) {

            final boolean isCategoryEmpty = dataHolder.getBoolean(KEY_IS_CATEGORY_EMPTY);
            LogUtils.LOGI(LOG_TAG, "GetPromotedChannel onMiss request id isCategoryEmpty " +
                    request.mRequestId + " " + isCategoryEmpty);
            String token = UserController.getInstance(mContext).getToken();
            if (isCategoryEmpty) {
                if (!mHasPendingGetCategoryRequest) {
                    GetCategoryRequest getPromotedRequest = new GetCategoryRequest();
                    getPromotedRequest.mRequestId = request.mRequestId;
                    GetCategoryInteractorImp.getInstance(mContext).getAsync(
                            getPromotedRequest, this
                    );
                    mHasPendingGetCategoryRequest = true;
                }

            } else {
                if (!mHasPendingGetCategoryRequest) {
                    ServiceHelper.getInstance(mContext)
                            .fetchPromotedChannels(
                                    request.mRequestId,
                                    token
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
            GetPromotedChannelInteractorCallback callback = mCallbackMap.get(requestId);
            if (callback == null) {
                mCallbackMap.remove(requestId);
                mPendingRequest.remove(requestId);
                return;
            }
            GetPromotedChannelResponse responseModel = new GetPromotedChannelResponse();
            responseModel.mMessage = message;
            responseModel.mState = state;
            responseModel.mRequestId = requestId;
            responseModel.mTotalServerItems = totalItem;
            callback.onResponse(responseModel);

            mCallbackMap.remove(requestId);
            mPendingRequest.remove(requestId);

            callback.onResponse(responseModel);
        }
    }


    @Override
    public void onResponse(GetCategoryResponse response) {
        synchronized (sLock) {
            mHasPendingGetCategoryRequest = false;
            LogUtils.LOGI(LOG_TAG, "GetPromotedChannel onResponse");
            if (response.mState == GetCategoryResponse.SUCCESS) {

                for (Integer i : mPendingRequest.keySet()) {
                    Task task = new GetPromotedChannelTask(
                            mContext,
                            mPendingRequest.get(i),
                            this
                    );
                    TaskManager.getInstance().execute(task);
                }

            } else {
                notifyObservers(response.mMessage, GetPromotedChannelResponse.FAIL, -1);
            }
        }
    }

    public void onEventAsync(FetchPromotedChannelMessage networkMessage) {

        synchronized (sLock) {

            int transactionId = networkMessage.mTransactionId;
            String rawResponse = networkMessage.mRawResponse;
            int statusCode = networkMessage.mStatusCode;

            TelegramChannelParser.TelegramChannelListParserResponse response = null;

            if (statusCode == HttpManager.SOCKET_TIME_OUT) {

                notifyObservers(
                        mContext.getString(R.string.io_error),
                        GetPromotedChannelResponse.FAIL,
                        -1);
                return;

            } else {
                try {
                    response = TelegramChannelParser.listParser(rawResponse);

                } catch (JSONException exp) {
                    exp.printStackTrace();
                    notifyObservers(
                            mContext.getString(R.string.server_unknown_format),
                            GetPromotedChannelResponse.FAIL,
                            -1
                    );
                    return;
                }
                if (statusCode == HttpManager.OK) {

                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_SUCCESS) {

                        if (response.mTelegramChannelList.size() > 0) {

                            Uri promotedUri = GoftagramContract
                                    .PromotedTelegramChannelEntry
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
                                        GoftagramContract.PromotedTelegramChannelEntry.COLUMN_SERVER_ID,
                                        serverId
                                );

                                categoryList.add(response.mTelegramChannelList.get(i).getCategory());
                            }


                            mContext.getContentResolver().bulkInsert(telegramChannelUri,
                                    telegramChannelCvs
                            );
                            mContext.getContentResolver().bulkInsert(promotedUri,
                                    telegramChannelIds
                            );
                        }
                        notifyObservers(
                                response.mMessage,
                                GetPromotedChannelResponse.SUCCESS,
                                response.mTelegramChannelList.size()
                        );
                    } else {
                        notifyObservers(response.mMessage, GetPromotedChannelResponse.FAIL, -1);
                    }
                } else {
                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_EXPIRE) {

                        mHasPendingLogInRequest = true;
                        UserController.getInstance(mContext).updateMainLogIn(false);
                        UserController.getInstance(mContext).doLoginAsync(transactionId);

                    } else if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_FAIL) {
                        notifyObservers(response.mMessage, GetPromotedChannelResponse.FAIL, -1);
                    }
                }
            }

        }
    }


    public void onEventAsync(UserController.UserLogInUiEvent event) {
        synchronized (sLock) {
            if (mHasPendingLogInRequest) {
                if (mPendingRequest.size() > 0 && mPendingRequest.get(event.getMessageId()) != null) {
                    Integer pendingRequest = mPendingRequest.get(event.getMessageId()).mRequestId;

                    ServiceHelper.getInstance(mContext)
                            .fetchPromotedChannels(
                                    pendingRequest,
                                    event.getToken());
                    mHasPendingLogInRequest = true;
                }
            }
        }
    }
}
