package com.goftagram.telegram.goftagram.application.usecases.absclasses.abspost;

public interface AbsPostInteractorCallback<R extends AbsPostResponse> {

    void onResponse(final R response);

}
