package com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetPresenter;

/**
 * Created by WORK on 10/30/2015.
 */
public interface GetChannelMetaDataPresenter extends AbsGetPresenter<GetChannelMetaDataViewModel> {

    public static final String KEY_TELEGRAM_CHANNEL_ID = "Telegram_Channel_Id";
    public static final String KEY_PAGE = "Page";

    public static final String KEY_MESSAGE = "Message";
    public static final String KEY_TELEGRAM_CHANNEL = "Telegram_Channel";
    public static final String KEY_RELATED_TELEGRAM_CHANNEL_LIST = "Related_Telegram_Channel_List";
    public static final String KEY_TAG_LIST = "Tag_List";
    public static final String KEY_LOCAL_COMMENT_LIST = "Local_Comment_List";
    public static final String KEY_DOWNLOADED_COMMENT_LIST = "Downloaded_Comment_List";
    public static final String KEY_HAS_MORE_ITEMS = "Has_More_Items";


    public static final String KEY_RATE = "Rate";

}
