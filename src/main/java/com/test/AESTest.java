package com.test;

import com.util.AES;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.util.AES.generateIv;
import static com.util.AES.generateKey;

public class AESTest {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String inputFilePath = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\OriginalDataWin\\文件类型\\文本1.txt";
        String enFilePath = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\OriginalDataWin\\文件类型\\文本1_en.txt";
        String deFilePath = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\OriginalDataWin\\文件类型\\文本1_de.txt";
        File inFile = new File(inputFilePath);
        File enFile = new File(enFilePath);
        File deFile = new File(deFilePath);

        // 文件存在性检查
        if(!inFile.exists()){
            System.out.println("Input file doesn't exist!");
            return;
        }
        if(!enFile.exists())
            enFile.createNewFile();
        if(!deFile.exists())
            deFile.createNewFile();

        // 这里使用了256的AES
        SecretKey key = generateKey(256);
        String algorithm = "AES/CBC/PKCS5Padding";
        IvParameterSpec ivParameterSec = generateIv();
        AES.encryptFile(algorithm, key, ivParameterSec, inFile, enFile);
        AES.decryptFile(algorithm, key, ivParameterSec, enFile, deFile);
    }
}
