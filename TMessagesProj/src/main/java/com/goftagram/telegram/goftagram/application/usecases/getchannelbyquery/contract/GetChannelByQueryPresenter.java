package com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetPresenter;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsListViewModel;

/**
 * Created by WORK on 10/30/2015.
 */
public interface GetChannelByQueryPresenter extends AbsGetPresenter<AbsListViewModel> {

    String KEY_QUERY                    = "Query";
    String KEY_PAGE                     = "Page";
}
