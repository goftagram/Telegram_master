package com.goftagram.telegram.goftagram.network.api.message;

/**
 * Created by mhossein on 10/31/15.
 */
public class AppVersionMessage extends NetworkMessage {

    public AppVersionMessage(int transactionId, String rawResponse, int statusCode,String token) {
        super(transactionId, rawResponse, statusCode,token);
    }


}
