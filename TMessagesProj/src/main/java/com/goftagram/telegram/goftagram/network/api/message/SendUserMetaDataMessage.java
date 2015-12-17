package com.goftagram.telegram.goftagram.network.api.message;

/**
* Created by WORK on 10/8/2015.
*/
public class SendUserMetaDataMessage extends NetworkMessage {
    public SendUserMetaDataMessage(int transactionId, int statusCode, String rawResponse, String token) {
        super(transactionId,rawResponse, statusCode,token);
    }
}
