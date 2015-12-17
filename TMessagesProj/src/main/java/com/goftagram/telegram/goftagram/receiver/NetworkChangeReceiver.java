package com.goftagram.telegram.goftagram.receiver;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.goftagram.telegram.goftagram.network.api.message.ConnectionNetworkMessage;
import com.goftagram.telegram.goftagram.util.LogUtils;

import de.greenrobot.event.EventBus;

/**
 * Created by mhossein on 10/10/15.
 */
public class NetworkChangeReceiver extends WakefulBroadcastReceiver {

    private final String LOG_TAG =  LogUtils.makeLogTag(NetworkChangeReceiver.class.getSimpleName());
    private static final boolean DEBUG = true;


    @Override
    public void onReceive(Context context, Intent intent) {


        LogUtils.LOGI(LOG_TAG, "Network connectivity change");
        if (intent.getExtras() != null) {
            final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

            if (ni != null && ni.isConnected()) {
                LogUtils.LOGI(LOG_TAG, "Network " + ni.getTypeName() + " connected");
                EventBus.getDefault().post(new ConnectionNetworkMessage(true));
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                LogUtils.LOGI(LOG_TAG, "There's no network connectivity");
            }
        }
    }
}
