package com.goftagram.telegram.goftagram.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.goftagram.telegram.goftagram.adapter.listener.OnTagClickListener;
import com.goftagram.telegram.goftagram.application.model.Tag;
import com.goftagram.telegram.goftagram.util.Utils;
import com.goftagram.telegram.messenger.R;

import java.util.List;

/**
 * Created by WORK on 11/5/2015.
 */
public class ChannelDetailTagViewHolder extends RecyclerView.ViewHolder {



    public static final int TAG_KEY = 0;

    public LinearLayout mTagContainerLinearLayout;
    public Button[] mTagButtonArrays;

    private OnTagClickListener mCallback;
    private Context mContext;


    public ChannelDetailTagViewHolder(View rootView,Context context) {

        super(rootView);
        mTagContainerLinearLayout = (LinearLayout)rootView.findViewById(R.id.tag_container);
        mContext = context;

    }

    public void fill(List<Tag> tagList,OnTagClickListener callback){
        mCallback = callback;
        LinearLayout ll = null;
        mTagContainerLinearLayout.removeAllViews();
        int counter = 0;
        mTagButtonArrays = new Button[tagList.size()];
        for (int i = 0 ; i < tagList.size() ; i++){

            counter = i;
            Button button = new Button(mTagContainerLinearLayout.getContext());
            button.setText(tagList.get(i).getName());
            button.setTag(tagList.get(i));
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            button.setBackgroundResource(R.drawable.tag_button_click_selector);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.OnTagChannelClick((Tag)view.getTag());
                }
            });
            if(counter%3 == 0){
                ll = new LinearLayout(mTagContainerLinearLayout.getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ll.setLayoutParams(lp);
                ll.setWeightSum(3);
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1);
            int margin = Utils.DpToPxInteger(mContext, 4);
            lp.setMargins(margin,margin,margin,margin);
            button.setLayoutParams(lp);
            ll.addView(button);

            if(counter%3 == 2 ){
                mTagContainerLinearLayout.addView(ll, Math.min(1, mTagContainerLinearLayout.getChildCount()));
            }
        }
        if(counter%3 != 2){
            mTagContainerLinearLayout.addView(ll, Math.min(1, mTagContainerLinearLayout.getChildCount()));
        }

    }

}
