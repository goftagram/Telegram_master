package com.goftagram.telegram.goftagram.application.usecases.ratechannel.implementation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.ChannelMetaDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataViewModel;
import com.goftagram.telegram.goftagram.application.usecases.ratechannel.contract.RateChannelInteractor;
import com.goftagram.telegram.goftagram.application.usecases.ratechannel.contract.RateChannelInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.ratechannel.contract.RateChannelPresenter;
import com.goftagram.telegram.goftagram.application.usecases.ratechannel.contract.RateChannelRequest;
import com.goftagram.telegram.goftagram.application.usecases.ratechannel.contract.RateChannelResponse;
import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WORK on 10/30/2015.
 */
public class RateChannelPresenterImp implements RateChannelPresenter,RateChannelInteractorCallback {

    private static RateChannelPresenterImp sRateChannelPresenterImp;
    private static final Object sLock = new Object();
    private Map<Integer,WeakReference<GetChannelMetaDataViewModel>> mRateChannelViewModel;
    private RateChannelInteractor mRateChannelInteractor;

    private RateChannelPresenterImp(Context context) {
        mRateChannelViewModel = new HashMap<>();
        mRateChannelInteractor = RateChannelInteractorImp.getInstance(context);
    }

    public static synchronized RateChannelPresenterImp getInstance(Context context) {
        if (sRateChannelPresenterImp == null) {
            sRateChannelPresenterImp = new RateChannelPresenterImp(context);
        }
        return sRateChannelPresenterImp;
    }


    @Override
    public void onResponse(final RateChannelResponse response) {
        synchronized (sLock) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mRateChannelViewModel.get(response.mRequestId)!=null &&
                            mRateChannelViewModel.get(response.mRequestId).get()!=null) {
                        ChannelMetaDataHolder dataHolder = new ChannelMetaDataHolder();
                        dataHolder.put(GetChannelMetaDataPresenter.KEY_MESSAGE,response.mMessage);
                        dataHolder.put(GetChannelMetaDataPresenter.KEY_RATE,response.mRate);
                        GetChannelMetaDataViewModel viewModel = mRateChannelViewModel
                                .get(response.mRequestId).get();

                            if (response.mState == RateChannelResponse.SUCCESS) {
                                viewModel.onSuccess(response.mRequestId,dataHolder);
                            } else {
                                viewModel.onFail(response.mRequestId,dataHolder);
                            }
                            mRateChannelViewModel.remove(response.mRequestId);


                    }
                }
            });

        }
    }



    @Override
    public int postAsync(GetChannelMetaDataViewModel viewModel, NameValueDataHolder dataHolder) {

        synchronized (sLock) {

            int requestId = UniqueIdGenerator.getInstance().getNewId();
            register(viewModel,requestId);
            RateChannelRequest requestModel = new RateChannelRequest();
            requestModel.mRequestId = requestId;
            requestModel.mRate = dataHolder.getInt(KEY_RATE);;
            requestModel.mTelegramChannelId = dataHolder.getString(KEY_TELEGRAM_CHANNEL_ID);
            mRateChannelInteractor.postAsync(requestModel, this);

            return requestId;
        }
    }

    @Override
    public void register(GetChannelMetaDataViewModel viewModel, int requestId) {
        synchronized (sLock) {
            if(mRateChannelViewModel.get(requestId)==null){
                mRateChannelViewModel.put(requestId,new WeakReference<GetChannelMetaDataViewModel>(viewModel));
            }else{
                mRateChannelViewModel.remove(requestId);
                mRateChannelViewModel.put(requestId, new WeakReference<GetChannelMetaDataViewModel>(viewModel));
            }

        }
    }

    @Override
    public void unregister(GetChannelMetaDataViewModel viewModel, int requestId) {
        synchronized (sLock) {
            mRateChannelViewModel.remove(requestId);
        }
    }



}
