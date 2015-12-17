package com.goftagram.telegram.goftagram.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.goftagram.telegram.goftagram.helper.ViewHelper;
import com.goftagram.telegram.messenger.R;

/**
 * Created by WORK on 11/7/2015.
 */
public  class TelegramChannelViewHolder extends RecyclerView.ViewHolder {
    public ImageView mImageView;
    public RatingBar mRatingBar;
    public TextView mTextView;
    public View mRootView;

    public TelegramChannelViewHolder(View view,Context context) {
        super(view);
        mRootView = view;
        mImageView = (ImageView) view.findViewById(R.id.imageView_grid_item);
        mRatingBar = (RatingBar) view.findViewById(R.id.ratingBar_grid_item);
        mTextView = (TextView) view.findViewById(R.id.textView_grid_item);
        ViewHelper.colorizeRatingBar(mRatingBar, context);
    }
}
