package com.goftagram.telegram.goftagram.application.usecases.reportchannel.implementation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.ChannelMetaDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataViewModel;
import com.goftagram.telegram.goftagram.application.usecases.reportchannel.contract.ReportChannelInteractor;
import com.goftagram.telegram.goftagram.application.usecases.reportchannel.contract.ReportChannelInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.reportchannel.contract.ReportChannelPresenter;
import com.goftagram.telegram.goftagram.application.usecases.reportchannel.contract.ReportChannelRequest;
import com.goftagram.telegram.goftagram.application.usecases.reportchannel.contract.ReportChannelResponse;
import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WORK on 10/30/2015.
 */
public class ReportChannelPresenterImp implements ReportChannelPresenter,ReportChannelInteractorCallback {

    private static ReportChannelPresenterImp sReportChannelPresenterImp;
    private static final Object sLock = new Object();
    private Map<Integer,WeakReference<GetChannelMetaDataViewModel>> mReportChannelViewModel;
    private ReportChannelInteractor mReportChannelInteractor;

    private ReportChannelPresenterImp(Context context) {
        mReportChannelViewModel = new HashMap<>();
        mReportChannelInteractor = ReportChannelInteractorImp.getInstance(context);
    }

    public static synchronized ReportChannelPresenterImp getInstance(Context context) {
        if (sReportChannelPresenterImp == null) {
            sReportChannelPresenterImp = new ReportChannelPresenterImp(context);
        }
        return sReportChannelPresenterImp;
    }


    @Override
    public void onResponse(final ReportChannelResponse response) {
        synchronized (sLock) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mReportChannelViewModel.get(response.mRequestId)!=null &&
                            mReportChannelViewModel.get(response.mRequestId).get()!=null) {
                        ChannelMetaDataHolder dataHolder = new ChannelMetaDataHolder();
                        dataHolder.put(GetChannelMetaDataPresenter.KEY_MESSAGE,response.mMessage);
                        GetChannelMetaDataViewModel viewModel = mReportChannelViewModel
                                .get(response.mRequestId).get();

                            if (response.mState == ReportChannelResponse.SUCCESS) {
                                viewModel.onSuccess(response.mRequestId,dataHolder);
                            } else {
                                viewModel.onFail(response.mRequestId,dataHolder);
                            }
                            mReportChannelViewModel.remove(response.mRequestId);
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
            ReportChannelRequest requestModel = new ReportChannelRequest();
            requestModel.mRequestId = requestId;
            requestModel.mReportText = dataHolder.getString(KEY_REPORT_TEXT);;
            requestModel.mTelegramChannelId = dataHolder.getString(KEY_TELEGRAM_CHANNEL_ID);
            mReportChannelInteractor.postAsync(requestModel, this);

            return requestId;
        }
    }

    @Override
    public void register(GetChannelMetaDataViewModel viewModel, int requestId) {
        synchronized (sLock) {
            if(mReportChannelViewModel.get(requestId)==null){
                mReportChannelViewModel.put(requestId,new WeakReference<GetChannelMetaDataViewModel>(viewModel));
            }else{
                mReportChannelViewModel.remove(requestId);
                mReportChannelViewModel.put(requestId, new WeakReference<GetChannelMetaDataViewModel>(viewModel));
            }

        }
    }

    @Override
    public void unregister(GetChannelMetaDataViewModel viewModel, int requestId) {
        synchronized (sLock) {
            mReportChannelViewModel.remove(requestId);
        }
    }



}
