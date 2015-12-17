package com.goftagram.telegram.goftagram.application.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.provider.GoftagramContract.SearchedQueryEntry;

/**
 * Created by WORK on 11/9/2015.
 */
public class Query implements Parcelable{

    private long _id;
    private String mQuery;
    private int mTotalTelegramChannel;

    public Query(String query,int totalTelegramChannel){
        mQuery = query;
        mTotalTelegramChannel = totalTelegramChannel;
    }

    public Query(){

    }

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public String getQuery() {
        return mQuery;
    }

    public void setQuery(String query) {
        this.mQuery = mQuery;
    }

    public int getTotalTelegramChannel() {
        return mTotalTelegramChannel;
    }

    public void setTotalTelegramChannel(int totalTelegramChannel) {
        this.mTotalTelegramChannel = totalTelegramChannel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Query> CREATOR
            = new Creator<Query>() {
        public Query createFromParcel(Parcel in) {
            return new Query(in);
        }

        public Query[] newArray(int size) {
            return new Query[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int i) {

        dest.writeLong(_id);
        dest.writeString(mQuery);
        dest.writeInt(mTotalTelegramChannel);

    }


    public Query(Parcel in) {

        _id    = in.readLong();
        mQuery = in.readString();
        mTotalTelegramChannel = in.readInt();

    }


    public static ContentValues queryToContentValues(final Query query) {

        ContentValues values = new ContentValues();
        values.put(SearchedQueryEntry.COLUMN_QUERY, query.getQuery());
        values.put(SearchedQueryEntry.COLUMN_STATE, GoftagramContract.SYNC_STATE_IDLE);
        long time = System.currentTimeMillis();
        values.put(SearchedQueryEntry.COLUMN_UPDATED, time);
        values.put(SearchedQueryEntry.COLUMN_TOTAL_TELEGRAM_CHANNEL, query.getTotalTelegramChannel());

        return values;
    }



    public static void insertQuery(Query query,Context context){

        ContentValues cvs = queryToContentValues(query);
        Uri insertUri = SearchedQueryEntry.buildSearchedQueryUri(SQLiteDatabase.CONFLICT_REPLACE);
        context.getContentResolver().insert(insertUri, cvs);

    }



}
