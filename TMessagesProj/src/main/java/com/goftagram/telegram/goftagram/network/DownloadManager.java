package com.goftagram.telegram.goftagram.network;

import android.content.Context;

import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.NewApkEntity;
import com.goftagram.telegram.goftagram.service.NewVersionDownloaderService;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.implementation.NewApkEntityImp;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * Created by mhossein on 9/30/15.
 * Download File
 */
public class DownloadManager implements FutureCallback<File> {

    private final String LOG_TAG = LogUtils.makeLogTag(DownloadManager.class.getSimpleName());

    private static DownloadManager instanse;

    private Context mContext;

    private DownloadManager(Context context) {

        mContext = context.getApplicationContext();

    }

    public static synchronized DownloadManager getInstanse(Context context) {

        if (instanse == null) {
            instanse = new DownloadManager(context);
        }

        return instanse;
    }

    /**
     * Download app apk & set status
     *
     * @param context
     * @param url
     */
    public synchronized void downloadApk(Context context, String url, String savePath) {

        File file = new File(savePath);
        if(file.exists()){
            file.delete();
        }
        try {

            file.createNewFile();

        } catch (IOException e) {

            e.printStackTrace();

            NewApkEntity apk = NewApkEntityImp.getInstance(mContext);
            apk.onDownloadCompleted(new Exception(),null);
            EventBus
                    .getDefault()
                    .post(new NewVersionDownloaderService.DownloadManagerFinishedMessage());

            return;

        }

        Ion.with(context)
                .load(url)
                .noCache()
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        LogUtils.LOGI(LOG_TAG, "progress : " + downloaded + "/" + total);
                    }
                })
                .write(file)
                .setCallback(this);
    }




    @Override
    public void onCompleted(final Exception e,final File file) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                LogUtils.LOGI(LOG_TAG, "onCompleted : " + e);
                LogUtils.LOGI(LOG_TAG, "onCompleted : " + file);
                NewApkEntity apk = NewApkEntityImp.getInstance(mContext);
                apk.onDownloadCompleted(e,file);
                EventBus
                        .getDefault()
                        .post(new NewVersionDownloaderService.DownloadManagerFinishedMessage());
                }
        }).start();


    }
}
