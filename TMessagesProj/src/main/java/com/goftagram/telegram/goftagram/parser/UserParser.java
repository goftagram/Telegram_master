package com.goftagram.telegram.goftagram.parser;


import android.content.Context;

import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.application.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserParser {

    public static final String STATUS = "status";
    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";
    public static final String EXPIRE = "expire";

    public static final String USER_SIGN_UP_DATA = "data";
    public static final String USER_SIGN_UP_MESSAGE = "message";
    public static final String USER_SIGN_UP_ERRORS = "errors";
    public static final String USER_SIGN_UP_PHONE = "phone";
    public static final String USER_SIGN_UP_FIRST_NAME = "first_name";
    public static final String USER_SIGN_UP_LAST_NAME = "last_name";
    public static final String USER_SIGN_UP_IMEI = "imei";
    public static final String USER_SIGN_UP_ID = "_id";
    public static final String USER_SIGN_UP_PASSWORD = "password";

    public static final String USER_LOG_IN_DATA = "data";
    public static final String USER_LOG_IN_MESSAGE = "message";
    public static final String USER_LOG_IN_TOKEN = "token";

    public static final String USER_METADATA_DATA = "data";
    public static final String USER_METADATA_MESSAGE = "message";


    public static final String USER_COMMENT_OWNER_ID = "id";
    public static final String USER_COMMENT_OWNER_TELEGRAM_ID = "telegram_id";
    public static final String USER_COMMENT_OWNER_FIRST_NAME = "first_name";
    public static final String USER_COMMENT_OWNER_LAST_NAME = "last_name";
    public static final String USER_COMMENT_OWNER_USERNAME = "username";


    public static User userCommentOwnerParser(String jsonUser) throws JSONException {
        User user = userCommentOwnerParser(new JSONObject(jsonUser));
        return user;
    }

    public static User userCommentOwnerParser(JSONObject jsonUser) throws JSONException {

        User user = new User();
        user.setFirstName(jsonUser.getString(USER_COMMENT_OWNER_FIRST_NAME));
        user.setLastName(jsonUser.getString(USER_COMMENT_OWNER_LAST_NAME));
        return user;
    }

    public static UserController.UserSignUpUiEvent signUpParser(String response, Context context)
            throws JSONException {

        JSONObject jsonResponse = new JSONObject(response);
        UserController.UserSignUpUiEvent event = null;

        String message = jsonResponse.getString(USER_SIGN_UP_MESSAGE);
        String status = jsonResponse.getString(STATUS);
        String id = "";
        String password = "";


        if (status.equals(SUCCESS)) {
            JSONObject dataJsonObject = jsonResponse.getJSONObject(USER_SIGN_UP_DATA);
            id = dataJsonObject.getString(USER_SIGN_UP_ID);
            password = dataJsonObject.getString(USER_SIGN_UP_PASSWORD);
            UserController.getInstance(context).updateMainUserPassword(password);
            UserController.getInstance(context).updateMainUserId(id);
            event = new UserController.UserSignUpUiEvent(-1, UserController.SUCCESS, message, null);

        } else if (status.equals(FAIL)) {
            JSONObject dataJsonObject = jsonResponse.getJSONObject(USER_SIGN_UP_DATA);
            JSONObject errorJsonObject = dataJsonObject.getJSONObject(USER_SIGN_UP_ERRORS);
            String[] phoneErrors = null;
            String[] firstNameErrors = null;
            String[] lastNameErrors = null;
            Map<String, String[]> errors = new HashMap<>();

            if (errorJsonObject.has(USER_SIGN_UP_PHONE)) {
                JSONArray phoneErrorsJsonArray = errorJsonObject.getJSONArray(USER_SIGN_UP_PHONE);
                phoneErrors = new String[phoneErrorsJsonArray.length()];
                for (int i = 0; i < phoneErrorsJsonArray.length(); ++i) {
                    phoneErrors[i] = phoneErrorsJsonArray.getString(i);
                }
                errors.put(UserController.UserSignUpUiEvent.PHONE_ERROR_KEY, phoneErrors);
            }

            if (errorJsonObject.has(USER_SIGN_UP_FIRST_NAME)) {
                JSONArray firstNameErrorsJsonArray = errorJsonObject.getJSONArray(USER_SIGN_UP_FIRST_NAME);
                firstNameErrors = new String[firstNameErrorsJsonArray.length()];
                for (int i = 0; i < firstNameErrorsJsonArray.length(); ++i) {
                    firstNameErrors[i] = firstNameErrorsJsonArray.getString(i);
                }
                errors.put(UserController.UserSignUpUiEvent.FIRST_NAME_ERROR_KEY, firstNameErrors);
            }


            if (errorJsonObject.has(USER_SIGN_UP_LAST_NAME)) {
                JSONArray lastNameErrorsJsonArray = errorJsonObject.getJSONArray(USER_SIGN_UP_LAST_NAME);
                lastNameErrors = new String[lastNameErrorsJsonArray.length()];
                for (int i = 0; i < lastNameErrorsJsonArray.length(); ++i) {
                    lastNameErrors[i] = lastNameErrorsJsonArray.getString(i);
                }
                errors.put(UserController.UserSignUpUiEvent.LAST_NAME_ERROR_KEY, lastNameErrors);
            }

            if (errorJsonObject.has(USER_SIGN_UP_IMEI)) {
                JSONArray lastNameErrorsJsonArray = errorJsonObject.getJSONArray(USER_SIGN_UP_IMEI);
                lastNameErrors = new String[lastNameErrorsJsonArray.length()];
                for (int i = 0; i < lastNameErrorsJsonArray.length(); ++i) {
                    lastNameErrors[i] = lastNameErrorsJsonArray.getString(i);
                }
                errors.put(UserController.UserSignUpUiEvent.IMEI_ERROR_KEY, lastNameErrors);
            }
            event = new UserController.UserSignUpUiEvent(-1, UserController.FAIL, message, errors);
        }

        return event;

    }

    public static UserController.UserLogInUiEvent logInParser(String response,Context context)
            throws JSONException {

        JSONObject jsonResponse = new JSONObject(response);
        UserController.UserLogInUiEvent event = null;

        String message = jsonResponse.getString(USER_LOG_IN_MESSAGE);
        String status = jsonResponse.getString(STATUS);
        String token = "";

        if (status.equals(SUCCESS)) {

            JSONObject dataJsonObject = jsonResponse.getJSONObject(USER_LOG_IN_DATA);
            token = dataJsonObject.getString(USER_LOG_IN_TOKEN);
            event = new UserController.UserLogInUiEvent(-1,UserController.SUCCESS,message,token);

        } else if (status.equals(FAIL)) {
            event = new UserController.UserLogInUiEvent(-1,UserController.FAIL,message,token);
        }
        return event;

    }


    public static String userMetaDataParser(String response) throws JSONException {

        JSONObject jsonResponse = new JSONObject(response);

        String status = jsonResponse.getString(STATUS);


        if (status.equals(SUCCESS)) {

           return SUCCESS;

        } else if (status.equals(FAIL)) {
            return FAIL;
        }
        return FAIL;

    }


}



