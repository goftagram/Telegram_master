package com.goftagram.telegram.goftagram.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.goftagram.telegram.goftagram.adapter.listener.OnLoadMoreListener;
import com.goftagram.telegram.goftagram.adapter.viewholder.LoadingViewHolder;
import com.goftagram.telegram.goftagram.adapter.viewholder.TelegramChannelViewHolder;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.messenger.R;

public class TelegramChannelGridAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {


    public interface onItemClickListener {
        public void onItemClick(String telegramChannelId, String title);
    }


    Context mContext;

    public final int VIEW_ITEM = 1;
    public final int VIEW_PROGRESSBAR = 0;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int mVisibleThreshold = 5;
    private int mLastVisibleItem, mTotalItemCount;
    private boolean mIsLoading;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean mHasMoreItem;
    private onItemClickListener mOnItemClickListener;




    public TelegramChannelGridAdapter(Context context, Cursor cursor, RecyclerView recyclerView) {
        super(context, cursor);
        mContext = context;
        mHasMoreItem = true;



        final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView
                .getLayoutManager();


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mHasMoreItem) {
                    mTotalItemCount = gridLayoutManager.getItemCount();
                    mLastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                    if (!mIsLoading && mTotalItemCount <= (mLastVisibleItem + mVisibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        mIsLoading = true;
                    }
                }

            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
//        Log.i(LOG_TAG,"onBindViewHolder mDataValid: " + mDataValid);
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");

        }
        if (!mCursor.moveToPosition(position)) {
            if (!(mHasMoreItem)) {
                throw new IllegalStateException("couldn't move cursor to position " + position);
            }
        }

        onBindViewHolder(viewHolder, mCursor);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {

            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.viewholder_channel_grid_item, parent, false);
            vh = new TelegramChannelViewHolder(view,mContext);
        } else {
            vh = new LoadingViewHolder(LayoutInflater.from(mContext).inflate(
                    R.layout.viewholder_gridlist_loading, parent, false));
        }


        return vh;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {

        if (viewHolder instanceof TelegramChannelViewHolder) {

            TelegramChannelViewHolder holder = (TelegramChannelViewHolder) viewHolder;

            final float rate = cursor.getFloat(
                    cursor.getColumnIndex(GoftagramContract.TelegramChannelEntry.COLUMN_RATE)
            );
            final String title = cursor.getString(
                    cursor.getColumnIndex(GoftagramContract.TelegramChannelEntry.COLUMN_TITLE)
            );
            final String thumbnail = cursor.getString(
                    cursor.getColumnIndex(GoftagramContract.TelegramChannelEntry.COLUMN_THUMBNAIL)
            );

            Glide.with(mContext)
                    .load(thumbnail)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.mImageView);


            holder.mRatingBar.setRating(rate);
            holder.mTextView.setText(title);

            final String telegramChannelId = cursor.getString(cursor.getColumnIndex(GoftagramContract
                    .TelegramChannelEntry.COLUMN_SERVER_ID));

            holder.mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(telegramChannelId, title);
                    }
                }
            });

        }

    }


    @Override
    public int getItemViewType(int position) {
//        if(DEBUG)Log.i(LOG_TAG,"position: " + position + " getCursor().getCount() " +
//                getCursor().getCount()+ " "
//                + "hasMoreItem()= " +  hasMoreItem());
        if (getCursor() != null && (getCursor().getCount() == position) && hasMoreItem()) {
            return VIEW_PROGRESSBAR;
        } else {
            return VIEW_ITEM;
        }

    }

    @Override
    public int getItemCount() {
        if (mHasMoreItem && mDataValid) {
            return (super.getItemCount() + 1);
        } else {
            return super.getItemCount();
        }

    }

    public boolean isLoading() {
        return mIsLoading;
    }

    public void setIsLoading(boolean isLoading) {
        this.mIsLoading = isLoading;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    public boolean hasMoreItem() {
        return mHasMoreItem;
    }

    public void setHasMoreItem(boolean hasMoreItem) {
        if (mHasMoreItem == true && mHasMoreItem != hasMoreItem) {
            notifyItemRemoved(mCursor.getCount());
        }
        this.mHasMoreItem = hasMoreItem;

    }

    public onItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}




