package com.goftagram.telegram.goftagram.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.goftagram.telegram.messenger.R;


/**
 * Created by mhossein on 10/4/15.
 */
public class Dialogs {

    private static Dialogs instanse = null;

    private Dialogs(){

    }

    public static Dialogs getInstanse(){

        if(instanse == null){
            instanse = new Dialogs();
        }

        return instanse;
    }

    /**
     * Display Terms of Use (using WebView & WebViewClient)
     *
     * @param activity:   Context of caller activity
     * @param messageUrl: Url of the web page that is consist of terms of use
     */
    public void ShowTermsOfUseDialog(final Activity activity, String messageUrl) {


        final AlertDialog.Builder builder =
                new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);




        // Get view from "Terms Of Use" view
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.terms_of_use_dialog, null);

        builder.setView(view);

        final AlertDialog dialog = builder.create();



        // Set clickListener for "ok" button in "Terms Of Use" dialog
        android.widget.Button btnOk = (android.widget.Button) view.findViewById(R.id.btn_ok);
//        FontUtils.settingFont(btnOk, activity);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Create WebView and get view for it
        WebView myWebView = (WebView) view.findViewById(R.id.webview_terms_of_use);
        myWebView.loadUrl(messageUrl);
        myWebView.setHorizontalScrollbarOverlay(true);
        myWebView.setVerticalScrollBarEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });



        // Check for internet connection and display suitable message
        if (NetworkUtils.isOnline(activity)) {
            dialog.show();
        } else
            Alerts.showErrorSnackbar(R.string.error_no_internet_connection, activity);
    }

//    public void showSearchDialog(Activity activity){
//
//        View container = LayoutInflater.from(activity).inflate(R.layout.dialog_search, null);
//        searchDialog = new MaterialDialog.Builder(activity)
//                .customView(container, true)
//                .title(R.string.search_dialog_title)
//                .positiveText(R.string.search_dialog_title)
//                .negativeText(R.string.cancel_string)
//                .show();
//    }


    public void showSuccessAddChannelDialog(final Activity activity, final String message, final String channel){

        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);


        builder
                .setMessage(message)
                .setPositiveButton(R.string.btn_ok_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(R.string.channel_link_copy_btn_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String suggestion = message.substring(
                                message.indexOf(activity.getString(R.string.channel_link_copy_btn_text_start)), message.length() - 1
                        );

                        Utils.CopyTextToClipboard(activity, suggestion, suggestion);
                        Toast.makeText(activity, activity.getString(R.string.copy) + "\n" + suggestion, Toast.LENGTH_LONG).show();
                        activity.finish();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        activity.finish();
                    }
                }).show();




//        successAddChannelDialog = new MaterialDialog.Builder(activity)
//                .content(message)
//                .positiveText(R.string.btn_ok_text)
//                .positiveColorRes(android.R.color.black)
//                .negativeText(R.string.channel_link_copy_btn_text)
//                .negativeColorRes(R.color.primary)
//                .backgroundColorRes(android.R.color.white)
//                .onNegative(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
//                        String suggestion = message.substring(
//                                message.indexOf(activity.getString(R.string.channel_link_copy_btn_text_start)), message.length() - 1
//                        );
//
//                        Utils.CopyTextToClipboard(activity, suggestion, suggestion);
//                        Toast.makeText(activity, activity.getString(R.string.copy) + "\n" + suggestion, Toast.LENGTH_LONG).show();
//                        activity.finish();
//                    }
//                })
//                .dismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialogInterface) {
//                        activity.finish();
//                    }
//                })
//                .show();
    }


    public void showRulesOfAddChannelDialog(final Activity activity){

        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);

        builder.setTitle(R.string.rules_title)
                .setView(R.layout.rules_of_add_channel)
                .setPositiveButton(R.string.btn_ok_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();

//        rulesOfAddChannelDialog = new MaterialDialog.Builder(activity)
//                .title(R.string.rules_title)
//                .customView(R.layout.rules_of_add_channel, true)
//                .positiveText(R.string.btn_ok_text)
//                .positiveColorRes(android.R.color.black)
//                .backgroundColorRes(android.R.color.white)
//                .show();
    }

//    public void showNewChannelLinkDialog(final Activity activity, String channel){
//
//        TextView textView = new TextView(activity);
//
//        String message = activity.getString(R.string.channel_link_dialog_text) +
//                "\n" + "https://telegram.me/allStoreBot?start=" + channel;
//
//        SpannableString spannable = new SpannableString(message);
//
//        spannable.setSpan(new ClickableSpan() {
//
//            @Override
//            public void updateDrawState(TextPaint textPaint) {
//                super.updateDrawState(textPaint);
//                textPaint.setColor(activity.getResources().getColor(R.color.primary));
//                textPaint.setUnderlineText(false);
//            }
//
//            @Override
//            public void onClick(View view) {
//
//            }
//        } , message.indexOf("https"), message.length() -1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        textView.setText(spannable);
//
//        newChannelLinkDialog = new MaterialDialog.Builder(activity)
//                .customView(textView, false)
//                .positiveText(R.string.btn_ok_text)
//                .show();
//    }


    public void showReportCommentDialog(
            final Activity activity,
            final DialogInterface.OnClickListener OnPositiveClickListener,
            final DialogInterface.OnClickListener OnNegativeClickListener
    ){

        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);

        builder.setTitle(R.string.report_policy_violation_title);
        builder.setMessage(R.string.report_policy_violation_for_comment);
        builder.setPositiveButton(R.string.yes, OnPositiveClickListener);
        builder.setNegativeButton(R.string.no, OnNegativeClickListener);
        builder.show();
    }

    public void showReportChannelDialog(
            final Activity activity,
            final DialogInterface.OnClickListener OnPositiveClickListener,
            final DialogInterface.OnClickListener OnNegativeClickListener
    ){

        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);

        builder.setTitle(R.string.report_policy_violation_title);
        builder.setMessage(R.string.report_policy_violation_for_channel);
        builder.setPositiveButton(R.string.yes, OnPositiveClickListener);
        builder.setNegativeButton(R.string.no, OnNegativeClickListener);
        builder.show();
    }


    public void showUserRateRequestDialog(
            final Activity activity,
            final DialogInterface.OnClickListener OnPositiveClickListener,
            final DialogInterface.OnClickListener OnNegativeClickListener
    ){

        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);

        builder.setTitle(R.string.cafebazar_rate_title);
        builder.setMessage(R.string.cafebazar_rate_text);
        builder.setPositiveButton(R.string.yes, OnPositiveClickListener);
        builder.setNegativeButton(R.string.already_rated, OnNegativeClickListener);
        builder.show();
    }
}
