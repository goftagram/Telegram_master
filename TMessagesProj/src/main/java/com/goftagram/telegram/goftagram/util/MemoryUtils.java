package com.goftagram.telegram.goftagram.util;

/**
 * Created by WORK on 11/15/2015.
 */
public class MemoryUtils {

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }
}
