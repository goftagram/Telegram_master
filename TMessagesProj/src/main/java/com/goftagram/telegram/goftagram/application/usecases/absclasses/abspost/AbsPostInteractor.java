package com.goftagram.telegram.goftagram.application.usecases.absclasses.abspost;

/**
 * Created by WORK on 10/30/2015.
 */
public interface AbsPostInteractor<R extends AbsPostRequest, T extends AbsPostInteractorCallback> {

     void postAsync(R request, T callback);

}
