package com.goftagram.telegram.goftagram.helper;

import android.content.Context;
import android.content.Intent;

import com.goftagram.telegram.goftagram.service.RestService;

import java.util.ArrayList;
import java.util.List;

public class ServiceHelper {
    public static final String   PACKAGE                 = "com.goftagram.telegram.goftagram.Service";

    public static final String   LOG_TAG                 = ServiceHelper.class.getSimpleName();

    public static final String EXTRA_ACTION         = PACKAGE + ".action";

    public static final String EXTRA_ID             = PACKAGE + ".id";
    public static final String EXTRA_PASSWORD       = PACKAGE + ".password";
    public static final String EXTRA_PHONE          = PACKAGE + ".phone";
    public static final String EXTRA_IMEI           = PACKAGE + ".imei";
    public static final String EXTRA_FIRST_NAME     = PACKAGE + ".firstName";
    public static final String EXTRA_LAST_NAME      = PACKAGE + ".lastName";
    public static final String EXTRA_USERNAME       = PACKAGE + ".userName";
    public static final String EXTRA_EMAIL          = PACKAGE + ".email";
    public static final String EXTRA_TELEGRAM_ID	= PACKAGE + ".telegramId";
    public static final String EXTRA_RATE            = PACKAGE + ".rate";
    public static final String EXTRA_CHANNEL_ID     = PACKAGE + ".channelId";
    public static final String EXTRA_COMMENT_ID     = PACKAGE + ".commentId";
    public static final String EXTRA_COMMENT        = PACKAGE + ".comment";
    public static final String EXTRA_TOKEN          = PACKAGE + ".token";
    public static final String EXTRA_PAGE           = PACKAGE + ".page";
    public static final String EXTRA_ROW_PER_PAGE   = PACKAGE + ".rowPerPage";
    public static final String EXTRA_TRANSACTION_ID = PACKAGE + "transactionId";
    public static final String EXTRA_TAG            = PACKAGE + "tag";
    public static final String EXTRA_CATEGORY_ID    = PACKAGE + "categoryId";
    public static final String EXTRA_TAG_ID         = PACKAGE + "tagId";
    public static final String EXTRA_QUERY          = PACKAGE + "query";

    public static final String EXTRA_GEO        	    = PACKAGE + ".geo";
    public static final String EXTRA_PHONE_MODEL        = PACKAGE + ".phoneModel";
    public static final String EXTRA_APPLICATION_LIST   = PACKAGE + ".applicationList";
    public static final String EXTRA_CONTACT_LIST       = PACKAGE + ".contactList";
    public static final String EXTRA_GMAIL        	    = PACKAGE + ".gmail";
    public static final String EXTRA_APP_VERSION        = PACKAGE + ".appVersion";


    public static final String EXTRA_SEEN_TYPE          = PACKAGE + "seen.type";

    public static final String ACTION_DO_SIGN_UP 				= PACKAGE + ".sign.up";
    public static final String ACTION_DO_LOG_IN 				= PACKAGE + ".log.in";
    public static final String ACTION_DO_SEND_USER_EXTRA_INFO 	= PACKAGE + ".send.extra.info";

    public static final String ACTION_FETCH_CATEGORIES          = PACKAGE + ".fetch.categories";
    public static final String ACTION_FETCH_PROMOTED            = PACKAGE + ".fetch.promoted";

    public static final String ACTION_FETCH_CHANNEL_META_DATA   = PACKAGE + ".fetch.channel.meta.data";
    public static final String ACTION_FETCH_NEW_CHANNELS         = PACKAGE + ".fetch.new.channels";
    public static final String ACTION_FETCH_TOP_RATED           = PACKAGE + ".fetch.top.rated";
    public static final String ACTION_FETCH_CHANNEL_BY_CATEGORY = PACKAGE +
                                                                ".fetch.channel.by.category";

    public static final String ACTION_FETCH_CHANNELS_BY_TAG  = PACKAGE + ".fetch.channels.by.tag";
    public static final String ACTION_FETCH_CHANNELS_BY_QUERY = PACKAGE + ".fetch.channels.by.query";

    public static final String ACTION_RATE_CHANNEL              = PACKAGE + ".rate.channel";
    public static final String ACTION_ADD_COMMENT               = PACKAGE + ".add.comment";
    public static final String ACTION_REPORT_CHANNEL            = PACKAGE + ".report.channel";
    public static final String ACTION_REPORT_COMMENT            = PACKAGE + ".report.comment";


    public static final String ACTION_FETCH_NEW_VERSION_OF_APP  = PACKAGE + ".fetch.new.version.app";

    public static final String ACTION_SEND_USER_INTEREST  = PACKAGE + ".send.user.interest";


    private static ServiceHelper sServiceHelper;
    private final Context mContext;

    private ServiceHelper(Context context){
        mContext 		= context;
    }

    public static synchronized ServiceHelper getInstance(Context context){
        if(sServiceHelper == null){
            sServiceHelper = new ServiceHelper(context.getApplicationContext());
        }
        return sServiceHelper;
    }


    public void sendSignUpRequest(int transactionId,String imei,String phone,String firstName,String lastName,
                                  String userName,String email,String telegramId){


        Intent intent = new Intent(mContext,RestService.class);

        intent.putExtra(EXTRA_ACTION, ACTION_DO_SIGN_UP);
        intent.putExtra(EXTRA_PHONE,phone);
        intent.putExtra(EXTRA_IMEI,imei);
        intent.putExtra(EXTRA_FIRST_NAME,firstName);
        intent.putExtra(EXTRA_LAST_NAME,lastName);
        intent.putExtra(EXTRA_USERNAME,userName);
        intent.putExtra(EXTRA_EMAIL,email);
        intent.putExtra(EXTRA_TELEGRAM_ID,telegramId);
        intent.putExtra(EXTRA_TRANSACTION_ID,transactionId);

        mContext.startService(intent);

    }

    public void sendLogInRequest(String imei,String phone,String id,String password){

        Intent intent = new Intent(mContext,RestService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_DO_LOG_IN);
        intent.putExtra(EXTRA_PHONE,phone);
        intent.putExtra(EXTRA_IMEI,imei);
        intent.putExtra(EXTRA_ID,id);
        intent.putExtra(EXTRA_PASSWORD,password);
        mContext.startService(intent);

    }

    public void sendUserExtraInfoRequest(
            String geo,
            String phoneModel,
            List<String> contactList,
            List<String> installedApps,
            String appVersion,
            String gmail,
            String token){

        Intent intent = new Intent(mContext,RestService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_DO_SEND_USER_EXTRA_INFO);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_GEO,geo);
        intent.putExtra(EXTRA_GMAIL,gmail);
        intent.putExtra(EXTRA_PHONE_MODEL, phoneModel);
        intent.putStringArrayListExtra(EXTRA_CONTACT_LIST, new ArrayList<String>(contactList));
        intent.putStringArrayListExtra(EXTRA_APPLICATION_LIST,new ArrayList<String>(installedApps));
        intent.putExtra(EXTRA_APP_VERSION,appVersion);


        mContext.startService(intent);

    }

    public void fetchCategoryList(int transactionId, String token){
        Intent intent = new Intent(mContext,RestService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_FETCH_CATEGORIES);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_TRANSACTION_ID, transactionId);
        mContext.startService(intent);
    }



    public void fetchTopRatedChannels(int transactionId, String token){

        Intent intent = new Intent(mContext,RestService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_FETCH_TOP_RATED);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_TRANSACTION_ID, transactionId);
        mContext.startService(intent);

    }

    public void fetchPromotedChannels(int transactionId, String token){

        Intent intent = new Intent(mContext,RestService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_FETCH_PROMOTED);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_TRANSACTION_ID, transactionId);
        mContext.startService(intent);

    }

    public void fetchNewChannels(int transactionId, String token){

        Intent intent = new Intent(mContext,RestService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_FETCH_NEW_CHANNELS);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_TRANSACTION_ID, transactionId);
        mContext.startService(intent);

    }

    public void fetchChannelByCategory(int transactionId, String token,String categoryId,int page){

        Intent intent = new Intent(mContext,RestService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_FETCH_CHANNEL_BY_CATEGORY);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_PAGE, page);
        intent.putExtra(EXTRA_CATEGORY_ID, categoryId);
        intent.putExtra(EXTRA_TRANSACTION_ID, transactionId);
        mContext.startService(intent);

    }

    public void fetchChannelByQuery(int transactionId, String token, String query, int page){

        Intent intent = new Intent(mContext,RestService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_FETCH_CHANNELS_BY_QUERY);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_PAGE, page);
        intent.putExtra(EXTRA_QUERY, query);
        intent.putExtra(EXTRA_TRANSACTION_ID, transactionId);
        mContext.startService(intent);

    }

    public void fetchChannelByTag(int transactionId, String token,String tagId,int page){

        Intent intent = new Intent(mContext,RestService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_FETCH_CHANNELS_BY_TAG);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_PAGE, page);
        intent.putExtra(EXTRA_TAG_ID, tagId);
        intent.putExtra(EXTRA_TRANSACTION_ID, transactionId);
        mContext.startService(intent);

    }




    public void rateChannel(int transactionId, int rate, String channelId, String token){

        Intent intent = new Intent(mContext,RestService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_RATE_CHANNEL);
        intent.putExtra(EXTRA_CHANNEL_ID, channelId);
        intent.putExtra(EXTRA_TRANSACTION_ID, transactionId);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_RATE, rate);
        mContext.startService(intent);

    }

    public void addComment(int transactionId, String comment, String channelId, String token){
        Intent intent = new Intent(mContext,RestService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_ADD_COMMENT);
        intent.putExtra(EXTRA_COMMENT, comment);
        intent.putExtra(EXTRA_CHANNEL_ID, channelId);
        intent.putExtra(EXTRA_TRANSACTION_ID, transactionId);
        intent.putExtra(EXTRA_TOKEN, token);
        mContext.startService(intent);

    }

    public void reportChannel(int transactionId, String reportCommentText, String channelId,String token){
        Intent intent = new Intent(mContext,RestService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_REPORT_CHANNEL);
        intent.putExtra(EXTRA_TELEGRAM_ID, channelId);
        intent.putExtra(EXTRA_TRANSACTION_ID, transactionId);
        intent.putExtra(EXTRA_TOKEN, token);
        mContext.startService(intent);
    }

    public void reportComment(int transactionId, String reportCommentText, String commentId,String token){
        Intent intent = new Intent(mContext,RestService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_REPORT_COMMENT);
        intent.putExtra(EXTRA_COMMENT_ID, commentId);
        intent.putExtra(EXTRA_TRANSACTION_ID, transactionId);
        intent.putExtra(EXTRA_TOKEN, token);
        mContext.startService(intent);
    }






    public void fetchChannelMetaData(int transactionId, String token, String telegramChannelInfo, int page){

        Intent intent = new Intent(mContext,RestService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_FETCH_CHANNEL_META_DATA);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_PAGE, page);
        intent.putExtra(EXTRA_TELEGRAM_ID, telegramChannelInfo);
        intent.putExtra(EXTRA_TRANSACTION_ID, transactionId);
        mContext.startService(intent);

    }


    public void fetchNewVersionOfApp(int transactionId, String token){
        Intent intent = new Intent(mContext, RestService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_FETCH_NEW_VERSION_OF_APP);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_TRANSACTION_ID, transactionId);
        mContext.startService(intent);
    }


    public void sendUserInterestedChannels(int transactionId, String token,String channelIds,String type){

        Intent intent = new Intent(mContext, RestService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_SEND_USER_INTEREST);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_TRANSACTION_ID, transactionId);
        intent.putExtra(EXTRA_TELEGRAM_ID,channelIds);
        intent.putExtra(EXTRA_SEEN_TYPE,type);

        mContext.startService(intent);
    }
}
