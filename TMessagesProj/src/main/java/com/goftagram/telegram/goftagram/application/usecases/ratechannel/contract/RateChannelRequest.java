package com.goftagram.telegram.goftagram.application.usecases.ratechannel.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.abspost.AbsPostRequest;


public class RateChannelRequest extends AbsPostRequest {

    public int mRate;
    public String mTelegramChannelId;


}
