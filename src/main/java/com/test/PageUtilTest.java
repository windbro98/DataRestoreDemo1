package com.test;

import java.io.*;

import static com.entity.ResManager.fileRestoreSingle;
import static com.util.FileToolUtil.*;
import static com.util.PageManagerUtil.filePaged;

public class PageUtilTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        // 备份测试
        String inFileName1 = "java学习笔记1.pdf";
        String srcRoot = "OriginalData";
        String inFilePath1 = fileConcat(srcRoot, inFileName1);
        File inFile1 = new File(inFilePath1);
        String backFilePath = "BackupData/backup";
        OutputStream os = new FileOutputStream(backFilePath);
        InputStream is1 = new FileInputStream(inFilePath1);
        filePaged(is1, os, inFileName1, inFile1);

//         恢复测试
        String resRoot = "RestoreData";
        FileInputStream backIs = new FileInputStream("BackupData/backup");
        fileRestoreSingle(backIs, resRoot);
    }
}
