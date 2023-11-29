package com.test;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class CRC {
    // 生成码
    private int[] generatingCode;


    // 设置生成码
    public void setGeneratingCode(String str) {
        //将生成码字符串转换为int数组
        generatingCode = stringToArray(str);
    }

    // 获取帧检验序列
    public String getFCS(String message) {//传进来的是被除数
        for (int i = 0; i < generatingCode.length - 1; i++) {
            //往后面加几个0（长度是比generatingCode的长度减1）
            message += "0";
        }
        return getRemainder(stringToArray(message));
    }

    // 生成新的数据报
    public String setChu(String chu) {//传进来的是被除数
        return getRemainder(stringToArray(chu));
    }


    // 将01字符串转换为数组
    private int[] stringToArray(String str) {
        char[] chars = str.toCharArray();
        int[] res = new int[chars.length];
        for (int i = 0; i < chars.length; i++) {
            res[i] = chars[i] - '0';
        }
        return res;
    }

    // 求余数
    private String getRemainder(int[] code) {
        int len = code.length - generatingCode.length + 1;
        for (int i = 0; i < len; i++) {
            if (code[i] != 0) {
                for (int j = 0; j < generatingCode.length; j++) {
                    code[i + j] ^= generatingCode[j];
                }
            }
        }
        StringBuilder res = new StringBuilder();
        for (int i = len; i < code.length; i++) {
            res.append(code[i]);
        }
        return res.toString();
    }

    //设置突变
    public String setMutation(String data){
        int[] res = stringToArray(data);
        Random rand = new Random();
        int i = rand.nextInt(res.length);
        //突变
        if(res[i]==0){
            res[i]=1;
        }else if(res[i]==1){
            res[i]=0;
        }
        //将数组转换为字符串
        StringBuilder a = new StringBuilder();
        for (int j = 0; j < res.length; j++) {
            a.append(res[j]);
        }
        return a.toString();
    }

    //判断余数是否为0
    public void judge(String data){
        int num = Integer.parseInt(data);
        if(num==0){
            System.out.println("接收的数据无差错！");
        }else{
            System.out.println("接收的数据有差错！");
        }
    }

}

