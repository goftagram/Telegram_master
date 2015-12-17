package com.goftagram.telegram.goftagram.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import com.goftagram.telegram.goftagram.adapter.listener.OnLinkClickListener;
import com.goftagram.telegram.goftagram.application.model.TelegramChannel;
import com.goftagram.telegram.messenger.R;

/**
 * Created by WORK on 11/5/2015.
 */
public class ChannelDetailLinkViewHolder extends RecyclerView.ViewHolder {




    public TextView mLinkTextView;


    public ChannelDetailLinkViewHolder(View rootView) {

        super(rootView);
        mLinkTextView = (TextView)rootView.findViewById(R.id.textView_link);

    }

    public void fill(final TelegramChannel telegramChannel,final OnLinkClickListener callback){

        mLinkTextView.setText(telegramChannel.getLink());
        Linkify.addLinks(mLinkTextView, Linkify.WEB_URLS);
        mLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.OnLinkClick(telegramChannel.getLink());
            }
        });


    }
}
