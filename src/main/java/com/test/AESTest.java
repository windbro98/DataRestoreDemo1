package com.test;

import com.util.AES;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.util.AES.*;

public class AESTest {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
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

        String password = "hello";

        byte[] ivByte = AES.encryptFile(password, inFile, enFile);

        String typePassword = "hedlo";
        byte[] keyByte = AES.generateKey(AES.keyLen, typePassword);
        byte[] res = new byte[ivLen + AES.keyLen];
        System.arraycopy(ivByte, 0, res, 0, ivLen);
        System.arraycopy(keyByte, 0, res, ivLen, keyLen);
        try{
            AES.decryptFile(res, enFile, deFile);
        } catch (Exception e){
            System.out.println("Incorrect password");
        }

    }
}
