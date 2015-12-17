package com.goftagram.telegram.goftagram.application.usecases.postuserinterest.implementation;

import android.content.Context;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataViewModel;
import com.goftagram.telegram.goftagram.application.usecases.postuserinterest.contract.PostUserInterestInteractor;
import com.goftagram.telegram.goftagram.application.usecases.postuserinterest.contract.PostUserInterestInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.postuserinterest.contract.PostUserInterestPresenter;
import com.goftagram.telegram.goftagram.application.usecases.postuserinterest.contract.PostUserInterestRequest;
import com.goftagram.telegram.goftagram.application.usecases.postuserinterest.contract.PostUserInterestResponse;
import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WORK on 10/30/2015.
 */
public class PostUserInterestPresenterImp implements
        PostUserInterestPresenter,
        PostUserInterestInteractorCallback {

    private static PostUserInterestPresenterImp sPostUserInterestPresenterImp;
    private static final Object sLock = new Object();
    private Map<Integer,WeakReference<GetChannelMetaDataViewModel>> mPostUserInterestViewModel;
    private PostUserInterestInteractor mPostUserInterestInteractor;

    private PostUserInterestPresenterImp(Context context) {
        mPostUserInterestViewModel = new HashMap<>();
        mPostUserInterestInteractor = PostUserInterestInteractorImp.getInstance(context);
    }

    public static synchronized PostUserInterestPresenterImp getInstance(Context context) {
        if (sPostUserInterestPresenterImp == null) {
            sPostUserInterestPresenterImp = new PostUserInterestPresenterImp(context);
        }
        return sPostUserInterestPresenterImp;
    }


    @Override
    public void onResponse(final PostUserInterestResponse response) {
        synchronized (sLock) {
        }
    }



    @Override
    public int postAsync(GetChannelMetaDataViewModel viewModel, NameValueDataHolder dataHolder) {

        synchronized (sLock) {

            int requestId = UniqueIdGenerator.getInstance().getNewId();
            PostUserInterestRequest requestModel = new PostUserInterestRequest();
            requestModel.mRequestId = requestId;
            requestModel.mTelegramChannelId = dataHolder.getString(KEY_TELEGRAM_CHANNEL_ID);
            requestModel.mType = dataHolder.getString(KEY_TYPE);
            mPostUserInterestInteractor.postAsync(requestModel, this);

            return requestId;
        }
    }

    @Override
    public void register(GetChannelMetaDataViewModel viewModel, int requestId) {
        synchronized (sLock) {
            if(mPostUserInterestViewModel.get(requestId)==null){
                mPostUserInterestViewModel.put(requestId,new WeakReference<GetChannelMetaDataViewModel>(viewModel));
            }else{
                mPostUserInterestViewModel.remove(requestId);
                mPostUserInterestViewModel.put(requestId, new WeakReference<GetChannelMetaDataViewModel>(viewModel));
            }

        }
    }

    @Override
    public void unregister(GetChannelMetaDataViewModel viewModel, int requestId) {
        synchronized (sLock) {
            mPostUserInterestViewModel.remove(requestId);
        }
    }



}
