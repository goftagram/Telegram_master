package com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract;

import java.io.File;

/**
 * Created by WORK on 11/2/2015.
 */
public interface NewApkEntity {

    long getLastCheckedTime();

    void setLastCheckedTime(long lastCheckedTime);

    boolean isUpdated();

    String getNewApkUrl();

    boolean isDownloading();

    boolean hasNewApkDownloaded();

    String getNewApkFileName();

    String getNewApkFilePath();

    String getNewApkMd5Sum();

    int getNewApkVersion();

    void downloadNewApk(String url, int versionCode, String md5Sum);

    void onDownloadCompleted(Exception e, File file);




}
