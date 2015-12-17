package com.goftagram.telegram.goftagram.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.goftagram.telegram.goftagram.application.MyApplication;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.UiLessViewModel;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract.GetNewVersionViewModel;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.implementation.GetNewVersionPresenterImp;
import com.goftagram.telegram.goftagram.application.usecases.getnewversion.implementation.NewApkEntityImp;
import com.goftagram.telegram.goftagram.application.usecases.postusermetadata.implementation.PostUserMetaDataPresenterImp;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.MainUserDao;
import com.goftagram.telegram.goftagram.fragment.CategoryListFragment;
import com.goftagram.telegram.goftagram.fragment.ChannelListFragment;
import com.goftagram.telegram.goftagram.gcm.GcmManager;
import com.goftagram.telegram.goftagram.helper.ViewHelper;
import com.goftagram.telegram.goftagram.myconst.MyUrl;
import com.goftagram.telegram.goftagram.provider.GoftagramContract;
import com.goftagram.telegram.goftagram.util.Dialogs;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.messenger.R;
import com.goftagram.telegram.ui.LaunchActivity;


public class GoftagramMainActivity extends BaseActivity implements GetNewVersionViewModel, UiLessViewModel{

    private final String LOG_TAG = LogUtils.makeLogTag(GoftagramMainActivity.class.getSimpleName());

    private static final int INSTALLING_NEW_APK_REQUEST_CODE = 500;

    Toolbar toolbar;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;


    AbsGetPresenter mGetNewVersionPresenter;
    int mGetNewVersionRequestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goftagram_main);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, getResources().getString(R.string.app_name));

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = mNavigationView.getHeaderView(0);

        ImageView mHeaderView = (ImageView) headerLayout.findViewById(R.id.nav_header_imageView);

        TextView textView = (TextView) headerLayout.findViewById(R.id.username_textview);

        textView.setText(MainUserDao.getInstance(GoftagramMainActivity.this)
                .getMainUser().getFirstName() + " " +
                MainUserDao.getInstance(GoftagramMainActivity.this).getMainUser().getLastName());

        Glide.with(GoftagramMainActivity.this)
                .load(R.drawable.nav_background)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mHeaderView);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

//      checkNewVersion();
      GcmManager.getInstance(this).registerGcm();

        PostUserMetaDataPresenterImp.getInstance(this).postAsync(this,null);



    }

    private void checkNewVersion() {

        mGetNewVersionPresenter = GetNewVersionPresenterImp.getInstance(this);
        mGetNewVersionRequestId = mGetNewVersionPresenter.getAsync(this, null);

    }

   
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(MyApplication.whatMarket().equals(MyApplication.cafebazaar)){
                exitIfUserHasRated();
            }else{
                super.onBackPressed();
            }
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
    protected void onResume() {
        super.onResume();
//        mGetNewVersionPresenter.register(this, mGetNewVersionRequestId);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mGetNewVersionPresenter.unregister(this, mGetNewVersionRequestId);
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
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.actionbar_refresh:
                LogUtils.LOGI(LOG_TAG, "Refresh Button");
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        getContentResolver().delete(GoftagramContract.ALL_URI, null, null);
                        Glide.get(GoftagramMainActivity.this).clearDiskCache();
                    }
                }).start();
        }
        return super.onOptionsItemSelected(item);
    }


    public void setupToolbar(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        menuItem.setChecked(true);

                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                finish();
                                break;
                            case R.id.nav_new_channel: {
                                Intent intent = new Intent(GoftagramMainActivity.this, NewChannelActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                GoftagramMainActivity.this.startActivity(intent);
                            }
                            break;
                            case R.id.nav_contact_us: {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                        "https://telegram.me/Channel_admin"
                                ));
                                startActivity(intent);
                            }
                            break;
                            case R.id.nav_about_us:
                                Dialogs.getInstanse().ShowTermsOfUseDialog(GoftagramMainActivity.this, MyUrl.ABOUT_US_URL);
                                break;
                        }

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void setupViewPager(ViewPager viewPager) {

        Adapter adapter = new Adapter(getSupportFragmentManager(), this);
        viewPager.setCurrentItem(1);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onSuccess(String message, String mUrl, boolean isUpdated) {
        String newApkFilePath = NewApkEntityImp.getInstance(this).getNewApkFilePath();
        if (!isUpdated) {
            makeApkInstallationRequest(newApkFilePath);
        }
    }


    private void makeApkInstallationRequest(String newApkFilePath) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        LogUtils.LOGI(LOG_TAG, newApkFilePath);
        intent.setDataAndType(Uri.parse("file://" + newApkFilePath), "application/vnd.android.package-archive");
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivityForResult(intent, INSTALLING_NEW_APK_REQUEST_CODE);
        } catch (Exception ex) {
            return;
        }
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
