package com.goftagram.telegram.goftagram.application;

import android.app.Application;


public class MyApplication extends Application {


    public static final String myket        = "myket";
    public static final String cafebazaar   = "cafebazaar";
    public static final String google       = "google-play";

    public MyApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();   
//        Fabric.with(this, new Crashlytics());
//		LeakCanary.install(this);
    }


    public static String whatMarket(){
        return cafebazaar;
//        return myket;
//        return google;

    }



}
