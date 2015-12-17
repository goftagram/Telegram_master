package com.goftagram.telegram.goftagram.network.api.message;

/**
* Created by WORK on 10/8/2015.
*/
public class RateChannelMessage extends NetworkMessage {
    public String mTelegramChannelId;
    public int mRate;
    public RateChannelMessage(int transactionId, String telegramChannelId,int rate,int statusCode, String rawResponse, String token) {
        super(transactionId,rawResponse, statusCode,token);
        mTelegramChannelId = telegramChannelId;
        mRate = rate;
    }
}
