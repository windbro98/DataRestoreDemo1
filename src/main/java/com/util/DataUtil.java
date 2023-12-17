package com.util;


import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    // 将byte数组转换为二进制，这里使用了大端存储，即存储的低位放置数据的高位
    public static String byteArray2binary(byte[] input){
        byte firstByte = input[0];
        input[0] = -1;
        String remain = new BigInteger(1, input).toString(2);
        StringBuilder sb = new StringBuilder();

        byte2binary(sb, firstByte);
        input[0] = firstByte;
        sb.append(remain.substring(8));
        return sb.toString();
    }

//    public static String byteArray2binary(byte[] input){
//        StringBuilder sb = new StringBuilder();
//
//        for (byte b : input) {
//            byte2binary(sb, b);
//        }
//        return sb.toString();
//    }

//    // 这里使用了小端存储，换取更高的速度
//    public static void byte2binary(StringBuilder sb, byte value){
//        int byteNum=8;
//        // 转为int，便于计算
//        int valueInt = value<0 ? 256+value : value;
//
//        for (int i = 0; i < byteNum; i++) {
//            sb.append(valueInt%2);
//            valueInt >>= 1;
//        }
//    }

        // 这里使用的是大端存储，理论上与实际存储方式完全一致，但是速度会稍稍慢一些
    public static void byte2binary(StringBuilder sb, byte value) {
        int divider = 128;
        int byteNum = 8;
        // 转为int，便于计算
        int valueInt = value < 0 ? 256 + value : value;

        for (int i = 0; i < byteNum; i++) {
            sb.append(valueInt / divider);
            valueInt %= divider;
            divider /= 2;
        }
    }

    // 将任意类型转换为byte[]
    public static byte[] enByteArray(Object o) throws IOException {
        byte[] res;
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(o);
        res = byteOut.toByteArray();
        out.flush();
        out.close();
        return res;
    }
    // 将byte[]重新转换为对应的对象
    public static Object deByteArray(byte[] b) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(b);
        ObjectInputStream in = new ObjectInputStream(byteIn);
        Object res = in.readObject();
        in.close();
        return res;
    }
    // 将01字符串转换为byte[]
    public static byte[] binaryToByteArray(String str){
        byte[] strByte;

        if(str.length()<8)
            str =  String.format("%-8s", str).replace(" ", "0"); // 表明不足一个字节，需要向后补齐0，否则翻译的位置就会错误
        strByte = new BigInteger(str, 2).toByteArray();
        // 根据是否以1开头，以1开头时翻译得到的byteArray开头为0（表示为负数），因此需要去掉
        return (str.startsWith("1")) ? Arrays.copyOfRange(strByte, 1, strByte.length) : strByte;
    }

    // 返回i代表子序列的初始位置，返回-1代表不是子序列
    public static int idxSubArray(byte[] origArray, byte[] subArray){
        int origLen = origArray.length;
        int subLen = subArray.length;

        for (int i = 0; i < origLen-subLen; i++) {
            // 找不到不相同的字节，即为相同
            if(Arrays.mismatch(Arrays.copyOfRange(origArray, i, i+subLen), subArray)==-1)
                return i;
        }
        return -1;
    }
}
