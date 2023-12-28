package com.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.*;

public class AES {
    public static void encryptFile(String algorithm, SecretKey key, IvParameterSpec iv,
                                   File origFile, File enFile) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {

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
    }

    public static void decryptFile(String algorithm, SecretKey key, IvParameterSpec iv, File enFile, File deFile) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
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

    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static IvParameterSpec generateIv(){
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

}
