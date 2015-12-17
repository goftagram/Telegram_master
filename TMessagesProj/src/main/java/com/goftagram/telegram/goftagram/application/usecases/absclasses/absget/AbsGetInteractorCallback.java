package com.goftagram.telegram.goftagram.application.usecases.absclasses.absget;

public interface AbsGetInteractorCallback<R extends AbsGetResponse> {

    void onResponse(final R response);

}
