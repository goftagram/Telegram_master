package com.goftagram.telegram.goftagram.application.model;


import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String mServerId;
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

    public User(String imei,String serverId,String phoneNumber,String firstName,String lastName,String userName,String email,
                String telegramId,String gmailAccount,String geo,String phoneModel,String password){

        mServerId =   serverId;
        mPhoneNumber = phoneNumber;
        mFirstName = firstName;
        mLastName = lastName;
        mUserName = userName;
        mEmail = email;
        mTelegramId = telegramId;
        mGmailAccount = gmailAccount;
        mGeo = geo;
        mPhoneModel = phoneModel;
        mPassword = password;
        mImei = imei;
    }

    public User(){

    }

    public String getServerId() {return mServerId;}

    public void setId(String serverId) {
        this.mServerId = serverId;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getTelegramId() {
        return mTelegramId;
    }

    public void setTelegramId(String telegramId) {
        this.mTelegramId = telegramId;
    }

    public String getGmailAccount() {
        return mGmailAccount;
    }

    public void setGmailAccount(String gmailAccount) {
        this.mGmailAccount = gmailAccount;
    }

    public String getGeo() {
        return mGeo;
    }

    public void setGeo(String geo) {
        this.mGeo = geo;
    }

    public String getPhoneModel() {
        return mPhoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.mPhoneModel = phoneModel;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public String getImei() {
        return mImei;
    }

    public void setImei(String imei) {
        this.mImei = imei;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR
            = new Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(mServerId);
        dest.writeString(mPhoneNumber);
        dest.writeString(mFirstName);
        dest.writeString(mLastName);
        dest.writeString(mUserName);
        dest.writeString(mEmail);
        dest.writeString(mTelegramId);
        dest.writeString(mGmailAccount);
        dest.writeString(mGeo);
        dest.writeString(mPhoneModel);
        dest.writeString(mPassword);
        dest.writeString(mImei);
    }


    public User(Parcel in) {
        mServerId =   in.readString();
        mPhoneNumber = in.readString();
        mFirstName = in.readString();
        mLastName = in.readString();
        mUserName = in.readString();
        mEmail = in.readString();
        mTelegramId = in.readString();
        mGmailAccount = in.readString();
        mGeo = in.readString();
        mPhoneModel = in.readString();
        mPassword = in.readString();
        mImei = in.readString();
    }

}

