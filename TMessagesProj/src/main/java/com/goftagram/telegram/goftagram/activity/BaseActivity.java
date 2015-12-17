package com.goftagram.telegram.goftagram.activity;

import android.app.PendingIntent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.goftagram.telegram.goftagram.application.model.NullEvent;
import com.goftagram.telegram.goftagram.receiver.AlarmReceiver;
import com.goftagram.telegram.goftagram.receiver.NetworkChangeReceiver;
import com.goftagram.telegram.goftagram.util.ComponentUtils;
import com.goftagram.telegram.goftagram.util.NetworkUtils;
import com.goftagram.telegram.messenger.R;

import de.greenrobot.event.EventBus;

public class BaseActivity extends AppCompatActivity{

  
    static Handler sHandler = new Handler();
    static boolean mIsVisible = false;


    @Override
    protected void onResume() {
        super.onResume();
        setIsVisible(true);
        EventBus.getDefault().register(this);
        if(!NetworkUtils.isOnline(BaseActivity.this)){
            Toast.makeText(BaseActivity.this,
                    getResources().getString(R.string.no_network_connection)
                    , Toast.LENGTH_SHORT
            ).show();
        }
        ComponentUtils.setComponentEnable(this, NetworkChangeReceiver.class);
        PendingIntent pendingIntent = AlarmReceiver.getDatabaseDeletionPendingIntent(this);
        AlarmReceiver.setAlarm(this, 60 * 45, pendingIntent);
//        AlarmReceiver.setAlarm(this, 3, pendingIntent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        setIsVisible(false);
        EventBus.getDefault().unregister(this);
        sHandler.removeCallbacksAndMessages(null);
        ComponentUtils.setComponentDisabled(this, NetworkChangeReceiver.class);
    }

    public void onEventMainThread(NullEvent event){

    }

    public synchronized static boolean isVisible() {
        return BaseActivity.mIsVisible;
    }

    public synchronized static void setIsVisible(boolean isVisible) {
        BaseActivity.mIsVisible = isVisible;
    }
}
