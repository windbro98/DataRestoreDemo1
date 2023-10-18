package com.util;

public class DataUtils {
    public static int getIndexForArray(String[]array, String item) {
        for(int i=0; i<array.length; i++) {
            if(array[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }
}
