package com.goftagram.telegram.goftagram.provider;

import android.content.ContentResolver;
import android.net.Uri;



public class GoftagramContract {

    public static final String SYNC_STATE_POSTING = "POSTING";
    public static final String SYNC_STATE_GETTING = "GETTING";
    public static final String SYNC_STATE_DELETING = "DELETING";
    public static final String SYNC_STATE_PUTTING = "PUTTING";
    public static final String SYNC_STATE_IDLE = "IDLE";

    public static final String SEEN_TYPE_IN_APP = "see_in_goftagram";
    public static final String SEEN_TYPE_IN_TELEGRAM = "see_in_telegram";


    interface SyncStateColumns {
        /**
         * REST API call state.
         */
        public static final String COLUMN_STATE = "state";
        /**
         * Last time this entry was updated or synchronized.
         */
        public static final String COLUMN_UPDATED = "updated";
    }

    public interface MyBaseColumns {
        public static final String _COUNT = "_count";
        public static final String _ID = "_id";
    }

    interface CommentColumns {
        public static final String COLUMN_SERVER_ID = "server_id";
        public static final String COLUMN_TELEGRAM_CHANNEL_ID = "telegram_channel_id";
        public static final String COLUMN_SHAMSI_DATE = "shamsi_date";
        public static final String COLUMN_COMMENT = "comment";
        public static final String COLUMN_USER_FIRST_NAME = "first_name";
        public static final String COLUMN_USER_LAST_NAME = "last_name";

    }

    interface CategoryColumns {
        public static final String COLUMN_SERVER_ID = "server_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_TOTAL_CHANNELS = "totalChannels";
        public static final String COLUMN_PER_PAGE_ITEM = "perPageItem";
        public static final String COLUMN_ORDER = "sortOrder";
        public static final String COLUMN_ESTIMATED_TOTAL_CHANNELS = "estimatedTotalChannels";

    }


    interface TagColumns {
        public static final String COLUMN_SERVER_ID = "tag_server_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TOTAL_TELEGRAM_CHANNEL = "total_telegram_channel";

    }


    interface TelegramChannelColumns {

        public static final String COLUMN_SERVER_ID = "server_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_RATE = "rate";

        public static final String COLUMN_STAR_5 = "star_5";
        public static final String COLUMN_STAR_4 = "star_4";
        public static final String COLUMN_STAR_3 = "star_3";
        public static final String COLUMN_STAR_2 = "star_2";
        public static final String COLUMN_STAR_1 = "star_1";

        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_CATEGORY_ID = "category";
        public static final String COLUMN_HAS_READ_ALL_COMMENTS = "has_read_all_comments";


        public static final String COLUMN_RANK_IN_CATEGORY = "category_rank";


    }



    interface TelegramChannelTagColumns {

        public static final String COLUMN_TELEGRAM_CHANNEL_SERVER_ID = "telegram_id";
        public static final String COLUMN_TAG_SERVER_ID = "tag_id";

    }


    interface SearchedQueryColumns {

        public static final String COLUMN_QUERY = "searched_query";
        public static final String COLUMN_TOTAL_TELEGRAM_CHANNEL = "total_telegram_channels";
    }

    interface TelegramChannelSearchedQueryColumns {

        public static final String COLUMN_SEARCHED_QUERY = "telegram_channel_searched_query";
        public static final String COLUMN_TELEGRAM_CHANNEL_SERVER_ID = "telegram_channel_server_id";
    }


    interface TelegramChannelTelegramChannelColumns {

        public static final String COLUMN_TELEGRAM_CHANNEL_ID_1 = "telegram_channel_id_1";
        public static final String COLUMN_TELEGRAM_CHANNEL_ID_2 = "telegram_channel_id_2";

    }



    interface TopRatedTelegramChannelColumns {
        public static final String COLUMN_SERVER_ID = "server_id";
    }

    interface PromotedTelegramChannelColumns {
        public static final String COLUMN_SERVER_ID = "server_id";
    }

    interface NewTelegramChannelColumns {
        public static final String COLUMN_SERVER_ID = "server_id";
    }

    interface GoftagramQueryParameter {
        public static final String QUERY_PARAMETER_TRANSACTION_ID = "transactionId";
        public static final String QUERY_PARAMETER_CONFLICT_FLAG = "conflictFlag";
        public static final String QUERY_PARAMETER_ID = "id";

    }

    public static final String CONTENT_AUTHORITY = "com.goftagram.telegram";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_ALL = "all";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_COMMENT = "comment";
    public static final String PATH_TAG = "tag";
    public static final String PATH_TELEGRAM_CHANNEL = "telegramChannel";
    public static final String PATH_TOP_RATED = "topRated";
    public static final String PATH_PROMOTED = "promoted";
    public static final String PATH_NEW = "new";
    public static final String PATH_DETAIL = "detail";
    public static final String PATH_ID = "id";
    public static final String PATH_SEARCHED_QUERY = "searchedQuery";
    public static final String PATH_SEARCHED_QUERY_TELEGRAM_CHANNEL = "searchedQueryTelegramChannel";

    public static final Uri ALL_URI = Uri.parse("content://" + CONTENT_AUTHORITY + "/" + PATH_ALL);


    public static class CommentEntry implements SyncStateColumns, GoftagramQueryParameter,
            MyBaseColumns, CommentColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMMENT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_COMMENT;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_COMMENT;

        public static final String TABLE_NAME = "comment";


        public static Uri buildCommentUri() {

            return CONTENT_URI
                    .buildUpon()
                    .build();
        }

        public static Uri buildCommentUri(int conflictFlag) {

            return CONTENT_URI
                    .buildUpon()
                    .appendQueryParameter(
                            QUERY_PARAMETER_CONFLICT_FLAG, String.valueOf(conflictFlag)
                    )
                    .build();
        }


    }


    public static class CategoryEntry implements SyncStateColumns, GoftagramQueryParameter,
            MyBaseColumns, CategoryColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_CATEGORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_CATEGORY;

        public static final String TABLE_NAME = "category";


        public static Uri buildCategoryByIdUri(String id) {

            return CONTENT_URI
                    .buildUpon()
                    .appendPath(PATH_ID)
                    .appendQueryParameter(QUERY_PARAMETER_ID, id)
                    .build();
        }

        public static Uri buildCategoryList() {

            return CONTENT_URI
                    .buildUpon()
                    .build();
        }

        public static Uri buildCategoryUriWithConflictFlag(int conflictFlag) {

            return CONTENT_URI
                    .buildUpon()
                    .appendQueryParameter(
                            QUERY_PARAMETER_CONFLICT_FLAG, String.valueOf(conflictFlag)
                    ).build();
        }

        public static Uri buildCategoryUriWithConflictFlag(String id, int conflictFlag) {

            return CONTENT_URI
                    .buildUpon()
                    .appendPath(PATH_ID)
                    .appendQueryParameter(QUERY_PARAMETER_ID, id)
                    .appendQueryParameter(
                            QUERY_PARAMETER_CONFLICT_FLAG, String.valueOf(conflictFlag)
                    ).build();
        }

        public static Uri buildCategoryUriWithTransactionId(String id, long transactionId) {

            return CONTENT_URI
                    .buildUpon()
                    .appendPath(PATH_ID)
                    .appendQueryParameter(QUERY_PARAMETER_ID, id)
                    .appendQueryParameter(
                            QUERY_PARAMETER_TRANSACTION_ID, String.valueOf(transactionId)
                    ).build();
        }

        public static Uri buildCategoryUriWithTransactionId(long transactionId) {

            return CONTENT_URI
                    .buildUpon()
                    .appendQueryParameter(
                            QUERY_PARAMETER_TRANSACTION_ID, String.valueOf(transactionId)
                    ).build();
        }
    }


    public static class TagEntry implements SyncStateColumns,
            GoftagramQueryParameter, MyBaseColumns, TagColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TAG).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_TAG;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_TAG;

        public static final String TABLE_NAME = "tag";

        public static final String QUERY_PARAMETER_TAG_ID = "tagId";


        public static Uri buildTagUri() {

            return CONTENT_URI
                    .buildUpon()
                    .build();
        }

        public static Uri buildTagUri(int conflictFlag) {

            return CONTENT_URI
                    .buildUpon()
                    .appendQueryParameter(
                            QUERY_PARAMETER_CONFLICT_FLAG, String.valueOf(conflictFlag)
                    )
                    .build();
        }


        public static Uri buildTelegramChannelOfTagUri(String tagId) {

            return CONTENT_URI
                    .buildUpon()
                    .appendPath(PATH_TELEGRAM_CHANNEL)
                    .appendQueryParameter(QUERY_PARAMETER_TAG_ID, tagId)
                    .build();
        }


    }


    public static class TelegramChannelEntry implements SyncStateColumns,
            GoftagramQueryParameter, MyBaseColumns, TelegramChannelColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TELEGRAM_CHANNEL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_TELEGRAM_CHANNEL;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_TELEGRAM_CHANNEL;

        public static final String TABLE_NAME = "telegram_channel";


        public static Uri buildTelegramChannel() {

            return CONTENT_URI
                    .buildUpon()
                    .build();
        }

        public static Uri buildTelegramChannel(int conflictFlag) {

            return CONTENT_URI
                    .buildUpon()
                    .appendQueryParameter(
                            QUERY_PARAMETER_CONFLICT_FLAG, String.valueOf(conflictFlag)
                    ).build();
        }


        public static Uri buildTelegramChannelCategoryUri() {

            return CONTENT_URI
                    .buildUpon().appendPath(PATH_CATEGORY)
                    .build();
        }


        public static Uri buildTelegramChannelDetail() {

            return CONTENT_URI
                    .buildUpon()
                    .appendPath(PATH_DETAIL)
                    .build();
        }


        public static Uri buildTelegramChannelTagUri() {

            return CONTENT_URI
                    .buildUpon()
                    .appendPath(PATH_DETAIL)
                    .appendPath(PATH_TAG)
                    .build();
        }


    }

    public static class TopRatedTelegramChannelEntry implements SyncStateColumns,
            GoftagramQueryParameter, MyBaseColumns, TopRatedTelegramChannelColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon()
                        .appendPath(PATH_TELEGRAM_CHANNEL)
                        .appendPath(PATH_TOP_RATED)
                        .build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_TELEGRAM_CHANNEL + PATH_TOP_RATED;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_TELEGRAM_CHANNEL + PATH_TOP_RATED;

        public static final String TABLE_NAME = "top_rated_telegram_channel";

        public static Uri buildTelegramChannelList() {

            return CONTENT_URI
                    .buildUpon()
                    .build();
        }
    }


    public static class PromotedTelegramChannelEntry implements SyncStateColumns,
            GoftagramQueryParameter, MyBaseColumns, PromotedTelegramChannelColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon()
                        .appendPath(PATH_TELEGRAM_CHANNEL)
                        .appendPath(PATH_PROMOTED)
                        .build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_TELEGRAM_CHANNEL + PATH_PROMOTED;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_TELEGRAM_CHANNEL + PATH_PROMOTED;

        public static final String TABLE_NAME = "promoted_telegram_channel";

        public static Uri buildTelegramChannelList() {

            return CONTENT_URI
                    .buildUpon()
                    .build();
        }


    }



    public static class NewTelegramChannelEntry implements SyncStateColumns,
            GoftagramQueryParameter, MyBaseColumns, NewTelegramChannelColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon()
                        .appendPath(PATH_TELEGRAM_CHANNEL)
                        .appendPath(PATH_NEW)
                        .build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_TELEGRAM_CHANNEL + PATH_NEW;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_TELEGRAM_CHANNEL + PATH_NEW;

        public static final String TABLE_NAME = "new_telegram_channel";

        public static Uri buildTelegramChannelList() {

            return CONTENT_URI
                    .buildUpon()
                    .build();
        }
    }



    public static class TelegramChannelTagEntry implements SyncStateColumns,
            GoftagramQueryParameter, MyBaseColumns, TelegramChannelTagColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TAG).appendPath(PATH_TELEGRAM_CHANNEL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_TAG + PATH_TELEGRAM_CHANNEL;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_TAG + PATH_TELEGRAM_CHANNEL;

        public static final String TABLE_NAME = "telegram_channel_tag";

        public static Uri buildTelegramChannelTagUri(int conflictFlag) {

            return CONTENT_URI
                    .buildUpon()
                    .appendQueryParameter(
                            QUERY_PARAMETER_CONFLICT_FLAG, String.valueOf(conflictFlag)
                    )
                    .build();
        }
    }

    public static class TelegramChannelTelegramChannelEntry implements SyncStateColumns,
            MyBaseColumns, GoftagramQueryParameter, TelegramChannelTelegramChannelColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TELEGRAM_CHANNEL)
                        .appendPath(PATH_TELEGRAM_CHANNEL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_TELEGRAM_CHANNEL + PATH_TELEGRAM_CHANNEL;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_TELEGRAM_CHANNEL + PATH_TELEGRAM_CHANNEL;

        public static final String TABLE_NAME = "telegram_channel_telegram_channel";

        public static Uri buildTelegramChannelTelegramChannelUri(int conflictFlag) {

            return CONTENT_URI
                    .buildUpon()
                    .appendQueryParameter(
                            QUERY_PARAMETER_CONFLICT_FLAG, String.valueOf(conflictFlag)
                    )
                    .build();
        }

        public static Uri buildTelegramChannelTelegramChannelUri() {

            return CONTENT_URI
                    .buildUpon()
                    .build();
        }


    }

    public static class SearchedQueryEntry implements SyncStateColumns,
            GoftagramQueryParameter, MyBaseColumns, SearchedQueryColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCHED_QUERY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_SEARCHED_QUERY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_SEARCHED_QUERY;

        public static final String TABLE_NAME = "searched_query";

        public static final String QUERY_PARAMETER_QUERY = "query";


        public static Uri buildSearchedQueryUri() {

            return CONTENT_URI
                    .buildUpon()
                    .build();
        }

        public static Uri buildSearchedQueryUri(int conflictFlag) {

            return CONTENT_URI
                    .buildUpon()
                    .appendQueryParameter(
                            QUERY_PARAMETER_CONFLICT_FLAG, String.valueOf(conflictFlag)
                    )
                    .build();
        }


        public static Uri buildTelegramChannelOfSearchedQueryUri(String searchedQuery) {

            return CONTENT_URI
                    .buildUpon()
                    .appendPath(PATH_TELEGRAM_CHANNEL)
                    .appendQueryParameter(QUERY_PARAMETER_QUERY, searchedQuery)
                    .build();
        }


        public static Uri buildTelegramChannelOfSearchedQueryUri(int conflictFlag) {

            return CONTENT_URI
                    .buildUpon()
                    .appendPath(PATH_TELEGRAM_CHANNEL)
                    .appendQueryParameter(
                            QUERY_PARAMETER_CONFLICT_FLAG, String.valueOf(conflictFlag)
                    )
                    .build();
        }

    }


    public static class TelegramChannelSearchedQueryEntry implements SyncStateColumns,
            GoftagramQueryParameter, MyBaseColumns, TelegramChannelSearchedQueryColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCHED_QUERY_TELEGRAM_CHANNEL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_SEARCHED_QUERY_TELEGRAM_CHANNEL;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_SEARCHED_QUERY_TELEGRAM_CHANNEL;

        public static final String TABLE_NAME = "searchQuery_telegramChannel";


    }


}
