package com.goftagram.telegram.goftagram.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.goftagram.telegram.goftagram.adapter.listener.OnCommentSendButtonClickListener;
import com.goftagram.telegram.goftagram.util.SoftInputUtils;
import com.goftagram.telegram.messenger.R;

/**
 * Created by WORK on 11/5/2015.
 */
public class ChannelDetailUserCommentViewHolder extends RecyclerView.ViewHolder {





    public EditText     mUserInputEditText;
    public ImageView       mUserInputSendImageView;
    Context mContext;

    public ChannelDetailUserCommentViewHolder(View rootView,Context context) {

        super(rootView);
        mUserInputEditText = (EditText)rootView.findViewById(R.id.editText_user_comment);
        mUserInputSendImageView = (ImageView)rootView.findViewById(R.id.imageview_send);
        mContext = context;

    }

    public void fill(final OnCommentSendButtonClickListener onCommentSendButtonClickCallback) {

        mUserInputSendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCommentSendButtonClickCallback.OnCommentSendButtonClick(mUserInputEditText.getText().toString());
                SoftInputUtils.hideSoftInput(mContext, view);
            }
        });


    }
}
