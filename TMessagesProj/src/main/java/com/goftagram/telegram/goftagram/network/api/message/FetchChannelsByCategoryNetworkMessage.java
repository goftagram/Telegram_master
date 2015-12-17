package com.goftagram.telegram.goftagram.network.api.message;


public  class FetchChannelsByCategoryNetworkMessage extends NetworkMessage {
    public int mPage;
    public String mCategoryId;

    public FetchChannelsByCategoryNetworkMessage(
            int page, String categoryId, int transactionId, int statusCode, String rawResponse,
            String token
    ) {
        super(transactionId, rawResponse, statusCode, token);
        mPage = page;
        mCategoryId = categoryId;
    }

}
