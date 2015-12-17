package com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;

/**
 * Created by WORK on 11/2/2015.
 */
public interface GetNewVersionTaskCallback {

    void onHit(GetNewVersionRequest request, NameValueDataHolder nameValueDataHolder);
    void onMiss(GetNewVersionRequest request, NameValueDataHolder nameValueDataHolder);
}
