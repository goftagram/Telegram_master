package com.goftagram.telegram.goftagram.adapter;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.goftagram.telegram.goftagram.adapter.viewholder.CategoryViewHolder;
import com.goftagram.telegram.goftagram.animator.AdapterAnimator;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.messenger.R;


public class CategoryGridAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {

    public interface onItemClickListener{
        void onItemClick(String categoryId, String title);
    }
    Context mContext;

    private AdapterAnimator mAdapterAnimator;
    private onItemClickListener mOnItemClickListener;


    public CategoryGridAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
        mAdapterAnimator = new AdapterAnimator();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        CategoryViewHolder holder = (CategoryViewHolder) viewHolder;


        String text = cursor.getString(
                cursor.getColumnIndex(GoftagramContract.CategoryEntry.COLUMN_TITLE)
        );
        String thumbnail = cursor.getString(
                cursor.getColumnIndex(GoftagramContract.CategoryEntry.COLUMN_THUMBNAIL)
        );

//        text = text + "   " + cursor.getInt(
//                cursor.getColumnIndex(GoftagramContract.CategoryEntry._ID));

        Glide.with(mContext)
                .load(thumbnail)
                .placeholder(R.drawable.placeholder)
                .into(holder.mImageView);

        holder.mTextView.setText(text);

        final String categoryId = cursor.getString(cursor.getColumnIndex(GoftagramContract
                .CategoryEntry.COLUMN_SERVER_ID));

        final String title = cursor.getString(cursor.getColumnIndex(GoftagramContract
                .CategoryEntry.COLUMN_TITLE));

        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(categoryId,title);
                }
            }
        });

        mAdapterAnimator.setAnimationToRow(holder,mCurrentPosition);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;

        View view = LayoutInflater.from(mContext).inflate(
                R.layout.viewholder_category_grid_item, parent, false);

        vh = new CategoryViewHolder(view);

        return vh;
    }

    public onItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


}
