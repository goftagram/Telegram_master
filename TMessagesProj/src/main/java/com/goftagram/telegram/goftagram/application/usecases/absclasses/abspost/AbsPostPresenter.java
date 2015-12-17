package com.goftagram.telegram.goftagram.application.usecases.absclasses.abspost;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;

/**
 * Created by WORK on 11/1/2015.
 */
public interface AbsPostPresenter<T> {

    void register(T viewModel, int requestId);

    void unregister(T viewModel, int requestId);

    int postAsync(T viewModel, NameValueDataHolder dataHolder);

}
