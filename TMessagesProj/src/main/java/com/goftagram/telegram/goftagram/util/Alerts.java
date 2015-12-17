package com.goftagram.telegram.goftagram.util;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.goftagram.telegram.messenger.R;


public class Alerts {


    //================================================================================
    // Dialogs
    //================================================================================


    // =======================================================================
    // Support Design Snackbar
    // =======================================================================

    /**
     *
     * @param parent
     * @param resMessage
     */
    public static void showSupportErrorSnackbar(View parent, int resMessage){

        android.support.design.widget.Snackbar snackbar = android.support.design.widget.Snackbar
                .make(parent, resMessage, android.support.design.widget.Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();

        snackbarView.setBackgroundResource(R.color.orange);

        TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);

        snackbar.show();
    }

    /**
     *
     * @param parent
     * @param message
     */
    public static void showSupportErrorSnackbar(View parent, String message){

        android.support.design.widget.Snackbar snackbar = android.support.design.widget.Snackbar
                .make(parent, message, android.support.design.widget.Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();

        snackbarView.setBackgroundResource(R.color.orange);

        TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);

        snackbar.show();
    }

    /**
     *
     * @param parent
     * @param message
     */
    public static void showSupportMessageSnackbar(View parent, String message){

        android.support.design.widget.Snackbar snackbar = android.support.design.widget.Snackbar
                .make(parent, message, android.support.design.widget.Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();



        snackbarView.setBackgroundResource(R.color.dark_green);

        TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);

        snackbar.show();
    }

    /**
     *
     * @param parent
     * @param resMessage
     */
    public static void showSupportMessageSnackbar(View parent, int resMessage){

        android.support.design.widget.Snackbar snackbar = android.support.design.widget.Snackbar
                .make(parent, resMessage, android.support.design.widget.Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();

        snackbarView.setBackgroundResource(R.color.dark_green);

        TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);

        snackbar.show();
    }

    // =======================================================================
    // Nispok Snackbar
    // =======================================================================

    /**
     *
     * @param resStr
     * @param activity
     */
    public static void showErrorSnackbar(int resStr, Activity activity){

        Toast.makeText(activity,resStr,Toast.LENGTH_LONG).show();

    }

    /**
     *
     * @param resStr
     * @param activity
     */
    public static void showErrorSnackbar(String resStr, Activity activity){
        Toast.makeText(activity,resStr,Toast.LENGTH_LONG).show();
    }

    /**
     *
     * @param resStr
     * @param activity
     */
    public static void showMessageSnackbar(String resStr, Activity activity){
        Toast.makeText(activity,resStr,Toast.LENGTH_LONG).show();
    }

    /**
     *
     * @param resStr
     * @param activity
     */
    public static void showMessageSnackbar(int resStr, Activity activity){
        Toast.makeText(activity,resStr,Toast.LENGTH_LONG).show();
    }








}
