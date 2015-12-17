package com.goftagram.telegram.goftagram.application.usecases.absclasses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WORK on 11/3/2015.
 */
public class NameValueDataHolder {

    private Map<String,String>  mStringHolder;
    private Map<String,Integer> mIntegerHolder;
    private Map<String,Float>   mFloatHolder;
    private Map<String,Double>  mDoubleHolder;
    private Map<String,Boolean> mBooleanHolder;
    private Map<String,List>    mListHolder;


    public  NameValueDataHolder(){

        mStringHolder   = new HashMap<>();
        mIntegerHolder  = new HashMap<>();
        mFloatHolder    = new HashMap<>();
        mDoubleHolder   = new HashMap<>();
        mBooleanHolder  = new HashMap<>();
        mListHolder     = new HashMap<>();

    }

    public void put(String key,Boolean value){
        mBooleanHolder.put(key,value);
    }

    public Boolean getBoolean(String key){
        return mBooleanHolder.get(key);
    }

    public void  put(String key,List value){
        mListHolder.put(key,value);
    }

    public List getList(String key){
        return mListHolder.get(key);
    }

    public void put(String key,String value){
        mStringHolder.put(key,value);
    }

    public String getString(String key){
        return mStringHolder.get(key);
    }

    public void put(String key,Integer value){
        mIntegerHolder.put(key,value);
    }

    public Integer getInt(String key){ return mIntegerHolder.get(key);}

    public void put(String key,Float value){
        mFloatHolder.put(key,value);
    }

    public Float getFloat(String key){
        return mFloatHolder.get(key);
    }

    public void put(String key,Double value){
        mDoubleHolder.put(key,value);
    }

    public Double getDouble(String key){
        return mDoubleHolder.get(key);
    }


}
