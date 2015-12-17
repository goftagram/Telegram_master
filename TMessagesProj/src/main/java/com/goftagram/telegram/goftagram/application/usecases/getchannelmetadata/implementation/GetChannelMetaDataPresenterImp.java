package com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.implementation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.ChannelMetaDataHolder;
import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataInteractorCallback;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataRequest;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataResponse;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataViewModel;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WORK on 10/30/2015.
 */
public class GetChannelMetaDataPresenterImp implements
        GetChannelMetaDataPresenter,
        GetChannelMetaDataInteractorCallback {

    private static GetChannelMetaDataPresenterImp sGetChannelMetaDataPresenterImp;
    private Context mContext;
    private static final Object sLock = new Object();
    private Map<Integer,WeakReference<GetChannelMetaDataViewModel>> mGetChannelMetaDataViewModel;
    private GetChannelMetaDataInteractor mGetChannelMetaDataInteractor;

    private GetChannelMetaDataPresenterImp(Context context) {
        mContext = context.getApplicationContext();
        mGetChannelMetaDataViewModel = new HashMap<>();
        mGetChannelMetaDataInteractor = GetChannelMetaDataInteractorImp.getInstance(context);
    }

    public static synchronized GetChannelMetaDataPresenterImp getInstance(Context context) {
        if (sGetChannelMetaDataPresenterImp == null) {
            sGetChannelMetaDataPresenterImp = new GetChannelMetaDataPresenterImp(context);
        }
        return sGetChannelMetaDataPresenterImp;
    }




    @Override
    public int getAsync(GetChannelMetaDataViewModel viewModel, NameValueDataHolder dataHolder) {
        synchronized (sLock) {

            int requestId = UniqueIdGenerator.getInstance().getNewId();
            register(viewModel,requestId);
            GetChannelMetaDataRequest requestModel = new GetChannelMetaDataRequest();
            requestModel.mRequestId = requestId;
            requestModel.mClientPageRequest = dataHolder.getInt(KEY_PAGE);
            requestModel.mTelegramChannelId = dataHolder.getString(KEY_TELEGRAM_CHANNEL_ID);
            mGetChannelMetaDataInteractor.getAsync(requestModel, this);
            return requestId;
        }
    }


    @Override
    public void register(GetChannelMetaDataViewModel viewModel, int requestId) {
        synchronized (sLock) {
            if(mGetChannelMetaDataViewModel.get(requestId)==null){
                mGetChannelMetaDataViewModel.put(requestId,new WeakReference<GetChannelMetaDataViewModel>(viewModel));
            }else{
                mGetChannelMetaDataViewModel.remove(requestId);
                mGetChannelMetaDataViewModel.put(requestId, new WeakReference<GetChannelMetaDataViewModel>(viewModel));
            }

        }
    }

    @Override
    public void unregister(GetChannelMetaDataViewModel viewModel, int requestId) {
        synchronized (sLock) {
            mGetChannelMetaDataViewModel.remove(requestId);
        }
    }




    @Override
    public void onResponse(final GetChannelMetaDataResponse response) {
        synchronized (sLock) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mGetChannelMetaDataViewModel.get(response.mRequestId)!=null &&
                            mGetChannelMetaDataViewModel.get(response.mRequestId).get()!=null  ) {
                        ChannelMetaDataHolder dataHolder = new ChannelMetaDataHolder();
                        dataHolder.put(KEY_MESSAGE ,response.mMessage);
                        GetChannelMetaDataViewModel viewModel =
                                        mGetChannelMetaDataViewModel.get(response.mRequestId).get();

                        if (response.mState == GetChannelMetaDataResponse.SUCCESS) {


                            dataHolder.put(KEY_TELEGRAM_CHANNEL             ,response.mTelegramChannel);
                            dataHolder.put(KEY_RELATED_TELEGRAM_CHANNEL_LIST,response.mRelatedTelegramChannelList);
                            dataHolder.put(KEY_TAG_LIST                     ,response.mTagList);
                            dataHolder.put(KEY_LOCAL_COMMENT_LIST           ,response.mLocalCommentList);
                            dataHolder.put(KEY_DOWNLOADED_COMMENT_LIST      ,response.mDownloadedCommentList);
                            dataHolder.put(KEY_HAS_MORE_ITEMS               ,response.mHasMoreComment);

                            viewModel.onSuccess(response.mRequestId,dataHolder);
                        } else {
                            viewModel.onFail(response.mRequestId,dataHolder);
                        }
                        mGetChannelMetaDataViewModel.remove(response.mRequestId);


                    }
                }
            });

        }
    }


}
