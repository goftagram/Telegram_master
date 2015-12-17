package com.goftagram.telegram.goftagram.application.usecases.signuplogin;

import android.content.Context;
import android.content.SharedPreferences;


import com.goftagram.telegram.goftagram.application.model.MainUser;
import com.goftagram.telegram.goftagram.application.model.User;

import de.greenrobot.event.EventBus;


public class MainUserDao {

    public static final String LOG_TAG = MainUserDao.class.getSimpleName();
    public static final boolean DEBUGE = true;


    private final Context mContext;
    private User mCurrentUser;
    private static MainUserDao sMainUserDao;
    private static boolean mHasLogIn;
    private static boolean mHasSignUp;

    private MainUserDao(Context context) {
        mContext = context;
        mCurrentUser = loadCurrentUser();
        mHasLogIn = loadHasLogIn();
        mHasSignUp = loadHasSignUp();
    }

    public static MainUserDao getInstance(Context context) {
        synchronized (UserController.class) {
            if (sMainUserDao == null) {
                sMainUserDao = new MainUserDao(context.getApplicationContext());
            }
            return sMainUserDao;
        }
    }

    private void storeCurrentUser(Context ctx, User user) {

        synchronized (UserController.class) {

            final SharedPreferences prefs = ctx.getSharedPreferences("User", 0);
            SharedPreferences.Editor editor = prefs.edit();
            String id = ((user.getServerId() == null) ? "" : user.getServerId());
            String phoneNumber = ((user.getPhoneNumber() == null) ? "" : user.getPhoneNumber());
            String firstName = ((user.getFirstName() == null) ? "" : user.getFirstName());
            String lastName = ((user.getLastName() == null) ? "" : user.getLastName());
            String userName = ((user.getUserName() == null) ? "" : user.getUserName());
            String email = ((user.getEmail() == null) ? "" : user.getEmail());
            String telegramId = ((user.getTelegramId() == null) ? "" : user.getTelegramId());
            String gmailAccount = ((user.getGmailAccount() == null) ? "" : user.getGmailAccount());
            String geo = ((user.getGeo() == null) ? "" : user.getGeo());
            String phoneModel = ((user.getPhoneModel() == null) ? "" : user.getPhoneModel());
            String password = ((user.getPassword() == null) ? "" : user.getPassword());
            String imei = ((user.getImei() == null) ? "" : user.getImei());

            editor.putString("id", id);
            editor.putString("phoneNumber", phoneNumber);
            editor.putString("firstName", firstName);
            editor.putString("lastName", lastName);
            editor.putString("userName", userName);
            editor.putString("email", email);
            editor.putString("telegramId", telegramId);
            editor.putString("gmailAccount", gmailAccount);
            editor.putString("geo", geo);
            editor.putString("phoneModel", phoneModel);
            editor.putString("password", password);
            editor.putString("imei", imei);
            editor.apply();
        }
    }

    private User loadCurrentUser() {

        synchronized (UserController.class) {
            final SharedPreferences prefs = mContext.getSharedPreferences("User", 0);
            String id = prefs.getString("id", "");
            String phoneNumber = prefs.getString("phoneNumber", "");
            String firstName = prefs.getString("firstName", "");
            String lastName = prefs.getString("lastName", "");
            String userName = prefs.getString("userName", "");
            String email = prefs.getString("email", "");
            String telegramId = prefs.getString("telegramId", "");
            String gmailAccount = prefs.getString("gmailAccount", "");
            String geo = prefs.getString("geo", "");
            String phoneModel = prefs.getString("phoneModel", "");
            String password = prefs.getString("password", "");
            String imei = prefs.getString("imei", "");

            User user = new User(imei,id, phoneNumber, firstName, lastName,
                    userName, email, telegramId, gmailAccount, geo, phoneModel, password);
            return user;
        }
    }

    private boolean loadHasLogIn() {
        synchronized (UserController.class) {
            final SharedPreferences prefs = mContext.getSharedPreferences("UserStatus", 0);
            return prefs.getBoolean("login", false);
        }
    }

    private void storeUserStatus() {

        final SharedPreferences prefs = mContext.getSharedPreferences("UserStatus", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("login", mHasLogIn);
        editor.putBoolean("signUp", mHasSignUp);
        editor.apply();
    }

    private boolean loadHasSignUp() {
        synchronized (UserController.class) {
            final SharedPreferences prefs = mContext.getSharedPreferences("UserStatus", 0);
            return prefs.getBoolean("signUp", false);
        }
    }



    void setHasSignUp(boolean hasSignUp) {
        synchronized (UserController.class) {
            mHasSignUp = hasSignUp;
            notifyMainUserUpdated();
            storeUserStatus();
        }

    }

    void setHasLogIn(boolean hasLogIn) {
        synchronized (UserController.class) {
//            Log.i(LOG_TAG, "setHasLogIn: " + hasLogIn);
            mHasLogIn = hasLogIn;
            notifyMainUserUpdated();
            storeUserStatus();
        }

    }

    void setId(String id) {
        synchronized (UserController.class) {
            mCurrentUser.setId(id);
            notifyMainUserUpdated();
        }
        storeUserField("id", id);

    }

    void setImei(String imei) {
        synchronized (UserController.class) {
            mCurrentUser.setImei(imei);
            notifyMainUserUpdated();
        }
        storeUserField("imei", imei);

    }

    void setPhoneNumber(String phoneNumber) {
        synchronized (UserController.class) {
            mCurrentUser.setPhoneNumber(phoneNumber);
            notifyMainUserUpdated();
        }
        storeUserField("phoneNumber", phoneNumber);
    }

    void setFirstName(String firstName) {
        synchronized (UserController.class) {
            mCurrentUser.setFirstName(firstName);
            notifyMainUserUpdated();
        }
        storeUserField("firstName", firstName);
    }

    void setLastName(String lastName) {
        synchronized (UserController.class) {
            mCurrentUser.setLastName(lastName);
            notifyMainUserUpdated();
        }
        storeUserField("lastName", lastName);
    }

    void setUserName(String userName) {
        synchronized (UserController.class) {
            mCurrentUser.setUserName(userName);
            notifyMainUserUpdated();
        }
        storeUserField("userName", userName);
    }

    void setEmail(String email) {
        synchronized (UserController.class) {
            mCurrentUser.setEmail(email);
            notifyMainUserUpdated();
        }
        storeUserField("email", email);
    }

    void setTelegramId(String telegramId) {
        synchronized (UserController.class) {
            mCurrentUser.setTelegramId(telegramId);
            notifyMainUserUpdated();
        }
        storeUserField("telegramId", telegramId);
    }

    void setGmailAccount(String gmailAccount) {
        synchronized (UserController.class) {
            mCurrentUser.setGmailAccount(gmailAccount);
            notifyMainUserUpdated();
        }
        storeUserField("gmailAccount", gmailAccount);

    }

    void setGeo(String geo) {
        synchronized (UserController.class) {
            mCurrentUser.setGeo(geo);
            notifyMainUserUpdated();
        }
        storeUserField("geo", geo);
    }

    void setPhoneModel(String phoneModel) {
        synchronized (UserController.class) {
            mCurrentUser.setPhoneModel(phoneModel);
            notifyMainUserUpdated();
        }
        storeUserField("phoneModel", phoneModel);
    }

    void setPassword(String password) {
        synchronized (UserController.class) {
            mCurrentUser.setPassword(password);
            notifyMainUserUpdated();
        }
        storeUserField("password", password);
    }


    private void storeUserField(String key, String value) {
        final SharedPreferences prefs = mContext.getSharedPreferences("User", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void notifyMainUserUpdated() {
        synchronized (UserController.class) {
            MainUser mainUser = createMainUser(mCurrentUser);
            EventBus.getDefault().post(new MainUserHasUpdatedMessage(mainUser));
        }
    }

    private MainUser createMainUser(User mCurrentUser) {
        synchronized (UserController.class) {
//            Log.i(LOG_TAG, "createMainUser "  + mHasLogIn);
            return new MainUser(mCurrentUser, mHasLogIn, mHasSignUp);
        }
    }

    public MainUser getMainUser() {
        return createMainUser(mCurrentUser);
    }

    class MainUserHasUpdatedMessage {
        private MainUser mMainUser;

        MainUserHasUpdatedMessage(MainUser mainUser) {
            mMainUser = mainUser;
        }

        public MainUser getMainUser() {
            return mMainUser;
        }

    };


}
