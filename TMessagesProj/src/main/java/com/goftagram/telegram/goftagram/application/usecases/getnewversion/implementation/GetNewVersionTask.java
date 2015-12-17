package com.goftagram.telegram.goftagram.application.usecases.getnewversion.implementation;

import android.content.Context;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetTask;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.GetNewVersionRequest;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.GetNewVersionTaskCallback;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.NewApkEntity;

/**
 * Created by WORK on 11/2/2015.
 */
public class GetNewVersionTask extends AbsGetTask {

    private Context mContext;
    private GetNewVersionRequest mRequestModel;
    private GetNewVersionTaskCallback mGetNewVersionTaskCallback;
    protected NameValueDataHolder mDataHolder;

    public GetNewVersionTask(
            Context context,
            GetNewVersionRequest requestModel,
            GetNewVersionTaskCallback callback
    ) {
        super(context);
        mRequestModel = requestModel;
        mContext = context;
        mGetNewVersionTaskCallback = callback;
        mDataHolder = new NameValueDataHolder();
    }

    @Override
    public void run() {

        NewApkEntity apk = NewApkEntityImp.getInstance(mContext);

        if (apk.isUpdated()) {

            mDataHolder.put(GetNewVersionInteractorImp.KEY_IS_UPDATED,true);
            mGetNewVersionTaskCallback.onHit(mRequestModel, mDataHolder);

        }else{

            mDataHolder.put(GetNewVersionInteractorImp.KEY_IS_UPDATED, false);

            if (apk.hasNewApkDownloaded()) {

                mDataHolder.put(GetNewVersionInteractorImp.KEY_HAS_NEW_APK_DOWNLOADED, true);
                mDataHolder.put(GetNewVersionInteractorImp.KEY_NEW_APK_URI, apk.getNewApkFilePath());
                mGetNewVersionTaskCallback.onHit(mRequestModel, mDataHolder);

            }else{

                mDataHolder.put(GetNewVersionInteractorImp.KEY_HAS_NEW_APK_DOWNLOADED,false);
                mGetNewVersionTaskCallback.onMiss(mRequestModel,mDataHolder);
            }
        }

    }
}
