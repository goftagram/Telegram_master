package com.goftagram.telegram.goftagram.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.goftagram.telegram.goftagram.application.MyApplication;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.UiLessViewModel;
import com.goftagram.telegram.goftagram.application.usecases.postusermetadata.implementation.PostUserMetaDataPresenterImp;
import com.goftagram.telegram.goftagram.fragment.CategoryListFragment;
import com.goftagram.telegram.goftagram.fragment.ChannelListFragment;
import com.goftagram.telegram.goftagram.gcm.GcmManager;
import com.goftagram.telegram.goftagram.helper.ViewHelper;
import com.goftagram.telegram.goftagram.util.Dialogs;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.messenger.R;
import com.goftagram.telegram.ui.LaunchActivity;


public class GoftagramMainActivity extends BaseActivity implements UiLessViewModel {

    private final String LOG_TAG = LogUtils.makeLogTag(GoftagramMainActivity.class.getSimpleName());

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goftagram_main);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, getResources().getString(R.string.channel_drawer_menu_name));

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        GcmManager.getInstance(this).registerGcm();
        PostUserMetaDataPresenterImp.getInstance(this).postAsync(this, null);


    }

    @Override
    public void onBackPressed() {

        if (MyApplication.whatMarket().equals(MyApplication.cafebazaar)) {
            exitIfUserHasRated();
        } else {
            super.onBackPressed();
        }

    }

    private void exitIfUserHasRated() {

        if (hasUserRated()) {
            super.onBackPressed();
        } else {
            Dialogs.getInstanse().showUserRateRequestDialog(
                    GoftagramMainActivity.this,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("bazaar://details?id=" + getApplicationContext().getPackageName()));
                            intent.setPackage("com.farsitel.bazaar");

                            try {
                                startActivity(intent);
                                dialogInterface.dismiss();
                                finish();
                            } catch (Exception ex) {
                                setHasUserRated(true);
                                dialogInterface.dismiss();
                                finish();
                            }
                        }
                    },
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setHasUserRated(true);
                            dialogInterface.dismiss();
                            finish();
                        }
                    }
            );

        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent(this, LaunchActivity.class);
        startActivity(intent);
        super.finish();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_channel_list, menu);
        MenuItem searchItem = menu.findItem(R.id.actionbar_search);
        ViewHelper.setupSearchView(this, searchItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupToolbar(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    }

//    private void setupDrawerContent(NavigationView navigationView) {
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem menuItem) {
////                        menuItem.setChecked(true);
//
//                        switch (menuItem.getItemId()) {
//                            case R.id.nav_home:
//                                finish();
//                                break;
//                            case R.id.nav_new_channel: {
//                                Intent intent = new Intent(GoftagramMainActivity.this, NewChannelActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                GoftagramMainActivity.this.startActivity(intent);
//                            }
//                            break;
//                            case R.id.nav_contact_us: {
//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
//                                        "https://telegram.me/Channel_admin"
//                                ));
//                                startActivity(intent);
//                            }
//                            break;
//                            case R.id.nav_about_us:
//                                Dialogs.getInstanse().ShowTermsOfUseDialog(GoftagramMainActivity.this, MyUrl.ABOUT_US_URL);
//                                break;
//                        }
//
//
//                        return true;
//                    }
//                });
//    }

    private void setupViewPager(ViewPager viewPager) {

        Adapter adapter = new Adapter(getSupportFragmentManager(), this);
        viewPager.setCurrentItem(1);
        viewPager.setAdapter(adapter);
    }


    static class Adapter extends FragmentStatePagerAdapter {

        private Context mContext;

        public Adapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }


        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = ChannelListFragment.promotedInstance(mContext);
            } else if (position == 1) {
                fragment = CategoryListFragment
                        .newInstance(mContext.getString(R.string.category));
            } else if (position == 2) {
                fragment = ChannelListFragment.topRatedInstance(mContext);

            } else if (position == 3) {
                fragment = ChannelListFragment.newChannelInstance(mContext);
            }

            return fragment;
        }

        @Override
        public int getCount() {

            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "";
            if (position == 0) {
                title = mContext.getString(R.string.promoted_channel);
            } else if (position == 1) {
                title = mContext.getString(R.string.category);
            } else if (position == 2) {
                title = mContext.getString(R.string.top_rated_channel);
            } else if (position == 3) {
                title = mContext.getString(R.string.new_channel);
            }
            return title;
        }
    }

    public boolean hasUserRated() {
        final SharedPreferences prefs = GoftagramMainActivity.this.getSharedPreferences("HasUserRated", 0);
        return prefs.getBoolean("HasUserRated", false);

    }


    public void setHasUserRated(boolean hasUserRated) {

        final SharedPreferences prefs = GoftagramMainActivity.this.getSharedPreferences("HasUserRated", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("HasUserRated", hasUserRated);
        editor.apply();

    }
}
