package com.goftagram.telegram.goftagram.application.usecases.absclasses.absget;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.AbsViewModel;

/**
 * Created by WORK on 11/1/2015.
 */
public interface AbsListViewModel extends AbsViewModel {

    void onSuccess(String message, int totalServerItems);

    void onFail(String message, int totalServerItems);
}
