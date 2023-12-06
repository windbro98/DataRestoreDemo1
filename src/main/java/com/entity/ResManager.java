package com.entity;
/**
 * todo: decryption和decompression书写
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.util.FileToolUtil.*;
import static com.util.PageManagerUtil.*;

// 恢复文件管理器
public class ResManager {
    private String resDir; // 恢复文件目录
    private String compType; // 文件压缩方式
    private String encryType; // 文件编码方式

    public ResManager(){}

    // 初始化
    public void initResManager(String resDir, String compType, String encryType) {
        this.resDir = resDir;
        this.compType = compType;
        this.encryType = encryType;
    }

    private static void fileDecryption(){

    }

    // 备份文件恢复，返回错误的文件
    public ArrayList<String> fileRestore(String backFilePath) throws IOException, ClassNotFoundException {
        // 备份文件提取
        File backFile = new File(backFilePath);
        FileInputStream is = new FileInputStream(backFile);
        ArrayList<String> errorFileList = new ArrayList<String>();

        while(is.available()>0)
        {
            String errorFile = fileRestoreSingle(is, this.resDir);
            if(!errorFile.isEmpty())
                errorFileList.add(errorFile);
        }
        return errorFileList;
    }

    // 单个文件恢复
    public static String fileRestoreSingle(FileInputStream is, String resRoot) throws IOException, ClassNotFoundException {
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
            dataRestore(is, resFile);
            setMetaData(resFile, metaData);
        }

        // 文件损坏检验，文件损坏则返回文件名
        return pageCheck(resFileName);
    }



    // 文件解压缩
    private static void fileDecompression(){

    }
}
