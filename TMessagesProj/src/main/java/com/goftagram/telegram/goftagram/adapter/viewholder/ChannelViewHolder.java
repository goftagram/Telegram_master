package com.goftagram.telegram.goftagram.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goftagram.telegram.goftagram.application.model.TelegramChannel;
import com.goftagram.telegram.messenger.R;

/**
 * Created by mhossein on 9/28/15.
 * Channel View Holder for {@link com.goftagram.telegram.goftagram.adapter.ChannelAdapter}
 */
public class ChannelViewHolder extends RecyclerView.ViewHolder {

    private ImageView chImage;
    private RatingBar chRate;
    private TextView chTitle;
    private View containerView;


    private View itemView;
    private Context context;



    public ChannelViewHolder(View itemView) {
        super(itemView);

        this.itemView   = itemView;
        this.context    = itemView.getContext();

        chImage         = (ImageView)itemView.findViewById(R.id.channel_row_image);
        chRate          = (RatingBar)itemView.findViewById(R.id.channel_row_rating_bar);
        chTitle         = (TextView)itemView.findViewById(R.id.channel_row_title);
        containerView   = itemView.findViewById(R.id.channel_row_view);


    }

    public void fill(TelegramChannel channel){

        // Load image of channel
        Glide.with(context)
                .load(channel.getThumbnail())
                .into(getChImage());

        // Set rate of channel
        getChRate().setRating(channel.getRate());

        // Set title of channel
        getChTitle().setText(channel.getTitle());

    }

    public ImageView getChImage() {
        return chImage;
    }

    public RatingBar getChRate() {
        return chRate;
    }

    public TextView getChTitle() {
        return chTitle;
    }

    public View getContainerView() {
        return containerView;
    }
}
