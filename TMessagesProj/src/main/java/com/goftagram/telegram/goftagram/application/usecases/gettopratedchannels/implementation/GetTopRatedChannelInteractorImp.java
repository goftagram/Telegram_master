package com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.implementation;

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
import com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.contract.GetTopRatedChannelInteractor;
import com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.contract.GetTopRatedChannelInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.contract.GetTopRatedChannelRequest;
import com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.contract.GetTopRatedChannelResponse;
import com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.contract.GetTopRatedChannelTaskCallback;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.network.api.message.FetchTopRatedChannelMessage;
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
public class GetTopRatedChannelInteractorImp implements
        GetTopRatedChannelInteractor,
        GetTopRatedChannelTaskCallback,
        GetCategoryInteractorCallback {

    private final String LOG_TAG = LogUtils.makeLogTag(GetTopRatedChannelInteractorImp.class.getSimpleName());
    private static final boolean DEBUG = true;

    private boolean mHasPendingRequest;
    private static GetTopRatedChannelInteractorImp sGetTopRatedChannelInteractorImp;
    private Context mContext;
    private static final Object sLock = new Object();

    private ConcurrentMap<Integer, GetTopRatedChannelInteractorCallback> mCallbackMap;
    private ConcurrentMap<Integer, GetTopRatedChannelRequest> mPendingRequest;


    private GetTopRatedChannelInteractorImp(Context context) {
        mContext = context.getApplicationContext();
        mCallbackMap = new ConcurrentHashMap<>();
        mPendingRequest = new ConcurrentHashMap<>();
        mHasPendingRequest = false;
        EventBus.getDefault().register(this);
    }

    public static synchronized GetTopRatedChannelInteractor getInstance(Context context) {
        if (sGetTopRatedChannelInteractorImp == null) {
            sGetTopRatedChannelInteractorImp = new GetTopRatedChannelInteractorImp(context);
        }
        return sGetTopRatedChannelInteractorImp;
    }

    @Override
    public void getAsync(GetTopRatedChannelRequest request, GetTopRatedChannelInteractorCallback callback) {

        LogUtils.LOGI(LOG_TAG,"GetTopRatedChannel request id = " + request.mRequestId);
            mCallbackMap.put(request.mRequestId, callback);
            mPendingRequest.put(request.mRequestId, request);
            Task task = new GetTopRatedChannelTask(mContext, request, this);
            TaskManager.getInstance().execute(task);

    }

    @Override
    public void onHit(GetTopRatedChannelRequest request, NameValueDataHolder dataHolder) {
        synchronized (sLock) {
            LogUtils.LOGI(LOG_TAG, "GetTopRatedChannel onHit request id = " + request.mRequestId);
            GetTopRatedChannelInteractorCallback callback = mCallbackMap.get(request.mRequestId);
            if(callback == null){
                mCallbackMap.remove(request.mRequestId);
                mPendingRequest.remove(request.mRequestId);
                return;
            }
            GetTopRatedChannelResponse responseModel = new GetTopRatedChannelResponse();
            responseModel.mMessage = "Succuss";
            responseModel.mState = GetTopRatedChannelResponse.SUCCESS;
            responseModel.mRequestId = request.mRequestId;
            responseModel.mTotalServerItems = dataHolder.getInt(KEY_TOTAL_CHANNELS);
            mCallbackMap.remove(request.mRequestId);
            mPendingRequest.remove(request.mRequestId);
            callback.onResponse(responseModel);
        }
    }

    @Override
    public void onMiss(GetTopRatedChannelRequest request, NameValueDataHolder dataHolder) {
        synchronized (sLock) {
            final boolean isCategoryEmpty = dataHolder.getBoolean(KEY_IS_CATEGORY_EMPTY);
            LogUtils.LOGI(LOG_TAG, "GetTopRatedChannel onMiss request id isCategoryEmpty " +
                    request.mRequestId + " " + isCategoryEmpty);
            String token = UserController.getInstance(mContext).getToken();
            if (isCategoryEmpty) {
                if (!mHasPendingRequest) {
                    GetCategoryRequest getTopRatedRequest = new GetCategoryRequest();
                    getTopRatedRequest.mRequestId = request.mRequestId;
                    GetCategoryInteractorImp.getInstance(mContext).getAsync(
                            getTopRatedRequest, this
                    );
                    mHasPendingRequest = true;
                }

            } else {
                if (!mHasPendingRequest) {
                    ServiceHelper.getInstance(mContext)
                            .fetchTopRatedChannels(
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
            LogUtils.LOGI(LOG_TAG, "GetTopRatedChannel onResponse");
            if (response.mState == GetCategoryResponse.SUCCESS) {

                for (Integer i : mPendingRequest.keySet()) {
                    Task task = new GetTopRatedChannelTask(
                            mContext,
                            mPendingRequest.get(i),
                            this
                    );
                    TaskManager.getInstance().execute(task);
                }

            } else {
                notifyObservers(response.mMessage, GetTopRatedChannelResponse.FAIL,-1);
            }
        }
    }

    private void notifyObservers(String message,int state,int totalItem){
        synchronized (sLock) {
            for (Integer pendingRequest : mPendingRequest.keySet()) {

                GetTopRatedChannelInteractorCallback callback = mCallbackMap.get(pendingRequest);
                if(callback == null)continue;
                GetTopRatedChannelResponse responseModel = new GetTopRatedChannelResponse();
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

    public void onEventAsync(FetchTopRatedChannelMessage networkMessage) {

        synchronized (sLock) {

            mHasPendingRequest = false;
            int transactionId = networkMessage.mTransactionId;
            String rawResponse = networkMessage.mRawResponse;
            int statusCode = networkMessage.mStatusCode;

            TelegramChannelParser.TelegramChannelListParserResponse response = null;

            if ( statusCode == HttpManager.SOCKET_TIME_OUT) {

                notifyObservers(
                        mContext.getString(R.string.io_error),
                        GetTopRatedChannelResponse.FAIL,
                        -1);
                return;

            } else {
                try {
                    response = TelegramChannelParser.listParser(rawResponse);

                } catch (JSONException exp) {
                    exp.printStackTrace();
                    notifyObservers(
                            mContext.getString(R.string.server_unknown_format),
                            GetTopRatedChannelResponse.FAIL,
                            -1
                    );
                    return;
                }
                if (statusCode == HttpManager.OK) {

                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_SUCCESS) {

                        if (response.mTelegramChannelList.size() > 0) {

                            Uri TopRatedUri = GoftagramContract
                                    .TopRatedTelegramChannelEntry
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
                                        GoftagramContract.TopRatedTelegramChannelEntry.COLUMN_SERVER_ID,
                                        serverId
                                );

                                categoryList.add(response.mTelegramChannelList.get(i).getCategory());
                            }


                            mContext.getContentResolver().bulkInsert(telegramChannelUri,
                                    telegramChannelCvs
                            );
                            mContext.getContentResolver().bulkInsert(TopRatedUri,
                                    telegramChannelIds
                            );
                        }
                        notifyObservers(
                                response.mMessage,
                                GetTopRatedChannelResponse.SUCCESS,
                                response.mTelegramChannelList.size()
                        );
                    } else {
                        notifyObservers(response.mMessage, GetTopRatedChannelResponse.FAIL, -1);
                    }
                } else {
                    if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_EXPIRE) {
                        UserController.getInstance(mContext).updateMainLogIn(false);
                        UserController.getInstance(mContext).doLoginAsync(transactionId);
                    } else if (response.mStatusCode == TelegramChannelParser.STATUS_CODE_FAIL) {
                        notifyObservers(response.mMessage, GetTopRatedChannelResponse.FAIL, -1);
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
                            .fetchTopRatedChannels(
                                    pendingRequest,
                                    event.getToken());
                    mHasPendingRequest = true;
                }
            }
        }
    }

}
