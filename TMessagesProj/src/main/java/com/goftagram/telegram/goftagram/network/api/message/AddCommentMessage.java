package com.goftagram.telegram.goftagram.network.api.message;

/**
* Created by WORK on 10/8/2015.
*/
public class AddCommentMessage extends NetworkMessage {
    String mTelegramChannelId;
    public AddCommentMessage(int transactionId,String telegramChannelId,int statusCode, String rawResponse,String token) {
        super(transactionId,rawResponse, statusCode,token);
        mTelegramChannelId = telegramChannelId;
    }
}
