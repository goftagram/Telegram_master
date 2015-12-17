package com.goftagram.telegram.goftagram.application.model;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.goftagram.telegram.goftagram.provider.GoftagramContract;

import java.util.List;

public class Category implements Parcelable {


    long _id = -1;
    String mServerId;
    String mTitle;
    String mThumbnail;
    int mEstimatedCount;
    int mOrder;
    int mTotalChannels;
    int mPerPageChannels;

    public Category(long id,String serverId,
                    String title,
                    String thumbnail,
                    int estimatedCount,
                    int order,
                    int totalChannels,
                    int perPageChannels){
        _id = id;
        mServerId = serverId;
        mTitle = title;
        mThumbnail = thumbnail;
        mEstimatedCount = estimatedCount;
        mOrder = order;
        mTotalChannels = totalChannels;
        mPerPageChannels = perPageChannels;

    }

    public Category(){

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

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.mThumbnail = thumbnail;
    }

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int order) {
        this.mOrder = order;
    }

    public int getEstimatedCount() {
        return mEstimatedCount;
    }

    public void setEstimatedCount(int estimatedCount) {
        this.mEstimatedCount = estimatedCount;
    }

    public int getTotalChannels() {
        return mTotalChannels;
    }

    public void setTotalChannels(int totalChannels) {
        this.mTotalChannels = totalChannels;
    }

    public int getPerPageChannels() {
        return mPerPageChannels;
    }

    public void setPerPageChannels(int perPageChannels) {
        this.mPerPageChannels = perPageChannels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Category> CREATOR
            = new Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int i) {

        dest.writeLong(_id);
        dest.writeString(mServerId);
        dest.writeString(mTitle);
        dest.writeString(mThumbnail);
        dest.writeInt(mEstimatedCount);
        dest.writeInt(mOrder);
        dest.writeInt(mTotalChannels);
        dest.writeInt(mPerPageChannels);


    }


    public Category(Parcel in) {

        _id                 = in.readLong();
        mServerId           = in.readString();
        mTitle              = in.readString();
        mThumbnail          = in.readString();
        mEstimatedCount     = in.readInt();
        mOrder              = in.readInt();
        mTotalChannels      = in.readInt();
        mPerPageChannels    = in.readInt();


    }


    public static  void insertCategoryArray(List<Category> categoryList,Context context) {

        ContentValues[] cvs = CategoryToContentValueArray(categoryList);
        Uri bulkInsert = GoftagramContract.CommentEntry.buildCommentUri();
        context.getContentResolver().bulkInsert(bulkInsert, cvs);
    }

    public static ContentValues CategoryToContentValues(final Category category) {

        ContentValues values = new ContentValues();
        if(category.getId() != -1)values.put(GoftagramContract.CategoryEntry._ID, category.getId());
        values.put(GoftagramContract.CategoryEntry.COLUMN_SERVER_ID, category.getServerId());
        values.put(GoftagramContract.CategoryEntry.COLUMN_THUMBNAIL, category.getThumbnail());
        values.put(GoftagramContract.CategoryEntry.COLUMN_TITLE, category.getTitle());
        values.put(GoftagramContract.CategoryEntry.COLUMN_ORDER, category.getOrder());
        values.put(GoftagramContract.CategoryEntry.COLUMN_ESTIMATED_TOTAL_CHANNELS, category.getEstimatedCount());
        return values;
    }


    public static ContentValues[] CategoryToContentValueArray
            (List<Category> categoryList) {

        ContentValues[] cvs = new ContentValues[categoryList.size()];
        for (int i = 0; i < categoryList.size(); ++i) {
            cvs[i] = CategoryToContentValues(categoryList.get(i));
        }

        return cvs;
    }

}
