package com.goftagram.telegram.goftagram.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.goftagram.telegram.goftagram.util.LogUtils;


public class BootReceiver extends BroadcastReceiver {

    private final String LOG_TAG = BootReceiver.class.getSimpleName();
    private static final boolean DEBUG = true;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            LogUtils.LOGI(LOG_TAG, "BootReceiver: action = ");
            AlarmReceiver.setAlarm(
                    context,
                    60,
                    AlarmReceiver.getDatabaseDeletionPendingIntent(context)
            );
        }
    }
}