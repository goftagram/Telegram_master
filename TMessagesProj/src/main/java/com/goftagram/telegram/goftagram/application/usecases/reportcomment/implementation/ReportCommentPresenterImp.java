package com.goftagram.telegram.goftagram.application.usecases.reportcomment.implementation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.ChannelMetaDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataViewModel;
import com.goftagram.telegram.goftagram.application.usecases.reportcomment.contract.ReportCommentInteractor;
import com.goftagram.telegram.goftagram.application.usecases.reportcomment.contract.ReportCommentInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.reportcomment.contract.ReportCommentPresenter;
import com.goftagram.telegram.goftagram.application.usecases.reportcomment.contract.ReportCommentRequest;
import com.goftagram.telegram.goftagram.application.usecases.reportcomment.contract.ReportCommentResponse;
import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WORK on 10/30/2015.
 */
public class ReportCommentPresenterImp implements ReportCommentPresenter,ReportCommentInteractorCallback {

    private static ReportCommentPresenterImp sReportChannelPresenterImp;
    private static final Object sLock = new Object();
    private Map<Integer,WeakReference<GetChannelMetaDataViewModel>> mReportChannelViewModel;
    private ReportCommentInteractor mReportChannelInteractor;

    private ReportCommentPresenterImp(Context context) {
        mReportChannelViewModel = new HashMap<>();
        mReportChannelInteractor = ReportCommentInteractorImp.getInstance(context);
    }

    public static synchronized ReportCommentPresenterImp getInstance(Context context) {
        if (sReportChannelPresenterImp == null) {
            sReportChannelPresenterImp = new ReportCommentPresenterImp(context);
        }
        return sReportChannelPresenterImp;
    }


    @Override
    public void onResponse(final ReportCommentResponse response) {
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

                            if (response.mState == ReportCommentResponse.SUCCESS) {
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
            ReportCommentRequest requestModel = new ReportCommentRequest();
            requestModel.mRequestId = requestId;
            requestModel.mReportText = dataHolder.getString(KEY_REPORT_TEXT);;
            requestModel.mCommentServerId = dataHolder.getString(KEY_COMMENT_ID);
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
