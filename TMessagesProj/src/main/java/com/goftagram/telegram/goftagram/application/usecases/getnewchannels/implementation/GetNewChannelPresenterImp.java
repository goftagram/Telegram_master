package com.goftagram.telegram.goftagram.application.usecases.getnewchannels.implementation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsListViewModel;
import com.goftagram.telegram.goftagram.application.usecases.getnewchannels.contract.GetNewChannelInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getnewchannels.contract.GetNewChannelInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getnewchannels.contract.GetNewChannelPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getnewchannels.contract.GetNewChannelRequest;
import com.goftagram.telegram.goftagram.application.usecases.getnewchannels.contract.GetNewChannelResponse;
import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WORK on 10/30/2015.
 */
public class GetNewChannelPresenterImp implements
        GetNewChannelPresenter,
        GetNewChannelInteractorCallback {

    private static GetNewChannelPresenterImp sGetTopRatedChannelPresenterImp;
    private Context mContext;
    private static final Object sLock = new Object();
    private Map<Integer,WeakReference<AbsListViewModel>> mGetNewChannelViewModel;
    private GetNewChannelInteractor mNewChannelInteractor;

    private GetNewChannelPresenterImp(Context context) {
        mContext = context.getApplicationContext();
        mGetNewChannelViewModel = new HashMap<>();
        mNewChannelInteractor = GetNewChannelInteractorImp.getInstance(context);
    }

    public static synchronized GetNewChannelPresenterImp getInstance(Context context) {
        if (sGetTopRatedChannelPresenterImp == null) {
            sGetTopRatedChannelPresenterImp = new GetNewChannelPresenterImp(context);
        }
        return sGetTopRatedChannelPresenterImp;
    }


    @Override
    public void onResponse(final GetNewChannelResponse response) {
        synchronized (sLock) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mGetNewChannelViewModel.get(response.mRequestId)!=null&&
                            mGetNewChannelViewModel.get(response.mRequestId).get()!=null) {
                        AbsListViewModel viewModel = mGetNewChannelViewModel
                                .get(response.mRequestId).get();

                            if (response.mState == GetNewChannelResponse.SUCCESS) {
                                viewModel.onSuccess(response.mMessage, response.mTotalServerItems);
                            } else {
                                viewModel.onFail(response.mMessage, response.mTotalServerItems);
                            }
                            mGetNewChannelViewModel.remove(response.mRequestId);


                    }
                }
            });

        }
    }


    @Override
    public int getAsync(AbsListViewModel viewModel, NameValueDataHolder dataHolder) {
        synchronized (sLock) {

            int requestId = UniqueIdGenerator.getInstance().getNewId();
            register(viewModel,requestId);
            GetNewChannelRequest requestModel = new GetNewChannelRequest();
            requestModel.mRequestId = requestId;
            mNewChannelInteractor.getAsync(requestModel, this);

            return requestId;
        }
    }


    @Override
    public void register(AbsListViewModel viewModel, int requestId) {
        synchronized (sLock) {
            if(mGetNewChannelViewModel.get(requestId)==null){
                mGetNewChannelViewModel.put(requestId,new WeakReference<AbsListViewModel>(viewModel));
            }else{
                mGetNewChannelViewModel.remove(requestId);
                mGetNewChannelViewModel.put(requestId, new WeakReference<AbsListViewModel>(viewModel));
            }

        }
    }

    @Override
    public void unregister(AbsListViewModel viewModel, int requestId) {
        synchronized (sLock) {
            mGetNewChannelViewModel.remove(requestId);
        }
    }


}
