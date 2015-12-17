package com.goftagram.telegram.goftagram.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goftagram.telegram.goftagram.util.Utils;

public class LoadingLayout extends RelativeLayout {
	
	public LoadingLayout(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}
	public LoadingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		// TODO Auto-generated constructor stub
	}
	public LoadingLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		// TODO Auto-generated constructor stub
	}
	
	private void init(){
		TextView textView = new TextView(getContext());


		textView.setText("Loading ...");

		int padding = Utils.DpToPxInteger(getContext(), 10);

		textView.setPadding(padding, padding, padding, padding);

		setGravity(Gravity.CENTER);

		addView(textView);
	}

}
