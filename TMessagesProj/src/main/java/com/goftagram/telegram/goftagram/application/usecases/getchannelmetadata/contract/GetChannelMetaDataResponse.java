package com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract;


import com.goftagram.telegram.goftagram.application.usecases.absclasses.absget.AbsGetResponse;
import com.goftagram.telegram.goftagram.application.model.Comment;
import com.goftagram.telegram.goftagram.application.model.Tag;
import com.goftagram.telegram.goftagram.application.model.TelegramChannel;

import java.util.List;

public class GetChannelMetaDataResponse extends AbsGetResponse {

    public TelegramChannel mTelegramChannel;
    public List<TelegramChannel> mRelatedTelegramChannelList;
    public List<Tag> mTagList;
    public List<Comment> mLocalCommentList;
    public List<Comment> mDownloadedCommentList;

    public boolean mHasMoreComment;

}
