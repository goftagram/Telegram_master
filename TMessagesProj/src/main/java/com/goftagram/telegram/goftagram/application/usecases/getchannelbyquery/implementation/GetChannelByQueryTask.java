package com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.implementation;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetTask;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract.GetChannelByQueryInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract.GetChannelByQueryRequest;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract.GetChannelByQueryTaskCallback;
import com.goftagram.telegram.goftagram.util.LogUtils;


public class GetChannelByQueryTask extends AbsGetTask {

    private final String LOG_TAG = LogUtils.makeLogTag(GetChannelByQueryTask.class.getSimpleName());

    private Context mContext;
    private GetChannelByQueryRequest mRequestModel;
    private GetChannelByQueryTaskCallback mGetChannelByTagTaskCallback;
    boolean mIsCategoryTableEmpty = false;
    protected NameValueDataHolder mDataHolder;
    public GetChannelByQueryTask(
            Context context,
            GetChannelByQueryRequest requestModel,
            GetChannelByQueryTaskCallback callback
    ) {
        super(context);
        mRequestModel = requestModel;
        mContext = context;
        mGetChannelByTagTaskCallback = callback;
        mDataHolder = new NameValueDataHolder();
    }

    @Override
    public void run() {


        Cursor allCategoryCursor = null;
        Cursor channelCursor = null;
        Cursor queryCursor = null;


        Uri channelUri = GoftagramContract.SearchedQueryEntry.buildTelegramChannelOfSearchedQueryUri(mRequestModel.mQuery);

        allCategoryCursor = mContext.getContentResolver().query(
                GoftagramContract.CategoryEntry.buildCategoryList(),
                null,
                null,
                null,
                null
        );

        int totalTaggedChannels = 0;


        if (!allCategoryCursor.moveToFirst()) {
            mIsCategoryTableEmpty = true;
            mRequestModel.mServerPageRequest = 1;
            mDataHolder.put(GetChannelByQueryInteractor.KEY_IS_CATEGORY_EMPTY,mIsCategoryTableEmpty);
            mGetChannelByTagTaskCallback.onMiss(mRequestModel, mDataHolder);
        } else {

            mIsCategoryTableEmpty = false;

            queryCursor = mContext.getContentResolver().query(
                    GoftagramContract.SearchedQueryEntry.buildSearchedQueryUri(),
                    null,
                    GoftagramContract.SearchedQueryEntry.COLUMN_QUERY  + " = ? ",
                    new String[]{mRequestModel.mQuery},
                    null
            );
            if( queryCursor.moveToFirst()) {
                totalTaggedChannels = queryCursor.getInt(
                        queryCursor.getColumnIndex(GoftagramContract.SearchedQueryEntry.COLUMN_TOTAL_TELEGRAM_CHANNEL)
                );
            }
            channelCursor = mContext.getContentResolver().query(
                    channelUri,
                    null,
                    null,
                    null,
                    null
            );


            int readChannels = channelCursor.getCount();
            LogUtils.LOGI(LOG_TAG,"mRequestModel.mClientPageRequest: " + mRequestModel.mClientPageRequest);
            LogUtils.LOGI(LOG_TAG,"totalTaggedChannels: " + totalTaggedChannels);
            if (readChannels <= 0) {
                mRequestModel.mServerPageRequest = 1;
                mDataHolder.put(GetChannelByQueryInteractor.KEY_IS_CATEGORY_EMPTY,mIsCategoryTableEmpty);
                mGetChannelByTagTaskCallback.onMiss(mRequestModel, mDataHolder);
            } else {
                mRequestModel.mServerPageRequest = mRequestModel.mClientPageRequest;

                if(readChannels < totalTaggedChannels || totalTaggedChannels == 0){
                    mDataHolder.put(GetChannelByQueryInteractor.KEY_IS_CATEGORY_EMPTY,mIsCategoryTableEmpty);
                    mGetChannelByTagTaskCallback.onMiss(mRequestModel, mDataHolder);
                }
                if (readChannels == totalTaggedChannels && totalTaggedChannels != 0) {
                    mDataHolder.put(GetChannelByQueryInteractor.KEY_TOTAL_CHANNELS,totalTaggedChannels);
                    mGetChannelByTagTaskCallback.onHit(mRequestModel, mDataHolder);
                }
            }
        }

        if (allCategoryCursor != null)  allCategoryCursor.close();
        if (channelCursor != null)      channelCursor.close();
        if (queryCursor != null)        queryCursor.close();


    }
}
