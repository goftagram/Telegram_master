package com.goftagram.telegram.goftagram.application.usecases.postuserinterest.implementation;

import android.content.Context;

import com.goftagram.telegram.goftagram.application.model.NullEvent;
import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.application.usecases.postuserinterest.contract.PostUserInterestInteractor;
import com.goftagram.telegram.goftagram.application.usecases.postuserinterest.contract.PostUserInterestInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.postuserinterest.contract.PostUserInterestRequest;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.goftagram.util.NetworkUtils;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.greenrobot.event.EventBus;

/**
 * Created by WORK on 10/30/2015.
 */
public class PostUserInterestInteractorImp implements
        PostUserInterestInteractor{

    private final String LOG_TAG = LogUtils.makeLogTag(PostUserInterestInteractorImp.class.getSimpleName());


    private static PostUserInterestInteractorImp sPostUserInterestInteractorImp;
    private Context mContext;
    private static final Object sLock = new Object();

    private ConcurrentMap<Integer, PostUserInterestRequest> mPendingRequest;


    private boolean mHasPendingRequest;

    private PostUserInterestInteractorImp(Context context) {
        mContext = context.getApplicationContext();
        mPendingRequest = new ConcurrentHashMap<>();
        EventBus.getDefault().register(this);
        mHasPendingRequest = false;
    }

    public static synchronized PostUserInterestInteractor getInstance(Context context) {
        if (sPostUserInterestInteractorImp == null) {
            sPostUserInterestInteractorImp = new PostUserInterestInteractorImp(context);
        }
        return sPostUserInterestInteractorImp;
    }


    @Override
    public void postAsync(PostUserInterestRequest request, PostUserInterestInteractorCallback callback) {


        String token = UserController.getInstance(mContext).getToken();
        if(NetworkUtils.isOnline(mContext)) {
            ServiceHelper.getInstance(mContext).sendUserInterestedChannels(
                    request.mRequestId, token, request.mTelegramChannelId,request.mType
            );
        }

    }

    public void onEventMainThread(NullEvent event){

    }


}
