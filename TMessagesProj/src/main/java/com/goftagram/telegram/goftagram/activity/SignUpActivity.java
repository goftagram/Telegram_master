package com.goftagram.telegram.goftagram.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.goftagram.telegram.goftagram.fragment.SignUpFragment;
import com.goftagram.telegram.messenger.R;


public class SignUpActivity extends BaseActivity {

    public static final String LOG_TAG = SignUpActivity.class.getSimpleName();

    public static final String TAG_FRAGMENT  = "SignUpActivity";

    public static final boolean DEBUGE = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        FragmentManager fm = getSupportFragmentManager();

        boolean mustGoToNewChannelActivity = getIntent().getBooleanExtra(SignUpFragment.EXTRA_MUST_GO_TO_NEW_CHANNEL_ACTIVITY,false);
        SignUpFragment fragment = SignUpFragment.newInstance(mustGoToNewChannelActivity);

        if(fm.findFragmentByTag(TAG_FRAGMENT) == null){
            fm.beginTransaction()
                    .add(
                            R.id.FrameLayout_SignUpFragment_FragmentContainer,
                            fragment,
                            TAG_FRAGMENT
                    ).commit();
        }



    }



}
