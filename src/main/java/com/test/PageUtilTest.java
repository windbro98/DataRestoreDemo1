package com.test;

import java.io.*;

import static com.util.FileToolUtil.*;

public class PageUtilTest {
    public static void main(String[] args) throws IOException {
//        // 备份测试
        String inFileName1 = "ZhuLou.jpg";
        String srcRoot = "OriginalData";
        String inFilePath1 = fileConcat(srcRoot, inFileName1);
        String backFilePath = "BackupData/backup";
        OutputStream os = new FileOutputStream(backFilePath);
        InputStream is1 = new FileInputStream(inFilePath1);
        fileCopy(is1, os, inFileName1);

//         恢复测试
        String resRoot = "RestoreData";
        FileInputStream backIs = new FileInputStream("BackupData/backup");
        String resFilePath = "D:/learning_programs/java_programs/DataRestoreDemo1/RestoreData/ZhuLou.jpg";
        int fileLen = 0;
        fileRestoreSingle(backIs, resRoot);
    }
}
