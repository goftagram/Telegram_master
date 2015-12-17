package com.goftagram.telegram.goftagram.application.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.provider.GoftagramContract.TagEntry;

import java.util.ArrayList;
import java.util.List;

public class Tag implements Parcelable{

    private long _id = -1;
    private String mName;
    private String mServerId;
    private int mTotalTelegramChannel;

    public Tag(long id,String name,String serverId,int totalTelegramChannel){
        _id = id;
        mName = name;
        mServerId = serverId;
        mTotalTelegramChannel = totalTelegramChannel;
    }

    public Tag(){

    }

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getServerId() {
        return mServerId;
    }

    public void setServerId(String serverId) {
        this.mServerId = serverId;
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

    public static final Creator<Tag> CREATOR
            = new Creator<Tag>() {
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int i) {

        dest.writeLong(_id);
        dest.writeString(mName);
        dest.writeString(mServerId);
        dest.writeInt(mTotalTelegramChannel);

    }


    public Tag(Parcel in) {

        _id   = in.readLong();
        mName = in.readString();
        mServerId = in.readString();
        mTotalTelegramChannel = in.readInt();

    }


    public static ContentValues tagToContentValues(final Tag tag) {
        ContentValues values = new ContentValues();
        if(tag.getId() != -1 )values.put(TagEntry._ID, tag.getId());
        values.put(TagEntry.COLUMN_SERVER_ID, tag.getServerId());
        values.put(TagEntry.COLUMN_NAME, tag.getName());
        values.put(TagEntry.COLUMN_STATE, GoftagramContract.SYNC_STATE_IDLE);
        long time = System.currentTimeMillis();
        values.put(TagEntry.COLUMN_UPDATED, time);
        values.put(TagEntry.COLUMN_TOTAL_TELEGRAM_CHANNEL, tag.getTotalTelegramChannel());

        return values;
    }


    public static ContentValues[] tagsToContentValueArray(Tag[] tagArray) {

        ContentValues[] cvs = new ContentValues[tagArray.length];
        for (int i = 0; i < tagArray.length; ++i) {
            cvs[i] = tagToContentValues(tagArray[i]);
        }
        return cvs;
    }

    public static void insertTagArray(Tag[] tagArrays,Context context){

        ContentValues[] cvs = tagsToContentValueArray(tagArrays);
        Uri bulkInsert = TagEntry.buildTagUri(SQLiteDatabase.CONFLICT_IGNORE);
        context.getContentResolver().bulkInsert(bulkInsert, cvs);

    }

    public static  List<Tag> cursorToList(Cursor cursor) {

        List<Tag> tagList = new ArrayList<Tag>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do{
                Tag tag = new Tag();

                tag.setId(cursor.getLong(cursor.getColumnIndex(
                        TagEntry._ID
                )));

                tag.setServerId(cursor.getString(cursor.getColumnIndex(
                        TagEntry.COLUMN_SERVER_ID
                )));
                tag.setName(cursor.getString(cursor.getColumnIndex(
                        TagEntry.COLUMN_NAME
                )));
                tag.setTotalTelegramChannel(cursor.getInt(cursor.getColumnIndex(
                        TagEntry.COLUMN_TOTAL_TELEGRAM_CHANNEL
                )));
                tagList.add(tag);
            }while (cursor.moveToNext());
        }
        return tagList;

    }

}
