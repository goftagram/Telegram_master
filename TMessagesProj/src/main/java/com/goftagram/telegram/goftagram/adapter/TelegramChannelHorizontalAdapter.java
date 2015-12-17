package com.goftagram.telegram.goftagram.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.goftagram.telegram.goftagram.adapter.listener.OnRelatedChannelClickListener;
import com.goftagram.telegram.goftagram.adapter.viewholder.TelegramChannelViewHolder;
import com.goftagram.telegram.goftagram.application.model.TelegramChannel;
import com.goftagram.telegram.messenger.R;

import java.util.List;

/**
 * Created by WORK on 11/7/2015.
 */
public class TelegramChannelHorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private List<TelegramChannel> mTelegramChannelList;
    private Context mContext;
    private OnRelatedChannelClickListener mCallback;

    public TelegramChannelHorizontalAdapter(Context context, List<TelegramChannel> telegramChannelList,
                                            OnRelatedChannelClickListener callback){
        mTelegramChannelList = telegramChannelList;
        mCallback = callback;
        mContext = context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.viewholder_relatedchannel_grid_item, parent, false);
        TelegramChannelViewHolder vh = new TelegramChannelViewHolder(view,mContext);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        TelegramChannelViewHolder holder = (TelegramChannelViewHolder) viewHolder;

        Glide.with(mContext)
                .load(mTelegramChannelList.get(position).getThumbnail())
                .placeholder(R.drawable.placeholder)
                .into(holder.mImageView);

        holder.mTextView.setText("" + mTelegramChannelList.get(position).getTitle());

        final TelegramChannel finalTelegramChannel = mTelegramChannelList.get(position);

        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.OnRelatedChannelClick(finalTelegramChannel);
            }
        });

        holder.mRatingBar.setRating(mTelegramChannelList.get(position).getRate());


    }

    @Override
    public int getItemCount() {

        return mTelegramChannelList.size();
    }
}
