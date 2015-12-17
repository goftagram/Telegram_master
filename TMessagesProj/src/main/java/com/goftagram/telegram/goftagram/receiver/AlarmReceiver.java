package com.goftagram.telegram.goftagram.receiver;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.goftagram.telegram.goftagram.service.DatabaseInvalidationService;
import com.goftagram.telegram.goftagram.util.ComponentUtils;
import com.goftagram.telegram.goftagram.util.LogUtils;

public class AlarmReceiver extends WakefulBroadcastReceiver {

        public static final String ACTION_DELETE_DATABASE = "DELETE DATABASE";

        private final String LOG_TAG = LogUtils.makeLogTag(AlarmReceiver.class.getSimpleName());
        private static final boolean DEBUG = true;


    @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            switch (action){
                case ACTION_DELETE_DATABASE:
                    LogUtils.LOGI(LOG_TAG, "WakefulBroadcastReceiver: action = " + ACTION_DELETE_DATABASE);
                    Intent service = new Intent(context, DatabaseInvalidationService.class);
                    startWakefulService(context, service);
                    break;
            }


        }



        public static void setAlarm(Context context,int realTimeSec,PendingIntent pendingIntent) {

            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                  SystemClock.elapsedRealtime() +  realTimeSec*1000, pendingIntent);

            ComponentUtils.setComponentEnable(context, BootReceiver.class);

        }



    public static PendingIntent getDatabaseDeletionPendingIntent(Context context){

        Intent intent = new Intent(ACTION_DELETE_DATABASE);
        intent.setClass(context,AlarmReceiver.class);
        intent.setPackage("com.goftagram.telegram.goftagram.receiver");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        return  pendingIntent;
    }

    }


