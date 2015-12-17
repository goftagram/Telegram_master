package com.goftagram.telegram.goftagram.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.goftagram.telegram.goftagram.network.DownloadManager;
import com.goftagram.telegram.goftagram.util.LogUtils;

import de.greenrobot.event.EventBus;

/**
 * Created by mhossein on 10/10/15.
 */
public class NewVersionDownloaderService extends Service {

    private final String LOG_TAG = LogUtils.makeLogTag(NewVersionDownloaderService.class.getSimpleName());

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String apkUrl = intent.getStringExtra("apkUrl");
        String savePath = intent.getStringExtra("savePath");
        DownloadManager.getInstanse(this).downloadApk(this, apkUrl,savePath);
        return START_NOT_STICKY;
    }


    public static class DownloadManagerFinishedMessage {
        public boolean mIsSuccess;
    }

    public void onEventAsync(DownloadManagerFinishedMessage message) {
        stopSelf();
    }
}
