package com.util.encrypt;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.lang.reflect.Array;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import static com.util.FileToolUtil.fileExistEval;

// AES CBC加密
public class AES {
    public static int keyLen=32; // 密码长度，以下均按byte计数
    public static int ivLen=16; // 初始向量长度
    public static int saltLen=16; // 加盐长度
    public static byte[] salt = new byte[saltLen]; // 加盐

    // AES初始化
    public AES(int customKeyLen){
        keyLen = customKeyLen;
    }

    // 文件加密
    public static byte[] encryptFile(String password, File origFile, File enFile) throws IOException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {

        // 创建加密文件
        fileExistEval(enFile, true);
        // 选择CBC算法
        String algorithm = "AES/CBC/PKCS5Padding";
        // 生成盐
        generateSalt();
        // 生成加盐哈希后密码
        byte[] keyByte = generateKey(keyLen, password);
        SecretKey key = new SecretKeySpec(keyByte, "AES");
        // 生成初始向量
        IvParameterSpec iv = generateIv(ivLen);
        // 加密器初始化
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        // 原始文件和加密文件
        FileInputStream is = new FileInputStream(origFile);
        FileOutputStream os = new FileOutputStream(enFile);
        // 文件加密
        byte[] buffer = new byte[256];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                os.write(output);
            }
        }
        // 最后一轮加密
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            os.write(outputBytes);
        }
        is.close();
        os.close();

        // 返回初始向量
        return iv.getIV();
    }

    // 解码文件
    public static void decryptFile(byte[] ivKey, File enFile, File deFile) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        String algorithm = "AES/CBC/PKCS5Padding"; // 加密算法
        IvParameterSpec iv = new IvParameterSpec(Arrays.copyOf(ivKey, ivLen)); // 初始向量
        SecretKey key = new SecretKeySpec(Arrays.copyOfRange(ivKey, ivLen, ivLen+keyLen), "AES"); // 密码

        // 解码器初始化
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        // 加密文件和家吗文件
        FileInputStream is = new FileInputStream(enFile);
        FileOutputStream os = new FileOutputStream(deFile);
        // 解码
        byte[] buffer = new byte[256];
        int bytesRead;
        while((bytesRead = is.read(buffer)) != -1){
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null){
                os.write(output);
            }
        }
        // 最后一轮解码
        byte[] outputBytes = cipher.doFinal();
        if(outputBytes != null){
            os.write(outputBytes);
        }
        is.close();
        os.close();
    }

    // 生成加盐后密码
    public static byte[] generateKey(int keyLen, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 1000, keyLen*8);
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
    }
    // 生成盐
    public static void generateSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstanceStrong();
        sr.nextBytes(salt);
    }
    // 生成初始向量
    public static IvParameterSpec generateIv(int n){
        byte[] iv = new byte[n];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

}
