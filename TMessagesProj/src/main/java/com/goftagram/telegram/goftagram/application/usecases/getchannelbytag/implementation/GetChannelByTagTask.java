package com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.implementation;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetTask;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.contract.GetChannelByTagInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.contract.GetChannelByTagRequest;
import com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.contract.GetChannelByTagTaskCallback;
import com.goftagram.telegram.goftagram.util.LogUtils;


public class GetChannelByTagTask extends AbsGetTask {

    private final String LOG_TAG = LogUtils.makeLogTag(GetChannelByTagTask.class.getSimpleName());

    private Context mContext;
    private GetChannelByTagRequest mRequestModel;
    private GetChannelByTagTaskCallback mGetChannelByTagTaskCallback;
    boolean mIsCategoryTableEmpty = false;
    protected NameValueDataHolder mDataHolder;
    public GetChannelByTagTask(
            Context context,
            GetChannelByTagRequest requestModel,
            GetChannelByTagTaskCallback callback
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
        Cursor tagCursor = null;


        Uri channelUri = GoftagramContract.TagEntry.buildTelegramChannelOfTagUri(mRequestModel.mTagId);

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
            mDataHolder.put(GetChannelByTagInteractor.KEY_IS_CATEGORY_EMPTY,mIsCategoryTableEmpty);
            mGetChannelByTagTaskCallback.onMiss(mRequestModel, mDataHolder);
        } else {

            mIsCategoryTableEmpty = false;

            tagCursor = mContext.getContentResolver().query(
                    GoftagramContract.TagEntry.buildTagUri(),
                    null,
                    GoftagramContract.TagEntry.COLUMN_SERVER_ID  + " = ? ",
                    new String[]{mRequestModel.mTagId},
                    null
            );
            if( tagCursor.moveToFirst()) {
                totalTaggedChannels = tagCursor.getInt(
                        tagCursor.getColumnIndex(GoftagramContract.TagEntry.COLUMN_TOTAL_TELEGRAM_CHANNEL)
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
                mDataHolder.put(GetChannelByTagInteractor.KEY_IS_CATEGORY_EMPTY,mIsCategoryTableEmpty);
                mGetChannelByTagTaskCallback.onMiss(mRequestModel, mDataHolder);
            } else {
                mRequestModel.mServerPageRequest = mRequestModel.mClientPageRequest;

                if(readChannels < totalTaggedChannels || totalTaggedChannels == 0){
                    mDataHolder.put(GetChannelByTagInteractor.KEY_IS_CATEGORY_EMPTY,mIsCategoryTableEmpty);
                    mGetChannelByTagTaskCallback.onMiss(mRequestModel, mDataHolder);
                }
                if (readChannels == totalTaggedChannels && totalTaggedChannels != 0) {
                    mDataHolder.put(GetChannelByTagInteractor.KEY_TOTAL_CHANNELS,totalTaggedChannels);
                    mGetChannelByTagTaskCallback.onHit(mRequestModel, mDataHolder);
                }
            }
        }

        if (allCategoryCursor != null)  allCategoryCursor.close();
        if (channelCursor != null)      channelCursor.close();
        if (tagCursor != null)          tagCursor.close();


    }
}
