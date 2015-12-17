package com.goftagram.telegram.goftagram.application.usecases.absclasses.abspost;

/**
 * Created by WORK on 11/1/2015.
 */
public abstract class AbsPostResponse {

    public static final int SUCCESS = 1;
    public static final int FAIL = 0;

    public int mRequestId;
    public int mState;
    public String mMessage;


}

