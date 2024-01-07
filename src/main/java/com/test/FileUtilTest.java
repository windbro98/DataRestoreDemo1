package com.test;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class FileUtilTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
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
