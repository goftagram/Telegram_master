package com.goftagram.telegram.goftagram.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.goftagram.telegram.goftagram.service.NewVersionDownloaderService;
import com.goftagram.telegram.goftagram.myconst.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mhossein on 9/29/15.
 */

public class Utils {

    /**
     * Convert dp to pixel
     * @param dp dp number
     * @return Size in pixels
     */
    public static int DpToPxInteger(Context context, int dp) {
        DisplayMetrics displayMetrics =
                context
                        .getResources()
                        .getDisplayMetrics();
        return (int) (dp * displayMetrics.density + 0.5f);
    }

    public static float DpToPxFloat(Context context, int dp) {
        DisplayMetrics displayMetrics =
                context
                        .getResources()
                        .getDisplayMetrics();
        return dp * displayMetrics.density;
    }

    /**
     * Get SD card path
     * @return SD directory
     */
    public static File GetSDCardDir(Context context){
        boolean ISSDCard;
        File[] Dirs = ContextCompat.getExternalFilesDirs(context, null);

        ISSDCard = false;
        for (File Dir : Dirs) {
            if (Dir != null) {
                if (Dir.getPath().contains("sdcard")) {
                    ISSDCard = true;
                    break;
                }
            }
        }

        File SDCardDir;
        if(ISSDCard && Dirs[Dirs.length -1] != null){
            SDCardDir = Dirs[Dirs.length -1];
        }else{
            SDCardDir = Dirs[0];
        }

        return SDCardDir;
    }



    public static void StartBackgroundService(Context context){
        // Start Activity --------------------------
        // Check for background service is running or not
        boolean isRunningService = false;
        ActivityManager manager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().equals("com.goftagram.telegram.goftagram.service.NewVersionDownloaderService")) {
                //Service is running;
                isRunningService = true;
                break;
            }
        }
        if (!isRunningService) { // Service is not running so start service
            Intent intent = new Intent(context, NewVersionDownloaderService.class);
            context.startService(intent);
        }

        //----------------------------------------------
    }

    public static void restartBackgroundService(Context context, String apkUrl,String savePath){
        // Start Activity --------------------------
        // Check for background service is running or not
        boolean isRunningService = false;
        ActivityManager manager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().equals("com.goftagram.telegram.goftagram.service.NewVersionDownloaderService")) {
                //Service is running;
                isRunningService = true;
                break;
            }
        }

        if (isRunningService) { // Service is not running so start service
//            context.stopService(intent);
            return;
        }


        Intent intent = new Intent(context, NewVersionDownloaderService.class);
        intent.putExtra("apkUrl", apkUrl);
        intent.putExtra("savePath", savePath);
        context.startService(intent);


        //----------------------------------------------
    }

    public static int getVersionCodeOfApk(Context context,String apkPath){

        final PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, 0);
        return info.versionCode;
    }


    public static byte[] createChecksum(String filename) throws Exception {
        InputStream fis =  new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    // see this How-to for a faster way to convert
    // a byte array to a HEX string
    public static String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        String result = "";

        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }

    /*
	 * returning image / video
	 */
    public static File GetOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory() + "/" + Constants.IMG_FILE_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + Constants.IMG_FILE_NAME + ".jpg");

        return mediaFile;
    }

    /**
     * Check whether input string is empty or null
     * @param inStr Input String
     * @return True (inStr is null/empty) | false (otherwise)
     */
    public static boolean EmptyOrNull(String inStr) {
        return (inStr == null) || (inStr.isEmpty());
    }

    /**
     * Get the screen width in dp
     *
     * @return Screen width (dp)
     */
    public static int GetScreenWidthDP(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int value = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpWidth,
                context.getResources().getDisplayMetrics());

        return value;
    }

    /**
     * Copy a text to clipboard
     *
     * @param label Label
     * @param text Text to be copied
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void CopyTextToClipboard(Context context,String label, String text) {

        int sdk = Build.VERSION.SDK_INT;

        if (sdk < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                    context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                    context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText(label, text);
            clipboard.setPrimaryClip(clip);
        }


    }


}
