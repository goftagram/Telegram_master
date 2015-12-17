package com.goftagram.telegram.goftagram.parser;

import com.goftagram.telegram.goftagram.application.model.Comment;
import com.goftagram.telegram.goftagram.application.model.Tag;
import com.goftagram.telegram.goftagram.application.model.TelegramChannel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TelegramChannelParser {


    public static final String STATUS = "status";
    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";
    public static final String EXPIRE = "expire";
    public static final String MESSAGE = "message";
    public static final String RESPONSE_DATA = "data";
    public static final String CHANNELS = "channels";
    public static final String CHANNELS_DATA = "data";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String LINK = "link";
    public static final String RATE = "rate";
    public static final String THUMB = "thumb";
    public static final String THUMB_PATH = "path";
    public static final String CATEGORY = "category";
    public static final String CATEGORY_ID = "id";
    public static final String CATEGORY_DATA = "data";


    public static final String CHANNEL = "channel";
    public static final String CHANNEL_DATA = "data";


    public static final String IMAGE = "img";
    public static final String IMAGE_PATH = "path";


    public static final String OWNER = "owner";
    public static final String OWNER_DATA = "data";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";

    public static final String TAGS = "tags";
    public static final String TAGS_DATA = "data";

    public static final String COMMENTS = "comments";
    public static final String COMMENTS_DATA = "data";


    public static final String RELATED_CHANNELS = "related_channels";
    public static final String RELATED_CHANNEL_DATA = "data";


    public static final String PAGINATION = "pagination";
    public static final String PAGINATION_TOTAL = "total";
    public static final String PAGINATION_PER_PAGE = "per_page";
    public static final String PAGINATION_LAST_PAGE = "last_page";


    public static final String RATES = "rates";
    public static final String STAR_5 = "r5";
    public static final String STAR_4 = "r4";
    public static final String STAR_3 = "r3";
    public static final String STAR_2 = "r2";
    public static final String STAR_1 = "r1";


    public static final int STATUS_CODE_SUCCESS = 1;
    public static final int STATUS_CODE_FAIL = 0;
    public static final int STATUS_CODE_EXPIRE = 2;


    public static TelegramChannelListParserResponse listParser(String TelegramChannelJson) throws JSONException {


        List<TelegramChannel> telegramChannels = null;
        int statusCode = 0;
        String message;
        boolean hasPagination = false;
        int total = 1;
        int perPage = 1;
        int lastPage = 1;


        JSONObject jsonResponse = new JSONObject(TelegramChannelJson);

        if (jsonResponse.getString(STATUS).equals(SUCCESS)) {
            telegramChannels = new ArrayList<>();
            statusCode = STATUS_CODE_SUCCESS;
            message = jsonResponse.getString(MESSAGE);
            JSONObject jsonData = jsonResponse.getJSONObject(RESPONSE_DATA);

            if (jsonData.has(CHANNELS)) {

                JSONObject jsonChannels = jsonData.getJSONObject(CHANNELS);
                JSONArray data = jsonChannels.getJSONArray(CHANNELS_DATA);
                for (int i = 0; i < data.length(); ++i) {
                    JSONObject jsonChannel = data.getJSONObject(i);
                    TelegramChannel telegramChannel = parser(jsonChannel);
                    telegramChannels.add(telegramChannel);

                }
                if (jsonData.has(PAGINATION)) {
                    hasPagination = true;
                    JSONObject paginationJsonObject = jsonData.getJSONObject(PAGINATION);
                    total = paginationJsonObject.getInt(PAGINATION_TOTAL);
                    perPage = paginationJsonObject.getInt(PAGINATION_PER_PAGE);
                    lastPage = paginationJsonObject.getInt(PAGINATION_LAST_PAGE);

                } else {
                    hasPagination = false;
                    total = telegramChannels.size();
                    perPage = telegramChannels.size();
                    lastPage = 1;
                }

            } else {
                statusCode = STATUS_CODE_FAIL;
                message = jsonResponse.getString(MESSAGE);
            }

        } else if (jsonResponse.getString(STATUS).equals(EXPIRE)) {
            statusCode = STATUS_CODE_EXPIRE;
            message = jsonResponse.getString(MESSAGE);
        } else {
            statusCode = STATUS_CODE_FAIL;
            message = jsonResponse.getString(MESSAGE);
        }
        TelegramChannelListParserResponse response = null;
        if (hasPagination) {
            response = new TelegramChannelListParserResponse(
                    message, statusCode, telegramChannels, total, perPage, lastPage);
        } else {
            response = new TelegramChannelListParserResponse(
                    message, statusCode, telegramChannels);
        }

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

        if (jsonTelegramChannel.has(THUMB)) {
            JSONObject jsonImage = jsonTelegramChannel.getJSONObject(THUMB);
            telegramChannel.setThumbnail(jsonImage.getString(THUMB_PATH));
        }

        if (jsonTelegramChannel.has(RATES)) {
            JSONObject jsonObjectRates = jsonTelegramChannel.getJSONObject(RATES);
            if (jsonObjectRates != null) {
                telegramChannel.setStar_5(jsonObjectRates.getInt(STAR_5));
                telegramChannel.setStar_4(jsonObjectRates.getInt(STAR_4));
                telegramChannel.setStar_3(jsonObjectRates.getInt(STAR_3));
                telegramChannel.setStar_2(jsonObjectRates.getInt(STAR_2));
                telegramChannel.setStar_1(jsonObjectRates.getInt(STAR_1));
            }
        }

        if (jsonTelegramChannel.has(IMAGE)) {
            JSONObject jsonImage = jsonTelegramChannel.getJSONObject(IMAGE);
            telegramChannel.setImage(jsonImage.getString(IMAGE_PATH));
        }
        telegramChannel.setRate(new Double(jsonTelegramChannel.getDouble(RATE)).floatValue());

        if (jsonTelegramChannel.has(CATEGORY)) {
            JSONObject jsonObject = jsonTelegramChannel.getJSONObject(CATEGORY);
            JSONObject jsonData = jsonObject.getJSONObject(CATEGORY_DATA);
            telegramChannel.setCategory(CategoryParser.parser(jsonData));
        }

        return telegramChannel;
    }

    public static TelegramChannelMetaDataParserResponse metaParser(String TelegramChannelInfoJson)
            throws JSONException {

        TelegramChannel telegramChannel = null;
        int statusCode = 0;
        String message;

        JSONObject jsonResponse = new JSONObject(TelegramChannelInfoJson);

        if (jsonResponse.getString(STATUS).equals(SUCCESS)) {
            telegramChannel = new TelegramChannel();
            statusCode = STATUS_CODE_SUCCESS;
            message = jsonResponse.getString(MESSAGE);
            JSONObject jsonData = jsonResponse.getJSONObject(RESPONSE_DATA);

            if (jsonData.has(CHANNEL)) {

                JSONObject jsonChannel = jsonData.getJSONObject(CHANNEL);
                JSONObject jsonChannelData = jsonChannel.getJSONObject(CHANNEL_DATA);

                telegramChannel = parser(jsonChannelData);

                JSONObject tagJsonObject = jsonChannelData.getJSONObject(TAGS);
                JSONArray tagJsonArray = tagJsonObject.getJSONArray(TAGS_DATA);
                Tag[] tagArrays = new Tag[tagJsonArray.length()];
                for (int i = 0; i < tagJsonArray.length(); ++i) {
                    JSONObject tagJson = tagJsonArray.getJSONObject(i);
                    Tag tag = TagParser.parser(tagJson);
                    tagArrays[i] = tag;
                }
                telegramChannel.setTags(tagArrays);


                JSONObject commentJsonObject = jsonChannelData.getJSONObject(COMMENTS);
                JSONArray commentsJsonArray = commentJsonObject.getJSONArray(COMMENTS_DATA);
                Comment[] commentArrays = new Comment[commentsJsonArray.length()];
                for (int i = 0; i < commentsJsonArray.length(); ++i) {
                    JSONObject commentJson = commentsJsonArray.getJSONObject(i);
                    Comment comment = CommentParser.parser(commentJson, telegramChannel.getServerId());
                    commentArrays[i] = comment;
                }
                telegramChannel.setComment(commentArrays);


                if (jsonData.has(RELATED_CHANNELS)) {

                    JSONObject relatedChannels = jsonData.getJSONObject(RELATED_CHANNELS);
                    JSONArray relatedChannelArrays = relatedChannels.getJSONArray(RELATED_CHANNEL_DATA);

                    TelegramChannel[] telegramChannelArrays
                            = new TelegramChannel[relatedChannelArrays.length()];

                    for (int i = 0; i < relatedChannelArrays.length(); ++i) {
                        JSONObject relatedTelegramChannelJsonObject
                                = relatedChannelArrays.getJSONObject(i);
                        TelegramChannel relatedTelegramChannel
                                = TelegramChannelParser.parser(relatedTelegramChannelJsonObject);
                        telegramChannelArrays[i] = relatedTelegramChannel;
                    }
                    telegramChannel.setRelatedChannel(telegramChannelArrays);
                }
            } else {
                statusCode = STATUS_CODE_FAIL;
                message = jsonResponse.getString(MESSAGE);
            }


        } else if (jsonResponse.getString(STATUS).equals(EXPIRE)) {
            statusCode = STATUS_CODE_EXPIRE;
            message = jsonResponse.getString(MESSAGE);
        } else {
            statusCode = STATUS_CODE_FAIL;
            message = jsonResponse.getString(MESSAGE);
        }
        TelegramChannelMetaDataParserResponse response = new TelegramChannelMetaDataParserResponse(
                message, statusCode, telegramChannel);


        return response;

    }


    public static TelegramChannelRateParserResponse rateParser(String TelegramChannelInfoJson)
            throws JSONException {

        int statusCode = 0;
        String message;
        float rate = 0;
        int star_1 = 0;
        int star_2 = 0;
        int star_3 = 0;
        int star_4 = 0;
        int star_5 = 0;

        JSONObject jsonResponse = new JSONObject(TelegramChannelInfoJson);

        if (jsonResponse.getString(STATUS).equals(SUCCESS)) {
            statusCode = STATUS_CODE_SUCCESS;
            message = jsonResponse.getString(MESSAGE);
            JSONObject jsonData = jsonResponse.getJSONObject(RESPONSE_DATA);
            rate = Double.valueOf(jsonData.getDouble(RATE)).floatValue();

            star_1 = jsonData.getJSONObject(RATES).getInt(STAR_1);
            star_2 = jsonData.getJSONObject(RATES).getInt(STAR_2);
            star_3 = jsonData.getJSONObject(RATES).getInt(STAR_3);
            star_4 = jsonData.getJSONObject(RATES).getInt(STAR_4);
            star_5 = jsonData.getJSONObject(RATES).getInt(STAR_5);




        } else if (jsonResponse.getString(STATUS).equals(EXPIRE)) {
            statusCode = STATUS_CODE_EXPIRE;
            message = jsonResponse.getString(MESSAGE);
        } else {
            statusCode = STATUS_CODE_FAIL;
            message = jsonResponse.getString(MESSAGE);
        }

        TelegramChannelRateParserResponse response = new TelegramChannelRateParserResponse();
        response.mMessage = message;
        response.mStatusCode = statusCode;
        response.mRate = rate;

        response.mStar_1 = star_1;
        response.mStar_2 = star_2;
        response.mStar_3 = star_3;
        response.mStar_4 = star_4;
        response.mStar_5 = star_5;


        return response;

    }

    public static TelegramChannelReportParserResponse reportParser(String TelegramChannelReport)
            throws JSONException {

        int statusCode = 0;
        String message;


        JSONObject jsonResponse = new JSONObject(TelegramChannelReport);

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

        TelegramChannelReportParserResponse response = new TelegramChannelReportParserResponse();
        response.mMessage = message;
        response.mStatusCode = statusCode;


        return response;

    }


    public static class TelegramChannelListParserResponse {
        public String mMessage;
        public int mStatusCode;
        public List<TelegramChannel> mTelegramChannelList;
        public boolean mHasPagination;
        public int mTotal;
        public int mPerPage;
        public int mLastPage;

        public TelegramChannelListParserResponse(
                String message, int statusCode,
                List<TelegramChannel> telegramChannelList) {
            mMessage = message;
            mStatusCode = statusCode;
            mTelegramChannelList = telegramChannelList;
            mHasPagination = false;

        }

        public TelegramChannelListParserResponse(
                String message, int statusCode,
                List<TelegramChannel> telegramChannelList, int total, int perPage, int lastPage) {
            mMessage = message;
            mStatusCode = statusCode;
            mTelegramChannelList = telegramChannelList;
            mHasPagination = true;
            mTotal = total;
            mPerPage = perPage;
            mLastPage = lastPage;
        }
    }

    public static class TelegramChannelMetaDataParserResponse {
        public String mMessage;
        public int mStatusCode;
        public TelegramChannel mTelegramChannel;

        public TelegramChannelMetaDataParserResponse(
                String message, int statusCode, TelegramChannel telegramChannel
        ) {
            mMessage = message;
            mStatusCode = statusCode;
            mTelegramChannel = telegramChannel;
        }
    }

    public static class TelegramChannelRateParserResponse{
        public String mMessage;
        public int mStatusCode;
        public float mRate;
        public int mStar_1;
        public int mStar_2;
        public int mStar_3;
        public int mStar_4;
        public int mStar_5;
    }

    public static class TelegramChannelReportParserResponse{
        public String mMessage;
        public int mStatusCode;

    }
}
