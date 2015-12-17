package com.goftagram.telegram.goftagram.util;


public class UniqueIdGenerator {

    private int mCurrentId;
    private static UniqueIdGenerator sUniqueIdGenerator;

    private UniqueIdGenerator() {
        mCurrentId = 0;
    }

    public static UniqueIdGenerator getInstance() {
        synchronized (UniqueIdGenerator.class) {
            if (sUniqueIdGenerator == null) {
                sUniqueIdGenerator = new UniqueIdGenerator();
            }
            return sUniqueIdGenerator;
        }
    }

    public int getNewId(){
        synchronized (UniqueIdGenerator.class) {
            mCurrentId++;
            return mCurrentId;
        }
    }


}
