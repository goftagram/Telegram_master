package com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract;

import com.goftagram.telegram.goftagram.application.model.TelegramChannel;
import com.goftagram.telegram.goftagram.application.usecases.absclasses.NameValueDataHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WORK on 11/5/2015.
 */
public class ChannelMetaDataHolder extends NameValueDataHolder {

    private Map<String,TelegramChannel> mTelegramChannelHolder;
    public ChannelMetaDataHolder(){
        super();
        mTelegramChannelHolder = new HashMap<>();
    }

    public void put(String key,TelegramChannel value){
        mTelegramChannelHolder.put(key,value);
    }

    public TelegramChannel getTelegramChannel(String key){
        return mTelegramChannelHolder.get(key);
    }

}
