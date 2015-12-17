/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2015.
 */

package com.goftagram.telegram.messenger;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.goftagram.telegram.goftagram.gcm.GcmPushMessageHandlerIntentService;
import com.goftagram.telegram.tgnet.ConnectionsManager;

import org.json.JSONObject;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        FileLog.d("tmessages", "GCM received intent: " + intent);

        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmPushMessageHandlerIntentService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));

        if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {

            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    ApplicationLoader.postInitApplication();

                    try {
                        String key = intent.getStringExtra("loc_key");
                        if ("DC_UPDATE".equals(key)) {
                            String data = intent.getStringExtra("custom");
                            JSONObject object = new JSONObject(data);
                            int dc = object.getInt("dc");
                            String addr = object.getString("addr");
                            String[] parts = addr.split(":");
                            if (parts.length != 2) {
                                return;
                            }
                            String ip = parts[0];
                            int port = Integer.parseInt(parts[1]);
                            ConnectionsManager.getInstance().applyDatacenterAddress(dc, ip, port);
                        }
                    } catch (Exception e) {
                        FileLog.e("tmessages", e);
                    }

                    ConnectionsManager.getInstance().resumeNetworkMaybe();
                }
            });
        } else if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
            String registration = intent.getStringExtra("registration_id");
            if (intent.getStringExtra("error") != null) {
                FileLog.e("tmessages", "Registration failed, should try again later.");
            } else if (intent.getStringExtra("unregistered") != null) {
                FileLog.e("tmessages", "unregistration done, new messages from the authorized sender will be rejected");
            } else if (registration != null) {
                FileLog.e("tmessages", "registration id = " + registration);
            }
        }

        setResultCode(Activity.RESULT_OK);
    }
}
