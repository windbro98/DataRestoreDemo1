package com.test;

import java.io.*;
import java.util.HashMap;

public class FileUtilTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Haspmap to byte test
        HashMap<String, String> hp = new HashMap<String, String>();
        hp.put("aaa", "111");
        hp.put("bbb", "222");
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(hp);

        byte[] myByte = byteOut.toByteArray();
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        HashMap<String, String> hp2 = (HashMap<String, String>) in.readObject();
        System.out.println(hp2.toString());
    }
}
