package com.goftagram.telegram.goftagram.network.api.message;

public  class UserLogInNetworkMessage extends NetworkMessage {
    public UserLogInNetworkMessage(int transactionId,int statusCode, String rawResponse,String token) {
        super(transactionId,rawResponse, statusCode,token);
    }
}