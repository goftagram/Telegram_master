package com.goftagram.telegram.goftagram.application.usecases.addcommentforchannel.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.abspost.AbsPostRequest;


public class AddCommentRequest extends AbsPostRequest {

    public String mComment;
    public String mTelegramChannelId;


}
