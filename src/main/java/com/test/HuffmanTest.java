package com.test;

import com.entity.BackManager;
import com.entity.ResManager;
import com.util.Huffman;

import java.io.File;
import java.io.IOException;

public class HuffmanTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Huffman hm = new Huffman();

        File backupFile = new File("BackupData/20231218_174359");
        File compreFile = new File("BackupData/20231218_174359.huffman");

//        hm.encode(backupFile);
//        backupFile.delete();
        hm.decode(compreFile);

//         文件的恢复
        BackManager backM = BackManager.getInstance();
        ResManager resM = ResManager.getInstance();
////
        // 压缩还原
        backM.initBackManager(backupFile.getAbsolutePath(), "", "");
//        // 源文件
////        backM.initBackManager(backupFile.getAbsolutePath(), "", "");
        resM.initResManager("RestoreDataWin/", "", "");
        resM.fileRestore(backM.getBackDir());
    }
}
