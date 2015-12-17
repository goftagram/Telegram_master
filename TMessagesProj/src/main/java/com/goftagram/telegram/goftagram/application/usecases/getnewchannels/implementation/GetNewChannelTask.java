package com.goftagram.telegram.goftagram.application.usecases.getnewchannels.implementation;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetTask;
import com.goftagram.telegram.goftagram.application.usecases.getnewchannels.contract.GetNewChannelInteractor;
import com.goftagram.telegram.goftagram.application.usecases.getnewchannels.contract.GetNewChannelRequest;
import com.goftagram.telegram.goftagram.application.usecases.getnewchannels.contract.GetNewChannelTaskCallback;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;


public class GetNewChannelTask extends AbsGetTask {

    private Context mContext;
    private GetNewChannelRequest mRequestModel;
    private GetNewChannelTaskCallback mGetNewChannelTaskCallback;
    protected NameValueDataHolder mDataHolder;
    boolean mIsCategoryTableEmpty = false;

    public GetNewChannelTask(
            Context context,
            GetNewChannelRequest requestModel,
            GetNewChannelTaskCallback callback
    ) {
        super(context);
        mRequestModel = requestModel;
        mContext = context;
        mGetNewChannelTaskCallback = callback;
        mDataHolder = new NameValueDataHolder();
    }

    @Override
    public void run() {
        mIsCategoryTableEmpty = false;
        Cursor categoryCursor = null;
        Uri newChannelUri = GoftagramContract.NewTelegramChannelEntry.buildTelegramChannelList();
        Cursor cursor = mContext.getContentResolver().query(newChannelUri, null, null, null, null);
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
            mDataHolder.put(GetNewChannelInteractor.KEY_IS_CATEGORY_EMPTY,mIsCategoryTableEmpty);
            mGetNewChannelTaskCallback.onMiss(mRequestModel, mDataHolder);
        }else{
            mDataHolder.put(GetNewChannelInteractor.KEY_TOTAL_CHANNELS,cursor.getCount());
            mGetNewChannelTaskCallback.onHit(mRequestModel, mDataHolder);
        }
        cursor.close();


    }
}
