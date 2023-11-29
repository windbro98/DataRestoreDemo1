package com.test;

import java.io.IOException;

public class FileUtilTest {
    public static void main(String[] args) throws IOException {
        int a = 1;
        a <<= 8;
        byte b = (byte) a;
        System.out.println(b);
    }
}
