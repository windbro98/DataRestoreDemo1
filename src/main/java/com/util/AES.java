package com.util;

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

public class AES {
    public static int keyLen=32; // 按byte计数
    public static int ivLen=16;
    public static byte[] salt = new byte[16];

    public AES(int customKeyLen){
        keyLen = customKeyLen;
    }

    public static byte[] encryptFile(String password, File origFile, File enFile) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {

        fileExistEval(enFile, true);
        String algorithm = "AES/CBC/PKCS5Padding";
        generateSalt();
        byte[] keyByte = generateKey(keyLen, password);
        SecretKey key = new SecretKeySpec(keyByte, "AES");
        IvParameterSpec iv = generateIv(ivLen);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        FileInputStream is = new FileInputStream(origFile);
        FileOutputStream os = new FileOutputStream(enFile);
        byte[] buffer = new byte[256];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                os.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            os.write(outputBytes);
        }
        is.close();
        os.close();

        // 返回值，iv + key
//        byte[] res = new byte[iv.getIV().length + keyByte.length];
//        System.arraycopy(iv.getIV(), 0, res, 0, ivLen);
//        System.arraycopy(keyByte, 0, res, ivLen, keyLen);
//        return res;
        return iv.getIV();
    }

    public static void decryptFile(byte[] ivKey, File enFile, File deFile) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        String algorithm = "AES/CBC/PKCS5Padding";
        IvParameterSpec iv = new IvParameterSpec(Arrays.copyOf(ivKey, ivLen));
        SecretKey key = new SecretKeySpec(Arrays.copyOfRange(ivKey, ivLen, ivLen+keyLen), "AES");

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        FileInputStream is = new FileInputStream(enFile);
        FileOutputStream os = new FileOutputStream(deFile);
        byte[] buffer = new byte[256];
        int bytesRead;
        while((bytesRead = is.read(buffer)) != -1){
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null){
                os.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if(outputBytes != null){
            os.write(outputBytes);
        }
        is.close();
        os.close();
    }

    public static byte[] generateKey(int keyLen, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 1000, keyLen*8);
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
    }

    public static void generateSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstanceStrong();
        sr.nextBytes(salt);
    }

    public static IvParameterSpec generateIv(int n){
        byte[] iv = new byte[n];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

}
