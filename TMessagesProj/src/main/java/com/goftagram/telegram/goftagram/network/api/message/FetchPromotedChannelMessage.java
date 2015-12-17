package com.goftagram.telegram.goftagram.network.api.message;

public class FetchPromotedChannelMessage extends NetworkMessage {
        public FetchPromotedChannelMessage(int transactionId,int statusCode, String rawResponse,String token) {
            super(transactionId,rawResponse, statusCode,token);
        }
    }

