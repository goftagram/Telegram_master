package com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract;

/**
 * Created by WORK on 11/2/2015.
 */
public class GetNewVersionResponse {

    public static final int SUCCESS = 1;
    public static final int FAIL = 0;


    public int mTransactionId;
    public int mState;
    public String mMessage;

    public String mUrl;
    public boolean mIsUpdated;


}
