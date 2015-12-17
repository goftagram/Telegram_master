package com.goftagram.telegram.goftagram.application.usecases.getcategory.implementation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryRequest;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryResponse;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryViewModel;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WORK on 10/30/2015.
 */
public class GetCategoryPresenterImp implements GetCategoryPresenter,GetCategoryInteractorCallback {

    private static GetCategoryPresenterImp sGetCategoryPresenterImp;
    private Context mContext;
    private static final Object sLock = new Object();
    private Map<Integer,WeakReference<GetCategoryViewModel>> mGetCategoryViewModel;
    private GetCategoryInteractor mGetCategoryInteractor;

    private GetCategoryPresenterImp(Context context) {
        mContext = context.getApplicationContext();
        mGetCategoryViewModel = new HashMap<>();
        mGetCategoryInteractor = GetCategoryInteractorImp.getInstance(context);
    }

    public static synchronized GetCategoryPresenterImp getInstance(Context context) {
        if (sGetCategoryPresenterImp == null) {
            sGetCategoryPresenterImp = new GetCategoryPresenterImp(context);
        }
        return sGetCategoryPresenterImp;
    }


    @Override
    public void onResponse(final GetCategoryResponse response) {
        synchronized (sLock) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mGetCategoryViewModel.get(response.mRequestId)!=null &&
                            mGetCategoryViewModel.get(response.mRequestId).get()!=null) {
                        GetCategoryViewModel viewModel = mGetCategoryViewModel
                                .get(response.mRequestId).get();

                            if (response.mState == GetCategoryResponse.SUCCESS) {
                                viewModel.onSuccess(response.mMessage, response.mTotalServerItems);
                            } else {
                                viewModel.onFail(response.mMessage, response.mTotalServerItems);
                            }
                            mGetCategoryViewModel.remove(response.mRequestId);


                    }
                }
            });

        }
    }



    @Override
    public int getAsync(GetCategoryViewModel viewModel, NameValueDataHolder dataHolder) {

        synchronized (sLock) {
            viewModel.showLoading();
            int requestId = UniqueIdGenerator.getInstance().getNewId();
            register(viewModel,requestId);
            GetCategoryRequest requestModel = new GetCategoryRequest();
            requestModel.mRequestId = requestId;
            mGetCategoryInteractor.getAsync(requestModel, this);

            return requestId;
        }
    }

    @Override
    public void register(GetCategoryViewModel viewModel, int requestId) {
        synchronized (sLock) {
            if(mGetCategoryViewModel.get(requestId)==null){
                mGetCategoryViewModel.put(requestId,new WeakReference<GetCategoryViewModel>(viewModel));
            }else{
                mGetCategoryViewModel.remove(requestId);
                mGetCategoryViewModel.put(requestId, new WeakReference<GetCategoryViewModel>(viewModel));
            }

        }
    }

    @Override
    public void unregister(GetCategoryViewModel viewModel, int requestId) {
        synchronized (sLock) {
            mGetCategoryViewModel.remove(requestId);
        }
    }



}
