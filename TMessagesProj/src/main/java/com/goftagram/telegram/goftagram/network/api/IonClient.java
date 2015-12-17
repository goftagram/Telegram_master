package com.goftagram.telegram.goftagram.network.api;

import android.content.Context;
import android.util.Log;

import com.dd.CircularProgressButton;
import com.goftagram.telegram.goftagram.activity.NewChannelActivity;
import com.goftagram.telegram.goftagram.myconst.Keys;
import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.myconst.MyUrl;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * Created by mhossein on 10/18/15.
 */
public class IonClient{

    private static IonClient instance;

    private Map<Integer,Object> pendingAddChannelRequest;

    private IonClient(){
        EventBus.getDefault().register(this);

        pendingAddChannelRequest = new HashMap<>();

    }

    public static IonClient getInstance(){

        if(instance == null){
            instance = new IonClient();
        }

        return instance;
    }


    public void AddChannel(final Context context, final CircularProgressButton submitBtn, final String ... fields){


        Ion.with(context)
                .load(MyUrl.BASE_URL + "/channel?token=" + UserController.getInstance(context).getToken())
                .uploadProgressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(long uploaded, long total) {
                        int percent = (int)((uploaded * 100)/total);
                        Log.i(getClass().getName(), "percent = " + percent + "uploaded = " + uploaded);
                        if(percent < 1 || percent > 99){
                            submitBtn.setIndeterminateProgressMode(true);
                            submitBtn.setProgress(50);
                        }else{
                            submitBtn.setIndeterminateProgressMode(false);
                            submitBtn.setProgress(percent);
                        }

                    }
                })
                .setMultipartParameter(Keys.TITLE, fields[0])
                .setMultipartParameter(Keys.DESCRIPTION, fields[1])
                .setMultipartParameter(Keys.CAT_ID, fields[2])
                .setMultipartParameter(Keys.TAGS, fields[3])
                .setMultipartFile(Keys.IMAGE, new File(fields[4]))
                .setMultipartParameter(Keys.USERNAME, fields[5])
                .setMultipartParameter(Keys.TYPE, fields[6])
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {



                        if(e == null){
                            String status = result.get(Keys.STATUS).getAsString();
                            if (status.equals(Keys.FAIL)) {
                                List<String> messages = new ArrayList<String>();
                                if(result.has(Keys.DATA)){
                                    JsonObject data = result.get(Keys.DATA).getAsJsonObject();
                                    JsonObject errors = data.get(Keys.ERRORS).getAsJsonObject();
                                    Set<Map.Entry<String, JsonElement>> entries = errors.entrySet();
                                    for (Map.Entry<String, JsonElement> entry : entries) {
                                        messages.add(errors.get(entry.getKey()).getAsString());
                                    }
                                }else{
                                    messages.add(result.get(Keys.MESSAGE).getAsString());
                                }


                                EventBus.getDefault().post(new NewChannelActivity.AddChannelMessage(messages, status));
                            } else if (result.get(Keys.STATUS).getAsString().equals(Keys.EXPIRED)) {
                                int requestId = UniqueIdGenerator.getInstance().getNewId();
                                pendingAddChannelRequest.put(requestId, new Object[]{context, submitBtn, fields});
                                UserController.getInstance(context).doLoginAsync(requestId);

                            } else {

                                List<String> messages = new ArrayList<String>();

                                messages.add(result.get(Keys.MESSAGE).getAsString());

                                EventBus.getDefault().post(new NewChannelActivity.AddChannelMessage(messages, status));

                            }
                        }



                    }
                });
    }

    public void onEventAsync(UserController.UserLogInUiEvent event){
        for (Integer i : pendingAddChannelRequest.keySet()) {
            Context context = (Context)((Object[])pendingAddChannelRequest.get(i))[0];
            CircularProgressButton btn = (CircularProgressButton)((Object[])pendingAddChannelRequest.get(i))[1];
            String[] fields = (String[])((Object[])pendingAddChannelRequest.get(i))[2];
            AddChannel(context, btn, fields);
        }
    }
}
