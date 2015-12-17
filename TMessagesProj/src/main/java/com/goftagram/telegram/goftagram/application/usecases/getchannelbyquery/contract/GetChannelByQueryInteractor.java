package com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetInteractor;

/**
 * Created by WORK on 10/30/2015.
 */
public interface GetChannelByQueryInteractor extends AbsGetInteractor<GetChannelByQueryRequest,GetChannelByQueryInteractorCallback>{

    public static String KEY_TOTAL_CHANNELS = "Total_Channels";
    public static String KEY_IS_CATEGORY_EMPTY = "Is_Category_Empty";


}
