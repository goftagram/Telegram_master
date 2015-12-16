package com.goftagram.telegram.myconfig;

import android.app.Activity;
import android.content.SharedPreferences;

import com.goftagram.telegram.messenger.ApplicationLoader;
import com.goftagram.telegram.messenger.LocaleController;

/**
 * Created by WORK on 12/6/2015.
 */
public class LanguageCustomizer {

    public static void setDefaultLanguage(){

        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        String lang = preferences.getString("language", null);
        if (lang == null) {

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("language", "fa");
            editor.commit();
            LocaleController.LocaleInfo currentInfo = LocaleController.getInstance().languagesDict.get("fa");
            LocaleController.getInstance().applyLanguage(currentInfo, true);
        }
    }

}
