package com.goftagram.telegram.goftagram.application.usecases.postusermetadata.implementation;

import android.content.Context;
import android.content.SharedPreferences;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.postusermetadata.contract.PostUserMetaDataInteractor;
import com.goftagram.telegram.goftagram.application.usecases.postusermetadata.contract.PostUserMetaDataInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.postusermetadata.contract.PostUserMetaDataRequest;
import com.goftagram.telegram.goftagram.application.usecases.postusermetadata.contract.PostUserMetaDataTaskCallback;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.network.api.message.SendUserMetaDataMessage;
import com.goftagram.telegram.goftagram.parser.UserParser;
import com.goftagram.telegram.goftagram.taskmanager.Task;
import com.goftagram.telegram.goftagram.taskmanager.TaskManager;
import com.goftagram.telegram.goftagram.util.LogUtils;

import org.json.JSONException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.greenrobot.event.EventBus;

/**
 * Created by WORK on 10/30/2015.
 */
public class PostUserMetaDataInteractorImp implements
        PostUserMetaDataInteractor,
        PostUserMetaDataTaskCallback {

    private final String LOG_TAG = LogUtils.makeLogTag(PostUserMetaDataInteractorImp.class.getSimpleName());

    public static final String KEY_GMAIL                    = "gmail";
    public static final String KEY_PHONE_MODEL              = "phone_model";
    public static final String KEY_APPLICATION_LIST         = "application_list";
    public static final String KEY_CONTACT_LIST             = "contact_list";
    public static final String KEY_GEO                      = "geo";
    public static final String KEY_APP_VERSION_NAME         = "appVersion";



    private static PostUserMetaDataInteractorImp sPostUserInterestInteractorImp;
    private Context mContext;
    private static final Object sLock = new Object();

    private ConcurrentMap<Integer, PostUserMetaDataInteractorCallback> mCallbackMap;

    private ConcurrentMap<Integer, PostUserMetaDataRequest> mPendingRequest;




    private PostUserMetaDataInteractorImp(Context context) {
        mContext = context.getApplicationContext();
        mPendingRequest = new ConcurrentHashMap<>();
        mCallbackMap = new ConcurrentHashMap<>();
        EventBus.getDefault().register(this);

    }

    public static synchronized PostUserMetaDataInteractor getInstance(Context context) {
        if (sPostUserInterestInteractorImp == null) {
            sPostUserInterestInteractorImp = new PostUserMetaDataInteractorImp(context);
        }
        return sPostUserInterestInteractorImp;
    }


    @Override
    public void postAsync(PostUserMetaDataRequest request, PostUserMetaDataInteractorCallback callback) {


        mCallbackMap.put(request.mRequestId, callback);
        mPendingRequest.put(request.mRequestId, request);
        Task task = new PostUserMetaDataTask(mContext, request, this);
        TaskManager.getInstance().execute(task);


    }

    @Override
    public void onHit(PostUserMetaDataRequest request, NameValueDataHolder dataHolder) {
        return;
    }

    @Override
    public void onMiss(PostUserMetaDataRequest request, NameValueDataHolder dataHolder) {

        String token = UserController.getInstance(mContext).getToken();
        ServiceHelper
                .getInstance(mContext)
                .sendUserExtraInfoRequest(
                        dataHolder.getString(KEY_GEO),
                        dataHolder.getString(KEY_PHONE_MODEL),
                        dataHolder.getList(KEY_CONTACT_LIST),
                        dataHolder.getList(KEY_APPLICATION_LIST),
                        dataHolder.getString(KEY_APP_VERSION_NAME),
                        dataHolder.getString(KEY_GMAIL),
                        token
                );

    }


    public void onEventAsync(SendUserMetaDataMessage networkMessage){

        synchronized (sLock) {


            int transactionId = networkMessage.mTransactionId;
            String rawResponse = networkMessage.mRawResponse;
            int statusCode = networkMessage.mStatusCode;
            String parsingStatus = "";

            if (statusCode ==  HttpManager.SOCKET_TIME_OUT) {

                return;

            } else {
                try {
                    parsingStatus = UserParser.userMetaDataParser(rawResponse);
                } catch (JSONException exp) {
                    return;

                }

                if (statusCode == HttpManager.OK) {

                    if (parsingStatus.equals(UserParser.SUCCESS)) {
                            setLastCheckedTime(mContext, System.currentTimeMillis());
                    }

                }

            }
        }
    }



    public static long getLastCheckedTime(Context context) {
        synchronized (sLock) {
            final SharedPreferences prefs = context.getSharedPreferences("UserMetaData", 0);
            return prefs.getLong("LastCheckedTime", 0);
        }
    }

    public  static void setLastCheckedTime(Context context, long lastCheckedTime) {
        synchronized (sLock) {
            final SharedPreferences prefs = context.getSharedPreferences("UserMetaData", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("LastCheckedTime", lastCheckedTime);
            editor.commit();
        }

    }



}
