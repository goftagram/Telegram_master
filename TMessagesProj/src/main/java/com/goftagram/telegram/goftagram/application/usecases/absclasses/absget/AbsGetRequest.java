package com.goftagram.telegram.goftagram.application.usecases.absclasses.absget;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.AbsRequest;

/**
 * Created by WORK on 11/1/2015.
 */
public abstract class AbsGetRequest extends AbsRequest{

    public static final int PRE_PROCESSING_REQUEST          = 0;
    public static final int READING_LOCAL_DATA_SOURCE          = 1;
    public static final int PROCESSING_LOCAL_DATA_SOURCE    = 2;
    public static final int MAKING_NETWORK_TRANSACTION      = 3;
    public static final int PARSING_SERVER_DATA             = 4;
    public static final int PROCESSING_NETWORK_DATA         = 5;
    public static final int CACHING_NETWORK_DATA            = 6;
    public static final int NOTIFYING_OBSERVER              = 7;

}
