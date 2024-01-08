package com.test;

import com.entity.BackManager;
import com.entity.ResManager;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import com.util.compress.Huffman;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

// 哈夫曼树测试
public class HuffmanTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException, ReedSolomonException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        Huffman hm = new Huffman();

        // 源文件
        File backupFile = new File("D:\\learning_programs\\java_programs\\DataRestoreDemo1\\src\\main\\java\\com\\test\\testFile\\文本1.txt");
        // 压缩文件
        File compreFile = new File("D:\\learning_programs\\java_programs\\DataRestoreDemo1\\src\\main\\java\\com\\test\\testFile\\文本1_huffman.txt");
        // 恢复文件
        File deFile = new File("D:\\learning_programs\\java_programs\\DataRestoreDemo1\\src\\main\\java\\com\\test\\testFile\\文本1_huffman_res.txt");

        // 压缩
        hm.encode(backupFile, compreFile);
        // 解压
        hm.decode(compreFile, deFile);
    }
}
