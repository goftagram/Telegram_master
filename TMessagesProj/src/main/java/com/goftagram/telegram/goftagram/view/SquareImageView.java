package com.goftagram.telegram.goftagram.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by WORK on 11/13/2015.
 */
public class SquareImageView extends ImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int  selectedDimension = height;

        if(height == 0){
            selectedDimension = width;
        }else {
            if(width != 0){
                selectedDimension = Math.min(height,width);
            }
        }

        setMeasuredDimension(selectedDimension, selectedDimension);




    }

}