package com.goftagram.telegram.goftagram.network.api.message;


public abstract class NetworkMessage {

    public int mTransactionId;
    public int mStatusCode;
    public String mRawResponse;
    public String mToken;

    public NetworkMessage(int transactionId,String rawResponse, int statusCode,String token) {
        mRawResponse = rawResponse;
        mStatusCode = statusCode;
        mTransactionId = transactionId;
        mToken = token;
    }
}
