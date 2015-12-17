package com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.implementation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsListViewModel;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract.GetCategoryChannelInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract.GetCategoryChannelInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract.GetCategoryChannelPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract.GetCategoryChannelRequest;
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract.GetCategoryChannelResponse;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WORK on 10/30/2015.
 */
public class GetCategoryChannelPresenterImp implements
        GetCategoryChannelPresenter,
        GetCategoryChannelInteractorCallback {




    private static GetCategoryChannelPresenterImp sGetCategoryChannelPresenterImp;
    private Context mContext;
    private static final Object sLock = new Object();
    private Map<Integer,WeakReference<AbsListViewModel>> mGetCategoryChannelViewModel;
    private GetCategoryChannelInteractor mGetCategoryChannelInteractor;

    private GetCategoryChannelPresenterImp(Context context) {
        mContext = context.getApplicationContext();
        mGetCategoryChannelViewModel = new HashMap<>();
        mGetCategoryChannelInteractor = GetCategoryChannelInteractorImp.getInstance(context);
    }

    public static synchronized GetCategoryChannelPresenterImp getInstance(Context context) {
        if (sGetCategoryChannelPresenterImp == null) {
            sGetCategoryChannelPresenterImp = new GetCategoryChannelPresenterImp(context);
        }
        return sGetCategoryChannelPresenterImp;
    }





    @Override
    public void register(AbsListViewModel viewModel, int requestId) {
        synchronized (sLock) {
            if(mGetCategoryChannelViewModel.get(requestId)==null){
                mGetCategoryChannelViewModel.put(requestId,new WeakReference<AbsListViewModel>(viewModel));
            }else{
                mGetCategoryChannelViewModel.remove(requestId);
                mGetCategoryChannelViewModel.put(requestId, new WeakReference<AbsListViewModel>(viewModel));
            }

        }
    }

    @Override
    public void unregister(AbsListViewModel viewModel, int requestId) {
        synchronized (sLock) {
            mGetCategoryChannelViewModel.remove(requestId);
        }
    }

    @Override
    public int getAsync(AbsListViewModel viewModel, NameValueDataHolder dataHolder) {
        synchronized (sLock) {

            int requestId = UniqueIdGenerator.getInstance().getNewId();
            register(viewModel,requestId);
            GetCategoryChannelRequest requestModel = new GetCategoryChannelRequest();
            requestModel.mRequestId = requestId;
            requestModel.mClientPageRequest = dataHolder.getInt(KEY_PAGE);
            requestModel.mCategoryId = dataHolder.getString(KEY_CATEGORY_ID);
            mGetCategoryChannelInteractor.getAsync(requestModel, this);
            return requestId;
        }
    }



    @Override
    public void onResponse(final GetCategoryChannelResponse response) {
        synchronized (sLock) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mGetCategoryChannelViewModel.get(response.mRequestId)!=null &&
                            mGetCategoryChannelViewModel.get(response.mRequestId).get()!=null  ) {
                        AbsListViewModel viewModel = mGetCategoryChannelViewModel
                                .get(response.mRequestId).get();

                        if (response.mState == GetCategoryChannelResponse.SUCCESS) {
                            viewModel.onSuccess(response.mMessage, response.mTotalServerItems);
                        } else {
                            viewModel.onFail(response.mMessage, response.mTotalServerItems);
                        }
                        mGetCategoryChannelViewModel.remove(response.mRequestId);


                    }
                }
            });

        }
    }

}
