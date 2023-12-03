package com.domain;

import java.io.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.util.FileToolUtil.*;
import static com.util.PageManagerUtil.nameCopy;
import static com.util.PageManagerUtil.fileCopy;

/**
    todo: 压缩、加密方式的默认赋值，使用
 */
// 备份目录管理器
public class BackManager {
    private String backDir; // 备份文件目录
    private String compType; // 压缩方式
    private String encryType; // 编码方式
    private String backFilePath=""; // 备份文件路径

    public String getBackFilePath() {
        return backFilePath;
    }
    // todo: 设置压缩和加密的默认值
    // 备份文件管理器初始化
    public BackManager(String backDir, String compType, String encryType) {
        this.backDir = backDir;
        this.compType = compType;
        this.encryType = encryType;
    }
    public BackManager() {
    }
    // 属性的设置与获取
    public void setBackFilePath(String backFilePath) {
        this.backFilePath = backFilePath;
    }

    public String getBackDir() {
        return backDir;
    }

    public String getCompType() {
        return compType;
    }

    public String getEncryType() {
        return encryType;
    }

    // 从源目录中提取并生成备份文件
    public boolean fileExtract(List<String> filePathSet, String srcDir) {
        File back = new File(this.backDir);
        // 备份目录存在性验证，否则直接返回
        if(!dirExistEval(back))
            return false;

        InputStream is = null;
        OutputStream os = null;
        int fileNum = filePathSet.size();
        // 备份文件命名，以备份时间为名
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Calendar calendar = Calendar.getInstance();
        this.backFilePath = fileConcat(this.backDir, df.format(calendar.getTime()));
        // 缓存容器
        try {
            os = new FileOutputStream(this.backFilePath);
            // 遍历所有源文件，并复制到备份文件中
            for (int i = 0; i < fileNum; i++) {
                String inFilePath = filePathSet.get(i);
                String inFilePathAbs = fileConcat(srcDir, inFilePath);
                File inFile = new File(inFilePathAbs);
                int fileType = (inFile.isFile())?1:0;
                if(fileType==1){
                    is = new FileInputStream(inFilePathAbs);
                    fileCopy(is, os, inFilePath, inFile);
                }
                else
                    nameCopy(os, inFilePath, inFile, fileType);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private void fileCompression(){

    }

    private void fileEncryption(){

    }

}
