package com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.implementation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsListViewModel;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.contract.GetChannelByTagInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.contract.GetChannelByTagInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.contract.GetChannelByTagPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.contract.GetChannelByTagRequest;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.contract.GetChannelByTagResponse;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WORK on 10/30/2015.
 */
public class GetChannelByTagPresenterImp implements
        GetChannelByTagPresenter,
        GetChannelByTagInteractorCallback {




    private static GetChannelByTagPresenterImp sGetChannelByTagPresenterImp;
    private Context mContext;
    private static final Object sLock = new Object();
    private Map<Integer,WeakReference<AbsListViewModel>> mGetChannelByTagViewModel;
    private GetChannelByTagInteractor mGetChannelByTagInteractor;

    private GetChannelByTagPresenterImp(Context context) {
        mContext = context.getApplicationContext();
        mGetChannelByTagViewModel = new HashMap<>();
        mGetChannelByTagInteractor = GetChannelByTagInteractorImp.getInstance(context);
    }

    public static synchronized GetChannelByTagPresenterImp getInstance(Context context) {
        if (sGetChannelByTagPresenterImp == null) {
            sGetChannelByTagPresenterImp = new GetChannelByTagPresenterImp(context);
        }
        return sGetChannelByTagPresenterImp;
    }





    @Override
    public void register(AbsListViewModel viewModel, int requestId) {
        synchronized (sLock) {
            if(mGetChannelByTagViewModel.get(requestId)==null){
                mGetChannelByTagViewModel.put(requestId,new WeakReference<AbsListViewModel>(viewModel));
            }else{
                mGetChannelByTagViewModel.remove(requestId);
                mGetChannelByTagViewModel.put(requestId, new WeakReference<AbsListViewModel>(viewModel));
            }

        }
    }

    @Override
    public void unregister(AbsListViewModel viewModel, int requestId) {
        synchronized (sLock) {
            mGetChannelByTagViewModel.remove(requestId);
        }
    }

    @Override
    public int getAsync(AbsListViewModel viewModel, NameValueDataHolder dataHolder) {
        synchronized (sLock) {

            int requestId = UniqueIdGenerator.getInstance().getNewId();
            register(viewModel,requestId);
            GetChannelByTagRequest requestModel = new GetChannelByTagRequest();
            requestModel.mRequestId = requestId;
            requestModel.mClientPageRequest = dataHolder.getInt(KEY_PAGE);
            requestModel.mTagId = dataHolder.getString(KEY_TAG_ID);
            mGetChannelByTagInteractor.getAsync(requestModel, this);
            return requestId;
        }
    }



    @Override
    public void onResponse(final GetChannelByTagResponse response) {
        synchronized (sLock) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mGetChannelByTagViewModel.get(response.mRequestId)!=null &&
                            mGetChannelByTagViewModel.get(response.mRequestId).get()!=null  ) {
                        AbsListViewModel viewModel = mGetChannelByTagViewModel
                                .get(response.mRequestId).get();

                        if (response.mState == GetChannelByTagResponse.SUCCESS) {
                            viewModel.onSuccess(response.mMessage, response.mTotalServerItems);
                        } else {
                            viewModel.onFail(response.mMessage, response.mTotalServerItems);
                        }
                        mGetChannelByTagViewModel.remove(response.mRequestId);


                    }
                }
            });

        }
    }

}
