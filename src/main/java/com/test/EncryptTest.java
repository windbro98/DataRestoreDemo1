package com.test;

import com.util.compress.LZ77Pro;
import com.util.encrypt.AES;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.util.FileToolUtil.readFile;
import static com.util.encrypt.AES.*;

// AES加密测试
public class EncryptTest {
    @Test
    public void AESTest() throws IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        String inputFilePath = "testFile\\文本1.txt";
        String enFilePath = "testFile\\文本1_aes.txt";
        String deFilePath = "testFile\\文本1_aes_res.txt";
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

        // 解密过程
        boolean flag = false; // 判断文件是否解密成功
        // 设置密码并加密
        String password = "hello";
        byte[] ivByte = AES.encryptFile(password, inFile, enFile);

        // 输入正确密码并解密
        String typePassword = "hello";
        byte[] keyByte = AES.generateKey(AES.keyLen, typePassword);
        byte[] res = new byte[ivLen + AES.keyLen];
        System.arraycopy(ivByte, 0, res, 0, ivLen);
        System.arraycopy(keyByte, 0, res, ivLen, keyLen);
        // 解密
        try{
            AES.decryptFile(res, enFile, deFile);
            flag = true;
        } catch (Exception e){
            flag = false;
        }
        // 判断文件是否成功解密
        Assert.assertTrue(flag);
        Assert.assertArrayEquals(readFile(inFile), readFile(deFile));

        // 输入错误密码并进行解密
        typePassword = "mine";
        keyByte = AES.generateKey(AES.keyLen, typePassword);
        res = new byte[ivLen + AES.keyLen];
        System.arraycopy(ivByte, 0, res, 0, ivLen);
        System.arraycopy(keyByte, 0, res, ivLen, keyLen);
        // 解密
        try{
            AES.decryptFile(res, enFile, deFile);
            flag = true;
        } catch (Exception e){
            flag = false;
        }
        // 判断文件是否成功解密
        Assert.assertFalse(flag);
    }
}
