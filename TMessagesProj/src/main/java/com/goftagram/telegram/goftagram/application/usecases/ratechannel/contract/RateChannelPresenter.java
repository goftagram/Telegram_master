package com.goftagram.telegram.goftagram.application.usecases.ratechannel.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.abspost.AbsPostPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataViewModel;

/**
 * Created by WORK on 11/10/2015.
 */
public interface RateChannelPresenter extends AbsPostPresenter<GetChannelMetaDataViewModel> {

    String KEY_RATE = "rate";
    String KEY_TELEGRAM_CHANNEL_ID = "Telegram_Channel_Id";
}

