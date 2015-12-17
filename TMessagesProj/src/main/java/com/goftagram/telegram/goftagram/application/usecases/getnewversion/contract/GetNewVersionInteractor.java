package com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract;


/**
 * Created by WORK on 11/2/2015.
 */
public interface GetNewVersionInteractor {

    void GetNewVersionAsync(GetNewVersionRequest request, GetNewVersionInteractorCallback callback);

}


