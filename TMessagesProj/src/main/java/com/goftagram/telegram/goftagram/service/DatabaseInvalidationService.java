package com.goftagram.telegram.goftagram.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;

import com.bumptech.glide.Glide;
import com.goftagram.telegram.goftagram.activity.BaseActivity;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.receiver.AlarmReceiver;
import com.goftagram.telegram.goftagram.receiver.BootReceiver;
import com.goftagram.telegram.goftagram.util.ComponentUtils;
import com.goftagram.telegram.goftagram.util.LogUtils;

public class DatabaseInvalidationService extends IntentService {

    private final String LOG_TAG = LogUtils.makeLogTag(DatabaseInvalidationService.class.getSimpleName());
    private static final boolean DEBUG = true;

    public DatabaseInvalidationService() {
        super("DatabaseInvalidationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        LogUtils.LOGI(LOG_TAG, "DatabaseInvalidationService: action = " + "ACTION DELETE DATABASE");

        if(!BaseActivity.isVisible()){

            LogUtils.LOGI(LOG_TAG, "BaseActivity.isVisible() = " + BaseActivity.isVisible());

            getContentResolver().delete(GoftagramContract.ALL_URI, null, null);

            Glide.get(this).clearDiskCache();

            ComponentUtils.setComponentDisabled(this, BootReceiver.class);

        }else{
            PendingIntent pendingIntent = AlarmReceiver.getDatabaseDeletionPendingIntent(this);
            AlarmReceiver.setAlarm(this, 60*5, pendingIntent);
//            AlarmReceiver.setAlarm(this, 3, pendingIntent);
        }
        AlarmReceiver.completeWakefulIntent(intent);

    }
}
