package com.goftagram.telegram.goftagram.network.api.message;


public  class FetchChannelsByTagNetworkMessage extends NetworkMessage {
    public int mPage;
    public String mTagId;

    public FetchChannelsByTagNetworkMessage(
            int page, String tagId, int transactionId, int statusCode, String rawResponse,
            String token
    ) {
        super(transactionId, rawResponse, statusCode, token);
        mPage = page;
        mTagId = tagId;
    }

}
