package com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.implementation;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetTask;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.contract.GetPromotedChannelInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.contract.GetPromotedChannelRequest;
import com.goftagram.telegram.goftagram.application.usecases.getpromotedchannels.contract.GetPromotedChannelTaskCallback;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;


public class GetPromotedChannelTask extends AbsGetTask {

    private Context mContext;
    private GetPromotedChannelRequest mRequestModel;
    private GetPromotedChannelTaskCallback mGetPromotedChannelTaskCallback;
    protected NameValueDataHolder mDataHolder;
    boolean mIsCategoryTableEmpty = false;

    public GetPromotedChannelTask(
            Context context,
            GetPromotedChannelRequest requestModel,
            GetPromotedChannelTaskCallback callback
    ) {
        super(context);
        mRequestModel = requestModel;
        mContext = context;
        mGetPromotedChannelTaskCallback = callback;
        mDataHolder = new NameValueDataHolder();
    }

    @Override
    public void run() {
        mIsCategoryTableEmpty = false;
        Cursor categoryCursor = null;
        Uri promotedChannelUri = GoftagramContract.PromotedTelegramChannelEntry.buildTelegramChannelList();
        Cursor cursor = mContext.getContentResolver().query(promotedChannelUri, null, null, null, null);
        if (cursor.getCount() <= 0) {

            categoryCursor = mContext.getContentResolver().query(
                    GoftagramContract.CategoryEntry.buildCategoryList(),
                    null,
                    null,
                    null,
                    null
            );
            if (!categoryCursor.moveToFirst()) {
                mIsCategoryTableEmpty = true;
            }
            mDataHolder.put(GetPromotedChannelInteractor.KEY_IS_CATEGORY_EMPTY,mIsCategoryTableEmpty);
            mGetPromotedChannelTaskCallback.onMiss(mRequestModel,mDataHolder);
        }else{
            mDataHolder.put(GetPromotedChannelInteractor.KEY_TOTAL_CHANNELS,cursor.getCount());
            mGetPromotedChannelTaskCallback.onHit(mRequestModel,mDataHolder);
        }
        cursor.close();


    }
}
