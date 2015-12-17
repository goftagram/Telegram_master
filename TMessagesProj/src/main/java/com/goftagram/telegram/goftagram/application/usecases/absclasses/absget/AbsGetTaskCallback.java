package com.goftagram.telegram.goftagram.application.usecases.absclasses.absget;

/**
 * Created by WORK on 11/1/2015.
 */
public interface AbsGetTaskCallback<T extends AbsGetRequest,S> {

    void onHit(T request, S dataHolder);
    void onMiss(T request, S dataHolder);
}
