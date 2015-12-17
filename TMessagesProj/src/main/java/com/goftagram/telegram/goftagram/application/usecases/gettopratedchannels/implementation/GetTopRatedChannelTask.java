package com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.implementation;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetTask;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.contract.GetTopRatedChannelInteractor;
import com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.contract.GetTopRatedChannelRequest;
import com.goftagram.telegram.goftagram.application.usecases.gettopratedchannels.contract.GetTopRatedChannelTaskCallback;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;


public class GetTopRatedChannelTask extends AbsGetTask {

    private Context mContext;
    private GetTopRatedChannelRequest mRequestModel;
    private GetTopRatedChannelTaskCallback mGetTopRatedChannelTaskCallback;
    protected NameValueDataHolder mDataHolder;
    boolean mIsCategoryTableEmpty = false;

    public GetTopRatedChannelTask(
            Context context,
            GetTopRatedChannelRequest requestModel,
            GetTopRatedChannelTaskCallback callback
    ) {
        super(context);
        mRequestModel = requestModel;
        mContext = context;
        mGetTopRatedChannelTaskCallback = callback;
        mDataHolder = new NameValueDataHolder();
    }

    @Override
    public void run() {
        mIsCategoryTableEmpty = false;
        Cursor categoryCursor = null;
        Uri TopRatedChannelUri = GoftagramContract.TopRatedTelegramChannelEntry.buildTelegramChannelList();
        Cursor cursor = mContext.getContentResolver().query(TopRatedChannelUri, null, null, null, null);
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
            mDataHolder.put(GetTopRatedChannelInteractor.KEY_IS_CATEGORY_EMPTY,mIsCategoryTableEmpty);
            mGetTopRatedChannelTaskCallback.onMiss(mRequestModel,mDataHolder);
        }else{
            mDataHolder.put(GetTopRatedChannelInteractor.KEY_TOTAL_CHANNELS,cursor.getCount());
            mGetTopRatedChannelTaskCallback.onHit(mRequestModel,mDataHolder);
        }
        cursor.close();


    }
}
