package com.test;

import java.util.Scanner;

class CRCTest {
    public static void main(String[] args) {

        CRC crc = new CRC();
        Scanner sc = new Scanner(System.in);

        System.out.print("请输入多项式系数：");
        String str = sc.next();
        crc.setGeneratingCode(str);

        System.out.print("请输入二进制数据：");
        String bei = sc.next();

        String FCS = crc.getFCS(bei);
        System.out.print("经过模2除法得到的冗余码为：");
        System.out.println(FCS);

        String data = bei+crc.getFCS(bei);
        System.out.println("生成新的数据包为：   "+data);
        crc.setChu(data);

        //设置突变
        String data2 = crc.setMutation(data);
        System.out.println("发生突变后的数据包为："+data2);


        System.out.print("经过模2除法得到的余数为：");
        System.out.println(crc.setChu(data2));

        crc.judge(crc.setChu(data2));

    }
}
