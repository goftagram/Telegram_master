package com.goftagram.telegram.goftagram.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;


public class ComponentUtils {

    public static synchronized void setComponentEnable(Context context, Class myClass){
        ComponentName receiver = new ComponentName(context, myClass);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static synchronized void setComponentDisabled(Context context, Class myClass){

        ComponentName receiver = new ComponentName(context, myClass);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

}
