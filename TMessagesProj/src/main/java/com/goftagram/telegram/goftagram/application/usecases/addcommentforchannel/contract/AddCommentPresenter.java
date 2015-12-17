package com.goftagram.telegram.goftagram.application.usecases.addcommentforchannel.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.abspost.AbsPostPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataViewModel;

/**
 * Created by WORK on 10/30/2015.
 */
public interface AddCommentPresenter extends AbsPostPresenter<GetChannelMetaDataViewModel> {

    String KEY_COMMENT = "comment";
    String KEY_TELEGRAM_CHANNEL_ID = "Telegram_Channel_Id";
}
