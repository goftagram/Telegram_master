package com.goftagram.telegram.goftagram.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.goftagram.telegram.goftagram.application.model.Query;
import com.goftagram.telegram.goftagram.fragment.ChannelListFragment;
import com.goftagram.telegram.messenger.R;


public class SearchableActivity extends BaseActivity{

    public static final String TAG_FRAGMENT  = "ChannelListFragment";

    String mTitle;
    Toolbar mToolbar;
    Fragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        final Intent intent = getIntent();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle   =  getQueryFromIntent(intent);
        setupToolbar(mToolbar, mTitle);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String queryStr =  getQueryFromIntent(intent);
                Query query = new Query(queryStr,0);
                Query.insertQuery(query,SearchableActivity.this);
                init(intent, queryStr);
            }
        }).start();

    }

    private String getQueryFromIntent(Intent intent){
        String query = intent.getStringExtra(SearchManager.QUERY);
        query.trim();
        return query;
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final Intent finalIntent = intent;
        mTitle   =  getQueryFromIntent(intent);
        setupToolbar(mToolbar, mTitle);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String queryStr =  getQueryFromIntent(finalIntent);
                Query query = new Query(queryStr,0);
                Query.insertQuery(query, SearchableActivity.this);
                init(finalIntent, queryStr);
            }
        }).start();


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        getMenuInflater().inflate(R.menu.menu_channel_list, menu);
////        MenuItem searchItem = menu.findItem(R.id.actionbar_search);
////        ViewHelper.setupSearchView(this, searchItem);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void init(Intent intent,String query){

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            FragmentManager fm = getSupportFragmentManager();
            mFragment = ChannelListFragment.searchedViewQueryInstance(query);
            if(fm.findFragmentByTag(TAG_FRAGMENT) == null){
                fm.beginTransaction()
                        .add(R.id.FrameLayout_ChannelListFragment_FragmentContainer,
                                mFragment,
                                TAG_FRAGMENT)
                        .commit();
            }

        }else{

            finish();
        }
    }

    public void setupToolbar(Toolbar toolbar,String title){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    }


}
