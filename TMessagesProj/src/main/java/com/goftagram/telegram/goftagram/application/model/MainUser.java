package com.goftagram.telegram.goftagram.application.model;


public class MainUser {

    private String mId;
    private String mPhoneNumber;
    private String mFirstName;
    private String mLastName;
    private String mUserName;
    private String mEmail;
    private String mTelegramId;
    private String mGmailAccount;
    private String mGeo;
    private String mPhoneModel;
    private String mPassword;
    private String mImei;
    private static boolean mHasLogIn;
    private static boolean mHasSignUp;


    public MainUser(User user, boolean hasLogIn, boolean hasSignUp) {
        mId = user.getServerId();
        mPhoneNumber = user.getPhoneNumber();
        mFirstName = user.getFirstName();
        mLastName = user.getLastName();
        mUserName = user.getUserName();
        mEmail = user.getEmail();
        mTelegramId = user.getTelegramId();
        mGmailAccount = user.getGmailAccount();
        mGeo = user.getGeo();
        mPhoneModel = user.getPhoneModel();
        mPassword = user.getPassword();
        mImei = user.getImei();
        mHasLogIn = hasLogIn;
        mHasSignUp = hasSignUp;
    }

    public String getId() {
        return mId;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getTelegramId() {
        return mTelegramId;
    }

    public String getGmailAccount() {
        return mGmailAccount;
    }

    public String getGeo() {
        return mGeo;
    }

    public String getPhoneModel() {
        return mPhoneModel;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getImei() {
        return mImei;
    }

    public boolean hasLogIn() {
        return mHasLogIn;
    }

    public boolean hasSignUp() {
        return mHasSignUp;
    }

}
