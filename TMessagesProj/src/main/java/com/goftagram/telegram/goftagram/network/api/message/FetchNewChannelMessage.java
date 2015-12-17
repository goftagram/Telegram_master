package com.goftagram.telegram.goftagram.network.api.message;

public class FetchNewChannelMessage extends NetworkMessage {
        public FetchNewChannelMessage(int transactionId, int statusCode, String rawResponse, String token) {
            super(transactionId,rawResponse, statusCode,token);
        }
    }

