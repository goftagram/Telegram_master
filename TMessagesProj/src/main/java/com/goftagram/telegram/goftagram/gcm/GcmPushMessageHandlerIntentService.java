package com.goftagram.telegram.goftagram.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.goftagram.telegram.goftagram.activity.ChannelDetailActivity;
import com.goftagram.telegram.goftagram.activity.GoftagramMainActivity;
import com.goftagram.telegram.goftagram.application.model.TelegramChannel;
import com.goftagram.telegram.goftagram.parser.PushNotificationParser;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.messenger.GcmBroadcastReceiver;
import com.goftagram.telegram.messenger.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by WORK on 12/16/2015.
 */
public class GcmPushMessageHandlerIntentService  extends IntentService {

    private final String LOG_TAG = LogUtils.makeLogTag(GcmPushMessageHandlerIntentService.class.getSimpleName());


    public GcmPushMessageHandlerIntentService() {
        super("GcmPushMessageHandlerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        parsePushMessage(intent.getExtras(),this);
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public static void parsePushMessage(Bundle data,Context context){

        if(data.get(PushNotificationParser.TYPE)==null)return;

        if (data.get(PushNotificationParser.TYPE).equals(PushNotificationParser.SIMPLE_TYPE)) {

            String rawData = data.getString(PushNotificationParser.TYPE_DATA);

            try {

                PushNotificationParser.SimpleTextPushNotification response
                        = PushNotificationParser.simpleParser(rawData);

                sendNotification(context,response.mTitle, response.mMessage);

            } catch (JSONException e) {

                e.printStackTrace();
                return;
            }

        }else if(data.get(PushNotificationParser.TYPE).equals(PushNotificationParser.CHANNEL_TYPE)){


            String rawData = data.getString(PushNotificationParser.TYPE_DATA);

            try {

                PushNotificationParser.NewChannelPushNotification response
                        = PushNotificationParser.newChannelParser(rawData);



                Uri telegramChannelUri = GoftagramContract.TelegramChannelEntry
                        .buildTelegramChannel(
                                SQLiteDatabase.CONFLICT_IGNORE
                        );

                ContentValues[] cvs = TelegramChannel.TelegramChannelsToContentValueArray(
                        new TelegramChannel[]{response.mTelegramChannel});

                context.getContentResolver().bulkInsert(telegramChannelUri, cvs);


                sendNotification(context,response.mTelegramChannel);

            } catch (JSONException e) {

                e.printStackTrace();
                return;
            }

        }else if(data.get(PushNotificationParser.TYPE).equals(PushNotificationParser.ADS_TYPE)){

            String rawData = data.getString(PushNotificationParser.TYPE_DATA);

            try {

                PushNotificationParser.AdsPushNotification response
                        = PushNotificationParser.adsParser(rawData);

                sendNotification(context,response.mTitle, response.mDescription,response.mImageUrl,response.mLink);

            } catch (JSONException e) {

                e.printStackTrace();
                return;
            }

        }
    }


    public static void sendNotification(Context context,String title, String message) {

        Intent intent = new Intent(context, GoftagramMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);



        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.logo_push)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setTicker(title) // status bar
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }


    public static void sendNotification(Context context,String title, String description,String imageUrl,String link) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final RemoteViews contentView = new RemoteViews(context.getPackageName(),R.layout.notification);
        Bitmap b = null;
        try {
            b = Picasso.with(context).load(imageUrl).get();
            contentView.setImageViewBitmap(R.id.icon, b);
        } catch (IOException e) {
            contentView.setImageViewResource(R.id.icon, R.drawable.logo_push);;
        }

        contentView.setTextViewText(R.id.title,title );
        contentView.setTextViewText(R.id.description, description);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.logo_push)
                .setContent(contentView)
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setTicker(title) // status bar
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }


    public static void sendNotification(Context context,TelegramChannel telegramChannel) {

        Intent intent = new Intent(context, ChannelDetailActivity.class);

        intent.putExtra(ChannelDetailActivity.EXTRA_TELEGRAM_CHANNEL_TITLE,telegramChannel.getTitle());
        intent.putExtra(ChannelDetailActivity.EXTRA_TELEGRAM_CHANNEL_ID,telegramChannel.getServerId());
        intent.putExtra(ChannelDetailActivity.EXTRA_COME_FROM_NOTIFICATION,true);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);


        final RemoteViews contentView = new RemoteViews(context.getPackageName(),R.layout.notification);
        Bitmap b = null;
        try {
            b = Picasso.with(context).load(telegramChannel.getThumbnail()).get();
            contentView.setImageViewBitmap(R.id.icon, b);
        } catch (IOException e) {
            contentView.setImageViewResource(R.id.icon, R.drawable.logo_push);;
        }



        contentView.setTextViewText(R.id.title, telegramChannel.getTitle());
        contentView.setTextViewText(R.id.description, telegramChannel.getDescription());


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.logo_push)
                .setContent(contentView)
                .setContentText(telegramChannel.getDescription())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(telegramChannel.getTitle()))
                .setTicker(telegramChannel.getTitle()) // status bar
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }


}
