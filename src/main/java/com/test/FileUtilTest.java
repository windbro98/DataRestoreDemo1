package com.test;

import java.io.*;
import java.nio.ByteBuffer;
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

        ByteBuffer b1 = ByteBuffer.allocate(10);
        ByteBuffer b2 = ByteBuffer.allocate(10);
        byte[] a1 = new byte[]{1, 2, 4, 4, 5};
        byte[] a2 = new byte[]{1, 2, 3, 4, 5};
        b1.put(a1);
        b2.put(a2);
        ByteBuffer b3 = b1.slice(1, 4);
        System.out.println(b1.slice(1, 4));
    }
}
