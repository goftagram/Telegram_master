package com.goftagram.telegram.goftagram.application.usecases.getcategory.implementation;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetTask;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryRequest;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.contract.GetCategoryTaskCallback;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;

/**
 * Created by WORK on 10/30/2015.
 */
public class GetCategoryTask extends AbsGetTask{

    private Context mContext;
    private GetCategoryRequest mRequestModel;
    private GetCategoryTaskCallback mGetCategoryTaskCallback;
    protected NameValueDataHolder mDataHolder;
    public GetCategoryTask(
            Context context,
            GetCategoryRequest requestModel,
            GetCategoryTaskCallback callback
    ){
        super(context);
        mRequestModel = requestModel;
        mContext = context;
        mGetCategoryTaskCallback = callback;
        mDataHolder = new NameValueDataHolder();
    }

    @Override
    public void run() {
        Uri categoryUri = GoftagramContract.CategoryEntry.buildCategoryList();
        Cursor cursor = mContext.getContentResolver().query(categoryUri, null, null, null, null);
        if (cursor.getCount() <= 0) {
            mGetCategoryTaskCallback.onMiss(mRequestModel,null);
        }else{
            mGetCategoryTaskCallback.onHit(mRequestModel,null);
        }
        cursor.close();
    }
}
