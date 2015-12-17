package com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetPresenter;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsListViewModel;

/**
 * Created by WORK on 10/30/2015.
 */
public interface GetCategoryChannelPresenter extends AbsGetPresenter<AbsListViewModel> {

    String KEY_CATEGORY_ID = "Category_Id";
    String KEY_PAGE = "Page";
}
