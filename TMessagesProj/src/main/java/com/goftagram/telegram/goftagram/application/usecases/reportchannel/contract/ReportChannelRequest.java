package com.goftagram.telegram.goftagram.application.usecases.reportchannel.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.abspost.AbsPostRequest;


public class ReportChannelRequest extends AbsPostRequest {

    public String mReportText;
    public String mTelegramChannelId;


}
