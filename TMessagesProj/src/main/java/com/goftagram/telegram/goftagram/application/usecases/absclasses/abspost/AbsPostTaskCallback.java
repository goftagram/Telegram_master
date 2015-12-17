package com.goftagram.telegram.goftagram.application.usecases.absclasses.abspost;

/**
 * Created by WORK on 11/1/2015.
 */
public interface AbsPostTaskCallback<T extends AbsPostRequest,S> {

    void onHit(T request, S dataHolder);
    void onMiss(T request, S dataHolder);
}
