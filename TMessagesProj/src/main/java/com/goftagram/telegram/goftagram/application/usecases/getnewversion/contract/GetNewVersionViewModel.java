package com.goftagram.telegram.goftagram.application.usecases.getnewversion.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.AbsViewModel;

/**
 * Created by WORK on 11/2/2015.
 */
public interface GetNewVersionViewModel extends AbsViewModel{

    void onSuccess(String message, String mUrl, boolean isUpdated);

}
