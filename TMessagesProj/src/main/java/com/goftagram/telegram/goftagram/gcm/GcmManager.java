package com.goftagram.telegram.goftagram.gcm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.goftagram.telegram.goftagram.myconst.Constants;


/**
 * Created by WORK on 11/23/2015.
 */
public class GcmManager {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static GcmManager sGcmManager;
    private Context mContext;

    public static GcmManager getInstance(Context context) {
        synchronized (GcmManager.class) {
            if (sGcmManager == null) {
                sGcmManager = new GcmManager(context);
            }
            return sGcmManager;
        }
    }

    public GcmManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public void registerGcm() {

        if(TextUtils.isEmpty(getRegistrationToken()))return;
        String token = getRegistrationToken();
        if (!hasTokenSent()) {
            if (!isRegistering()) {

                Intent intent = new Intent(mContext, RegistrationIntentService.class);
                intent.putExtra(RegistrationIntentService.TOKEN_EXTRA, token);
                mContext.startService(intent);
                setLastCheckedTime(System.currentTimeMillis());
                setRegistering(true);

            } else {

                long storedTime = getLastCheckedTime();
                long currentTime = System.currentTimeMillis();
                long diffTime = currentTime - storedTime;
                long diffTimeHrs = (diffTime / ((1000) * (3600)));

                if (diffTimeHrs > Constants.CHECK_GCM_RETRYING) {
                    setRegistering(false);
                }

            }
        }

    }


    public boolean isRegistering() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean isRegistering = sharedPreferences.getBoolean(GcmPreferences.IS_REGISTERING, false);
        return isRegistering;
    }

    public void setRegistering(boolean isRegistering) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreferences.edit().putBoolean(GcmPreferences.IS_REGISTERING, isRegistering).apply();
    }

    public boolean hasTokenSent() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean hasTokenSent = sharedPreferences.getBoolean(GcmPreferences.SENT_TOKEN_TO_SERVER, false);
        return hasTokenSent;
    }

    public void setTokenSent(boolean hasSent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreferences.edit().putBoolean(GcmPreferences.SENT_TOKEN_TO_SERVER, hasSent).apply();
    }


    public long getLastCheckedTime() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getLong("LastCheckedTime", 0);

    }

    public void setLastCheckedTime(long lastCheckedTime) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("LastCheckedTime", lastCheckedTime);
        editor.commit();
    }


    public String getRegistrationToken() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String token = sharedPreferences.getString(GcmPreferences.REGISTRATION_TOKEN, "");
        return token;
    }

    public void setRegistrationToken(String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreferences.edit().putString(GcmPreferences.REGISTRATION_TOKEN, token).apply();
    }


}
