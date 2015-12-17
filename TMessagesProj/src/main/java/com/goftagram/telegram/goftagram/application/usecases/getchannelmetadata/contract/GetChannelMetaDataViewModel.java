package com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.AbsViewModel;

/**
 * Created by WORK on 11/4/2015.
 */
public interface GetChannelMetaDataViewModel extends AbsViewModel{

//    void onSuccess(int requestId,String message,
//                   TelegramChannel telegramChannel,
//                   List<TelegramChannel> relatedTelegramChannelList,
//                   List<Tag> tagList,
//                   List<Comment> localCommentList,
//                   List<Comment> downloadedCommentList,
//                   boolean hasMoreItem);
//
//    void onFail(int requestId,String message);


    void onSuccess(final int requestId, final ChannelMetaDataHolder dataHolder);

    void onFail(final int requestId, final ChannelMetaDataHolder dataHolder);
}
