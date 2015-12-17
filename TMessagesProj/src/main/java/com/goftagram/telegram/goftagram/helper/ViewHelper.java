package com.goftagram.telegram.goftagram.helper;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.RatingBar;

import com.goftagram.telegram.goftagram.activity.SearchableActivity;
import com.goftagram.telegram.goftagram.util.SoftInputUtils;
import com.goftagram.telegram.messenger.R;

/**
 * Created by mhossein on 9/29/15.
 */
public class ViewHelper {

    public static void clear(View v) {
        ViewCompat.setAlpha(v, 1);
        ViewCompat.setScaleY(v, 1);
        ViewCompat.setScaleX(v, 1);
        ViewCompat.setTranslationY(v, 0);
        ViewCompat.setTranslationX(v, 0);
        ViewCompat.setRotation(v, 0);
        ViewCompat.setRotationY(v, 0);
        ViewCompat.setRotationX(v, 0);
        // @TODO https://code.google.com/p/android/issues/detail?id=80863
        ViewCompat.setPivotY(v, v.getMeasuredHeight() / 2);
//        v.setPivotY(v.getMeasuredHeight() / 2);

        ViewCompat.setPivotX(v, v.getMeasuredWidth() / 2);
        ViewCompat.animate(v).setInterpolator(null);
    }


    public ViewHelper() {
        super();
    }

    public static void colorizeRatingBar(RatingBar ratingBar, Context context){

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(context.getResources().getColor(R.color.yellow_rating_star)
                , PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(context.getResources().getColor(R.color.yellow_rating_star)
                , PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(context.getResources()
                .getColor(R.color.detail_activity_default_background), PorterDuff.Mode.SRC_ATOP);
    }

    public static void setupSearchView(final Context context,MenuItem searchItem){

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint(context.getString(R.string.drawer_search_item_text));

        SearchManager searchManager = (SearchManager) context
                .getSystemService(Context.SEARCH_SERVICE);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(context.getApplicationContext(),SearchableActivity.class)
        ));

        searchView.setSubmitButtonEnabled(true);

        AutoCompleteTextView searchPlateView =
                (AutoCompleteTextView)searchView.findViewById(
                        android.support.v7.appcompat.R.id.search_src_text
                );
        if (searchPlateView != null) {
            searchPlateView.setTextColor(Color.WHITE);
            searchPlateView.setHintTextColor(Color.WHITE);

        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // Hide soft keyboard
                SoftInputUtils.hideSoftInput(context, searchView);
                searchView.onActionViewCollapsed();
                searchView.setQuery(null, false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {

                return false;
            }
        });

    }

}
