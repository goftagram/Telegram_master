package com.goftagram.telegram.goftagram.util;

import android.content.Context;
import android.content.res.Configuration;

import com.goftagram.telegram.messenger.R;


/**
 * Created by WORK on 10/12/2015.
 */
public class DeviceConfigUtils {

    public static boolean isTablet(Context context) {

        return context.getResources().getBoolean(R.bool.isTablet);
    }

    public static boolean isLandscape(Context context) {

        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
