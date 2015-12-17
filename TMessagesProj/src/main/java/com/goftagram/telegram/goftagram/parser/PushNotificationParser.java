package com.goftagram.telegram.goftagram.parser;

import com.goftagram.telegram.goftagram.application.model.TelegramChannel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WORK on 11/24/2015.
 */
public class PushNotificationParser {



    public static final String DATA             = "data";
    public static final String TYPE             = "type";
    public static final String TYPE_DATA        = "data";

    public static final String SIMPLE_TYPE      = "simple";
    public static final String TITLE            = "title";
    public static final String DESCRIPTION      = "description";

    public static final String CHANNEL_TYPE     = "channel";
    public static final String CHANNEL          = "data";


    public static final String ADS_TYPE          = "ads";
    public static final String IMAGE_URL         = "imageUrl";
    public static final String LINK              = "link";




    public static SimpleTextPushNotification simpleParser(String jsonStr) throws JSONException {

        String message = "";
        String title  ="";

        JSONObject jsonObject = new JSONObject(jsonStr);

        message = jsonObject.getString(DESCRIPTION);
        title   = jsonObject.getString(TITLE);

        SimpleTextPushNotification response = new SimpleTextPushNotification();
        response.mMessage = message;
        response.mTitle  = title;
        return response;
    }



    public static NewChannelPushNotification newChannelParser(String jsonStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonStr);

        TelegramChannel telegramChannel = TelegramChannelParser.parser(jsonObject);

        NewChannelPushNotification response = new NewChannelPushNotification();

        response.mTelegramChannel = telegramChannel;


        return response;
    }



    public static AdsPushNotification adsParser(String jsonStr) throws JSONException {


        String description = "";
        String title  ="";
        String link = "";
        String imageUrl = "";

        JSONObject jsonObject = new JSONObject(jsonStr);

        description = jsonObject.getString(DESCRIPTION);
        title       = jsonObject.getString(TITLE);
        link        = jsonObject.getString(LINK);
        imageUrl    = jsonObject.getString(IMAGE_URL);



        AdsPushNotification response = new AdsPushNotification();

        response.mDescription   = description;
        response.mTitle         = title;
        response.mLink          = link;
        response.mImageUrl      = imageUrl;
        return response;
    }



    public static class SimpleTextPushNotification{
        public String mMessage;
        public String mTitle;
    }


    public static class NewChannelPushNotification{
        public TelegramChannel mTelegramChannel;
    }


    public static class AdsPushNotification{
        public String mDescription;
        public String mTitle;
        public String mLink;
        public String mImageUrl;
    }

}
