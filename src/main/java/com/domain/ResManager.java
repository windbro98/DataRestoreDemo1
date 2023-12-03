package com.domain;
/**
 * todo: decryption和decompression书写
 */

import com.alibaba.fastjson2.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.util.FileToolUtil.*;
import static com.util.PageManagerUtil.fileRestoreSingle;

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

    // 备份文件恢复，返回错误的文件
    public ArrayList<String> fileRestore(String backFilePath) throws IOException {
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



    // 文件解压缩
    private static void fileDecompression(){

    }
}
