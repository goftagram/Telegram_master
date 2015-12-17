package com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.implementation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsListViewModel;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract.GetChannelByQueryInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract.GetChannelByQueryInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract.GetChannelByQueryPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract.GetChannelByQueryRequest;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract.GetChannelByQueryResponse;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WORK on 10/30/2015.
 */
public class GetChannelByQueryPresenterImp implements
        GetChannelByQueryPresenter,
        GetChannelByQueryInteractorCallback {




    private static GetChannelByQueryPresenterImp sGetChannelByQueryPresenterImp;
    private Context mContext;
    private static final Object sLock = new Object();
    private Map<Integer,WeakReference<AbsListViewModel>> mGetChannelByQueryViewModel;
    private GetChannelByQueryInteractor mGetChannelByQueryInteractor;

    private GetChannelByQueryPresenterImp(Context context) {
        mContext = context.getApplicationContext();
        mGetChannelByQueryViewModel = new HashMap<>();
        mGetChannelByQueryInteractor = GetChannelByQueryInteractorImp.getInstance(context);
    }

    public static synchronized GetChannelByQueryPresenterImp getInstance(Context context) {
        if (sGetChannelByQueryPresenterImp == null) {
            sGetChannelByQueryPresenterImp = new GetChannelByQueryPresenterImp(context);
        }
        return sGetChannelByQueryPresenterImp;
    }





    @Override
    public void register(AbsListViewModel viewModel, int requestId) {
        synchronized (sLock) {
            if(mGetChannelByQueryViewModel.get(requestId)==null){
                mGetChannelByQueryViewModel.put(requestId,new WeakReference<AbsListViewModel>(viewModel));
            }else{
                mGetChannelByQueryViewModel.remove(requestId);
                mGetChannelByQueryViewModel.put(requestId, new WeakReference<AbsListViewModel>(viewModel));
            }

        }
    }

    @Override
    public void unregister(AbsListViewModel viewModel, int requestId) {
        synchronized (sLock) {
            mGetChannelByQueryViewModel.remove(requestId);
        }
    }

    @Override
    public int getAsync(AbsListViewModel viewModel, NameValueDataHolder dataHolder) {
        synchronized (sLock) {

            int requestId = UniqueIdGenerator.getInstance().getNewId();
            register(viewModel,requestId);
            GetChannelByQueryRequest requestModel = new GetChannelByQueryRequest();
            requestModel.mRequestId = requestId;
            requestModel.mClientPageRequest = dataHolder.getInt(KEY_PAGE);
            requestModel.mQuery= dataHolder.getString(KEY_QUERY);
            mGetChannelByQueryInteractor.getAsync(requestModel, this);
            return requestId;
        }
    }



    @Override
    public void onResponse(final GetChannelByQueryResponse response) {
        synchronized (sLock) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mGetChannelByQueryViewModel.get(response.mRequestId)!=null &&
                            mGetChannelByQueryViewModel.get(response.mRequestId).get()!=null  ) {
                        AbsListViewModel viewModel = mGetChannelByQueryViewModel
                                .get(response.mRequestId).get();

                        if (response.mState == GetChannelByQueryResponse.SUCCESS) {
                            viewModel.onSuccess(response.mMessage, response.mTotalServerItems);
                        } else {
                            viewModel.onFail(response.mMessage, response.mTotalServerItems);
                        }
                        mGetChannelByQueryViewModel.remove(response.mRequestId);


                    }
                }
            });

        }
    }

}
