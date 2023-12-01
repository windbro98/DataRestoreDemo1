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

    public static String byteArray2binary(byte[] input){
        StringBuilder sb = new StringBuilder("");
        int inputLen = input.length;
        for (byte b : input) {
            byte2binary(sb, b);
        }
        return sb.toString();
    }

    // 这里使用了小端存储，换取更高的速度
    public static void byte2binary(StringBuilder sb, byte value){
        int byteNum=8;
        // 转为int，便于计算
        int valueInt = value<0 ? 256+value : value;

        for (int i = 0; i < byteNum; i++) {
            sb.append(valueInt%2);
            valueInt >>= 1;
        }

//    // 这里使用的是大端存储，理论上与实际存储方式完全一致，但是速度会稍稍慢一些
//    public static void byte2binary(StringBuilder sb, byte value){
//        int divider=128;
//        int byteNum=8;
//        // 转为int，便于计算
//        int valueInt = value<0 ? 256+value : value;
//
//        for (int i = 0; i < byteNum; i++) {
//            sb.append(valueInt/divider);
//            valueInt %= divider;
//            divider /= 2;
//        }
    }
}
