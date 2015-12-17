package com.goftagram.telegram.goftagram.parser;


import com.goftagram.telegram.goftagram.application.model.Comment;


import org.json.JSONException;
import org.json.JSONObject;

public class CommentParser {

    public static final String STATUS           = "status";
    public static final String SUCCESS          = "success";
    public static final String FAIL             = "fail";
    public static final String EXPIRE           = "expire";
    public static final String MESSAGE          = "message";




    public static final String ID = "id";
    public static final String COMMENT = "comment";
    public static final String USER = "user";
    public static final String USER_DATA = "data";
    public static final String CREATED_AT = "created_at";
    public static final String DATE = "updated_at";


    public static final int STATUS_CODE_SUCCESS     = 1;
    public static final int STATUS_CODE_FAIL        = 0;
    public static final int STATUS_CODE_EXPIRE      = 2;


    public static Comment parser(String jsonComment, String telegramChannelId) throws JSONException {
        Comment comment = parser(new JSONObject(jsonComment), telegramChannelId);
        return comment;
    }

    public static Comment parser(JSONObject jsonComment, String telegramChannelId) throws JSONException {

        Comment comment = new Comment();


        comment.setServerId(jsonComment.getString(ID));
        comment.setComment(jsonComment.getString(COMMENT));


        comment.setShamsiDate(jsonComment.getString(DATE));

        JSONObject userJson = jsonComment.getJSONObject(USER);
        JSONObject userDataJson = userJson.getJSONObject(USER_DATA);

        comment.setUserFirstName(UserParser.userCommentOwnerParser(userDataJson).getFirstName());
        comment.setUserLastName(UserParser.userCommentOwnerParser(userDataJson).getLastName());

        comment.setTelegramChannelId(telegramChannelId);
        return comment;
    }



    public static AddCommentResponse addCommentParser(String addCommentResponse) throws JSONException {

        int statusCode = 0;
        String message;

        JSONObject jsonResponse = new JSONObject(addCommentResponse);

        if (jsonResponse.getString(STATUS).equals(SUCCESS)) {

            statusCode = STATUS_CODE_SUCCESS;
            message = jsonResponse.getString(MESSAGE);


        } else if (jsonResponse.getString(STATUS).equals(EXPIRE)) {
            statusCode = STATUS_CODE_EXPIRE;
            message = jsonResponse.getString(MESSAGE);

        } else {
            statusCode = STATUS_CODE_FAIL;
            message = jsonResponse.getString(MESSAGE);
        }


        AddCommentResponse response = new AddCommentResponse();
        response.mStatusCode = statusCode;
        response.mMessage = message;

        return response;



    }


    public static ReportCommentResponse reportCommentParser(String reportCommentResponse) throws JSONException {

        int statusCode = 0;
        String message;

        JSONObject jsonResponse = new JSONObject(reportCommentResponse);

        if (jsonResponse.getString(STATUS).equals(SUCCESS)) {

            statusCode = STATUS_CODE_SUCCESS;
            message = jsonResponse.getString(MESSAGE);


        } else if (jsonResponse.getString(STATUS).equals(EXPIRE)) {
            statusCode = STATUS_CODE_EXPIRE;
            message = jsonResponse.getString(MESSAGE);

        } else {
            statusCode = STATUS_CODE_FAIL;
            message = jsonResponse.getString(MESSAGE);
        }


        ReportCommentResponse response = new ReportCommentResponse();
        response.mStatusCode = statusCode;
        response.mMessage = message;

        return response;



    }


    public static class ReportCommentResponse {
        public String mMessage;
        public int mStatusCode;
    }

    public static class AddCommentResponse {
        public String mMessage;
        public int mStatusCode;
    }
}
