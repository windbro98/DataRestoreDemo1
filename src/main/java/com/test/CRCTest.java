package com.test;

import com.util.CRC;

import static com.util.DataUtil.byte2binary;
import static com.util.DataUtil.byteArray2binary;


class CRCTest {
    public static void main(String[] args) {

        CRC crc = new CRC();

        byte[] divided = {33, -48                                                                                                };
        String dividedBin = byteArray2binary(divided);
        System.out.println("原数据的二进制为：");
        System.out.println(dividedBin);

        System.out.println("除数为：");
        System.out.println("101000110");

        byte FCS = crc.getFCS(divided);
        System.out.print("经过模2除法得到的冗余码为：");
        System.out.println(FCS);

        StringBuilder sb = new StringBuilder();
        byte2binary(sb, FCS);
        String FCS_bin = sb.toString();
        System.out.println("冗余码的二进制为：");
        System.out.println(FCS_bin);

        // 测试数据损坏
        byte[] dividedMut = crc.setMutation(divided);

        boolean res;

        res = crc.judge(divided, FCS);
        System.out.println("正常数据的判定结果为：");
        System.out.println(res);

        res = crc.judge(dividedMut, FCS);
        System.out.println("损坏数据的判定结果为：");
        System.out.println(res);

    }
}
