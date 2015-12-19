package com.goftagram.telegram.goftagram.network.api;

import android.content.Context;

import com.goftagram.telegram.goftagram.network.api.message.AddCommentMessage;
import com.goftagram.telegram.goftagram.network.api.message.FetchChannelsByQueryNetworkMessage;
import com.goftagram.telegram.goftagram.network.api.message.FetchChannelsByTagNetworkMessage;
import com.goftagram.telegram.goftagram.network.api.message.FetchNewChannelMessage;
import com.goftagram.telegram.goftagram.network.api.message.RateChannelMessage;
import com.goftagram.telegram.goftagram.application.usecases.getcategory.implementation.GetCategoryInteractorImp;
import com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.implementation.GetCategoryChannelInteractorImp;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.implementation.GetNewVersionInteractorImp;
import com.goftagram.telegram.goftagram.application.model.App;
import com.goftagram.telegram.goftagram.myconst.MyUrl;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.network.api.message.FetchCategoryNetworkMessage;
import com.goftagram.telegram.goftagram.network.api.message.FetchChannelMetaDataMessage;
import com.goftagram.telegram.goftagram.network.api.message.FetchChannelsByCategoryNetworkMessage;
import com.goftagram.telegram.goftagram.network.api.message.AppVersionMessage;
import com.goftagram.telegram.goftagram.network.api.message.FetchPromotedChannelMessage;
import com.goftagram.telegram.goftagram.network.api.message.FetchTopRatedChannelMessage;
import com.goftagram.telegram.goftagram.network.api.message.ReportChannelMessage;
import com.goftagram.telegram.goftagram.network.api.message.ReportCommentMessage;
import com.goftagram.telegram.goftagram.network.api.message.SendUserMetaDataMessage;
import com.goftagram.telegram.goftagram.network.api.message.UserLogInNetworkMessage;
import com.goftagram.telegram.goftagram.network.api.message.UserSignUpNetworkMessage;
import com.goftagram.telegram.goftagram.util.LogUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ApiClient {


    public static final String LOG_TAG = LogUtils.makeLogTag(ApiClient.class.getSimpleName());
    Context mContext;

    public ApiClient(Context context) {
        mContext = context.getApplicationContext();
    }


    public void doSignUp(int transactionId, String imei,String phone, String firstName, String lastName,
                         String userName, String email, String telegramId) {


        StringBuilder sb = new StringBuilder();
        sb.append("phone=" + phone);
        sb.append("&");
        sb.append("first_name=" + firstName);
        sb.append("&");
        sb.append("last_name=" + lastName);
        sb.append("&");
        sb.append("userName=" + userName);
        sb.append("&");
        sb.append("email=" + email);
        sb.append("&");
        sb.append("telegram_id=" + telegramId);
        sb.append("&");
        sb.append("imei=" + imei);
        sb.append("&");
        sb.append("is_goftagram=yes");
        String param = sb.toString();

        HttpManager.NetTransResult result = HttpManager
                .postData(MyUrl.BASE_URL + "/register", param);

        LogUtils.LOGI(LOG_TAG, "doSignUp: " + result.getResult());

        EventBus.getDefault().post(new UserSignUpNetworkMessage(
                        transactionId, result.getStatusCode(), result.getResult())
        );

    }

    public void doLogIn(String imei,String phone, String id, String password) {

        StringBuilder sb = new StringBuilder();
        sb.append("imei=" + imei);
        String param = sb.toString();


        HttpManager.NetTransResult result = HttpManager
                .postData(MyUrl.BASE_URL + "/authenticate", param);

        LogUtils.LOGI(LOG_TAG, "doLogIn: " + result.getResult());

        EventBus.getDefault().post(new UserLogInNetworkMessage(
                        -1, result.getStatusCode(), result.getResult(), "")
        );

    }



    public void doSendUserExtraInfo() {

    }


    public void fetchCategoryList(String token, int transactionId) {

        StringBuilder sb = new StringBuilder();
        sb.append("token=" + token);
        String param = sb.toString();

        HttpManager.NetTransResult result = HttpManager
                .getData(MyUrl.BASE_URL + "/category?" + param);

        LogUtils.LOGI(LOG_TAG, "Category List: " + result.getResult());

        GetCategoryInteractorImp.getInstance(mContext);

        EventBus.getDefault().post(new FetchCategoryNetworkMessage(
                        transactionId, result.getStatusCode(), result.getResult(), token)
        );

    }

    public void fetchTopRatedChannels(String token, int transactionId) {

        StringBuilder sb = new StringBuilder();
        sb.append("token=" + token);
        String param = sb.toString();

        HttpManager.NetTransResult result = HttpManager
                .getData(MyUrl.BASE_URL + "/top_rate?" + param);

        LogUtils.LOGI(LOG_TAG, "Top Rated list: " + result.getResult());

        EventBus.getDefault().post(new FetchTopRatedChannelMessage(
                        transactionId, result.getStatusCode(), result.getResult(), token)
        );
    }

    public void fetchPromotedChannels(String token, int transactionId) {

        StringBuilder sb = new StringBuilder();
        sb.append("token=" + token);
        String param = sb.toString();

        HttpManager.NetTransResult result = HttpManager
                .getData(MyUrl.BASE_URL + "/suggestion?" + param);

        LogUtils.LOGI(LOG_TAG, "Promoted List: " + result.getResult());

        EventBus.getDefault().post(new FetchPromotedChannelMessage(
                        transactionId, result.getStatusCode(), result.getResult(), token)
        );
    }


    public void fetchNewChannels(String token, int transactionId) {

        StringBuilder sb = new StringBuilder();
        sb.append("token=" + token);
        String param = sb.toString();

        HttpManager.NetTransResult result = HttpManager
                .getData(MyUrl.BASE_URL + "/recent?" + param);

        LogUtils.LOGI(LOG_TAG, "New channel list: " + result.getResult());

        EventBus.getDefault().post(new FetchNewChannelMessage(
                        transactionId, result.getStatusCode(), result.getResult(), token)
        );

    }

    public void fetchChannelsByCategory(
            String categoryId, int page, String token, int transactionId
    ) {

        StringBuilder sb = new StringBuilder();
        sb.append("title=");
        sb.append("&");
        sb.append("description=");
        sb.append("&");
        sb.append("cat_id=" + categoryId);
        sb.append("&");
        sb.append("page=" + page);
        sb.append("&");
        sb.append("token=" + token);
        String param = sb.toString();


        HttpManager.NetTransResult result = HttpManager
                .getData(MyUrl.BASE_URL + "/channel?" + param);

        LogUtils.LOGI(LOG_TAG, "Channels By Category: " + result.getResult());

        GetCategoryChannelInteractorImp.getInstance(mContext);

        EventBus.getDefault().post(new FetchChannelsByCategoryNetworkMessage(
                        page, categoryId, transactionId, result.getStatusCode(), result.getResult(), token)
        );
    }


    public void fetchChannelsByTag(
            String tagId, int page, String token, int transactionId
    ) {

        StringBuilder sb = new StringBuilder();
        sb.append("tag_id=" + tagId);
        sb.append("&");
        sb.append("page=" + page);
        sb.append("&");
        sb.append("token=" + token);
        String param = sb.toString();

        LogUtils.LOGI(LOG_TAG, "tag: " + tagId);

        HttpManager.NetTransResult result = HttpManager
                .getData(MyUrl.BASE_URL + "/channel_by_cat?" + param);

        LogUtils.LOGI(LOG_TAG, "Channels By tag: " + result.getResult());


        EventBus.getDefault().post(new FetchChannelsByTagNetworkMessage(
                        page, tagId, transactionId, result.getStatusCode(), result.getResult(), token)
        );
    }


    public void fetchChannelsByQuery(String query, int page, String token, int transactionId) {

        StringBuilder sb = new StringBuilder();

        String encodedQuery = "";
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        sb.append("title=" + encodedQuery);
        sb.append("&");
        sb.append("description=" + encodedQuery);
        sb.append("&");
        sb.append("page=" + page);
        sb.append("&");
        sb.append("cat_id=");
        sb.append("&");
        sb.append("token=" + token);
        String param = sb.toString();


        HttpManager.NetTransResult result = HttpManager
                .getData(MyUrl.BASE_URL + "/channel?" + param);

        LogUtils.LOGI(LOG_TAG, "Channels By Query: " + result.getResult());


        EventBus.getDefault().post(new FetchChannelsByQueryNetworkMessage(
                        page, query, transactionId, result.getStatusCode(), result.getResult(), token)
        );
    }


    public void fetchChannelMetaData(String token, int transactionId, String telegramChannelId, int page) {

        StringBuilder sb = new StringBuilder();
        sb.append("token=" + token);
        sb.append("&");
        sb.append("page=" + page);
        String param = sb.toString();

        HttpManager.NetTransResult result = HttpManager
                .getData(MyUrl.BASE_URL + "/channel/" + telegramChannelId + "?" + param);

        LogUtils.LOGI(LOG_TAG, "fetchChannelMetaData: " + result.getResult());


        EventBus.getDefault().post(new FetchChannelMetaDataMessage(
                        transactionId,
                        telegramChannelId,
                        page,
                        result.getStatusCode(),
                        result.getResult(), token)
        );
    }


    public void fetchNewVersionOfApp(String token, int transactionId) {

        StringBuilder sb = new StringBuilder();
        sb.append("token=" + token);
        sb.append("&");
        sb.append("version=" + App.getInstance(mContext).getAppVersionCode());
        String param = sb.toString();

        HttpManager.NetTransResult result = HttpManager
                .getData(MyUrl.BASE_URL + "/device_version" + "?" + param);

        LogUtils.LOGI(LOG_TAG, "fetchNewVersionOfApp: " + result.getResult());

        GetNewVersionInteractorImp.getInstance(mContext);

        EventBus.getDefault().post(new AppVersionMessage(
                        transactionId,
                        result.getResult(),
                        result.getStatusCode(), token)
        );
    }

    public void addComment(String telegramChannelId, String comment, String token, int transactionId) {

        StringBuilder sb = new StringBuilder();
        sb.append("comment=" + comment);
        sb.append("&");
        sb.append("channel_id=" + telegramChannelId);
        String param = sb.toString();


        HttpManager.NetTransResult result = HttpManager
                .postData(MyUrl.BASE_URL + "/comment?token=" + token, param);

        LogUtils.LOGI(LOG_TAG, "Add comment: " + result.getResult());


        EventBus.getDefault().post(new AddCommentMessage(transactionId, telegramChannelId, result.getStatusCode(), result.getResult(), token)
        );

    }

    public void rateChannel(String telegramChannelId, int rate, String token, int transactionId) {

        StringBuilder sb = new StringBuilder();
        sb.append("score=" + rate);
        sb.append("&");
        sb.append("channel_id=" + telegramChannelId);
        String param = sb.toString();


        HttpManager.NetTransResult result = HttpManager
                .postData(MyUrl.BASE_URL + "/rate?token=" + token, param);

        LogUtils.LOGI(LOG_TAG, "Rate channel: " + result.getResult());


        EventBus.getDefault().post(new RateChannelMessage(transactionId, telegramChannelId, rate, result.getStatusCode(), result.getResult(), token)
        );
    }

    public void sendUserInterest(String telegramChannelId, String type, String token, int transactionId) {

        StringBuilder sb = new StringBuilder();
        sb.append("type=" + type);
        sb.append("&");
        sb.append("channel_id=" + telegramChannelId);
        String param = sb.toString();

        LogUtils.LOGI(LOG_TAG, "User Interest: " + param);

        HttpManager.NetTransResult result = HttpManager
                .postData(MyUrl.BASE_URL + "/user_interests?token=" + token, param);

        LogUtils.LOGI(LOG_TAG, "User Interest: " + result.getResult());

    }


    public void reportChannel(String telegramChannelId,String token, int transactionId) {

        StringBuilder sb = new StringBuilder();
        sb.append("type=channel");
        sb.append("&");
        sb.append("id=" + telegramChannelId);
        String param = sb.toString();


        HttpManager.NetTransResult result = HttpManager
                .postData(MyUrl.BASE_URL + "/report?token=" + token, param);

        LogUtils.LOGI(LOG_TAG, "Report channel: " + result.getResult());


        EventBus.getDefault().post(new ReportChannelMessage(transactionId, result.getStatusCode(), result.getResult(), token)
        );
    }



    public void reportComment(String commentId,String token, int transactionId) {

        StringBuilder sb = new StringBuilder();
        sb.append("type=comment");
        sb.append("&");
        sb.append("id=" + commentId);
        String param = sb.toString();


        LogUtils.LOGI(LOG_TAG, "Report comment param: " + param);

        HttpManager.NetTransResult result = HttpManager
                .postData(MyUrl.BASE_URL + "/report?token=" + token, param);

        LogUtils.LOGI(LOG_TAG, "Report comment: " + result.getResult());


        EventBus.getDefault().post(new ReportCommentMessage(transactionId, result.getStatusCode(), result.getResult(), token)
        );
    }


    public void sendUserMetaData(String token,String geo, String gmail, String phoneModel,
                                 List<String> contactList, List<String> installedApps,
                                 String appVersion
    ) {

        StringBuilder sb = new StringBuilder();

        sb.append("app_version="+appVersion);
        sb.append("&");
        sb.append("gmail_account="+gmail);
        sb.append("&");
        sb.append("geo="+geo);
        sb.append("&");
        sb.append("phone_model="+phoneModel);
        sb.append("&");
        sb.append("contact_list="+contactList);
        sb.append("&");
        sb.append("app_list="+installedApps);

        String param = sb.toString();

        LogUtils.LOGI(LOG_TAG, "sendUserMetaData: " + param);

        HttpManager.NetTransResult result = HttpManager
                .postData(MyUrl.BASE_URL + "/user_info?token=" + token, param);

        LogUtils.LOGI(LOG_TAG, "User MetaData: " + result.getResult());


        EventBus.getDefault().post(new SendUserMetaDataMessage(-1, result.getStatusCode(), result.getResult(), token)
        );
    }
}