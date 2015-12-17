package com.goftagram.telegram.goftagram.parser;


import com.goftagram.telegram.goftagram.application.model.Comment;
import com.goftagram.telegram.goftagram.application.model.Tag;
import com.goftagram.telegram.goftagram.application.model.TelegramChannel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChannelInfoParser {


    public static final String STATUS           = "status";
    public static final String SUCCESS          = "success";
    public static final String FAIL             = "fail";
    public static final String EXPIRE           = "expire";
    public static final String MESSAGE          = "message";
    public static final String RESPONSE_DATA    = "data";
    public static final String CHANNEL          = "channel";
    public static final String CHANNEL_DATA     = "data";
    public static final String ID               = "id";
    public static final String TITLE            = "title";
    public static final String DESCRIPTION      = "description";
    public static final String LINK             = "link";
    public static final String RATE             = "rate";
    public static final String THUMB            = "thumb";
    public static final String TAGS             = "tags";
    public static final String TAGS_DATA        = "data";
    public static final String COMMENTS         = "comments";
    public static final String COMMENTS_DATA    = "data";
    public static final String CATEGORY         = "category";
    public static final String CATEGORY_DATA    = "data";

    public static ChannelListParserResponse listParser(String categoryJson) throws JSONException {


        List<TelegramChannel> telegramChannels = null;
        int statusCode = 0;
        String message;

        JSONObject jsonResponse = new JSONObject(categoryJson);

        if(jsonResponse.getString(STATUS).equals(SUCCESS)){
            telegramChannels = new ArrayList<>();
            statusCode = 1;
            message = jsonResponse.getString(MESSAGE);
            JSONObject jsonData   = jsonResponse.getJSONObject(RESPONSE_DATA);
            JSONObject jsonChannels = jsonData.getJSONObject(CHANNEL);
            JSONArray data = jsonChannels.getJSONArray(CHANNEL_DATA);
            for(int i = 0 ; i < data.length();++i){
                JSONObject jsonChannel = data.getJSONObject(i);
                TelegramChannel telegramChannel = parser(jsonChannel);
                telegramChannels.add(telegramChannel);

            }
        }else if (jsonResponse.getString(STATUS).equals(EXPIRE)){
            statusCode = 2;
            message = jsonResponse.getString(MESSAGE);
        }else{
            statusCode = 0;
            message = jsonResponse.getString(MESSAGE);
        }
        ChannelListParserResponse response = new ChannelListParserResponse(message,statusCode,telegramChannels);
        return response;

    }

    public static TelegramChannel parser(String jsonCategory) throws JSONException {
        TelegramChannel telegramChannel = parser(new JSONObject(jsonCategory));
        return telegramChannel;
    }

    public static TelegramChannel parser(JSONObject jsonTelegramChannel) throws JSONException {

        TelegramChannel telegramChannel = new TelegramChannel();
        telegramChannel.setServerId(jsonTelegramChannel.getString(ID));
        telegramChannel.setTitle(jsonTelegramChannel.getString(TITLE));
        telegramChannel.setDescription(jsonTelegramChannel.getString(DESCRIPTION));
        telegramChannel.setLink(jsonTelegramChannel.getString(LINK));
        telegramChannel.setThumbnail(jsonTelegramChannel.getString(THUMB));
        telegramChannel.setRate(new Double(jsonTelegramChannel.getDouble(RATE)).floatValue());
        JSONObject jsonCategory = jsonTelegramChannel.getJSONObject(CATEGORY);
        telegramChannel.setCategory(CategoryParser.parser(jsonCategory.getJSONObject(CATEGORY_DATA)));

        JSONObject jsonTags = jsonTelegramChannel.getJSONObject(TAGS);
        JSONArray jsonTagArray = jsonTags.getJSONArray(TAGS_DATA);
        Tag[] tags = new Tag[jsonTagArray.length()];
        for(int i = 0 ; i < jsonTagArray.length();++i){
            JSONObject jsonTag = jsonTagArray.getJSONObject(i);
            tags[i] = TagParser.parser(jsonTag);
        }

        JSONObject jsonComments = jsonTelegramChannel.getJSONObject(COMMENTS);
        JSONArray jsonCommentArray = jsonComments.getJSONArray(COMMENTS_DATA);
        Comment[] comments = new Comment[jsonCommentArray.length()];
        for(int i = 0 ; i < jsonCommentArray.length();++i){
            JSONObject jsonComment = jsonCommentArray.getJSONObject(i);
            comments[i] = CommentParser.parser(jsonComment,telegramChannel.getServerId());
        }
        telegramChannel.setComment(comments);
        telegramChannel.setTags(tags);

        return telegramChannel;
    }

    public static class ChannelListParserResponse{
        public String mMessage;
        public int mStatusCode;
        public List<TelegramChannel> mTelegramChannelList;

        public ChannelListParserResponse(String message,int statusCode,List<TelegramChannel> telegramChannelList){
            mMessage = message;
            mStatusCode = statusCode;
            mTelegramChannelList = telegramChannelList;
        }

    }
}
