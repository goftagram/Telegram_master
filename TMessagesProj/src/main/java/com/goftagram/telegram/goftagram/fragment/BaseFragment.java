package com.goftagram.telegram.goftagram.fragment;


import android.os.Handler;
import android.support.v4.app.Fragment;
import com.goftagram.telegram.goftagram.application.model.NullEvent;


import de.greenrobot.event.EventBus;


public class BaseFragment extends Fragment{


    static Handler sHandler = new Handler();


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        sHandler.removeCallbacksAndMessages(null);
    }

    public void onEventMainThread(NullEvent event){

    }
}
