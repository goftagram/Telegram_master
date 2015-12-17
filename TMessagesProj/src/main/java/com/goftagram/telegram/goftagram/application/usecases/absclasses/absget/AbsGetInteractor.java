package com.goftagram.telegram.goftagram.application.usecases.absclasses.absget;

/**
 * Created by WORK on 10/30/2015.
 */
public interface AbsGetInteractor<R extends AbsGetRequest, T extends AbsGetInteractorCallback> {

     void getAsync(R request, T callback);

}
