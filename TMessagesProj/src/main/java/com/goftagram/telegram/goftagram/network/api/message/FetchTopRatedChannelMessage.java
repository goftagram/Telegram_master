package com.goftagram.telegram.goftagram.network.api.message;

public class FetchTopRatedChannelMessage extends NetworkMessage {
        public FetchTopRatedChannelMessage(int transactionId, int statusCode, String rawResponse,String token) {
            super(transactionId,rawResponse, statusCode,token);
        }
    }

