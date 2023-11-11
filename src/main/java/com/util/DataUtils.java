package com.util;

public class DataUtils {
    // 工具类，禁止创建对象
    private  DataUtils(){

    }

    // 获取元素在列表中对应坐标
    public static int getIndexForArray(String[]array, String item) {
        for(int i=0; i<array.length; i++) {
            if(array[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }
}
