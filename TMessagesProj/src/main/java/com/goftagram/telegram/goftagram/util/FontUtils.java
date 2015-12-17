package com.goftagram.telegram.goftagram.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;


public class FontUtils {

    public static String fontNameWithAddress = "fonts/HOMA.TTF";

    public static void settingFont(TextView view,Context context){

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontNameWithAddress);
        view.setTypeface(typeface);
        return;
    }
}
