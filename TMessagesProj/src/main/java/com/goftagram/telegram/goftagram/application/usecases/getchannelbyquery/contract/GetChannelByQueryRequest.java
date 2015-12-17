package com.goftagram.telegram.goftagram.application.usecases.getchannelbyquery.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsPaginationGetRequest;


public class GetChannelByQueryRequest extends AbsPaginationGetRequest{


    public String mQuery;
    public boolean mSearchInsideDescription;
    public boolean mSearchInsideTitle;
    public boolean mCategoryId;


    public int mServerPageRequest;

}
