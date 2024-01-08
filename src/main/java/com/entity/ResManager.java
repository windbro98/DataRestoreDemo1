package com.entity;

import com.google.zxing.common.reedsolomon.ReedSolomonException;
import com.util.encrypt.AES;
import com.util.compress.Huffman;
import com.util.compress.LZ77;
import com.util.compress.LZ77Pro;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.util.encrypt.AES.keyLen;
import static com.util.DataUtil.byteArray2intArray;
import static com.util.DataUtil.deByteArray;
import static com.util.FileToolUtil.*;
import static com.util.page.PageManagerUtil.*;

// 恢复文件管理器，负责备份文件的解压、解密和恢复
public class ResManager {
    // 单例模式
    private final static ResManager INSTANCE = new ResManager();
    private ResManager(){}
    public static ResManager getInstance(){
        return INSTANCE;
    }
    /*
        属性
     */
    private String resDir; // 恢复文件目录
    private String password; // 密码，针对加密的备份文件
    public int compressType; // 备份文件压缩方式
    public int encryptType; // 备份文件加密方式
    int[] headMeta; // head文件元数据，包含5个变量：压缩类型，压缩辅助信息长度，加密类型，iv长度，salt长度
    byte[] headData; // head文件数据，包含压缩和加密所需的辅助数据

    // 设置密码，主要针对加密的备份文件
    public void setPassword(String password) {
        this.password = password;
    }

    // 初始化
    public void initResManager(String resDir, String backFilePath) throws IOException {
        // 恢复目录
        this.resDir = resDir;
        // 读取head文件元数据和辅助数据并进行初始化
        File backHeadFile = new File(backFilePath+"_head");
        // 读取head元数据和数据
        byte[] headMetaByte = new byte[BackManager.getInstance().headMetaLen*4];
        FileInputStream is = new FileInputStream(backHeadFile);
        is.read(headMetaByte);
        this.headData = is.readAllBytes();
        // 将元数据转为int，并解析压缩和加密方式
        this.headMeta = byteArray2intArray(headMetaByte);
        this.compressType = headMeta[0];
        this.encryptType = headMeta[3];
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
            case 1: { // AES256 CBC
                // 解析具体的head元数据
                int encodeMapLen = headMeta[1]; // 压缩对应的编码表长度
                int ivLen = headMeta[4]; // 加密的初始向量长度
                int saltLen = headMeta[5]; // 加盐长度
                // 解密后文件
                File decryptBackFile = new File(backFilePath+"_decrypt");
                // 获取初始向量
                byte[] ivByte = Arrays.copyOfRange(headData, encodeMapLen, encodeMapLen+ivLen);
                // 获取加盐后的密码
                byte[] salt = Arrays.copyOfRange(headData, encodeMapLen+ivLen, encodeMapLen+ivLen+saltLen);
                AES.salt = salt;
                byte[] keyByte = AES.generateKey(AES.keyLen, this.password);
                // 初始向量和加盐密码的结合，用于解密
                byte[] ivKey = new byte[ivLen + AES.keyLen];
                System.arraycopy(ivByte, 0, ivKey, 0, ivLen);
                System.arraycopy(keyByte, 0, ivKey, ivLen, keyLen);
                AES.decryptFile(ivKey, backFile, decryptBackFile);
                // 生成解密文件
                tmpBackFile = decryptBackFile;
                break;
            }
        }
        // 解压
        switch (this.compressType){
            case 1: { // Huffman
                // 获取编码表
                int encodeMapLen = headMeta[1];
                int lastLen = headMeta[2];
                byte[] encodeMapByte = Arrays.copyOfRange(headData, 0, encodeMapLen);
                String[] encodeMap = (String[]) deByteArray(encodeMapByte);
                // 将编码表写入Huffman类
                Huffman hm = new Huffman();
                hm.setEncodeMap(encodeMap);
                hm.lastLen = lastLen; // 最后一个字节长度
                // 解压文件
                File decompressFile = new File(backFilePath+"_decompress");
                hm.decode(tmpBackFile, decompressFile);
                tmpBackFile = decompressFile;
                break;
            }
            case 2: { // LZ77，不需要额外的辅助数据，直接解压
                LZ77 lz = new LZ77();
                File decompressFile = new File(backFilePath+"_decompress");
                lz.unCompress(tmpBackFile, decompressFile);
                tmpBackFile = decompressFile;
                break;
            }
            case 3: { // LZ77pro, 同LZ77，也不需要额外的辅助数据
                LZ77Pro lz = new LZ77Pro();
                File decompressFile = new File(backFilePath+"_decompress");
                lz.unCompress(tmpBackFile, decompressFile);
                tmpBackFile = decompressFile;
                break;
            }
        }

        // is指向的备份文件已经经过了解压和解密
        FileInputStream is = new FileInputStream(tmpBackFile);
        while(is.available()>0)
        {
            // 每次恢复一个文件，并根据CRC校验码判定该文件数据是否出错
            // 出错则返回文件名，不出错则返回""
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
        // 获取恢复文件类型
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
            dataRestore(is, resFile);
            setMetaData(resFile, metaData);
        }

        // 文件损坏检验，文件损坏则返回文件名
        return fileCheck(resFileName);
    }
}
