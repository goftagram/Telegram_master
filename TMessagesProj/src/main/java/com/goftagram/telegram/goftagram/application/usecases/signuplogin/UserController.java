package com.goftagram.telegram.goftagram.application.usecases.signuplogin;

import android.content.Context;
import android.content.SharedPreferences;

import com.goftagram.telegram.goftagram.application.model.MainUser;
import com.goftagram.telegram.goftagram.helper.ServiceHelper;
import com.goftagram.telegram.goftagram.network.HttpManager;
import com.goftagram.telegram.goftagram.network.api.message.UserLogInNetworkMessage;
import com.goftagram.telegram.goftagram.network.api.message.UserSignUpNetworkMessage;
import com.goftagram.telegram.goftagram.parser.UserParser;
import com.goftagram.telegram.messenger.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;


public class UserController {

    public static final String PACKAGE = "com.goftagram.telegram.goftagram.controller";

    public static final String LOG_TAG = UserController.class.getSimpleName();
    public static final boolean DEBUGE = true;

    public static final String DO_LOG_IN = "doLogIn";
    public static final String DO_SIGN_UP = "doSignUp";
    public static final String DO_SEND_USER_EXTRA_INFO = "doSendUserExtraInfo";

    public static final String PENDING = "pending";
    public static final String SUCCESS = "success";
    public static final String EXPIRE = "expire";
    public static final String FAIL = "fail";

    private String mToken = "";

    private static UserController sUserController;



    private final Context mContext;
    private List<Integer> mLogInRequestIds;
    private List<Integer> mSignUpRequestIds;
    private List<Integer> mSendExtraUserInfoRequestIds;

    private UserController(Context context) {

        mContext = context;

        mLogInRequestIds = new ArrayList<>();
        mSignUpRequestIds = new ArrayList<>();
        mSendExtraUserInfoRequestIds = new ArrayList<>();

        EventBus.getDefault().register(this);
        mToken = loadToken(mContext);
    }

    public static UserController getInstance(Context context) {
        synchronized (UserController.class) {
            if (sUserController == null) {
                sUserController = new UserController(context.getApplicationContext());
            }
            return sUserController;
        }
    }

    public void doSignUpAsync(int requestId) {
        synchronized (UserController.class) {
            UserSignUpUiEvent userSignUpUiEvent;
            MainUser mainUser = MainUserDao.getInstance(mContext).getMainUser();
            boolean hasSignedUp = mainUser.hasSignUp();
            if (hasSignedUp) {

                userSignUpUiEvent =
                        new UserSignUpUiEvent(requestId,
                                SUCCESS,
                                "User has sign up successfully", null);

                EventBus.getDefault().post(userSignUpUiEvent);
                doLoginAsync(requestId);
                return;

            }else if(mSignUpRequestIds.isEmpty()){

                String phone = mainUser.getPhoneNumber();
                String firstName = mainUser.getFirstName();
                String lastName = mainUser.getLastName();
                String userName = mainUser.getUserName();
                String email = mainUser.getEmail();
                String telegramId = mainUser.getTelegramId();
                String imei = mainUser.getImei();


                ServiceHelper.getInstance(mContext)
                        .sendSignUpRequest(requestId,imei,phone, firstName, lastName, userName, email, telegramId);
                MainUserDao.getInstance(mContext).setHasSignUp(false);
                mSignUpRequestIds.add(requestId);

            }else{

                mSignUpRequestIds.add(requestId);
            }
        }
    }

    public void doLoginAsync(int requestId) {

        synchronized (UserController.class) {
//            Log.i(LOG_TAG, "doLoginAsync " + requestId);
            UserLogInUiEvent userLogInUiEvent;
            MainUser mainUser = MainUserDao.getInstance(mContext).getMainUser();
            boolean hasLogIn = mainUser.hasLogIn();
            // The user has already logged in so done
//            Log.i(LOG_TAG, "hasLogIn " + hasLogIn);
            if (hasLogIn) {
                userLogInUiEvent =
                        new UserLogInUiEvent(requestId,
                                SUCCESS, "User has already logged in successfully", mToken);
                EventBus.getDefault().post(userLogInUiEvent);
                return;
            }else if(mLogInRequestIds.isEmpty()){
                String phone = mainUser.getPhoneNumber();
                String password = mainUser.getPassword();
                String id = mainUser.getId();
                String imei = mainUser.getImei();
                mLogInRequestIds.add(requestId);
                ServiceHelper.getInstance(mContext).sendLogInRequest(imei,phone, id, password);
                MainUserDao.getInstance(mContext).setHasLogIn(false);
            }else{
                mLogInRequestIds.add(requestId);
            }
        }
    }

    public void doSendUserExtraInfo(int requestId) {
        synchronized (UserController.class) {

        }
    }


    public void updateMainLogIn(boolean hasLogIn) {
        synchronized (UserController.class) {
//            Log.i(LOG_TAG, "updateMainLogIn: " + hasLogIn);
            MainUserDao.getInstance(mContext).setHasLogIn(hasLogIn);
        }

    }

    public void updateMainUserId(String id) {
        synchronized (UserController.class) {
            MainUserDao.getInstance(mContext).setId(id);
        }

    }

    public void updateMainUserImei(String imei) {
        synchronized (UserController.class) {
            MainUserDao.getInstance(mContext).setImei(imei);
        }

    }

    public void updateMainUserPhoneNumber(String phoneNumber) {
        synchronized (UserController.class) {
            MainUserDao.getInstance(mContext).setPhoneNumber(phoneNumber);
        }

    }

    public void updateMainUserFirstName(String firstName) {
        synchronized (UserController.class) {
            MainUserDao.getInstance(mContext).setFirstName(firstName);
        }

    }

    public void updateMainUserLastName(String lastName) {
        synchronized (UserController.class) {
            MainUserDao.getInstance(mContext).setLastName(lastName);

        }

    }

    public void updateMainUserUserName(String userName) {
        synchronized (UserController.class) {
            MainUserDao.getInstance(mContext).setUserName(userName);

        }

    }

    public void updateMainUserEmail(String email) {
        synchronized (UserController.class) {
            MainUserDao.getInstance(mContext).setEmail(email);

        }

    }

    public void updateMainUserTelegramId(String telegramId) {
        synchronized (UserController.class) {
            MainUserDao.getInstance(mContext).setTelegramId(telegramId);
        }

    }

    public void updateMainUserGmailAccount(String gmailAccount) {
        synchronized (UserController.class) {
            MainUserDao.getInstance(mContext).setGmailAccount(gmailAccount);
        }


    }

    public void updateMainUserGeo(String geo) {
        synchronized (UserController.class) {
            MainUserDao.getInstance(mContext).setGeo(geo);
        }

    }

    public void updateMainUserPhoneModel(String phoneModel) {
        synchronized (UserController.class) {
            MainUserDao.getInstance(mContext).setPhoneModel(phoneModel);

        }

    }

    public void updateMainUserPassword(String password) {
        synchronized (UserController.class) {
            MainUserDao.getInstance(mContext).setPassword(password);
        }

    }


    public String getToken() {
        synchronized (UserController.class) {
            return mToken;
        }
    }

    private void setToken(String token) {
        synchronized (UserController.class) {
            mToken = token;
        }
        saveToken(mContext, mToken);
    }


    private String loadToken(Context context) {
        synchronized (UserController.class) {
            final SharedPreferences prefs = mContext.getSharedPreferences("Token", 0);
            String token = prefs.getString("token", "");
            return token;
        }
    }

    private void saveToken(Context context, String token) {

        final SharedPreferences prefs = context.getSharedPreferences("Token", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", token);
        editor.apply();

    }



    public static class UserLogInUiEvent {


        String mToken;
        int mMessageId;
        String mStatus;
        String mMessage;


        public UserLogInUiEvent(int messageId, String status, String message, String token) {
            mMessageId = messageId;
            mToken = token;
            mStatus = status;
            mMessage = message;

        }

        public UserLogInUiEvent(UserLogInUiEvent userLogInUiEvent) {
            mMessageId = userLogInUiEvent.getMessageId();
            mToken = userLogInUiEvent.getToken();
            mStatus = userLogInUiEvent.getStatus();
            mMessage = userLogInUiEvent.getMessage();

        }

        public int getMessageId() {
            return mMessageId;
        }

        public void setMessageId(int messageId) {
            this.mMessageId = messageId;
        }

        public String getStatus() {
            return mStatus;
        }

        public void setStatus(String status) {
            this.mStatus = status;
        }

        public String getMessage() {
            return mMessage;
        }

        public void setMessage(String message) {
            this.mMessage = message;
        }

        public String getToken() {
            return mToken;
        }

        public void setToken(String token) {
            this.mToken = token;
        }


    }

    public static class UserSignUpUiEvent {

        public static final String PHONE_ERROR_KEY      = "phone.error";
        public static final String FIRST_NAME_ERROR_KEY = "first.name.error";
        public static final String LAST_NAME_ERROR_KEY  = "last.name.error";
        public static final String IMEI_ERROR_KEY  = "imei.error";

        int mMessageId;
        String mMessage;
        String mStatus;
        Map<String, String[]> mErrors;

        public UserSignUpUiEvent(int messageId, String status, String message, Map<String, String[]> errors) {
            mMessageId = messageId;
            mMessage = message;
            mStatus = status;
            mErrors = errors;
        }

        public UserSignUpUiEvent(UserSignUpUiEvent userSignUpUiEvent) {
            mMessageId = userSignUpUiEvent.getMessageId();
            mMessage = userSignUpUiEvent.getMessage();
            mStatus = userSignUpUiEvent.getStatus();
            mErrors = userSignUpUiEvent.getErrors();
        }

        public int getMessageId() {
            return mMessageId;
        }

        public void setMessageId(int messageId) {
            this.mMessageId = messageId;
        }

        public String getStatus() {
            return mStatus;
        }

        public void setStatus(String status) {
            this.mStatus = status;
        }

        public String getMessage() {
            return mMessage;
        }

        public void setMessage(String message) {
            this.mMessage = message;
        }

        public Map<String, String[]> getErrors() {
            return mErrors;
        }

        public void setErrors(Map<String, String[]> errors) {
            this.mErrors = errors;
        }
    }


    public void onEventAsync(UserSignUpNetworkMessage networkMessage) {
        synchronized (UserController.class) {
            UserSignUpUiEvent event;
            String response = networkMessage.mRawResponse;
            int statusCode = networkMessage.mStatusCode;
            if (statusCode == HttpManager.OK) {
                try {
                    event = UserParser.signUpParser(response, mContext);
                    if (event.getStatus().equals(SUCCESS)) {
                        MainUserDao.getInstance(mContext).setHasSignUp(true);
                        doLoginAsync(networkMessage.mTransactionId);
                        return;
                    }
                } catch (JSONException exp) {
                    event = new UserSignUpUiEvent(-1, FAIL,
                            mContext.getString(R.string.server_unknown_format), null);

                }
            } else {
                event = new UserSignUpUiEvent(-1, FAIL, mContext.getString(R.string.io_error), null);
            }
            for (Integer i : mSignUpRequestIds) {
                UserSignUpUiEvent postEvent = new UserSignUpUiEvent(event);
                postEvent.setMessageId(i);
                EventBus.getDefault().post(postEvent);
            }
            mSignUpRequestIds.clear();
        }
    }

    public void onEventAsync(UserLogInNetworkMessage networkMessage) {
        synchronized (UserController.class) {
            String response = networkMessage.mRawResponse;
            int statusCode = networkMessage.mStatusCode;
            UserLogInUiEvent event;
            if (statusCode == HttpManager.OK) {

                try {
                    event = UserParser.logInParser(response, mContext);
                    setToken(event.getToken());
                    MainUserDao.getInstance(mContext).setHasLogIn(true);

                } catch (JSONException exp) {
                    event = new UserLogInUiEvent(-1, FAIL,
                            mContext.getString(R.string.server_unknown_format), null);

                }
            } else {

                if (statusCode == 401) {
                    event = new UserLogInUiEvent(-1, FAIL,
                            mContext.getString(R.string.authentication_error), null);
                } else if (statusCode == 404) {
                    event = new UserLogInUiEvent(-1, FAIL,
                            mContext.getString(R.string.not_found_user), null);
                } else {
                    event = new UserLogInUiEvent(-1, FAIL,
                            mContext.getString(R.string.io_error), null);
                }

            }
            for (Integer i : mLogInRequestIds) {
                UserLogInUiEvent postEvent = new UserLogInUiEvent(event);
                postEvent.setMessageId(i);
                EventBus.getDefault().post(postEvent);
            }
            mLogInRequestIds.clear();
        }
    }



}