package com.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import javafx.scene.control.TextField;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class FileToolUtil {
    // 工具类，禁止创建对象
    private FileToolUtil() {
    }
    
    public static int tmpDataLen = 1024;

    // 递归遍历文件夹，获取文件
    public static void fileWalkLoop(String srcDir, List<String> filePathSet) {
        // 递归获取源目录下所有文件的路径
        File src = new File(srcDir);
        String[] srcfilePathSets = src.list();
        for (String file : srcfilePathSets) {
            String filePath = fileConcat(srcDir, file);
            File f = new File(filePath);
            if (f.isFile())
                filePathSet.add(filePath);
            if (f.isDirectory())
                fileWalkLoop(filePath + '/', filePathSet);
        }
    }

    // 文件恢复
    public static int fileCopy(InputStream is, OutputStream os) throws IOException {
        int tmpLen=-1, fileLen=0;
        byte[] tmpData = new byte[tmpDataLen];
        while((tmpLen=is.read(tmpData)) != -1){
            fileLen += tmpLen;
            os.write(tmpData, 0, tmpLen);
        }
        return fileLen;
    }

    // 单个文件恢复
    public static void fileRestoreSingle(FileInputStream is, String resFilePath, int fileLen) throws IOException {
        File resFile = new File(resFilePath);
        fileExistEval(resFile, true);
        FileOutputStream os = new FileOutputStream(resFile);

        byte[] tmpData = new byte[tmpDataLen];
        int tmpLen=-1;
        while(fileLen>=tmpDataLen){
            is.read(tmpData);
            os.write(tmpData, 0, tmpDataLen);
            fileLen -= tmpDataLen;
        }
        if(fileLen>0){
            is.read(tmpData, 0, fileLen);
            os.write(tmpData, 0, fileLen);
        }
    }
    
    // 将map<String, String>写入json文件
    public static void writeJson(String jsonPath, Map<String, String> inMap){
        String data = JSON.toJSONString(inMap);
        File jsonFile = new File(jsonPath);
        try {
            // 验证json文件是否存在，不存在且创建失败则直接返回
            if(!fileExistEval(jsonFile, true))
                return;

            // 写入json文件
            FileWriter fw = new FileWriter(jsonFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data);
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 将json文件内容读取到map<String, String>
    public static JSONObject readJson(String jsonPath) throws IOException {
        File jsonFile = new File(jsonPath);
        String jsonString = Files.readString(jsonFile.toPath());
        JSONObject jo = JSONObject.parseObject(jsonString);
        return jo;
    }

    // 判断文件是否存在，不存在则创建
    public static boolean fileExistEval(File file, boolean create) throws IOException {
        boolean flag=true;
        if(file.exists()){
            return true;
        }
        else if(create){
            String dir = file.getParent();
            flag = dirExistEval(new File(dir));
            if(flag)
                flag = file.createNewFile();;
            return flag;
        }
        else{
            return false;
        }
    }

    // 判断目录是否存在，不存在则创建
    public static boolean dirExistEval(File dir){
        boolean flag=true;
        if(!dir.exists())
            flag = dir.mkdirs();
        return flag;
    }

    // 目录文件与文件名拼接
    public static String fileConcat(String dir, String file){
        return Paths.get(dir, file).toString();
    }

    // 判断文本框是否为空
    public static boolean tfIsEmpty(TextField tf){return tf.getText().isEmpty();}
}