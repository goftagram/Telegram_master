package com.goftagram.telegram.goftagram.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;

import com.goftagram.telegram.goftagram.adapter.listener.OnRatingSendButtonClickListener;
import com.goftagram.telegram.goftagram.helper.ViewHelper;
import com.goftagram.telegram.messenger.R;

/**
 * Created by WORK on 11/5/2015.
 */
public class ChannelDetailUserStarViewHolder extends RecyclerView.ViewHolder {




    public RatingBar    mUserInputRatingBar;
//    public Button       mUserInputSendButton;

    public ChannelDetailUserStarViewHolder(View rootView , Context context) {

        super(rootView);
        mUserInputRatingBar = (RatingBar)rootView.findViewById(R.id.ratingBar_user_rate);
        ViewHelper.colorizeRatingBar(mUserInputRatingBar, context);
//        mUserInputSendButton = (Button)rootView.findViewById(R.id.button_send);

    }

    public void fill(final OnRatingSendButtonClickListener mOnRatingSendButtonClickCallback) {
        mUserInputRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mOnRatingSendButtonClickCallback.OnRatingSendButtonClick(Float.valueOf(v).intValue());
            }
        });
    }
}
