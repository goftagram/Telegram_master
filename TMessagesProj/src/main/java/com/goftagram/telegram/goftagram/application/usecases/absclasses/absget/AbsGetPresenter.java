package com.goftagram.telegram.goftagram.application.usecases.absclasses.absget;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;

/**
 * Created by WORK on 11/1/2015.
 */
public interface AbsGetPresenter<T> {

    void register(T viewModel, int requestId);

    void unregister(T viewModel, int requestId);

    int getAsync(T viewModel, NameValueDataHolder dataHolder);

}
