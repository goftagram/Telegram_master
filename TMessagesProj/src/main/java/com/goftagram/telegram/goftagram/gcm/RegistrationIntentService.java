/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goftagram.telegram.goftagram.gcm;

import android.app.IntentService;
import android.content.Intent;

import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.myconst.MyUrl;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.util.LogUtils;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {


    private final String LOG_TAG = LogUtils.makeLogTag(RegistrationIntentService.class.getSimpleName());

    public static final String TOKEN_EXTRA = "token_extra";

    public RegistrationIntentService() {
        super("RegIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            String token = intent.getStringExtra(TOKEN_EXTRA);

            LogUtils.LOGI(LOG_TAG, "GCM Registration Token: " + token);

            sendRegistrationToServer(token);

            GcmManager.getInstance(this).setTokenSent(true);

        } catch (Exception e) {

            LogUtils.LOGI(LOG_TAG, "Failed to complete token refresh" + e);

            GcmManager.getInstance(this).setTokenSent(false);

        }

        GcmManager.getInstance(this).setRegistering(false);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) throws IOException {
        // Add custom implementation, as needed.

        String userToken = UserController.getInstance(RegistrationIntentService.this).getToken();

        StringBuilder sb = new StringBuilder();
        sb.append("registration_token=" + token);
        String param = sb.toString();


        HttpManager.NetTransResult result = HttpManager
                .postData(MyUrl.BASE_URL + "/registration_token/update?token=" + userToken, param);

        if(result.getStatusCode() != HttpManager.OK){
            throw new IOException("Can not register");
        }

        LogUtils.LOGI(LOG_TAG, "sendRegistrationToServer: " + result.getResult());

    }

}
