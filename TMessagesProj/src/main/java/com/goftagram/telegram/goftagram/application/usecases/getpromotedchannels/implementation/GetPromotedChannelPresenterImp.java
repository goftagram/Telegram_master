package com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.implementation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsListViewModel;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.contract.GetPromotedChannelInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.contract.GetPromotedChannelInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.contract.GetPromotedChannelPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.contract.GetPromotedChannelRequest;
import com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.contract.GetPromotedChannelResponse;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WORK on 10/30/2015.
 */
public class GetPromotedChannelPresenterImp implements
        GetPromotedChannelPresenter,
        GetPromotedChannelInteractorCallback {

    private static GetPromotedChannelPresenterImp sGetPromotedChannelPresenterImp;
    private Context mContext;
    private static final Object sLock = new Object();
    private Map<Integer,WeakReference<AbsListViewModel>> mGetPromotedChannelViewModel;
    private GetPromotedChannelInteractor mGetPromotedChannelInteractor;

    private GetPromotedChannelPresenterImp(Context context) {
        mContext = context.getApplicationContext();
        mGetPromotedChannelViewModel = new HashMap<>();
        mGetPromotedChannelInteractor = GetPromotedChannelInteractorImp.getInstance(context);
    }

    public static synchronized GetPromotedChannelPresenterImp getInstance(Context context) {
        if (sGetPromotedChannelPresenterImp == null) {
            sGetPromotedChannelPresenterImp = new GetPromotedChannelPresenterImp(context);
        }
        return sGetPromotedChannelPresenterImp;
    }


    @Override
    public void onResponse(final GetPromotedChannelResponse response) {
        synchronized (sLock) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mGetPromotedChannelViewModel.get(response.mRequestId)!=null&&
                            mGetPromotedChannelViewModel.get(response.mRequestId).get()!=null) {
                        AbsListViewModel viewModel = mGetPromotedChannelViewModel
                                .get(response.mRequestId).get();

                            if (response.mState == GetPromotedChannelResponse.SUCCESS) {
                                viewModel.onSuccess(response.mMessage, response.mTotalServerItems);
                            } else {
                                viewModel.onFail(response.mMessage, response.mTotalServerItems);
                            }
                            mGetPromotedChannelViewModel.remove(response.mRequestId);


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
            GetPromotedChannelRequest requestModel = new GetPromotedChannelRequest();
            requestModel.mRequestId = requestId;
            mGetPromotedChannelInteractor.getAsync(requestModel, this);

            return requestId;
        }
    }


    @Override
    public void register(AbsListViewModel viewModel, int requestId) {
        synchronized (sLock) {
            if(mGetPromotedChannelViewModel.get(requestId)==null){
                mGetPromotedChannelViewModel.put(requestId,new WeakReference<AbsListViewModel>(viewModel));
            }else{
                mGetPromotedChannelViewModel.remove(requestId);
                mGetPromotedChannelViewModel.put(requestId, new WeakReference<AbsListViewModel>(viewModel));
            }

        }
    }

    @Override
    public void unregister(AbsListViewModel viewModel, int requestId) {
        synchronized (sLock) {
            mGetPromotedChannelViewModel.remove(requestId);
        }
    }


}
