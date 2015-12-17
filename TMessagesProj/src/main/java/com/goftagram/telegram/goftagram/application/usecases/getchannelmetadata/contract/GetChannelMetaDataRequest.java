package com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsPaginationGetRequest;


public class GetChannelMetaDataRequest extends AbsPaginationGetRequest{

    public String mTelegramChannelId;

    public int mServerPageRequest;

}
