package com.goftagram.telegram.goftagram.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.goftagram.telegram.goftagram.application.model.TelegramChannel;
import com.goftagram.telegram.goftagram.helper.ViewHelper;
import com.goftagram.telegram.messenger.R;

/**
 * Created by WORK on 11/5/2015.
 */
public class ChannelDetailRateViewHolder extends RecyclerView.ViewHolder {


    public RatingBar m5StarRatingBar;
    public RatingBar m4StarRatingBar;
    public RatingBar m3StarRatingBar;
    public RatingBar m2StarRatingBar;
    public RatingBar m1StarRatingBar;

    public RatingBar mTotalStarRatingBar;

    public TextView m5StarPercentTextView;
    public TextView m4StarPercentTextView;
    public TextView m3StarPercentTextView;
    public TextView m2StarPercentTextView;
    public TextView m1StarPercentTextView;

    public TextView mTotalStarTextView;


    public ChannelDetailRateViewHolder(View rootView,Context context) {

        super(rootView);

        m5StarRatingBar = (RatingBar)rootView.findViewById(R.id.ratingBar_star_5);
        m4StarRatingBar = (RatingBar)rootView.findViewById(R.id.ratingBar_star_4);
        m3StarRatingBar = (RatingBar)rootView.findViewById(R.id.ratingBar_star_3);
        m2StarRatingBar = (RatingBar)rootView.findViewById(R.id.ratingBar_star_2);
        m1StarRatingBar = (RatingBar)rootView.findViewById(R.id.ratingBar_star_1);

        mTotalStarRatingBar = (RatingBar)rootView.findViewById(R.id.ratingBar_star_total);

        m5StarPercentTextView = (TextView)rootView.findViewById(R.id.textView_star_5);
        m4StarPercentTextView = (TextView)rootView.findViewById(R.id.textView_star_4);
        m3StarPercentTextView = (TextView)rootView.findViewById(R.id.textView_star_3);
        m2StarPercentTextView = (TextView)rootView.findViewById(R.id.textView_star_2);
        m1StarPercentTextView = (TextView)rootView.findViewById(R.id.textView_star_1);

        mTotalStarTextView = (TextView)rootView.findViewById(R.id.textView_star_total);

        ViewHelper.colorizeRatingBar(m5StarRatingBar, context);
        ViewHelper.colorizeRatingBar(m4StarRatingBar, context);
        ViewHelper.colorizeRatingBar(m3StarRatingBar, context);
        ViewHelper.colorizeRatingBar(m2StarRatingBar, context);
        ViewHelper.colorizeRatingBar(m1StarRatingBar, context);

        ViewHelper.colorizeRatingBar(mTotalStarRatingBar, context);


    }

    public void fill(TelegramChannel telegramChannel){
        mTotalStarRatingBar.setRating(telegramChannel.getRate());
        mTotalStarTextView.setText("" + telegramChannel.getRate() + "/5.0");
        m5StarPercentTextView.setText("" + telegramChannel.getStar_5());
        m4StarPercentTextView.setText("" + telegramChannel.getStar_4());
        m3StarPercentTextView.setText("" + telegramChannel.getStar_3());
        m2StarPercentTextView.setText("" + telegramChannel.getStar_2());
        m1StarPercentTextView.setText("" + telegramChannel.getStar_1());

    }
}
