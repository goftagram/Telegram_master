package com.goftagram.telegram.goftagram.application.usecases.addcommentforchannel.implementation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.ChannelMetaDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataViewModel;
import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.addcommentforchannel.contract.AddCommentInteractor;
import com.goftagram.telegram.goftagram.application.usecases.addcommentforchannel.contract.AddCommentInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.addcommentforchannel.contract.AddCommentPresenter;
import com.goftagram.telegram.goftagram.application.usecases.addcommentforchannel.contract.AddCommentRequest;
import com.goftagram.telegram.goftagram.application.usecases.addcommentforchannel.contract.AddCommentResponse;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WORK on 10/30/2015.
 */
public class AddCommentPresenterImp implements AddCommentPresenter,AddCommentInteractorCallback {

    private static AddCommentPresenterImp sAddCommentPresenterImp;
    private static final Object sLock = new Object();
    private Map<Integer,WeakReference<GetChannelMetaDataViewModel>> mAddCommentViewModel;
    private AddCommentInteractor mAddCommentInteractor;

    private AddCommentPresenterImp(Context context) {
        mAddCommentViewModel = new HashMap<>();
        mAddCommentInteractor = AddCommentInteractorImp.getInstance(context);
    }

    public static synchronized AddCommentPresenterImp getInstance(Context context) {
        if (sAddCommentPresenterImp == null) {
            sAddCommentPresenterImp = new AddCommentPresenterImp(context);
        }
        return sAddCommentPresenterImp;
    }


    @Override
    public void onResponse(final AddCommentResponse response) {
        synchronized (sLock) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mAddCommentViewModel.get(response.mRequestId)!=null &&
                            mAddCommentViewModel.get(response.mRequestId).get()!=null) {
                        ChannelMetaDataHolder dataHolder = new ChannelMetaDataHolder();
                        dataHolder.put(GetChannelMetaDataPresenter.KEY_MESSAGE,response.mMessage);
                        GetChannelMetaDataViewModel viewModel = mAddCommentViewModel
                                .get(response.mRequestId).get();

                            if (response.mState == AddCommentResponse.SUCCESS) {
                                viewModel.onSuccess(response.mRequestId,dataHolder);
                            } else {
                                viewModel.onFail(response.mRequestId,dataHolder);
                            }
                            mAddCommentViewModel.remove(response.mRequestId);


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
            AddCommentRequest requestModel = new AddCommentRequest();
            requestModel.mRequestId = requestId;
            requestModel.mComment = dataHolder.getString(KEY_COMMENT);;
            requestModel.mTelegramChannelId = dataHolder.getString(KEY_TELEGRAM_CHANNEL_ID);
            mAddCommentInteractor.postAsync(requestModel, this);

            return requestId;
        }
    }

    @Override
    public void register(GetChannelMetaDataViewModel viewModel, int requestId) {
        synchronized (sLock) {
            if(mAddCommentViewModel.get(requestId)==null){
                mAddCommentViewModel.put(requestId,new WeakReference<GetChannelMetaDataViewModel>(viewModel));
            }else{
                mAddCommentViewModel.remove(requestId);
                mAddCommentViewModel.put(requestId, new WeakReference<GetChannelMetaDataViewModel>(viewModel));
            }

        }
    }

    @Override
    public void unregister(GetChannelMetaDataViewModel viewModel, int requestId) {
        synchronized (sLock) {
            mAddCommentViewModel.remove(requestId);
        }
    }



}
