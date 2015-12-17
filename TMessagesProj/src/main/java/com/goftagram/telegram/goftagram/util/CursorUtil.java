package com.goftagram.telegram.goftagram.util;

import android.database.Cursor;

/**
 * Created by mhossein on 10/1/15.
 */
public class CursorUtil {
    /**
     *
     * @param cursor
     * @param column
     * @return String value from column
     */
    public static String getStringColumnFromCursor(Cursor cursor, String column){
        return cursor.getString(cursor.getColumnIndex(column));
    }


    /**
     *
     * @param cursor
     * @param column
     * @return float value from column
     */
    public static float getFloatColumnFromCursor(Cursor cursor, String column){
        return cursor.getFloat(cursor.getColumnIndex(column));
    }
}
