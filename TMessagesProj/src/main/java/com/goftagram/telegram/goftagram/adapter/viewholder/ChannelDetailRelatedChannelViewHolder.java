package com.goftagram.telegram.goftagram.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.goftagram.telegram.goftagram.adapter.TelegramChannelHorizontalAdapter;
import com.goftagram.telegram.goftagram.adapter.WrappingLinearLayoutManager;
import com.goftagram.telegram.goftagram.adapter.listener.OnRelatedChannelClickListener;
import com.goftagram.telegram.goftagram.application.model.TelegramChannel;
import com.goftagram.telegram.messenger.R;

import java.util.List;

/**
 * Created by WORK on 11/5/2015.
 */
public class ChannelDetailRelatedChannelViewHolder extends RecyclerView.ViewHolder {


    public RecyclerView mRecyclerView;
    private WrappingLinearLayoutManager mWrappingLinearLayoutManager;
    private Context mContext;
    private OnRelatedChannelClickListener mCallback;


    public ChannelDetailRelatedChannelViewHolder(View rootView) {

        super(rootView);
        mContext = rootView.getContext();
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView_related_channel);
        mWrappingLinearLayoutManager = new WrappingLinearLayoutManager(rootView.getContext(),LinearLayoutManager.HORIZONTAL,false);

        mRecyclerView.setLayoutManager(mWrappingLinearLayoutManager);

    }

    public void fill(List<TelegramChannel> telegramChannelList,OnRelatedChannelClickListener callback){

        mCallback = callback;
        TelegramChannelHorizontalAdapter adapter
                = new TelegramChannelHorizontalAdapter(mContext,telegramChannelList,callback);
        mRecyclerView.setAdapter(adapter);

    }
}
