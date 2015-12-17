package com.goftagram.telegram.goftagram.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.goftagram.telegram.goftagram.application.model.Tag;
import com.goftagram.telegram.goftagram.fragment.ChannelListFragment;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.messenger.R;

public class ChannelListActivity extends BaseActivity {

    private final String LOG_TAG = LogUtils.makeLogTag(ChannelListActivity.class.getSimpleName());

    public static final String EXTRA_TITLE = "Extra_Fragment_Title";

    public static final String TAG_FRAGMENT  = "ChannelListFragment";
    private String mTitle;
    private ChannelListFragment mFragment;
    private String mAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);

        mTitle = getIntent().getStringExtra(EXTRA_TITLE);
        mAction = getIntent().getStringExtra(ChannelListFragment.EXTRA_ACTION);

        if( getIntent().getStringExtra(ChannelListFragment.EXTRA_ACTION) == null){
            throw new IllegalArgumentException("Error: no extra action");

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar,mTitle);


        FragmentManager fm = getSupportFragmentManager();

        switch (mAction){
            case ChannelListFragment.ACTION_GET_CHANNELS_BY_CATEGORY:
                String categoryId = getIntent().getStringExtra(ChannelListFragment.EXTRA_CATEGORY_ID);
                mFragment = ChannelListFragment.categoryChannelInstance(categoryId, mTitle);
                break;

            case ChannelListFragment.ACTION_SEARCH_BY_TAG:
                Tag tag = getIntent().getParcelableExtra(ChannelListFragment.EXTRA_TAG);
                mFragment = ChannelListFragment.searchedTagInstance(tag);
                break;
        }



        if(fm.findFragmentByTag(TAG_FRAGMENT) == null){
            fm.beginTransaction()
                    .add(
                            R.id.FrameLayout_ChannelDetailFragment_FragmentContainer,
                            mFragment,
                            TAG_FRAGMENT
                    ).commit();
        }





    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
