package com.domain;
/**
 * todo: decryption和decompression书写
 */

import com.alibaba.fastjson2.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.util.FileToolUtil.*;

// 恢复文件管理器
public class ResManager {
    private String resDir; // 恢复文件目录
    private String compType; // 文件压缩方式
    private String encryType; // 文件编码方式

    // 初始化
    public ResManager(String resDir, String compType, String encryType) {
        this.resDir = resDir;
        this.compType = compType;
        this.encryType = encryType;
    }

    private static void fileDecryption(){

    }

    // 备份文件恢复
    public void fileRestore(String backFilePath) throws IOException {
        // 文件元数据提取
        String backJsonPath = backFilePath+".json";
        JSONObject metaJson = readJson(backJsonPath);
        // 备份文件提取
        File backFile = new File(backFilePath);
        FileInputStream is = new FileInputStream(backFile);
        int fileLen = -1;
        for(String key : metaJson.keySet()){
            fileLen = metaJson.getIntValue(key);
            fileRestoreSingle(is,  fileConcat(this.resDir, key), fileLen);
        }
    }



    // 文件解压缩
    private static void fileDecompression(){

    }
}
