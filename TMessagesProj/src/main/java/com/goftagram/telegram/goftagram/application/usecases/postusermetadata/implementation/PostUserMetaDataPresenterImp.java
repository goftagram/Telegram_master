package com.goftagram.telegram.goftagram.application.usecases.postusermetadata.implementation;

import android.content.Context;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.UiLessViewModel;
import com.goftagram.telegram.goftagram.application.usecases.postusermetadata.contract.PostUserMetaDataInteractor;
import com.goftagram.telegram.goftagram.application.usecases.postusermetadata.contract.PostUserMetaDataInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.postusermetadata.contract.PostUserMetaDataPresenter;
import com.goftagram.telegram.goftagram.application.usecases.postusermetadata.contract.PostUserMetaDataRequest;
import com.goftagram.telegram.goftagram.application.usecases.postusermetadata.contract.PostUserMetaDataResponse;
import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WORK on 10/30/2015.
 */
public class PostUserMetaDataPresenterImp implements
        PostUserMetaDataPresenter,
        PostUserMetaDataInteractorCallback {

    private static PostUserMetaDataPresenterImp sPostUserInterestPresenterImp;
    private static final Object sLock = new Object();
    private Map<Integer,WeakReference<UiLessViewModel>> mPostUserInterestViewModel;
    private PostUserMetaDataInteractor mPostUserInterestInteractor;

    private PostUserMetaDataPresenterImp(Context context) {
        mPostUserInterestViewModel = new HashMap<>();
        mPostUserInterestInteractor = PostUserMetaDataInteractorImp.getInstance(context);
    }

    public static synchronized PostUserMetaDataPresenterImp getInstance(Context context) {
        if (sPostUserInterestPresenterImp == null) {
            sPostUserInterestPresenterImp = new PostUserMetaDataPresenterImp(context);
        }
        return sPostUserInterestPresenterImp;
    }


    @Override
    public void onResponse(final PostUserMetaDataResponse response) {
        synchronized (sLock) {
            mPostUserInterestViewModel.remove(response.mRequestId);
        }
    }



    @Override
    public int postAsync(UiLessViewModel viewModel, NameValueDataHolder dataHolder) {

        synchronized (sLock) {

            int requestId = UniqueIdGenerator.getInstance().getNewId();
            PostUserMetaDataRequest requestModel = new PostUserMetaDataRequest();
            requestModel.mRequestId = requestId;
            mPostUserInterestInteractor.postAsync(requestModel, this);

            return requestId;
        }
    }

    @Override
    public void register(UiLessViewModel viewModel, int requestId) {
        synchronized (sLock) {
            if(mPostUserInterestViewModel.get(requestId)==null){
                mPostUserInterestViewModel.put(requestId,new WeakReference<UiLessViewModel>(viewModel));
            }else{
                mPostUserInterestViewModel.remove(requestId);
                mPostUserInterestViewModel.put(requestId, new WeakReference<UiLessViewModel>(viewModel));
            }

        }
    }

    @Override
    public void unregister(UiLessViewModel viewModel, int requestId) {
        synchronized (sLock) {
            mPostUserInterestViewModel.remove(requestId);
        }
    }



}
