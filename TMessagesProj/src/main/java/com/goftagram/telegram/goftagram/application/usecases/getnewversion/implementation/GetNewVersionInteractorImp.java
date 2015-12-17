package com.goftagram.telegram.goftagram.application.usecases.getnewversion.implementation;


import android.content.Context;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryResponse;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.GetNewVersionInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.GetNewVersionInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.GetNewVersionRequest;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.GetNewVersionResponse;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.GetNewVersionTaskCallback;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.NewApkEntity;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.network.api.message.AppVersionMessage;
import com.goftagram.telegram.goftagram.parser.AppVersionParser;
import com.goftagram.telegram.goftagram.taskmanager.Task;
import com.goftagram.telegram.goftagram.taskmanager.TaskManager;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.messenger.R;

import org.json.JSONException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.greenrobot.event.EventBus;

/**
 * Created by WORK on 11/2/2015.
 */
public class GetNewVersionInteractorImp implements
        GetNewVersionInteractor,
        GetNewVersionTaskCallback {

    private final String LOG_TAG = LogUtils.makeLogTag(GetNewVersionInteractorImp.class.getSimpleName());

    public static final String KEY_IS_UPDATED = "Is_Updated";
    public static final String KEY_IS_DOWNLOADING = "Is_Downloading";
    public static final String KEY_HAS_NEW_APK_DOWNLOADED = "Has_New_Apk_Downloaded";
    public static final String KEY_NEW_APK_URI = "New_Apk";
    public static final String KEY_MISS_CHECK_NEW_APK_FREQUENCY = "Miss_Check_New_Apk_Frequency";

    private static GetNewVersionInteractorImp sGetNewVersionInteractorImp;
    private Context mContext;
    private static final Object sLock = new Object();
    private ConcurrentMap<Integer, GetNewVersionRequest> mPendingRequest;
    private ConcurrentMap<Integer, GetNewVersionInteractorCallback> mCallbackMap;
    private boolean mHasPendingRequest;

    private GetNewVersionInteractorImp(Context context) {
        mContext = context.getApplicationContext();
        mCallbackMap = new ConcurrentHashMap<>();
        mPendingRequest = new ConcurrentHashMap<>();
        EventBus.getDefault().register(this);
        mHasPendingRequest = false;
    }

    public static synchronized GetNewVersionInteractor getInstance(Context context) {
        if (sGetNewVersionInteractorImp == null) {
            sGetNewVersionInteractorImp = new GetNewVersionInteractorImp(context);
        }
        return sGetNewVersionInteractorImp;
    }


    @Override
    public void GetNewVersionAsync(GetNewVersionRequest request, GetNewVersionInteractorCallback callback) {

        if (!hasPendingRequest()) {

            mCallbackMap.put(request.mRequestId, callback);
            mPendingRequest.put(request.mRequestId, request);

            Task task = new GetNewVersionTask(mContext, request, this);
            TaskManager.getInstance().execute(task);

        }
    }


    @Override
    public void onHit(GetNewVersionRequest request, NameValueDataHolder nameValueDataHolder) {
        synchronized (sLock) {

            GetNewVersionInteractorCallback callback = mCallbackMap.get(request.mRequestId);
            if(callback == null){
                mCallbackMap.remove(request.mRequestId);
                mPendingRequest.remove(request.mRequestId);
                return;
            }
            GetNewVersionResponse responseModel = new GetNewVersionResponse();
            responseModel.mMessage = "Succuss";
            responseModel.mState = GetCategoryResponse.SUCCESS;
            responseModel.mTransactionId = request.mRequestId;

            if (nameValueDataHolder.getBoolean(KEY_IS_UPDATED)) {
                responseModel.mIsUpdated = true;
            } else {
                responseModel.mIsUpdated = false;
                responseModel.mUrl = nameValueDataHolder.getString(KEY_NEW_APK_URI);

            }

            mCallbackMap.remove(request.mRequestId);
            mPendingRequest.remove(request.mRequestId);
            callback.onResponse(responseModel);
        }
    }

    @Override
    public void onMiss(GetNewVersionRequest request, NameValueDataHolder nameValueDataHolder) {

        synchronized (sLock) {

            if (!hasPendingRequest()) {
                String token = UserController.getInstance(mContext).getToken();
                ServiceHelper.getInstance(mContext).fetchNewVersionOfApp(request.mRequestId, token);
                setHasPendingRequest(true);
            }


        }
    }


    public void onEventAsync(AppVersionMessage networkMessage) {

        synchronized (sLock) {

            setHasPendingRequest(false);
            int transactionId = networkMessage.mTransactionId;
            String rawResponse = networkMessage.mRawResponse;
            int statusCode = networkMessage.mStatusCode;
            AppVersionParser.AppVersionParserResponse response = null;

            if (statusCode == HttpManager.SOCKET_TIME_OUT) {

                notifyObservers(
                        mContext.getString(R.string.io_error),
                        GetNewVersionResponse.FAIL, false, "");
                return;

            } else {
                try {
                    response = AppVersionParser.parser(rawResponse);


                } catch (JSONException exp) {

                    exp.printStackTrace();
                    notifyObservers(
                            mContext.getString(R.string.server_unknown_format),
                            GetNewVersionResponse.FAIL,
                            false,
                            ""
                    );
                    return;
                }

                if (statusCode == HttpManager.OK) {

                    if (response.mStatusCode == AppVersionParser.STATUS_CODE_SUCCESS) {

                        makeDownloadRequest(response);
                        notifyObservers(response.mMessage, GetNewVersionResponse.FAIL, false, "");
                        return;

                    } else {
                        notifyObservers(response.mMessage, GetNewVersionResponse.FAIL, false, "");
                        return;
                    }

                } else {

                    if (response.mStatusCode == AppVersionParser.STATUS_CODE_EXPIRE) {

                        UserController.getInstance(mContext).updateMainLogIn(false);
                        UserController.getInstance(mContext).doLoginAsync(transactionId);


                    } else if (response.mStatusCode == AppVersionParser.STATUS_CODE_FAIL) {

                        notifyObservers(response.mMessage, GetNewVersionResponse.FAIL, false, "");
                        return;
                    }
                }

            }
        }
    }


    private void notifyObservers(String message, int state, boolean isUpdated, String url) {
        synchronized (sLock) {
            for (Integer pendingRequest : mPendingRequest.keySet()) {

                GetNewVersionInteractorCallback callback = mCallbackMap.get(pendingRequest);
                if (callback == null) continue;
                GetNewVersionResponse responseModel = new GetNewVersionResponse();
                responseModel.mMessage = message;
                responseModel.mState = state;
                responseModel.mIsUpdated = isUpdated;
                responseModel.mUrl = url;
                responseModel.mTransactionId = pendingRequest;
                callback.onResponse(responseModel);
            }
            mCallbackMap.clear();
            mPendingRequest.clear();
        }

    }

    private void makeDownloadRequest(AppVersionParser.AppVersionParserResponse response) {
        synchronized (sLock) {
            NewApkEntity apk = NewApkEntityImp.getInstance(mContext);
            apk.downloadNewApk(response.mUpdateLink, response.mVersionCode, response.mMd5Sum);
        }
    }

    public void onEventAsync(UserController.UserLogInUiEvent event) {
        synchronized (sLock) {

            if (!hasPendingRequest()) {
                if (mPendingRequest.size() > 0 && mPendingRequest.get(event.getMessageId()) != null) {

                    Integer pendingRequest = mPendingRequest.get(event.getMessageId()).mRequestId;
                    ServiceHelper.getInstance(mContext).fetchNewVersionOfApp(
                            pendingRequest, event.getToken()
                    );
                    setHasPendingRequest(true);
                }
            }
        }
    }

    public boolean hasPendingRequest() {
        synchronized (sLock) {
            return mHasPendingRequest;
        }
    }

    public void setHasPendingRequest(boolean hasPendingRequest) {
        synchronized (sLock) {
            this.mHasPendingRequest = hasPendingRequest;
        }
    }

}
