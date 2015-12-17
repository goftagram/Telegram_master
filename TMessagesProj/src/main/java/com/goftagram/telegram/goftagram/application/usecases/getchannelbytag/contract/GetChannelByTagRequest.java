package com.goftagram.telegram.goftagram.application.usecases.getchannelbytag.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsPaginationGetRequest;


public class GetChannelByTagRequest extends AbsPaginationGetRequest{

    public String mTagId;

    public int mServerPageRequest;

}
