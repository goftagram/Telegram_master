package com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetInteractor;

/**
 * Created by WORK on 10/30/2015.
 */
public interface GetChannelMetaDataInteractor extends AbsGetInteractor<GetChannelMetaDataRequest,GetChannelMetaDataInteractorCallback>{


    int  STATE_IDLE                             = 1;

    String KEY_HAS_MORE_COMMENTS                = "Has_More_Comments";
    String KEY_RELATED_TELEGRAM_CHANNEL_LIST    = "Related_Telegram_Channel_List";
    String KEY_TAG_LIST                         = "Tag_List";
    String KEY_LOCAL_COMMENT_LIST               = "Local_Comment_List";
    String KEY_DOWNLOADED_COMMENT_LIST          = "Downloaded_Comment_List";
    String KEY_TELEGRAM_CHANNEL                 = "Telegram_Channel";

    String KEY_IS_CATEGORY_EMPTY                = "Is_Category_Empty";


}
