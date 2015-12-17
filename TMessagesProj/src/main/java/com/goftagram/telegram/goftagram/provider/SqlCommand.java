package com.goftagram.telegram.goftagram.provider;


import com.goftagram.telegram.goftagram.provider.GoftagramContract.*;

public class SqlCommand {



    public static final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " +
            CategoryEntry.TABLE_NAME + " (" +
            CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CategoryEntry.COLUMN_STATE + " TEXT , " +
            CategoryEntry.COLUMN_UPDATED + " REAL , " +
            CategoryEntry.COLUMN_SERVER_ID + " TEXT UNIQUE NOT NULL, " +
            CategoryEntry.COLUMN_THUMBNAIL + " TEXT , " +
            CategoryEntry.COLUMN_TITLE + " TEXT , " +
            CategoryEntry.COLUMN_TOTAL_CHANNELS + " INTEGER , " +
            CategoryEntry.COLUMN_ORDER + " INTEGER , " +
            CategoryEntry.COLUMN_ESTIMATED_TOTAL_CHANNELS + " INTEGER , " +
            CategoryEntry.COLUMN_PER_PAGE_ITEM + " INTEGER , " +
            " UNIQUE (" + CategoryEntry.COLUMN_SERVER_ID + ") ON CONFLICT REPLACE);";



    public static final String SQL_CREATE_COMMENT_TABLE = "CREATE TABLE " +
            CommentEntry.TABLE_NAME + " (" +
            CommentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CommentEntry.COLUMN_SERVER_ID + " TEXT UNIQUE NOT NULL, " +
            CommentEntry.COLUMN_STATE + " TEXT , " +
            CommentEntry.COLUMN_UPDATED + " REAL , " +
            CommentEntry.COLUMN_TELEGRAM_CHANNEL_ID + " TEXT , " +
            CommentEntry.COLUMN_SHAMSI_DATE + " TEXT , " +
            CommentEntry.COLUMN_USER_FIRST_NAME + " TEXT , " +
            CommentEntry.COLUMN_USER_LAST_NAME + " TEXT , " +
            CommentEntry.COLUMN_COMMENT + " TEXT , " +
            " UNIQUE (" + CommentEntry.COLUMN_SERVER_ID + ") ON CONFLICT REPLACE);";

    public static final String SQL_CREATE_TAG_TABLE = "CREATE TABLE " +
            TagEntry.TABLE_NAME + " (" +
            TagEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TagEntry.COLUMN_STATE + " TEXT , " +
            TagEntry.COLUMN_UPDATED + " REAL , " +
            TagEntry.COLUMN_SERVER_ID + " TEXT UNIQUE NOT NULL, " +
            TagEntry.COLUMN_NAME + " TEXT , " +
            TagEntry.COLUMN_TOTAL_TELEGRAM_CHANNEL + " INTEGER , " +
            " UNIQUE (" + TagEntry.COLUMN_SERVER_ID + " , " +
            TagEntry.COLUMN_NAME + ") ON CONFLICT REPLACE);";



    public static final String SQL_CREATE_TOP_RATED_TELEGRAM_CHANNEL = "CREATE TABLE " +
            TopRatedTelegramChannelEntry.TABLE_NAME + " (" +
            TopRatedTelegramChannelEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TopRatedTelegramChannelEntry.COLUMN_SERVER_ID + " TEXT UNIQUE NOT NULL, " +
            " UNIQUE (" + TopRatedTelegramChannelEntry.COLUMN_SERVER_ID +
            ") ON CONFLICT REPLACE);";

    public static final String SQL_CREATE_PROMOTED_TELEGRAM_CHANNEL = "CREATE TABLE " +
            PromotedTelegramChannelEntry.TABLE_NAME + " (" +
            PromotedTelegramChannelEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            PromotedTelegramChannelEntry.COLUMN_SERVER_ID + " TEXT UNIQUE NOT NULL, " +
            " UNIQUE (" + PromotedTelegramChannelEntry.COLUMN_SERVER_ID +
            ") ON CONFLICT REPLACE);";

        public static final String SQL_CREATE_NEW_TELEGRAM_CHANNEL = "CREATE TABLE " +
                NewTelegramChannelEntry.TABLE_NAME + " (" +
                NewTelegramChannelEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NewTelegramChannelEntry.COLUMN_SERVER_ID + " TEXT UNIQUE NOT NULL, " +
                " UNIQUE (" + NewTelegramChannelEntry.COLUMN_SERVER_ID +
                ") ON CONFLICT REPLACE);";


        public static final String SQL_CREATE_TELEGRAM_CHANNEL_TABLE = "CREATE TABLE " +
            TelegramChannelEntry.TABLE_NAME + " (" +
            TelegramChannelEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TelegramChannelEntry.COLUMN_STATE + " TEXT , " +
            TelegramChannelEntry.COLUMN_UPDATED + " REAL , " +
            TelegramChannelEntry.COLUMN_CATEGORY_ID + " TEXT , " +
            TelegramChannelEntry.COLUMN_SERVER_ID + " TEXT UNIQUE NOT NULL, " +
            TelegramChannelEntry.COLUMN_DESCRIPTION + " TEXT , " +
            TelegramChannelEntry.COLUMN_IMAGE + " TEXT , " +
            TelegramChannelEntry.COLUMN_LINK + " TEXT , " +
            TelegramChannelEntry.COLUMN_RATE + " REAL , " +
            TelegramChannelEntry.COLUMN_STAR_5 + " INTEGER , " +
            TelegramChannelEntry.COLUMN_STAR_4 + " INTEGER , " +
            TelegramChannelEntry.COLUMN_STAR_3 + " INTEGER , " +
            TelegramChannelEntry.COLUMN_STAR_2 + " INTEGER , " +
            TelegramChannelEntry.COLUMN_STAR_1 + " INTEGER , " +
            TelegramChannelEntry.COLUMN_RANK_IN_CATEGORY + " INTEGER , " +
            TelegramChannelEntry.COLUMN_THUMBNAIL + " TEXT  , " +
            TelegramChannelEntry.COLUMN_TITLE + " TEXT , " +
            TelegramChannelEntry.COLUMN_HAS_READ_ALL_COMMENTS + " INTEGER , " +

            " UNIQUE (" + TelegramChannelEntry.COLUMN_SERVER_ID + ") ON CONFLICT REPLACE);";



    public static final String SQL_CREATE_TELEGRAM_CHANNEL_TAG_TABLE = "CREATE TABLE " +
            TelegramChannelTagEntry.TABLE_NAME + " (" +
            TelegramChannelTagEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TelegramChannelTagEntry.COLUMN_STATE + " TEXT , " +
            TelegramChannelTagEntry.COLUMN_UPDATED + " REAL , " +
            TelegramChannelTagEntry.COLUMN_TAG_SERVER_ID + " TEXT , " +
            TelegramChannelTagEntry.COLUMN_TELEGRAM_CHANNEL_SERVER_ID + " TEXT, " +
            " UNIQUE (" + TelegramChannelTagEntry.COLUMN_TAG_SERVER_ID + "," +
            TelegramChannelTagEntry.COLUMN_TELEGRAM_CHANNEL_SERVER_ID + ") ON CONFLICT REPLACE);";




    public static final String SQL_CREATE_TELEGRAM_CHANNEL_TELEGRAM_CHANNEL_TABLE = "CREATE TABLE " +
            TelegramChannelTelegramChannelEntry.TABLE_NAME + " (" +
            TelegramChannelTelegramChannelEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TelegramChannelTelegramChannelEntry.COLUMN_STATE + " TEXT , " +
            TelegramChannelTelegramChannelEntry.COLUMN_UPDATED + " REAL , " +
            TelegramChannelTelegramChannelEntry.COLUMN_TELEGRAM_CHANNEL_ID_1 + " TEXT , " +
            TelegramChannelTelegramChannelEntry.COLUMN_TELEGRAM_CHANNEL_ID_2 + " TEXT,  " +
            " UNIQUE (" + TelegramChannelTelegramChannelEntry.COLUMN_TELEGRAM_CHANNEL_ID_1 + "," +
            TelegramChannelTelegramChannelEntry.COLUMN_TELEGRAM_CHANNEL_ID_2 + ") ON CONFLICT REPLACE);";



        public static final String SQL_CREATE_SEARCHED_QUERY = "CREATE TABLE " +

                SearchedQueryEntry.TABLE_NAME + " (" +
                SearchedQueryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SearchedQueryEntry.COLUMN_QUERY + " TEXT, " +
                SearchedQueryEntry.COLUMN_STATE + " TEXT , " +
                SearchedQueryEntry.COLUMN_UPDATED + " REAL , " +
                SearchedQueryEntry.COLUMN_TOTAL_TELEGRAM_CHANNEL + " TEXT , " +

                " UNIQUE (" + SearchedQueryEntry.COLUMN_QUERY  + ") ON CONFLICT REPLACE);";


        public static final String SQL_CREATE_SEARCHED_QUERY_TELEGRAM_CHANNEL = "CREATE TABLE " +

                TelegramChannelSearchedQueryEntry.TABLE_NAME + " (" +
                TelegramChannelSearchedQueryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TelegramChannelSearchedQueryEntry.COLUMN_STATE + " TEXT , " +
                TelegramChannelSearchedQueryEntry.COLUMN_UPDATED + " REAL , " +
                TelegramChannelSearchedQueryEntry.COLUMN_SEARCHED_QUERY + " TEXT , " +
                TelegramChannelSearchedQueryEntry.COLUMN_TELEGRAM_CHANNEL_SERVER_ID + " TEXT , " +

                " UNIQUE (" +

                TelegramChannelSearchedQueryEntry.COLUMN_SEARCHED_QUERY  + ","   +
                TelegramChannelSearchedQueryEntry.COLUMN_TELEGRAM_CHANNEL_SERVER_ID  +

                ") ON CONFLICT REPLACE);";






}
