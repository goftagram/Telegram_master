package com.goftagram.telegram.goftagram.network.api.message;


public  class FetchChannelsByQueryNetworkMessage extends NetworkMessage {
    public int mPage;
    public String mQuery;

    public FetchChannelsByQueryNetworkMessage(
            int page, String query, int transactionId, int statusCode, String rawResponse,
            String token
    ) {
        super(transactionId, rawResponse, statusCode, token);
        mPage = page;
        mQuery = query;
    }

}
