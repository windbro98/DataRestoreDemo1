package com.entity;

import com.util.AES;
import com.util.Huffman;
import com.util.LZ77;
import com.util.LZ77Pro;

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

import static com.util.FileToolUtil.*;
import static com.util.PageManagerUtil.*;

/**
    todo: 压缩、加密方式的默认赋值，使用
 */
// 备份目录管理器
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
    private String encryptType=""; // 编码方式
    private String backFilePath=""; // 备份文件路径
    private String password = "";

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBackFilePath() {
        return backFilePath;
    }
    public String getCompressType() {
        return compressType;
    }

    public void setCompressType(String compressType) {
        this.compressType = compressType;
    }

    public String getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(String encryptType) {
        this.encryptType = encryptType;
    }


    // todo: 设置压缩和加密的默认值
    // 备份文件管理器初始化
    public void initBackManager(String backDir) {
        this.backDir = backDir;
    }

    // 属性的设置与获取
    public void setBackFilePath(String backFilePath) {
        this.backFilePath = backFilePath;
    }

    public String getBackDir() {
        return backDir;
    }

    // 从源目录中提取并生成备份文件
    public boolean fileExtract(List<String> filePathSet, String srcDir) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        File backDir = new File(this.backDir);
        // 备份目录存在性验证，否则直接返回
        if(!dirExistEval(backDir))
            return false;

        InputStream is = null;
        OutputStream os = null;
        int fileNum = filePathSet.size();
        // 备份文件命名，以备份时间为名
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Calendar calendar = Calendar.getInstance();
        this.backFilePath = fileConcat(this.backDir, df.format(calendar.getTime()));
        File backFile = new File(this.backFilePath);
        // 临时备份文件，后续可能直接转正，也可能被删除
        String tmpFilePath = this.backFilePath+"_tmp";
        File tmpFile = new File(tmpFilePath);
        tmpFile.createNewFile();
        // 将所有文件提取到备份文件
        try {
            os = new FileOutputStream(tmpFile);
            // 遍历所有源文件，并复制到备份文件中
            for (int i = 0; i < fileNum; i++) {
                String inFilePath = filePathSet.get(i);
                String inFilePathAbs = fileConcat(srcDir, inFilePath);
                File inFile = new File(inFilePathAbs);
                int fileType = (inFile.isFile())?1:0;
                if(fileType==1){
                    is = new FileInputStream(inFilePathAbs);
                    filePaged(is, os, inFilePath, inFile);
                }
                else
                    dirPaged(os, inFilePath, inFile);
            }
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 文件的压缩和加密
        File compressFile = new File(this.backFilePath+"_compression");
        File encryptFile = new File(this.backFilePath+"_encryption");
        // 备份文件压缩
        switch (this.compressType){
            case "Huffman": {
                Huffman hm = new Huffman();
                hm.encode(tmpFile, compressFile);
                break;
            }
            case "LZ77": {
                LZ77 lz = new LZ77();
                lz.compress(tmpFile, compressFile);
                break;
            }
            case "LZ77Pro": {
                LZ77Pro lz = new LZ77Pro();
                lz.compress(tmpFile, compressFile);
                break;
            }
            default:
                break;

        }
        // 压缩后处理，将目前的处理指针tmpFile切换到处理后的文件上，并删除之前的文件
        if(!this.compressType.isEmpty()){
            tmpFile.delete();
            tmpFile = compressFile;
        }
        // 备份文件加密
        switch(this.encryptType){
            case "AES256": {
                AES.encryptFile(password, tmpFile, encryptFile);
                break;
            }
            default:
                break;
        }
        if(!this.encryptType.isEmpty()){
            // todo: 这里没有成功删除
            tmpFile.delete();
            tmpFile = encryptFile;
        }
        // 将最终修改后的文件名称修改为backup文件名称，而不再携带编码等信息
        tmpFile.renameTo(backFile);

        return true;
    }

    private void fileCompression(){

    }

    private void fileEncryption(){

    }

}
