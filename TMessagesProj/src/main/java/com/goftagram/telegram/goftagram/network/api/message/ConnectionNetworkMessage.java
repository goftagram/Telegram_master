package com.goftagram.telegram.goftagram.network.api.message;


public class ConnectionNetworkMessage {

    private boolean mIsConnected;

    public ConnectionNetworkMessage(boolean isConnected){
        mIsConnected = isConnected;
    }

    public boolean isConnected() {
        return mIsConnected;
    }

    public void setConnected(boolean isConnected) {
        this.mIsConnected = isConnected;
    }
}
