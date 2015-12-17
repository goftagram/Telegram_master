package com.goftagram.telegram.goftagram.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dd.CircularProgressButton;
import com.goftagram.telegram.goftagram.activity.GoftagramMainActivity;
import com.goftagram.telegram.goftagram.application.model.MainUser;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.MainUserDao;
import com.goftagram.telegram.goftagram.application.usecases.signuplogin.UserController;
import com.goftagram.telegram.goftagram.util.FontUtils;
import com.goftagram.telegram.goftagram.util.LogUtils;
import com.goftagram.telegram.goftagram.util.NetworkUtils;
import com.goftagram.telegram.goftagram.util.UniqueIdGenerator;
import com.goftagram.telegram.messenger.R;
import com.goftagram.telegram.messenger.UserConfig;

import java.util.Map;


public class SignUpFragment extends BaseFragment {


    public static final String LOG_TAG = LogUtils.makeLogTag(SignUpFragment.class.getSimpleName());



    private ImageView mBackground;
    private CircularProgressButton mCircularProgressButton;

    String mFirstName;
    String mLastName;
    String mImei;
    String mPhoneNumber;

    EditText mFirstNameEditText,mLastNameEditText,mPhoneNumberEditText;

    int mSignUpRequestId;
    boolean mHasSentRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mFirstNameEditText      = (EditText) view.findViewById(R.id.editText_firstName);
        mLastNameEditText       = (EditText) view.findViewById(R.id.editText_lastName);
        mPhoneNumberEditText    = (EditText) view.findViewById(R.id.editText_phoneNumber);
        TextView title          = (TextView)view.findViewById(R.id.title);
        mCircularProgressButton = (CircularProgressButton) view.findViewById(R.id.btnWithText);
        mBackground = (ImageView) view.findViewById(R.id.signupfragment_background);

        FontUtils.settingFont(mFirstNameEditText,getActivity());
        FontUtils.settingFont(mLastNameEditText,getActivity());
        FontUtils.settingFont(mPhoneNumberEditText,getActivity());
        FontUtils.settingFont(title, getActivity());
        FontUtils.settingFont(mCircularProgressButton, getActivity());


        TelephonyManager mngr = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        mImei = mngr.getDeviceId();
        UserController.getInstance(getActivity()).updateMainUserImei(mImei);

        Glide.with(getActivity())
                .load(R.drawable.signupfragment_background)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mBackground);




        mCircularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!NetworkUtils.isOnline(getActivity())){

                    mCircularProgressButton.setProgress(-1);
                    mCircularProgressButton.setErrorText(getString(R.string.no_network_connection));
                    sHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCircularProgressButton.setProgress(0);

                        }
                    }, 4000);
                    return;
                }

                mFirstName = mFirstNameEditText.getText().toString().trim();
                mLastName = mLastNameEditText.getText().toString().trim();
                mPhoneNumber = mPhoneNumberEditText.getText().toString().trim();


                mCircularProgressButton.setIndeterminateProgressMode(true);
                mCircularProgressButton.setProgress(50);
                mHasSentRequest = true;

                String firstName = UserConfig.getCurrentUser().first_name;
                String lastName = UserConfig.getCurrentUser().last_name;
                String phone = UserConfig.getCurrentUser().phone;


                UserController.getInstance(getActivity()).updateMainUserFirstName(firstName);
                UserController.getInstance(getActivity()).updateMainUserLastName(lastName);
                UserController.getInstance(getActivity()).updateMainUserPhoneNumber(phone);


                mSignUpRequestId = UniqueIdGenerator.getInstance().getNewId();
                UserController.getInstance(getActivity()).doSignUpAsync(mSignUpRequestId);
            }
        });

        MainUser mainUser = MainUserDao.getInstance(getActivity()).getMainUser();
        if(mainUser.hasLogIn()){
            Intent intent = new Intent(getActivity(), GoftagramMainActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHasSentRequest) {
            mSignUpRequestId = UniqueIdGenerator.getInstance().getNewId();
            UserController.getInstance(getActivity()).doSignUpAsync(mSignUpRequestId);
        }
    }

    public void onEventMainThread(UserController.UserSignUpUiEvent event) {
        if (event.getMessageId() == mSignUpRequestId) {
            LogUtils.LOGI(LOG_TAG, "UserSignUpUiEvent: " + event.getMessage());
            mHasSentRequest = false;

            if(event.getStatus().equals(UserController.FAIL)){
                final Map<String,String[]> errors = event.getErrors();


                mCircularProgressButton.setProgress(-1);

                sHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCircularProgressButton.setProgress(0);
                    }
                }, 4000);

                if(errors == null)return;

                if(errors.get(UserController.UserSignUpUiEvent.FIRST_NAME_ERROR_KEY)!= null){
                    mCircularProgressButton.setErrorText(
                            errors.get(UserController.UserSignUpUiEvent.FIRST_NAME_ERROR_KEY)[0]);
                    return;
                }

                if(errors.get(UserController.UserSignUpUiEvent.LAST_NAME_ERROR_KEY)!= null){
                    mCircularProgressButton.setErrorText(
                            errors.get(UserController.UserSignUpUiEvent.LAST_NAME_ERROR_KEY)[0]);
                    return;
                }

                if(errors.get(UserController.UserSignUpUiEvent.PHONE_ERROR_KEY)!= null){

                    mCircularProgressButton.setErrorText(
                            errors.get(UserController.UserSignUpUiEvent.PHONE_ERROR_KEY)[0]);
                    return;
                }

                if(errors.get(UserController.UserSignUpUiEvent.IMEI_ERROR_KEY)!= null){

                    mCircularProgressButton.setErrorText(
                            errors.get(UserController.UserSignUpUiEvent.IMEI_ERROR_KEY)[0]);
                    return;
                }

            }
        }
    }

    public void onEventMainThread(UserController.UserLogInUiEvent event) {


        if (event.getMessageId() == mSignUpRequestId) {
            final String message = event.getMessage();
            Log.i(LOG_TAG, "UserLogInUiEvent: " + event.getMessage());

            if(event.getStatus().equals(UserController.SUCCESS)){
                mCircularProgressButton.setProgress(100);
                sHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(), GoftagramMainActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();

                    }
                }, 2000);

                return;
            }else{

                mCircularProgressButton.setProgress(-1);
                mCircularProgressButton.setErrorText(message);
                sHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCircularProgressButton.setProgress(0);

                    }
                }, 4000);
            }

            mHasSentRequest = false;

        }
    }



}
