package com.goftagram.telegram.goftagram.application.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.provider.GoftagramContract.CommentEntry;

import java.util.ArrayList;
import java.util.List;

public class Comment  implements Parcelable {


    private long _id = -1;
    private String mServerId;
    private String mTelegramChannelId;
    private String mShamsiDate;
    private String mComment;
    private String mUserFirstName;
    private String mUserLastName;

    public Comment(long id,String serverId,String telegramChannelId, String date, String firstName,
                   String lastName,String comment){
        _id = id;
        mServerId = serverId;
        mTelegramChannelId = telegramChannelId;
        mShamsiDate = date;
        mUserFirstName = firstName;
        mUserLastName = lastName;
        mComment = comment;

    }

    public Comment(){

    }

    public String getServerId() {
        return mServerId;
    }

    public void setServerId(String serverId) {
        this.mServerId = serverId;
    }

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public String getTelegramChannelId() {
        return mTelegramChannelId;
    }

    public void setTelegramChannelId(String telegramChannelId) {
        this.mTelegramChannelId = telegramChannelId;
    }

    public String getShamsiDate() {
        return mShamsiDate;
    }

    public void setShamsiDate(String shamsiDate) {
        this.mShamsiDate = shamsiDate;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }

    public String getUserFirstName() {
        return mUserFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.mUserFirstName = userFirstName;
    }

    public String getUserLastName() {
        return mUserLastName;
    }

    public void setUserLastName(String userLastName) {
        this.mUserLastName = userLastName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Comment> CREATOR
            = new Creator<Comment>() {
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(_id);
        dest.writeString(mServerId);
        dest.writeString(mTelegramChannelId);
        dest.writeString(mShamsiDate);
        dest.writeString(mUserFirstName);
        dest.writeString(mUserLastName);
        dest.writeString(mComment);

    }


    public Comment(Parcel in) {

        _id = in.readLong();
        mServerId = in.readString();
        mTelegramChannelId = in.readString();
        mShamsiDate = in.readString();
        mUserFirstName = in.readString();
        mUserLastName = in.readString();
        mComment =  in.readString();
    }




    public static ContentValues CommentToContentValues(final Comment comment) {
        ContentValues values = new ContentValues();

        if(comment.getId() != -1)values.put(CommentEntry._ID, comment.getId());
        values.put(CommentEntry.COLUMN_SERVER_ID, comment.getServerId());
        values.put(CommentEntry.COLUMN_COMMENT, comment.getComment());
        values.put(CommentEntry.COLUMN_SHAMSI_DATE, comment.getShamsiDate());
        values.put(CommentEntry.COLUMN_TELEGRAM_CHANNEL_ID,comment.getTelegramChannelId());
        values.put(CommentEntry.COLUMN_USER_FIRST_NAME, comment.getUserFirstName());
        values.put(CommentEntry.COLUMN_USER_LAST_NAME, comment.getUserLastName());
        values.put(CommentEntry.COLUMN_STATE, GoftagramContract.SYNC_STATE_IDLE);
        long time = System.currentTimeMillis();
        values.put(CommentEntry.COLUMN_UPDATED, time);


        return values;
    }


    public static ContentValues[] commentsToContentValueArray(Comment[] commentArray) {

        ContentValues[] cvs = new ContentValues[commentArray.length];
        for (int i = 0; i < commentArray.length; ++i) {
            cvs[i] = CommentToContentValues(commentArray[i]);
        }
        return cvs;
    }


    public  static void insertCommentArray(Comment[] commentArrays,Context context){

        ContentValues[] cvs = commentsToContentValueArray(commentArrays);
        Uri bulkInsert = CommentEntry.buildCommentUri(SQLiteDatabase.CONFLICT_REPLACE);
        context.getContentResolver().bulkInsert(bulkInsert, cvs);
    }



    public static  List<Comment> cursorToList(Cursor cursor) {

        List<Comment> commentList = new ArrayList<Comment>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do{
                Comment comment = new Comment();

                comment.setId(cursor.getLong(cursor.getColumnIndex(
                        CommentEntry._ID
                )));

                comment.setServerId(cursor.getString(cursor.getColumnIndex(
                        CommentEntry.COLUMN_SERVER_ID
                )));
                comment.setUserFirstName(cursor.getString(cursor.getColumnIndex(
                        CommentEntry.COLUMN_USER_FIRST_NAME
                )));
                comment.setUserLastName(cursor.getString(cursor.getColumnIndex(
                        CommentEntry.COLUMN_USER_LAST_NAME
                )));
                comment.setTelegramChannelId(cursor.getString(cursor.getColumnIndex(
                        CommentEntry.COLUMN_TELEGRAM_CHANNEL_ID
                )));
                comment.setComment(cursor.getString(cursor.getColumnIndex(
                        CommentEntry.COLUMN_COMMENT
                )));
                comment.setShamsiDate(cursor.getString(cursor.getColumnIndex(
                        CommentEntry.COLUMN_SHAMSI_DATE
                )));
               commentList.add(comment);
            }while (cursor.moveToNext());
        }
        return commentList;

    }

}
