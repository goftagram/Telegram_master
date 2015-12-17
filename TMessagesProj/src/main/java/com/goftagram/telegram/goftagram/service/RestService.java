package com.goftagram.telegram.goftagram.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.network.api.ApiClient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is used to do all network transaction. ServiceHelper1 class sends required data
 * to this service and then this service invokes appropriate method of ApiClient class.
 */
public class RestService extends Service {


    /**
     * The ExecutorService to run network on worker thread
     */
    ExecutorService mExecutor = null;
    /**
     * Queue to keep track of network call and used to stop the service
     */
    private Integer mCounter;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_DO_SIGN_UP)){

            final String phone    	  	= intent.getStringExtra(ServiceHelper.EXTRA_PHONE);
            final String firstName   	= intent.getStringExtra(ServiceHelper.EXTRA_FIRST_NAME);
            final String lastName    	= intent.getStringExtra(ServiceHelper.EXTRA_LAST_NAME);
            final String userName    	= intent.getStringExtra(ServiceHelper.EXTRA_USERNAME);
            final String email    		= intent.getStringExtra(ServiceHelper.EXTRA_EMAIL);
            final String telegramId    	= intent.getStringExtra(ServiceHelper.EXTRA_TELEGRAM_ID);
            final String imei    	    = intent.getStringExtra(ServiceHelper.EXTRA_IMEI);
            final int transactionId    	= intent.getIntExtra(ServiceHelper.EXTRA_TRANSACTION_ID, -1);
            increaseCounter();

            Runnable task  = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.doSignUp(transactionId,imei,phone,firstName,lastName,userName,email,telegramId);
                    decreaseCounter();
                    if(getCounter() <= 0){
                        stopSelf();
                    }
                }
            };

            mExecutor.execute(task);

        }else if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_DO_LOG_IN)){

            final String phone    	  	= intent.getStringExtra(ServiceHelper.EXTRA_PHONE);
            final String id   			= intent.getStringExtra(ServiceHelper.EXTRA_ID);
            final String password    	= intent.getStringExtra(ServiceHelper.EXTRA_PASSWORD);
            final String imei    	    = intent.getStringExtra(ServiceHelper.EXTRA_IMEI);
            increaseCounter();

            Runnable task  = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.doLogIn(imei,phone,id,password);
                    decreaseCounter();
                    if(getCounter() <= 0){
                        stopSelf();
                    }
                }
            };
            mExecutor.execute(task);

        }else if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_DO_SEND_USER_EXTRA_INFO)){

            final String token    	  	= intent.getStringExtra(ServiceHelper.EXTRA_TOKEN);
            final String gmail    	  	= intent.getStringExtra(ServiceHelper.EXTRA_GMAIL);
            final String phoneModel    	  	= intent.getStringExtra(ServiceHelper.EXTRA_PHONE_MODEL);
            final String geo = intent.getStringExtra(ServiceHelper.EXTRA_GEO);

            final List<String> contactList  = intent.getStringArrayListExtra(ServiceHelper.EXTRA_CONTACT_LIST);
            final List<String> installedApps  = intent.getStringArrayListExtra(ServiceHelper.EXTRA_APPLICATION_LIST);

            final String appVersion    	  	= intent.getStringExtra(ServiceHelper.EXTRA_APP_VERSION);

            increaseCounter();

            Runnable task  = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.sendUserMetaData(token, geo, gmail, phoneModel, contactList, installedApps, appVersion);
                    decreaseCounter();
                    if(getCounter() <= 0){
                        stopSelf();
                    }
                }
            };
            mExecutor.execute(task);

        }else if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_FETCH_TOP_RATED)) {

            final String token         = intent.getStringExtra(ServiceHelper.EXTRA_TOKEN);
            final int transactionId    = intent.getIntExtra(ServiceHelper.EXTRA_TRANSACTION_ID, -1);

            increaseCounter();

            Runnable task = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.fetchTopRatedChannels(token, transactionId);
                    decreaseCounter();
                    if (getCounter() <= 0) {
                        stopSelf();
                    }
                }
            };
            mExecutor.execute(task);

        }else if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_FETCH_PROMOTED)) {

            final String token         = intent.getStringExtra(ServiceHelper.EXTRA_TOKEN);
            final int transactionId    = intent.getIntExtra(ServiceHelper.EXTRA_TRANSACTION_ID, -1);

            increaseCounter();

            Runnable task = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.fetchPromotedChannels(token, transactionId);
                    decreaseCounter();
                    if (getCounter() <= 0) {
                        stopSelf();
                    }
                }
            };
            mExecutor.execute(task);

        }else if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_FETCH_NEW_CHANNELS)) {

            final String token         = intent.getStringExtra(ServiceHelper.EXTRA_TOKEN);
            final int transactionId    = intent.getIntExtra(ServiceHelper.EXTRA_TRANSACTION_ID, -1);

            increaseCounter();

            Runnable task = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.fetchNewChannels(token, transactionId);
                    decreaseCounter();
                    if (getCounter() <= 0) {
                        stopSelf();
                    }
                }
            };
            mExecutor.execute(task);

        }else if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_FETCH_CHANNEL_BY_CATEGORY)) {

            final String token          = intent.getStringExtra(ServiceHelper.EXTRA_TOKEN);
            final String categoryId     = intent.getStringExtra(ServiceHelper.EXTRA_CATEGORY_ID);
            final int transactionId     = intent.getIntExtra(ServiceHelper.EXTRA_TRANSACTION_ID, -1);
            final int page              = intent.getIntExtra(ServiceHelper.EXTRA_PAGE,1);

            increaseCounter();

            Runnable task = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.fetchChannelsByCategory(categoryId, page, token, transactionId);
                    decreaseCounter();
                    if (getCounter() <= 0) {
                        stopSelf();
                    }
                }
            };
            mExecutor.execute(task);

        }else if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_FETCH_CATEGORIES)) {

            final String token = intent.getStringExtra(ServiceHelper.EXTRA_TOKEN);
            final int transactionId = intent.getIntExtra(ServiceHelper.EXTRA_TRANSACTION_ID, -1);
            increaseCounter();

            Runnable task = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.fetchCategoryList(token, transactionId);
                    decreaseCounter();
                    if (getCounter() <= 0) {
                        stopSelf();
                    }
                }
            };
            mExecutor.execute(task);


        }else if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_FETCH_CHANNEL_META_DATA)) {

            final String token = intent.getStringExtra(ServiceHelper.EXTRA_TOKEN);
            final int transactionId = intent.getIntExtra(ServiceHelper.EXTRA_TRANSACTION_ID, -1);
            final String telegramChannelId = intent.getStringExtra(ServiceHelper.EXTRA_TELEGRAM_ID);
            final int page            = intent.getIntExtra(ServiceHelper.EXTRA_PAGE, 1);

            increaseCounter();

            Runnable task = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.fetchChannelMetaData(token, transactionId, telegramChannelId, page);
                    decreaseCounter();
                    if (getCounter() <= 0) {
                        stopSelf();
                    }
                }
            };
            mExecutor.execute(task);

        }else if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_FETCH_NEW_VERSION_OF_APP)){

            final String token = intent.getStringExtra(ServiceHelper.EXTRA_TOKEN);
            final int transactionId = intent.getIntExtra(ServiceHelper.EXTRA_TRANSACTION_ID, -1);

            increaseCounter();

            Runnable task = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.fetchNewVersionOfApp(token, transactionId);
                    decreaseCounter();
                    if (getCounter() <= 0) {
                        stopSelf();
                    }
                }
            };
            mExecutor.execute(task);

        }else if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_FETCH_CHANNELS_BY_TAG)) {

            final String token = intent.getStringExtra(ServiceHelper.EXTRA_TOKEN);
            final String tagId = intent.getStringExtra(ServiceHelper.EXTRA_TAG_ID);
            final int transactionId    = intent.getIntExtra(ServiceHelper.EXTRA_TRANSACTION_ID, -1);
            final int page            = intent.getIntExtra(ServiceHelper.EXTRA_PAGE,1);

            increaseCounter();

            Runnable task = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.fetchChannelsByTag(tagId, page, token, transactionId);
                    decreaseCounter();
                    if (getCounter() <= 0) {
                        stopSelf();
                    }
                }
            };
            mExecutor.execute(task);

        }else if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_FETCH_CHANNELS_BY_QUERY)) {

            final String token = intent.getStringExtra(ServiceHelper.EXTRA_TOKEN);
            final String query = intent.getStringExtra(ServiceHelper.EXTRA_QUERY);
            final int transactionId = intent.getIntExtra(ServiceHelper.EXTRA_TRANSACTION_ID, -1);
            final int page = intent.getIntExtra(ServiceHelper.EXTRA_PAGE, 1);

            increaseCounter();

            Runnable task = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.fetchChannelsByQuery(query, page, token, transactionId);
                    decreaseCounter();
                    if (getCounter() <= 0) {
                        stopSelf();
                    }
                }
            };
            mExecutor.execute(task);

        }else if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_ADD_COMMENT)) {

            final String token = intent.getStringExtra(ServiceHelper.EXTRA_TOKEN);
            final String comment = intent.getStringExtra(ServiceHelper.EXTRA_COMMENT);
            final String telegramChannelId = intent.getStringExtra(ServiceHelper.EXTRA_CHANNEL_ID);
            final int transactionId = intent.getIntExtra(ServiceHelper.EXTRA_TRANSACTION_ID, -1);

            increaseCounter();

            Runnable task = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.addComment(telegramChannelId, comment, token, transactionId);
                    decreaseCounter();
                    if (getCounter() <= 0) {
                        stopSelf();
                    }
                }
            };
            mExecutor.execute(task);
        }else if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_RATE_CHANNEL)) {

            final String token = intent.getStringExtra(ServiceHelper.EXTRA_TOKEN);
            final int rate = intent.getIntExtra(ServiceHelper.EXTRA_RATE, 5);
            final String telegramChannelId = intent.getStringExtra(ServiceHelper.EXTRA_CHANNEL_ID);
            final int transactionId = intent.getIntExtra(ServiceHelper.EXTRA_TRANSACTION_ID, -1);

            increaseCounter();

            Runnable task = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.rateChannel(telegramChannelId, rate, token, transactionId);
                    decreaseCounter();
                    if (getCounter() <= 0) {
                        stopSelf();
                    }
                }
            };
            mExecutor.execute(task);

        }else if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_SEND_USER_INTEREST)) {

            final String token = intent.getStringExtra(ServiceHelper.EXTRA_TOKEN);
            final String telegramChannelId = intent.getStringExtra(ServiceHelper.EXTRA_TELEGRAM_ID);
            final int transactionId = intent.getIntExtra(ServiceHelper.EXTRA_TRANSACTION_ID, -1);
            final String seenType = intent.getStringExtra(ServiceHelper.EXTRA_SEEN_TYPE);

            increaseCounter();

            Runnable task = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.sendUserInterest(telegramChannelId, seenType, token, transactionId);
                    decreaseCounter();
                    if (getCounter() <= 0) {
                        stopSelf();
                    }
                }
            };
            mExecutor.execute(task);

        }else if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_REPORT_CHANNEL)) {

            final String token = intent.getStringExtra(ServiceHelper.EXTRA_TOKEN);
            final String telegramChannelId = intent.getStringExtra(ServiceHelper.EXTRA_TELEGRAM_ID);
            final int transactionId = intent.getIntExtra(ServiceHelper.EXTRA_TRANSACTION_ID, -1);


            increaseCounter();

            Runnable task = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.reportChannel(telegramChannelId, token, transactionId);
                    decreaseCounter();
                    if (getCounter() <= 0) {
                        stopSelf();
                    }
                }
            };
            mExecutor.execute(task);
        }else if(intent.getStringExtra(ServiceHelper.EXTRA_ACTION).equals(ServiceHelper.ACTION_REPORT_COMMENT)) {

            final String token = intent.getStringExtra(ServiceHelper.EXTRA_TOKEN);
            final String commentId = intent.getStringExtra(ServiceHelper.EXTRA_COMMENT_ID);
            final int transactionId = intent.getIntExtra(ServiceHelper.EXTRA_TRANSACTION_ID, -1);


            increaseCounter();

            Runnable task = new Runnable() {

                @Override
                public void run() {
                    ApiClient apiClient = new ApiClient(getApplicationContext());
                    apiClient.reportComment(commentId,token, transactionId);
                    decreaseCounter();
                    if (getCounter() <= 0) {
                        stopSelf();
                    }
                }
            };
            mExecutor.execute(task);
        }

        return START_NOT_STICKY;
    }

    public synchronized int getCounter(){
        return mCounter;
    }

    public synchronized void increaseCounter(){
        mCounter++;
    }

    public synchronized void decreaseCounter(){
        mCounter--;
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void onCreate() {
        super.onCreate();
        mExecutor = Executors.newFixedThreadPool(5);
        mCounter = 0;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    @Override
    public void onDestroy() {
        mExecutor.shutdown();
        super.onDestroy();
    }


}
