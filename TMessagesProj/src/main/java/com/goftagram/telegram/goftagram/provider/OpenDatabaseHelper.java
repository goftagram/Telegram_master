package com.goftagram.telegram.goftagram.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenDatabaseHelper extends SQLiteOpenHelper {

    private static OpenDatabaseHelper sInstance;

    private static final String DATABASE_NAME = "mydb.db";
    private static final int DATABASE_VERSION = 6;


    public static synchronized OpenDatabaseHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new OpenDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private OpenDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        try {
            db.beginTransaction();

            db.execSQL(SqlCommand.SQL_CREATE_CATEGORY_TABLE);
            db.execSQL(SqlCommand.SQL_CREATE_COMMENT_TABLE);
            db.execSQL(SqlCommand.SQL_CREATE_TAG_TABLE);
            db.execSQL(SqlCommand.SQL_CREATE_TELEGRAM_CHANNEL_TABLE);
            db.execSQL(SqlCommand.SQL_CREATE_TOP_RATED_TELEGRAM_CHANNEL);
            db.execSQL(SqlCommand.SQL_CREATE_PROMOTED_TELEGRAM_CHANNEL);
            db.execSQL(SqlCommand.SQL_CREATE_NEW_TELEGRAM_CHANNEL);
            db.execSQL(SqlCommand.SQL_CREATE_TELEGRAM_CHANNEL_TAG_TABLE);
            db.execSQL(SqlCommand.SQL_CREATE_TELEGRAM_CHANNEL_TELEGRAM_CHANNEL_TABLE);
            db.execSQL(SqlCommand.SQL_CREATE_SEARCHED_QUERY);
            db.execSQL(SqlCommand.SQL_CREATE_SEARCHED_QUERY_TELEGRAM_CHANNEL);




            db.setTransactionSuccessful();
        }
        finally{
            db.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + GoftagramContract.CategoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GoftagramContract.CommentEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GoftagramContract.TagEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GoftagramContract.TelegramChannelEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GoftagramContract.TopRatedTelegramChannelEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GoftagramContract.PromotedTelegramChannelEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GoftagramContract.NewTelegramChannelEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GoftagramContract.TelegramChannelTagEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GoftagramContract.TelegramChannelTelegramChannelEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GoftagramContract.SearchedQueryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GoftagramContract.TelegramChannelSearchedQueryEntry.TABLE_NAME);

        onCreate(db);


    }

    public void close(){
        this.close();
    }


}
