package com.goftagram.telegram.goftagram.parser;


import com.goftagram.telegram.goftagram.application.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;


public class AppVersionParser {

    public static final String STATUS           = "status";
    public static final String SUCCESS          = "success";
    public static final String FAIL             = "fail";
    public static final String EXPIRE           = "expire";
    public static final String MESSAGE          = "message";
    public static final String RESPONSE_DATA    = "data";
    public static final String ANDROID          = "android";
    public static final String UPDATE_LINK      = "update_link";
    public static final String VERSION_CODE     = "version";
    public static final String MD5SUM           = "md5";


    public static final String CAFEBAZAAR       = "cafebazaar";
    public static final String MYKET            = "myket";
    public static final String GOOGLE_PLAY      = "google_play";


    public static final int STATUS_CODE_SUCCESS     = 1;
    public static final int STATUS_CODE_FAIL        = 0;
    public static final int STATUS_CODE_EXPIRE      = 2;


    public static AppVersionParserResponse parser(String jsString) throws JSONException {



        int statusCode = 0;
        String message ="";
        String md5sum = "";
        String updateLink = "";
        int versionCode = 0;


        JSONObject jsonResponse = new JSONObject(jsString);

        if(jsonResponse.getString(STATUS).equals(SUCCESS)){

            statusCode = STATUS_CODE_SUCCESS;
            message = jsonResponse.getString(MESSAGE);
            JSONObject jsonData   = jsonResponse.getJSONObject(RESPONSE_DATA);

            if( jsonData.has(ANDROID)){

                JSONObject jsonAndroid   = jsonData.getJSONObject(ANDROID);
                JSONObject jsonMarket = null;
                if(MyApplication.whatMarket().equals(MyApplication.cafebazaar)){
                    jsonMarket = jsonAndroid.getJSONObject(CAFEBAZAAR);
                }else if(MyApplication.whatMarket().equals(MyApplication.myket)){
                    jsonMarket = jsonAndroid.getJSONObject(MYKET);
                }else if(MyApplication.whatMarket().equals(MyApplication.google)){
                    jsonMarket = jsonAndroid.getJSONObject(GOOGLE_PLAY);
                }else{
                    jsonMarket = jsonAndroid;
                }
                updateLink = jsonMarket.getString(UPDATE_LINK);
                versionCode= jsonMarket.getInt(VERSION_CODE);
                md5sum     = jsonMarket.getString(MD5SUM);

            }else{

                statusCode = STATUS_CODE_FAIL;
                message = jsonResponse.getString(MESSAGE);
            }

        }else if(jsonResponse.getString(STATUS).equals(EXPIRE)){
            statusCode = STATUS_CODE_EXPIRE;
            message = jsonResponse.getString(MESSAGE);
        }else {
            statusCode = STATUS_CODE_FAIL;
            message = jsonResponse.getString(MESSAGE);
        }
        AppVersionParserResponse response = new AppVersionParserResponse();
        response.mMd5Sum        =   md5sum;
        response.mMessage       = message;
        response.mStatusCode    =statusCode;
        response.mUpdateLink    = updateLink;
        response.mVersionCode   = versionCode;

        return response;

    }


    public static class AppVersionParserResponse{
        public String mMessage;
        public int mStatusCode;
        public String mUpdateLink;
        public String mMd5Sum;
        public Integer mVersionCode;
    }
}
