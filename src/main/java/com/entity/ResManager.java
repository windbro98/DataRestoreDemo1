package com.entity;
/**
 * todo: decryption和decompression书写
 */

import com.google.zxing.common.reedsolomon.ReedSolomonException;
import com.util.AES;
import com.util.Huffman;
import com.util.LZ77;
import com.util.LZ77Pro;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.util.AES.ivLen;
import static com.util.AES.keyLen;
import static com.util.DataUtil.byteArray2intArray;
import static com.util.DataUtil.deByteArray;
import static com.util.FileToolUtil.*;
import static com.util.PageManagerUtil.*;

// 恢复文件管理器
public class ResManager {
    // 单例模式
    private final static ResManager INSTANCE = new ResManager();
    private ResManager(){}
    public static ResManager getInstance(){
        return INSTANCE;
    }
    private String resDir; // 恢复文件目录
    private String password;
    public int compressType;
    public int encryptType;
    int[] headMeta;
    byte[] headData;

    public void setPassword(String password) {
        this.password = password;
    }


    // 初始化
    public void initResManager(String resDir) {
        this.resDir = resDir;
    }

    private static void fileDecryption(){

    }

    public void initHead(String backFilePath) throws IOException {
        File backHeadFile = new File(backFilePath+"_head");
        // 读取head元数据和数据
        byte[] headMetaByte = new byte[BackManager.getInstance().headMetaLen*4];
        FileInputStream is = new FileInputStream(backHeadFile);
        is.read(headMetaByte);
        this.headData = is.readAllBytes();
        // 将元数据转为int，并进行具体的解析
        this.headMeta = byteArray2intArray(headMetaByte);
        this.compressType = headMeta[0];
        this.encryptType = headMeta[2];
    }

    // 备份文件恢复，返回错误的文件
    public ArrayList<String> fileRestore(String backFilePath) throws IOException, ClassNotFoundException, ReedSolomonException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        // 备份文件提取
        File backFile = new File(backFilePath);
        ArrayList<String> errorFileList = new ArrayList<String>();

        // 对backFile进行解压缩和恢复，在备份时先压缩后加密，因此在恢复时先解密后压缩
        File tmpBackFile = backFile;
        // 解密
        switch (this.encryptType){
            case 1: {
                int encodeMapLen = headMeta[1];
                int ivLen = headMeta[3];
                int saltLen = headMeta[4];
                File decryptBackFile = new File(backFilePath+"_decrypt");
                byte[] ivByte = Arrays.copyOfRange(headData, encodeMapLen, encodeMapLen+ivLen);
                byte[] salt = Arrays.copyOfRange(headData, encodeMapLen+ivLen, encodeMapLen+ivLen+saltLen);
                AES.salt = salt;
                byte[] keyByte = AES.generateKey(AES.keyLen, this.password);
                byte[] ivKey = new byte[ivLen + AES.keyLen];
                System.arraycopy(ivByte, 0, ivKey, 0, ivLen);
                System.arraycopy(keyByte, 0, ivKey, ivLen, keyLen);
                AES.decryptFile(ivKey, backFile, decryptBackFile);
                tmpBackFile = decryptBackFile;
                break;
            }
        }
        // 解压
        switch (this.compressType){
            case 1: {
                int encodeMapLen = headMeta[1];
                byte[] encodeMapByte = Arrays.copyOfRange(headData, 0, encodeMapLen);
                String[] encodeMap = (String[]) deByteArray(encodeMapByte);
                Huffman hm = new Huffman();
                hm.setEncodeMap(encodeMap);
                File decompressFile = new File(backFilePath+"_decompress");
                hm.decode(tmpBackFile, decompressFile);
                tmpBackFile = decompressFile;
                break;
            }
            case 2: {
                LZ77 lz = new LZ77();
                File decompressFile = new File(backFilePath+"_decompress");
                lz.unCompress(tmpBackFile, decompressFile);
                tmpBackFile = decompressFile;
                break;
            }
            case 3: {
                LZ77Pro lz = new LZ77Pro();
                File decompressFile = new File(backFilePath+"_decompress");
                lz.unCompress(tmpBackFile, decompressFile);
                tmpBackFile = decompressFile;
                break;
            }
        }

        FileInputStream is = new FileInputStream(tmpBackFile);
        while(is.available()>0)
        {
            String errorFile = fileRestoreSingle(is, this.resDir);
            if(!errorFile.isEmpty())
                errorFileList.add(errorFile);
        }
        is.close();
        // 删除中间文件
        tmpBackFile = new File(backFilePath+"_decrypt");
        tmpBackFile.delete();
        tmpBackFile = new File(backFilePath+"_decompress");
        tmpBackFile.delete();
        return errorFileList;
    }

    // 单个文件恢复
    public static String fileRestoreSingle(FileInputStream is, String resRoot) throws IOException, ClassNotFoundException, ReedSolomonException {
        // 获取恢复文件名
        String resFileName = getResFileName(is);
        String resFilePath = fileConcat(resRoot, resFileName);
        File resFile = new File(resFilePath);
        int fileType = getFileType();

        // 创建目录或文件
        if(fileType==0)
            dirExistEval(resFile);
        else
            fileExistEval(resFile, true);

        // 获取文件元数据
        String[] metaData = getResMeta(is);

        // 如果该对象是目录，则设置元数据
        if(fileType==0){
            setMetaData(resFile, metaData);
        } else {
            // 如果该对象是文件，则先恢复数据，后设置元数据
            // todo: 当使用huffman编码的时候，这里出错，最终的读取数据居然不为256
            dataRestore(is, resFile);
            setMetaData(resFile, metaData);
        }

        // 文件损坏检验，文件损坏则返回文件名
        return fileCheck(resFileName);
    }
}
