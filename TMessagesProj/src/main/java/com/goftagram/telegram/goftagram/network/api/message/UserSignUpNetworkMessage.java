package com.goftagram.telegram.goftagram.network.api.message;


public  class UserSignUpNetworkMessage extends NetworkMessage {
    public UserSignUpNetworkMessage(int transactionId,int statusCode, String rawResponse) {
        super(transactionId,rawResponse, statusCode,"");

    }
}
