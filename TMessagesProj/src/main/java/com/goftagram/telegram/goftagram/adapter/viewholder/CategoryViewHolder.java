package com.goftagram.telegram.goftagram.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goftagram.telegram.messenger.R;


/**
 * Created by WORK on 11/8/2015.
 */
public class CategoryViewHolder extends RecyclerView.ViewHolder {
    public ImageView mImageView;
    public TextView mTextView;
    public View mRootView;

    public CategoryViewHolder(View view) {
        super(view);
        mRootView = view;
        mImageView = (ImageView) view.findViewById(R.id.category_grid_item_imageView);
        mTextView = (TextView) view.findViewById(R.id.category_grid_item_title);

    }
}
