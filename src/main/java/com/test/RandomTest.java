package com.test;

import java.io.IOException;

import static com.util.DataUtil.byteArray2intArray;
import static com.util.DataUtil.intArray2byteArray;


public class RandomTest {
    public static void main(String[] args) throws IOException {
        char c=255;
        System.out.println(c);
        int[] testInt = {0, 1, 2, 3, 4};
        byte[] testByte = intArray2byteArray(testInt);
        int[] resInt = byteArray2intArray(testByte);
        System.out.println("here");
    }
}
