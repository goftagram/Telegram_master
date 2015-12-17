package com.goftagram.telegram.goftagram.util;

/**
 * Created by WORK on 10/25/2015.
 */
public class TimeUtils {

    public static boolean isElapsed(long from,long to, int minuets){
        if(from > to )throw new IllegalArgumentException("Invalid input");
        return ((from - to) > (minuets * 60));
    }
}
