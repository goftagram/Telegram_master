package com.goftagram.telegram.goftagram.application.model;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.provider.GoftagramContract.TelegramChannelEntry;

import java.util.ArrayList;
import java.util.List;

public class TelegramChannel implements Parcelable {

    private long _id = -1;
    private String mServerId;
    private String mTitle;
    private String mDescription;
    private String mLink;
    private float mRate;

    private int mStar_5;
    private int mStar_4;
    private int mStar_3;
    private int mStar_2;
    private int mStar_1;

    private String mThumbnail;
    private String mImage;
    private Tag[] mTags;
    private Comment[] mComment;
    private TelegramChannel[] mRelatedChannel;
    private Category mCategory;

    public TelegramChannel(long id,String serverId, String title, String description, String link, float rate,
                           String thumbnail, String image, Tag[] tags, Comment[] comment, TelegramChannel[] relatedChannel,
                           Category category) {
        _id = id;
        mServerId = serverId;
        mTitle = title;
        mDescription = description;
        mLink = link;
        mRate = rate;
        mThumbnail = thumbnail;
        mImage = image;
        mTags = tags;
        mComment = comment;
        mRelatedChannel = relatedChannel;
        mCategory = category;
    }

    public TelegramChannel() {

    }

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public int getStar_5() {
        return mStar_5;
    }

    public void setStar_5(int star_5) {
        this.mStar_5 = star_5;
    }

    public int getStar_4() {
        return mStar_4;
    }

    public void setStar_4(int star_4) {
        this.mStar_4 = star_4;
    }

    public int getStar_3() {
        return mStar_3;
    }

    public void setStar_3(int star_3) {
        this.mStar_3 = star_3;
    }

    public int getStar_2() {
        return mStar_2;
    }

    public void setStar_2(int star_2) {
        this.mStar_2 = star_2;
    }

    public int getStar_1() {
        return mStar_1;
    }

    public void setStar_1(int star_1) {
        this.mStar_1 = star_1;
    }

    public String getServerId() {
        return mServerId;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
    }

    public void setServerId(String serverId) {
        this.mServerId = serverId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        this.mLink = link;
    }

    public float getRate() {
        return mRate;
    }

    public void setRate(float rate) {
        this.mRate = rate;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.mThumbnail = thumbnail;
    }

    public Tag[] getTags() {
        return mTags;
    }

    public void setTags(Tag[] tags) {
        this.mTags = tags;
    }

    public Comment[] getComment() {
        return mComment;
    }

    public void setComment(Comment[] comment) {
        this.mComment = comment;
    }

    public TelegramChannel[] getRelatedChannel() {
        return mRelatedChannel;
    }

    public void setRelatedChannel(TelegramChannel[] relatedChannel) {
        this.mRelatedChannel = relatedChannel;
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        this.mCategory = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TelegramChannel> CREATOR
            = new Creator<TelegramChannel>() {
        public TelegramChannel createFromParcel(Parcel in) {
            return new TelegramChannel(in);
        }

        public TelegramChannel[] newArray(int size) {
            return new TelegramChannel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(_id);
        dest.writeString(mServerId);
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mLink);
        dest.writeFloat(mRate);
        dest.writeString(mThumbnail);
        dest.writeString(mImage);
        dest.writeTypedArray(mTags, flags);
        dest.writeTypedArray(mComment, flags);
        dest.writeTypedArray(mRelatedChannel, flags);
        dest.writeParcelable(mCategory, flags);

    }


    public TelegramChannel(Parcel in) {
        _id = in.readLong();
        mServerId = in.readString();
        mTitle = in.readString();
        mDescription = in.readString();
        mLink = in.readString();
        mRate = in.readFloat();
        mThumbnail = in.readString();
        mImage = in.readString();
        mTags = in.createTypedArray(Tag.CREATOR);
        mComment = in.createTypedArray(Comment.CREATOR);
        mRelatedChannel = in.createTypedArray(TelegramChannel.CREATOR);
        mCategory = in.readParcelable(Category.class.getClassLoader());


    }


    public static ContentValues[] TelegramChannelsToContentValueArray
            (TelegramChannel[] telegramChannelArray) {

        ContentValues[] cvs = new ContentValues[telegramChannelArray.length];
        for (int i = 0; i < telegramChannelArray.length; ++i) {
            cvs[i] = TelegramChannelToContentValues(telegramChannelArray[i]);
        }

        return cvs;
    }

    public static ContentValues TelegramChannelToContentValues(final TelegramChannel telegramChannel) {
        ContentValues values = new ContentValues();

        if(telegramChannel.getId() != -1 )values.put(TelegramChannelEntry._ID, telegramChannel.getId());
        values.put(TelegramChannelEntry.COLUMN_SERVER_ID, telegramChannel.getServerId());
        values.put(TelegramChannelEntry.COLUMN_TITLE, telegramChannel.getTitle());
        values.put(TelegramChannelEntry.COLUMN_DESCRIPTION, telegramChannel.getDescription());
        values.put(TelegramChannelEntry.COLUMN_LINK, telegramChannel.getLink());
        values.put(TelegramChannelEntry.COLUMN_RATE, telegramChannel.getRate());
        values.put(TelegramChannelEntry.COLUMN_THUMBNAIL, telegramChannel.getThumbnail());
        values.put(TelegramChannelEntry.COLUMN_IMAGE, telegramChannel.getImage());
        values.put(TelegramChannelEntry.COLUMN_CATEGORY_ID, telegramChannel.getCategory().getServerId());

        values.put(TelegramChannelEntry.COLUMN_STAR_5, telegramChannel.getStar_5());
        values.put(TelegramChannelEntry.COLUMN_STAR_4, telegramChannel.getStar_4());
        values.put(TelegramChannelEntry.COLUMN_STAR_3, telegramChannel.getStar_3());
        values.put(TelegramChannelEntry.COLUMN_STAR_2, telegramChannel.getStar_2());
        values.put(TelegramChannelEntry.COLUMN_STAR_1, telegramChannel.getStar_1());

        values.put(TelegramChannelEntry.COLUMN_RANK_IN_CATEGORY, -1);

        long updatedTime = System.currentTimeMillis();
        values.put(TelegramChannelEntry.COLUMN_UPDATED, updatedTime);

        values.put(TelegramChannelEntry.COLUMN_STATE, GoftagramContract.SYNC_STATE_GETTING);


        return values;
    }


    public static ContentValues[] TelegramChannelsToContentValueArray
            (List<TelegramChannel> telegramChannelList) {

        ContentValues[] cvs = new ContentValues[telegramChannelList.size()];
        for (int i = 0; i < telegramChannelList.size(); ++i) {
            cvs[i] = TelegramChannelToContentValues(telegramChannelList.get(i));
        }

        return cvs;
    }


    public static List<TelegramChannel> cursorToTelegramChannelList(Cursor cursor) {

        List<TelegramChannel> telegramChannelList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                TelegramChannel telegramChannel = cursorToTelegramChannel(cursor);
                telegramChannelList.add(telegramChannel);
            } while (cursor.moveToNext());
        }
        return telegramChannelList;

    }


    public static TelegramChannel cursorToTelegramChannel(Cursor cursor) {

        TelegramChannel telegramChannel = new TelegramChannel();

        telegramChannel.setId(cursor.getLong(cursor.getColumnIndex(
                TelegramChannelEntry._ID
        )));

        telegramChannel.setServerId(cursor.getString(cursor.getColumnIndex(
                TelegramChannelEntry.COLUMN_SERVER_ID
        )));

        telegramChannel.setTitle(cursor.getString(cursor.getColumnIndex(
                TelegramChannelEntry.COLUMN_TITLE
        )));

        telegramChannel.setDescription(cursor.getString(cursor.getColumnIndex(
                TelegramChannelEntry.COLUMN_DESCRIPTION
        )));

        telegramChannel.setLink(cursor.getString(cursor.getColumnIndex(
                TelegramChannelEntry.COLUMN_LINK
        )));

        telegramChannel.setRate(cursor.getFloat(cursor.getColumnIndex(
                TelegramChannelEntry.COLUMN_RATE
        )));

        telegramChannel.setStar_5(cursor.getInt(cursor.getColumnIndex(
                TelegramChannelEntry.COLUMN_STAR_5
        )));

        telegramChannel.setStar_4(cursor.getInt(cursor.getColumnIndex(
                TelegramChannelEntry.COLUMN_STAR_4
        )));

        telegramChannel.setStar_3(cursor.getInt(cursor.getColumnIndex(
                TelegramChannelEntry.COLUMN_STAR_3
        )));

        telegramChannel.setStar_2(cursor.getInt(cursor.getColumnIndex(
                TelegramChannelEntry.COLUMN_STAR_2
        )));

        telegramChannel.setStar_1(cursor.getInt(cursor.getColumnIndex(
                TelegramChannelEntry.COLUMN_STAR_1
        )));

        telegramChannel.setThumbnail(cursor.getString(cursor.getColumnIndex(
                TelegramChannelEntry.COLUMN_THUMBNAIL
        )));

        telegramChannel.setImage(cursor.getString(cursor.getColumnIndex(
                TelegramChannelEntry.COLUMN_IMAGE
        )));

        return telegramChannel;

    }

}
