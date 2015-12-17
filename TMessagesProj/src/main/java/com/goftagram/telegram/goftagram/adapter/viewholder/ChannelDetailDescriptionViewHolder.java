package com.goftagram.telegram.goftagram.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.goftagram.telegram.goftagram.application.model.TelegramChannel;
import com.goftagram.telegram.messenger.R;

/**
 * Created by WORK on 11/5/2015.
 */
public class ChannelDetailDescriptionViewHolder extends RecyclerView.ViewHolder {




    public TextView mDescriptionTextView;


    public ChannelDetailDescriptionViewHolder(View rootView) {

        super(rootView);
        mDescriptionTextView = (TextView)rootView.findViewById(R.id.textView_description);

    }

    public void fill(TelegramChannel telegramChannel){

        mDescriptionTextView.setText(telegramChannel.getDescription());


    }
}
