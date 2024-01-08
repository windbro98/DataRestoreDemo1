package com.util.redundancyCheck;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import static com.util.DataUtil.byteArray2binary;

// CRC校验码
// 注：这里将CRC校验码的数据报按照小端存储的方式进行解析，因此直接生成的校验码并不是原数据包的校验码，而是原数据报每1byte翻转后的校验码
// 但是没有关系，因此对于整体来说，我们的编程是以字节Byte为单位进行的。只要每个字节都是正确的，我们这里显示的二进制编码方式并不重要
// 前提：发送和传输的时候使用的都是本套CRC校验码
// 出于对其的目的，这里将生成的校验码也按照小端的方式解析为byte数据
public class CRC {
    // 生成码
    private int[] generatingCode={1, 0, 1, 0, 0, 0, 1, 0, 1};

    // 获取帧检验序列，这里应该修改为tmpData的内容
    public byte getFCS(byte[] messageByte) {//传进来的是被除数
        StringBuilder message = new StringBuilder(byteArray2binary(messageByte));
        for (int i = 0; i < generatingCode.length - 1; i++) {
            //往后面加几个0（长度是比generatingCode的长度减1）
            message.append("0");
        }
        return getRemainder(stringToArray(message.toString()));
    }

    public boolean judge(byte[] messageByte, byte remainder){
        // 获取message+remainder字符串
        int messageLen = messageByte.length;
        byte[] messageBytePlus = new byte[messageLen+1];
        System.arraycopy(messageByte, 0, messageBytePlus, 0, messageLen);
        messageBytePlus[messageLen] = remainder;
        String messagePlus = byteArray2binary(messageBytePlus);
        // 获取余数
        int remainer = getRemainder(stringToArray(messagePlus));
        return (remainer==0);
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
    private byte getRemainder(int[] code) {
        int genLen = generatingCode.length;
        int dataLen = code.length - genLen + 1;
        for (int i = 0; i < dataLen; i++) {
            if (code[i] != 0) {
                for (int j = 0; j < genLen; j++) {
                    code[i + j] ^= generatingCode[j];
                }
            }
        }

        int res=0;
//        // 小端存储
//        for (int i = 0; i < code.length-len; i++) {
//            res += (code[i+len]<<i);
//        }
        // 大端存储
        for (int i = 0; i < genLen-1; i++) {
            res += (code[dataLen+i] << (genLen-2-i)); // genLen-1是总的位数，genLen-2是2的次方数
        }
        return (byte) res;
    }

    //设置突变，这里仅针对测试的时候
    public byte[] setMutation(byte[] data){
        int dataLen = data.length;
        byte[] dataMut = new byte[data.length];
        System.arraycopy(data, 0, dataMut, 0, dataLen);
        Random rand = new Random();
        // 随机位置突变
        int i = rand.nextInt(dataMut.length);
        //突变
        dataMut[i] += (byte)((dataMut[i]<61) ? 1: -1);
        return dataMut;
    }
}

