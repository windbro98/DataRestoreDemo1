package com.test;

import com.entity.BackManager;
import com.entity.ResManager;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import com.util.Huffman;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class HuffmanTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException, ReedSolomonException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        Huffman hm = new Huffman();

        File backupFile = new File("BackupData/20231228_203501");
        File compreFile = new File("BackupData/20231228_203501.huffman");
        File deFile = new File("BackupData/20231228_203501.de");

        hm.encode(backupFile, compreFile);
//        backupFile.delete();
        hm.decode(compreFile, deFile);

//         文件的恢复
        BackManager backM = BackManager.getInstance();
        ResManager resM = ResManager.getInstance();
////
        // 压缩还原
        backM.initBackManager(backupFile.getAbsolutePath());
//        // 源文件
////        backM.initBackManager(backupFile.getAbsolutePath(), "", "");
        resM.initResManager("RestoreDataWin/");
        resM.fileRestore(backM.getBackDir());
    }
}
