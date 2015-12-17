package com.goftagram.telegram.goftagram.network.api.message;

public  class FetchChannelMetaDataMessage extends NetworkMessage {


    public String mTelegramChannelServerId;
    public int mPage;

    public FetchChannelMetaDataMessage(
            int transactionId, String telegramChannelServerId,int page, int statusCode,
            String rawResponse,String token
    ) {
        super(transactionId,rawResponse, statusCode,token);
        mTelegramChannelServerId = telegramChannelServerId;
        mPage = page;

    }

}