package com.test;

import com.util.encrypt.AES;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.util.encrypt.AES.*;

// AES加密测试
public class AESTest {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        String inputFilePath = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\src\\main\\java\\com\\test\\testFile\\文本1.txt";
        String enFilePath = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\src\\main\\java\\com\\test\\testFile\\文本1_aes.txt";
        String deFilePath = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\src\\main\\java\\com\\test\\testFile\\文本1_aes_res.txt";
        // 源文件
        File inFile = new File(inputFilePath);
        // 加密文件
        File enFile = new File(enFilePath);
        // 解码文件
        File deFile = new File(deFilePath);

        // 源文件存在性检查
        if(!inFile.exists()){
            System.out.println("Input file doesn't exist!");
            return;
        }
        if(!enFile.exists())
            enFile.createNewFile();
        if(!deFile.exists())
            deFile.createNewFile();

        // 设置密码并加密
        String password = "hello";
        byte[] ivByte = AES.encryptFile(password, inFile, enFile);
        // 输入密码并解密
        String typePassword = "hello";
        byte[] keyByte = AES.generateKey(AES.keyLen, typePassword);
        byte[] res = new byte[ivLen + AES.keyLen];
        System.arraycopy(ivByte, 0, res, 0, ivLen);
        System.arraycopy(keyByte, 0, res, ivLen, keyLen);
        // 若解密失败，表明密码错误
        try{
            AES.decryptFile(res, enFile, deFile);
        } catch (Exception e){
            System.out.println("Incorrect password");
        }

    }
}
