package com.goftagram.telegram.goftagram.animator;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.goftagram.telegram.goftagram.helper.ViewHelper;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by WORK on 11/11/2015.
 */
public class AdapterAnimator {

    private int mLastPosition = -1;
    protected int mDuration = 400;
    protected Interpolator mInterpolator = new LinearInterpolator();


    public AdapterAnimator(){

    }

    public  void setAnimationToRow(RecyclerView.ViewHolder holder, int position){
        if (position > mLastPosition) {
            for (Animator anim : getAnimators(holder.itemView)) {
                anim.setDuration(mDuration).start();
                anim.setInterpolator(mInterpolator);
            }
            mLastPosition = position;
        } else {
            ViewHelper.clear(holder.itemView);
        }
    }

    protected Animator[] getAnimators(View view){
        return new ObjectAnimator[]{
                ObjectAnimator.ofFloat(view, "translationY", view.getMeasuredHeight(), 0),
        };
    }

}
