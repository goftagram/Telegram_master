package com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.implementation;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetTask;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract.GetCategoryChannelInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract.GetCategoryChannelRequest;
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract.GetCategoryChannelTaskCallback;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.util.LogUtils;


public class GetCategoryChannelTask extends AbsGetTask {

    private final String LOG_TAG = LogUtils.makeLogTag(GetCategoryChannelTask.class.getSimpleName());

    private Context mContext;
    private GetCategoryChannelRequest mRequestModel;
    private GetCategoryChannelTaskCallback mGetCategoryChannelTaskCallback;
    boolean mIsCategoryTableEmpty = false;
    protected NameValueDataHolder mDataHolder;
    public GetCategoryChannelTask(
            Context context,
            GetCategoryChannelRequest requestModel,
            GetCategoryChannelTaskCallback callback
    ) {
        super(context);
        mRequestModel = requestModel;
        mContext = context;
        mGetCategoryChannelTaskCallback = callback;
        mDataHolder = new NameValueDataHolder();
    }

    @Override
    public void run() {


        Cursor allCategoryCursor = null;
        Cursor channelCursor = null;
        Cursor categoryCursor = null;

        Uri channelUri = GoftagramContract.TelegramChannelEntry.buildTelegramChannelCategoryUri();

        categoryCursor = mContext.getContentResolver().query(
                GoftagramContract.CategoryEntry.buildCategoryList(),
                null,
                GoftagramContract.CategoryEntry.COLUMN_SERVER_ID + " = ? ",
                new String[]{mRequestModel.mCategoryId},
                null
        );

        int totalCategoryChannels = 0;


        if (!categoryCursor.moveToFirst()) {
            mIsCategoryTableEmpty = true;
            mRequestModel.mServerPageRequest = 1;
            mDataHolder.put(GetCategoryChannelInteractor.KEY_IS_CATEGORY_EMPTY,mIsCategoryTableEmpty);
            mGetCategoryChannelTaskCallback.onMiss(mRequestModel,mDataHolder);
        } else {

            mIsCategoryTableEmpty = false;
            totalCategoryChannels = categoryCursor.getInt(
                    categoryCursor.getColumnIndex(
                            GoftagramContract.CategoryEntry.COLUMN_TOTAL_CHANNELS
                    )
            );
            channelCursor = mContext.getContentResolver().query(
                    channelUri,
                    null,
                    GoftagramContract.TelegramChannelEntry.COLUMN_CATEGORY_ID + " = ? ",
                    new String[]{mRequestModel.mCategoryId},
                    null
            );

            int readChannels = channelCursor.getCount();
            LogUtils.LOGI(LOG_TAG,"mRequestModel.mClientPageRequest: " + mRequestModel.mClientPageRequest);
            LogUtils.LOGI(LOG_TAG,"totalCategoryChannels: " + totalCategoryChannels);
            if (readChannels <= 0) {
                mRequestModel.mServerPageRequest = 1;
                mDataHolder.put(GetCategoryChannelInteractor.KEY_IS_CATEGORY_EMPTY,mIsCategoryTableEmpty);
                mGetCategoryChannelTaskCallback.onMiss(mRequestModel,mDataHolder);
            } else {
                mRequestModel.mServerPageRequest = mRequestModel.mClientPageRequest;

                if(readChannels < totalCategoryChannels || totalCategoryChannels == 0){
                    mDataHolder.put(GetCategoryChannelInteractor.KEY_IS_CATEGORY_EMPTY,mIsCategoryTableEmpty);
                    mGetCategoryChannelTaskCallback.onMiss(mRequestModel,mDataHolder);
                }
                if (readChannels == totalCategoryChannels && totalCategoryChannels != 0) {
                    mDataHolder.put(GetCategoryChannelInteractor.KEY_TOTAL_CHANNELS,totalCategoryChannels);
                    mGetCategoryChannelTaskCallback.onHit(mRequestModel, mDataHolder);
                }
            }
        }

        if (allCategoryCursor != null)  allCategoryCursor.close();
        if (channelCursor != null)      channelCursor.close();
        if (categoryCursor != null)     categoryCursor.close();

    }
}
