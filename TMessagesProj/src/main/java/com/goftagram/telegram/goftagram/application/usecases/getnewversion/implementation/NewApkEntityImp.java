package com.goftagram.telegram.goftagram.application.usecases.getnewversion.implementation;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.URLUtil;

import com.goftagram.telegram.goftagram.application.model.App;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.NewApkEntity;
import com.goftagram.telegram.goftagram.myconst.Constants;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.goftagram.util.MemoryUtils;
import com.goftagram.telegram.goftagram.util.Utils;

import java.io.File;

/**
 * Created by WORK on 11/2/2015.
 */
public class NewApkEntityImp implements NewApkEntity {

    private final String LOG_TAG = LogUtils.makeLogTag(NewApkEntityImp.class.getSimpleName());

    private static final Object sLock = new Object();

    private static NewApkEntityImp mInstance;

    private Context mContext;

    private NewApkEntityImp(Context context) {

        mContext = context.getApplicationContext();

    }

    public static synchronized NewApkEntity getInstance(Context context) {

        if (mInstance == null) {
            mInstance = new NewApkEntityImp(context);
        }

        return mInstance;
    }

    @Override
    public boolean hasNewApkDownloaded() {
        synchronized (sLock) {
            final SharedPreferences prefs = mContext.getSharedPreferences("NewVersionApk", 0);
            return prefs.getBoolean("HasDownloaded", false);
        }
    }


    private void setHasNewApkDownloaded(boolean hasDownloaded) {
        synchronized (sLock) {
            final SharedPreferences prefs = mContext.getSharedPreferences("NewVersionApk", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("HasDownloaded", hasDownloaded);
            editor.commit();
        }
    }

    @Override
    public String getNewApkFileName() {
        synchronized (sLock) {
            final SharedPreferences prefs = mContext.getSharedPreferences("NewVersionApk", 0);
            return prefs.getString("NewApkFileName", "");
        }
    }


    private void setNewApkFileName(String fileName) {

        synchronized (sLock) {
            final SharedPreferences prefs = mContext.getSharedPreferences("NewVersionApk", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("NewApkFileName", fileName);
            editor.commit();
        }
    }

    @Override
    public String getNewApkFilePath() {
        synchronized (sLock) {
            final SharedPreferences prefs = mContext.getSharedPreferences("NewVersionApk", 0);
            return prefs.getString("NewApkFilePath", "");
        }
    }


    private void setNewApkFilePath(String filePath) {

        synchronized (sLock) {
            final SharedPreferences prefs = mContext.getSharedPreferences("NewVersionApk", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("NewApkFilePath", filePath);
            editor.commit();
        }
    }

    @Override
    public String getNewApkMd5Sum() {
        synchronized (sLock) {
            final SharedPreferences prefs = mContext.getSharedPreferences("NewVersionApk", 0);
            return prefs.getString("MD5SUM", "");
        }
    }


    private void setNewApkMd5Sum(String md5Sum) {

        synchronized (sLock) {
            final SharedPreferences prefs = mContext.getSharedPreferences("NewVersionApk", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("MD5SUM", md5Sum);
            editor.commit();
        }
    }

    @Override
    public int getNewApkVersion() {
        synchronized (sLock) {
            final SharedPreferences prefs = mContext.getSharedPreferences("NewVersionApk", 0);
            return prefs.getInt("VersionCode", 1);
        }
    }

    public boolean isUpdated() {

        synchronized (sLock) {

            long storedTime = getLastCheckedTime();
            long currentTime = System.currentTimeMillis();
            long diffTime = currentTime - storedTime;
            long diffTimeHrs = (diffTime / ((1000) * (3600)));

            LogUtils.LOGI(LOG_TAG,"MyVersion:" + App.getInstance(mContext).getAppVersionCode());
            LogUtils.LOGI(LOG_TAG,"DownloadedVersion:" + getNewApkVersion());

            if (diffTimeHrs < Constants.CHECK_NEW_APK_FREQUENCY) {
                if (App.getInstance(mContext).getAppVersionCode() == getNewApkVersion()) {
                    File newApkFile = new File(getNewApkFilePath());
                    newApkFile.delete();
                    setHasNewApkDownloaded(false);
                    setIsDownloading(false);
                    return true;
                }else{
                    return false;
                }
            }else{
                setIsDownloading(false);
                return false;
            }
        }

    }



    @Override
    public void downloadNewApk(String url, int versionCode,String md5Sum) {

        synchronized (sLock) {

            if(versionCode > App.getInstance(mContext).getAppVersionCode()) {

                setHasNewApkDownloaded(false);
                setNewApkMd5Sum(md5Sum);
                setNewApkUrl(url);
                setNewApkVersion(versionCode);
                setNewApkFileName(URLUtil.guessFileName(url, null, null));

                if (MemoryUtils.externalMemoryAvailable()) {

                    setLastCheckedTime(System.currentTimeMillis());

                    String dirPath = mContext.getExternalFilesDir(null).getAbsoluteFile().toString() +
                            "/" + Constants.DOWNLOADED_APK_PATH;
                    File file = new File(dirPath);
                    file.mkdir();

                    String filePath =
                            mContext.getExternalFilesDir(null).getAbsoluteFile().toString() +
                                    "/" + Constants.DOWNLOADED_APK_PATH + "/" +
                                    getNewApkFileName();

                    setNewApkFilePath(filePath);
                    if (!isDownloading()) {
                        setIsDownloading(true);
                        Utils.restartBackgroundService(mContext, url, filePath);
                    }
                }
            }
        }
    }

    @Override
    public void onDownloadCompleted(Exception e, File file) {
        synchronized (sLock) {
            setIsDownloading(false);
            if (e == null) {
                try {

                    String md5SumOfDownloadedFile
                            = Utils.getMD5Checksum(file.getAbsolutePath().toString());
                    md5SumOfDownloadedFile.toLowerCase();
                    setIsDownloading(false);
                    String md5SumServer = getNewApkMd5Sum().toLowerCase();

                    if (md5SumOfDownloadedFile.equals(md5SumServer)) {
                        setHasNewApkDownloaded(true);
                    } else {
                        setHasNewApkDownloaded(false);
                        file.delete();
                    }

                } catch (Exception e1) {

                    setHasNewApkDownloaded(false);
                    file.delete();

                }
            } else {
                setHasNewApkDownloaded(false);
            }
        }
    }


    private void setNewApkVersion(int versionCode) {


        synchronized (sLock) {
            final SharedPreferences prefs = mContext.getSharedPreferences("NewVersionApk", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("VersionCode", versionCode);
            editor.commit();
        }
    }


    public long getLastCheckedTime() {
        synchronized (sLock) {
            final SharedPreferences prefs = mContext.getSharedPreferences("NewVersionApk", 0);
            return prefs.getLong("LastCheckedTime", 0);
        }
    }

    public void setLastCheckedTime(long lastCheckedTime) {
        synchronized (sLock) {
            final SharedPreferences prefs = mContext.getSharedPreferences("NewVersionApk", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("LastCheckedTime", lastCheckedTime);
            editor.commit();
        }

    }


    public String getNewApkUrl() {
        synchronized (sLock) {
            final SharedPreferences prefs = mContext.getSharedPreferences("NewVersionApk", 0);
            return prefs.getString("NewApkUrl", "");
        }

    }

    private void setNewApkUrl(String url) {
        synchronized (sLock) {
            final SharedPreferences prefs = mContext.getSharedPreferences("NewVersionApk", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("NewApkUrl", url);
            editor.commit();
        }

    }

    @Override
    public boolean isDownloading() {
        synchronized (sLock) {
            final SharedPreferences prefs = mContext.getSharedPreferences("NewVersionApk", 0);
            return prefs.getBoolean("IsDownloading", false);
        }

    }


    private void setIsDownloading(boolean isDownloading) {

        synchronized (sLock) {
            final SharedPreferences prefs = mContext.getSharedPreferences("NewVersionApk", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("IsDownloading", isDownloading);
            editor.commit();
        }
    }

}
