package com.entity;

import com.util.encrypt.AES;
import com.util.compress.Huffman;
import com.util.compress.LZ77;
import com.util.compress.LZ77Pro;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.util.DataUtil.*;
import static com.util.FileToolUtil.*;
import static com.util.page.PageManagerUtil.*;

/*
    文件备份管理器，负责源文件的打包、压缩和加密
 */
public class BackManager {
    // 单例模式
    private final static BackManager INSTANCE = new BackManager();
    private BackManager() {
    }
    public static BackManager getInstance(){
        return INSTANCE;
    }

    private String backDir; // 备份文件目录
    private String compressType=""; // 压缩方式
    private String encryptType=""; // 加密方式
    private String backFilePath=""; // 备份文件路径
    private String password = ""; // 加密时的备份文件密码
    final int headCompressMetaLen = 3; // head文件中压缩元数据长度
    final int headEncryptMetaLen = 3; // head文件中加密元数据长度
    int headMetaLen = 6; // 备份文件会有阈值对应的head文件，这里是head文件中元数据的数量
    /*
        类属性设置
     */

    // 设置密码
    public void setPassword(String password) {
        this.password = password;
    }
    // 设置压缩方式
    public void setCompressType(String compressType) {
        this.compressType = compressType;
    }
    // 设置加密方式
    public void setEncryptType(String encryptType) {
        this.encryptType = encryptType;
    }
    // 设置备份文件路径
    public void setBackFilePath(String backFilePath) {
        this.backFilePath = backFilePath;
    }
    /*
        类属性获取
     */

    // 获取备份文件路径
    public String getBackFilePath() {
        return backFilePath;
    }
    // 获取备份文件目录
    public String getBackDir() {
        return backDir;
    }
    /*
        初始化
     */
    // 备份文件管理器初始化，仅初始化备份文件所在的目录
    public void initBackManager(String backDir) {
        this.backDir = backDir;
    }
    /*
        提取文件
     */

    // 从源目录中提取并生成备份文件
    public boolean fileExtract(List<String> filePathSet, String srcDir) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        File backDir = new File(this.backDir);
        // 备份目录存在性验证，否则直接返回
        if(!dirExistEval(backDir))
            return false;


        // 备份文件命名，以备份时间为名
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Calendar calendar = Calendar.getInstance();
        this.backFilePath = fileConcat(this.backDir, df.format(calendar.getTime()));
        File backFile = new File(this.backFilePath);
        // 临时备份文件，若后续经过压缩或加密则会删除，否则将作为最终备份文件
        String tmpFilePath = this.backFilePath+"_tmp";
        File tmpFile = new File(tmpFilePath);
        tmpFile.createNewFile();
        // is: 源文件字节输入流
        // os: 备份文件字节输入流
        InputStream is = null;
        OutputStream os = null;
        int fileNum = filePathSet.size(); // 源目录下文件数量
        // 将所有文件提取到备份文件
        try {
            os = new FileOutputStream(tmpFile);
            // 遍历所有源文件，并复制到备份文件中
            for (int i = 0; i < fileNum; i++) {
                // 获取输入文件的绝对路径
                String inFilePath = filePathSet.get(i);
                String inFilePathAbs = fileConcat(srcDir, inFilePath);
                File inFile = new File(inFilePathAbs);
                // 判断该输入文件为文件还是目录，文件对应1，目录对应0
                int fileType = (inFile.isFile())?1:0;
                if(fileType==1){ // 文件备份
                    is = new FileInputStream(inFilePathAbs);
                    filePaged(is, os, inFilePath, inFile);
                    is.close();
                }
                else // 目录备份
                    dirPaged(os, inFilePath, inFile);
            }
            os.close(); // 完成备份
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // headMeta中包含5个信息：压缩方式，压缩对应的辅助数据长度（如Huffman中的编码表），加密方式，iv长度，salt长度
        int[] headMeta = new int[headMetaLen];
        byte[] headData = new byte[0]; // 存储的压缩或加密的辅助数据，如Huffman的编码表等
        // 文件的压缩和加密
        File compressFile = new File(this.backFilePath+"_compression");
        File encryptFile = new File(this.backFilePath+"_encryption");
        /*
            压缩
         */
        switch (this.compressType){
            case "Huffman": {
                Huffman hm = new Huffman();
                hm.encode(tmpFile, compressFile); // 压缩
                // 设置head文件的元数据：压缩类型和编码表长度，并将编码表写入文件
                headMeta[0] = 1;
                byte[] encodeMapByte = enByteArray(hm.getEncodeMap());
                headMeta[1] = encodeMapByte.length;
                headData = byteArrayConcat(headData, encodeMapByte);
                headMeta[2] = hm.lastLen; // 最后一个字节对应的实际二进制长度
                break;
            }
            case "LZ77": {
                LZ77 lz = new LZ77();
                lz.compress(tmpFile, compressFile); // 压缩
                // 设置head文件的元数据：压缩类型和编码表长度
                headMeta[0] = 2;
                headMeta[1] = 0;
                headMeta[2] = 0;
                break;
            }
            case "LZ77Pro": {
                LZ77Pro lz = new LZ77Pro();
                lz.compress(tmpFile, compressFile); // 压缩
                // 设置head文件的元数据：压缩类型和编码表长度
                headMeta[0] = 3;
                headMeta[1] = 0;
                headMeta[2] = 0;
                break;
            }
            default:
                break;

        }
        // 压缩后处理，将目前的处理指针tmpFile切换到处理后的文件上，并删除之前的文件（如果经过了压缩）
        if(!this.compressType.isEmpty()){
            tmpFile.delete();
            tmpFile = compressFile;
        }
        /*
            加密
         */
        switch(this.encryptType){
            case "AES256": {
                byte[] ivByte = AES.encryptFile(this.password, tmpFile, encryptFile);
                // 设置head元数据，分别为加密类型，初始向量长度和加盐长度
                headMeta[headCompressMetaLen] = 1;
                headMeta[headCompressMetaLen+1] = AES.ivLen;
                headMeta[headCompressMetaLen+2] = AES.saltLen;
                // 将初始向量和盐存入head文件
                headData = byteArrayConcat(headData, ivByte);
                headData = byteArrayConcat(headData, AES.salt);
                break;
            }
            default:
                break;
        }
        // 加密后处理，删除之前的文件，将指针指向当前的文件
        if(!this.encryptType.isEmpty()){
            tmpFile.delete();
            tmpFile = encryptFile;
        }
        // 将最终修改后的文件名称修改为backup文件名称，而不再携带编码等信息
        tmpFile.renameTo(backFile);
        // 生成head文件
        writeFile(this.backFilePath+"_head",
                byteArrayConcat(intArray2byteArray(headMeta), headData));
        return true;
    }
}
