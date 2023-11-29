package com.util;

public class DataUtil {
    // 工具类，禁止创建对象
    private DataUtil(){

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
