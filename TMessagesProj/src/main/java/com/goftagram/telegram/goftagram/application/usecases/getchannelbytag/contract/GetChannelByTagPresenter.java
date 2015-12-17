package com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetPresenter;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsListViewModel;

/**
 * Created by WORK on 10/30/2015.
 */
public interface GetChannelByTagPresenter extends AbsGetPresenter<AbsListViewModel> {

    String KEY_TAG_ID = "Tag_Id";
    String KEY_PAGE = "Page";
}
