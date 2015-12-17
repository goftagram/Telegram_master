package com.goftagram.telegram.goftagram.network.api.message;

public class FetchCategoryNetworkMessage extends NetworkMessage {
    public FetchCategoryNetworkMessage(int transactionId,int statusCode, String rawResponse,String token) {
        super(transactionId,rawResponse, statusCode,token);

    }
}
