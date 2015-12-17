package com.goftagram.telegram.goftagram.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goftagram.telegram.goftagram.adapter.listener.OnCommentReportButtonClickListener;
import com.goftagram.telegram.goftagram.application.model.Comment;
import com.goftagram.telegram.messenger.R;

/**
 * Created by WORK on 11/5/2015.
 */
public class ChannelDetailCommentViewHolder extends RecyclerView.ViewHolder {



    private TextView mUserTv;
    private TextView mDateTv;
    private TextView mCommentTv;
    private ImageView mReportCommentImageView;


    public ChannelDetailCommentViewHolder(View itemView) {
        super(itemView);

        mUserTv = (TextView)itemView.findViewById(R.id.comment_row_user);
        mDateTv = (TextView)itemView.findViewById(R.id.comment_row_date);
        mCommentTv = (TextView)itemView.findViewById(R.id.comment_row_comment);
        mReportCommentImageView = (ImageView)itemView.findViewById(R.id.comment_row_report_btn);
    }

    public void fill(final Comment comment,final OnCommentReportButtonClickListener callback){

        mUserTv.setText(comment.getUserFirstName() +" " + comment.getUserLastName());
        mDateTv.setText(comment.getShamsiDate());
        mCommentTv.setText(comment.getComment());
        mReportCommentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.OnCommentReportButtonClick(comment);
            }
        });


    }


}
