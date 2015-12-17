package com.goftagram.telegram.goftagram.application.usecases.getnewversion.implementation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetPresenter;
import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.GetNewVersionInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.GetNewVersionInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.GetNewVersionRequest;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.GetNewVersionResponse;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.GetNewVersionViewModel;


import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WORK on 11/2/2015.
 */
public class GetNewVersionPresenterImp implements AbsGetPresenter<GetNewVersionViewModel>,GetNewVersionInteractorCallback{

    private static GetNewVersionPresenterImp sGetNewVersionPresenterImp;
    private Context mContext;
    private static final Object sLock = new Object();
    private Map<Integer,WeakReference<GetNewVersionViewModel>> mGetNewVersionViewModel;
    private GetNewVersionInteractor mGetNewVersionInteractor;

    private GetNewVersionPresenterImp(Context context) {
        mContext = context.getApplicationContext();
        mGetNewVersionViewModel = new HashMap<>();
        mGetNewVersionInteractor = GetNewVersionInteractorImp.getInstance(context);
    }

    public static synchronized GetNewVersionPresenterImp getInstance(Context context) {
        if (sGetNewVersionPresenterImp == null) {
            sGetNewVersionPresenterImp = new GetNewVersionPresenterImp(context);
        }
        return sGetNewVersionPresenterImp;
    }


    @Override
    public void onResponse(final GetNewVersionResponse response) {
        synchronized (sLock) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mGetNewVersionViewModel.get(response.mTransactionId) != null &&
                            mGetNewVersionViewModel.get(response.mTransactionId).get() != null) {
                        GetNewVersionViewModel viewModel = mGetNewVersionViewModel
                                .get(response.mTransactionId).get();

                        if (response.mState == GetNewVersionResponse.SUCCESS) {
                            viewModel.onSuccess(response.mMessage, response.mUrl, response.mIsUpdated);
                        }
                        mGetNewVersionViewModel.remove(response.mTransactionId);


                    }
                }
            });

        }
    }


    @Override
    public int getAsync(GetNewVersionViewModel viewModel, NameValueDataHolder dataHolder) {
        synchronized (sLock) {

            int requestId = UniqueIdGenerator.getInstance().getNewId();
            register(viewModel,requestId);
            GetNewVersionRequest requestModel = new GetNewVersionRequest();
            requestModel.mRequestId = requestId;
            mGetNewVersionInteractor.GetNewVersionAsync(requestModel, this);

            return requestId;
        }
    }


    @Override
    public void register(GetNewVersionViewModel viewModel, int requestId) {
        synchronized (sLock) {
            if(mGetNewVersionViewModel.get(requestId)==null){
                mGetNewVersionViewModel.put(requestId,new WeakReference<GetNewVersionViewModel>(viewModel));
            }else{
                mGetNewVersionViewModel.remove(requestId);
                mGetNewVersionViewModel.put(requestId, new WeakReference<GetNewVersionViewModel>(viewModel));
            }

        }
    }

    @Override
    public void unregister(GetNewVersionViewModel viewModel, int requestId) {
        synchronized (sLock) {
            mGetNewVersionViewModel.remove(requestId);
        }
    }




}
