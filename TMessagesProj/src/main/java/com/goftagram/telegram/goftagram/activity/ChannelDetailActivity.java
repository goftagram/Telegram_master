package com.goftagram.telegram.goftagram.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.goftagram.telegram.goftagram.fragment.ChannelDetailFragment;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.messenger.R;

public class ChannelDetailActivity extends BaseActivity{

    private final String LOG_TAG = LogUtils.makeLogTag(ChannelDetailActivity.class.getSimpleName());


    public static final String EXTRA_TELEGRAM_CHANNEL_TITLE = "Extra_Telegram_Channel_Title";
    public static final String EXTRA_TELEGRAM_CHANNEL_ID = "Extra_Telegram_Channel_ID";
    public static final String EXTRA_COME_FROM_NOTIFICATION = "Extra_Come_From_Notification";
    public static final String TAG_FRAGMENT  = "ChannelDetailActivity";

    Toolbar mToolbar;
    boolean isComeFromNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_detail);

        isComeFromNotification = getIntent().getBooleanExtra(EXTRA_COME_FROM_NOTIFICATION,false);

        String title = getIntent().getStringExtra(EXTRA_TELEGRAM_CHANNEL_TITLE);
        String telegramChannelId = getIntent().getStringExtra(EXTRA_TELEGRAM_CHANNEL_ID);


        FragmentManager fm = getSupportFragmentManager();
        ChannelDetailFragment fragment = ChannelDetailFragment.newInstance(
                telegramChannelId,title
        );

        if(fm.findFragmentByTag(TAG_FRAGMENT) == null){
            fm.beginTransaction()
                    .add(
                            R.id.FrameLayout_ChannelDetailFragment_FragmentContainer,
                            fragment,
                            TAG_FRAGMENT
                    ).commit();
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        isComeFromNotification = intent.getBooleanExtra(EXTRA_COME_FROM_NOTIFICATION, false);
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


    @Override
    public void finish() {
        if(isComeFromNotification){
            Intent intent = new Intent(ChannelDetailActivity.this,GoftagramMainActivity.class);
            startActivity(intent);
        }
        super.finish();
    }

}
