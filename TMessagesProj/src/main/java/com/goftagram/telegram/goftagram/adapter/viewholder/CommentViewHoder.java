package com.goftagram.telegram.goftagram.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.goftagram.telegram.goftagram.application.model.Comment;
import com.goftagram.telegram.messenger.R;

/**
 * Created by mhossein on 10/1/15.
 */
public class CommentViewHoder extends RecyclerView.ViewHolder {

    private TextView userTv;
    private TextView dateTv;
    private TextView commentTv;
//    private ImageView reportBtn;

    public CommentViewHoder(View itemView) {
        super(itemView);

        userTv = (TextView)itemView.findViewById(R.id.comment_row_user);
        dateTv = (TextView)itemView.findViewById(R.id.comment_row_date);
        commentTv = (TextView)itemView.findViewById(R.id.comment_row_comment);
//        reportBtn = (ImageView)itemView.findViewById(R.id.comment_row_report_btn);
    }

    public void fill(Comment comment){
//        User user = comment.getUser();
//        userTv.setText(user.getUserName());
//        dateTv.setText(comment.getShamsiDate());
//        commentTv.setText(comment.getComment());

    }
}
