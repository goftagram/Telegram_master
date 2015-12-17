package com.goftagram.telegram.goftagram.application.usecases.getcategorychannel.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsPaginationGetRequest;


public class GetCategoryChannelRequest extends AbsPaginationGetRequest{

    public String mCategoryId;

    public int mServerPageRequest;

}
