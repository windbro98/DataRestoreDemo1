package com.domain;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import static com.util.FileToolUtil.*;

/**
 * 压缩、加密方式的默认赋值，使用
 */
public class BackManager {
    private String backDir;
    private String compType;
    private String encryType;
    private String backFilePath;

    public String getBackFilePath() {
        return backFilePath;
    }
    // todo: 设置压缩和加密的默认值
    public BackManager(String backDir, String compType, String encryType) {
        this.backDir = backDir;
        this.compType = compType;
        this.encryType = encryType;
    }
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

    public BackManager() {
    }

    public String fileExtract(List<String> filePathSet, String srcDir, int[] srcSize) {
        String backFilePath = "";
        File back = new File(this.backDir);
        // 判断目的目录是否存在，不存在且创建失败则直接返回
        if(!dirExistEval(back))
            return backFilePath;

        InputStream is = null;
        OutputStream os = null;
        int fileNum = filePathSet.size();
        LinkedHashMap<String, String> metaMap = new LinkedHashMap<String, String>();
        // 根据当前时间，为备份文件命名
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Calendar calendar = Calendar.getInstance();
        backFilePath = fileConcat(this.backDir, df.format(calendar.getTime()));
        // 缓存容器
        try {
            os = new FileOutputStream(backFilePath);
            // 遍历所有源文件，并复制到备份文件中
            for (int i = 0; i < fileNum; i++) {
                String inFilePath = filePathSet.get(i);
                is = new FileInputStream(fileConcat(srcDir, inFilePath));
                srcSize[i] = fileCopy(is, os);
                metaMap.put(inFilePath, ""+srcSize[i]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeJson(backFilePath+".json", metaMap);

        return backFilePath;
    }

    private void fileCompression(){

    }

    private void fileEncryption(){

    }

}
