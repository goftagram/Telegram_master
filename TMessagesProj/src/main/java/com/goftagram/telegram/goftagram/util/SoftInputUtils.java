package com.goftagram.telegram.goftagram.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by WORK on 11/11/2015.
 */
public class SoftInputUtils {
    public static void showSoftInput(Context context,View view){
        final InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.showSoftInput(view, 0);

    }

    public static void hideSoftInput(Context context,View view){
        final InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
}
