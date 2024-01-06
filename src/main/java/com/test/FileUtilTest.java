package com.test;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class FileUtilTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Haspmap to byte test
//        HashMap<String, String> hp = new HashMap<String, String>();
//        hp.put("aaa", "111");
//        hp.put("bbb", "222");
//        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//        ObjectOutputStream out = new ObjectOutputStream(byteOut);
//        out.writeObject(hp);
//
//        byte[] myByte = byteOut.toByteArray();
//        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
//        ObjectInputStream in = new ObjectInputStream(byteIn);
//        HashMap<String, String> hp2 = (HashMap<String, String>) in.readObject();
//        System.out.println(hp2.toString());
//
//        ByteBuffer b1 = ByteBuffer.allocate(10);
//        ByteBuffer b2 = ByteBuffer.allocate(10);
//        byte[] a1 = new byte[]{1, 2, 4, 4, 5};
//        byte[] a2 = new byte[]{1, 2, 3, 4, 5};
//        b1.put(a1);
//        b2.put(a2);
//        ByteBuffer b3 = b1.slice(1, 4);
//        System.out.println(b1.slice(1, 4));
        // todo: 这里需要验证一下，能否在一个文件的修改过程中，读取该文件。即Inputstream是否直接读取了完整内容
        File file1 = new File("D:\\learning_programs\\java_programs\\DataRestoreDemo1\\src\\main\\java\\com\\test\\file1.txt");
        File file2 = new File("D:\\learning_programs\\java_programs\\DataRestoreDemo1\\src\\main\\java\\com\\test\\file2.txt");
        FileInputStream is = new FileInputStream(file1);
        FileOutputStream os1 = new FileOutputStream(file1);
        FileOutputStream os2 = new FileOutputStream(file2);

//        byte[] content1 = "I'am the new content".getBytes();
//        os1.write(content1);

        byte[] buffer = new byte[256];
        int readNum;
        while((readNum=is.read()) != -1){
            os2.write(buffer);
        }

    }
}
