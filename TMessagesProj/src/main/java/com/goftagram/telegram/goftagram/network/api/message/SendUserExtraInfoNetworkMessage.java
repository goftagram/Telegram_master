package com.goftagram.telegram.goftagram.network.api.message;

public class SendUserExtraInfoNetworkMessage extends NetworkMessage {
    public SendUserExtraInfoNetworkMessage(int transactionId,int statusCode, String rawResponse,String token) {
        super(transactionId,rawResponse, statusCode,token);
    }
}