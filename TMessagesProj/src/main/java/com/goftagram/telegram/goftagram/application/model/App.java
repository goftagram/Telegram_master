package com.goftagram.telegram.goftagram.application.model;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.goftagram.telegram.messenger.R;


public class App {

    private String mAppVersion;
    private String mAppId;
    private static App sInstance;
    private Context mContext;

    private App(Context ctx){
        mContext = ctx;
        mAppId = mContext.getString(R.string.app_name);
        mAppVersion = getAppVersionName();
    }

    public static synchronized App getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new App(context.getApplicationContext());
        }
        return sInstance;
    }


    public String getAppVersionName() {
        try {
            PackageInfo packageInfo = mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public int getAppVersionCode() {
        try {
            PackageInfo packageInfo = mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

}
