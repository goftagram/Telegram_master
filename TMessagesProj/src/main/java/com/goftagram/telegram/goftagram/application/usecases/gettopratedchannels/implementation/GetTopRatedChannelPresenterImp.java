package com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.implementation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsListViewModel;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.contract.GetTopRatedChannelInteractor;
import com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.contract.GetTopRatedChannelInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.contract.GetTopRatedChannelPresenter;
import com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.contract.GetTopRatedChannelRequest;
import com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.contract.GetTopRatedChannelResponse;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WORK on 10/30/2015.
 */
public class GetTopRatedChannelPresenterImp implements
        GetTopRatedChannelPresenter,
        GetTopRatedChannelInteractorCallback {

    private static GetTopRatedChannelPresenterImp sGetTopRatedChannelPresenterImp;
    private Context mContext;
    private static final Object sLock = new Object();
    private Map<Integer,WeakReference<AbsListViewModel>> mGetTopRatedChannelViewModel;
    private GetTopRatedChannelInteractor mGetTopRatedChannelInteractor;

    private GetTopRatedChannelPresenterImp(Context context) {
        mContext = context.getApplicationContext();
        mGetTopRatedChannelViewModel = new HashMap<>();
        mGetTopRatedChannelInteractor = GetTopRatedChannelInteractorImp.getInstance(context);
    }

    public static synchronized GetTopRatedChannelPresenterImp getInstance(Context context) {
        if (sGetTopRatedChannelPresenterImp == null) {
            sGetTopRatedChannelPresenterImp = new GetTopRatedChannelPresenterImp(context);
        }
        return sGetTopRatedChannelPresenterImp;
    }


    @Override
    public void onResponse(final GetTopRatedChannelResponse response) {
        synchronized (sLock) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mGetTopRatedChannelViewModel.get(response.mRequestId)!=null&&
                            mGetTopRatedChannelViewModel.get(response.mRequestId).get()!=null) {
                        AbsListViewModel viewModel = mGetTopRatedChannelViewModel
                                .get(response.mRequestId).get();

                            if (response.mState == GetTopRatedChannelResponse.SUCCESS) {
                                viewModel.onSuccess(response.mMessage, response.mTotalServerItems);
                            } else {
                                viewModel.onFail(response.mMessage, response.mTotalServerItems);
                            }
                            mGetTopRatedChannelViewModel.remove(response.mRequestId);


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
            GetTopRatedChannelRequest requestModel = new GetTopRatedChannelRequest();
            requestModel.mRequestId = requestId;
            mGetTopRatedChannelInteractor.getAsync(requestModel, this);

            return requestId;
        }
    }


    @Override
    public void register(AbsListViewModel viewModel, int requestId) {
        synchronized (sLock) {
            if(mGetTopRatedChannelViewModel.get(requestId)==null){
                mGetTopRatedChannelViewModel.put(requestId,new WeakReference<AbsListViewModel>(viewModel));
            }else{
                mGetTopRatedChannelViewModel.remove(requestId);
                mGetTopRatedChannelViewModel.put(requestId, new WeakReference<AbsListViewModel>(viewModel));
            }

        }
    }

    @Override
    public void unregister(AbsListViewModel viewModel, int requestId) {
        synchronized (sLock) {
            mGetTopRatedChannelViewModel.remove(requestId);
        }
    }


}
