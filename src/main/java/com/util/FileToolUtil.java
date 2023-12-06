package com.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import javafx.scene.control.TextField;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.min;

public class FileToolUtil {

    // 工具类，禁止创建对象
    private FileToolUtil() {
    }

    // 获取文件元数据
    public static String[] getMetaData(File file) throws IOException {
        String owner, creationTime, lastAccessTime, lastModifiedTime, filePermission;
        Path path = file.toPath();
        FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(path, FileOwnerAttributeView.class);
        BasicFileAttributeView basicFileAttributeView = Files.getFileAttributeView(path, BasicFileAttributeView.class);
        BasicFileAttributes attributes = basicFileAttributeView.readAttributes();

        // 属主
        owner = ownerAttributeView.getOwner().getName();
        // 时间，包括创建时间、最后访问时间和最后修改时间
        creationTime = String.valueOf(attributes.creationTime().toMillis());
        lastAccessTime = String.valueOf(attributes.lastAccessTime().toMillis());
        lastModifiedTime = String.valueOf(attributes.lastModifiedTime().toMillis());
        // 权限
        int filePermissionInt = 0;
        if(file.canExecute()) filePermissionInt += 1;
        if(file.canRead()) filePermissionInt += 2;
        if(file.canWrite()) filePermissionInt += 4;
        filePermission = String.valueOf(filePermissionInt);

        return new String[]{owner, creationTime, lastAccessTime, lastModifiedTime, filePermission};
    }

    // 设置文件元数据
    public static void setMetaData(File file, String[] metaData) throws IOException {
        String owner=metaData[0], creationTime=metaData[1], lastAccessTime=metaData[2], lastModifiedTime=metaData[3], filePermission=metaData[4];

        Path filePath = Paths.get(file.getAbsolutePath());
        // owner
        UserPrincipalLookupService upls = FileSystems.getDefault().getUserPrincipalLookupService();
        UserPrincipal newOwner = upls.lookupPrincipalByName(owner);
        try{
            Files.setOwner(filePath, newOwner);
        }catch(IOException ade){
            // todo: 无权限警告
            System.out.println("无权限");
        }

        // permission
        int filePermisson =Integer.valueOf(filePermission);
        if(filePermisson%2==1) file.setExecutable(true);
        if((filePermisson>>1)%2==1) file.setReadable(true);
        if((filePermisson>>2)%2==1) file.setWritable(true);

        // lastModifiedTime, lastAccessTime, creationTime
        BasicFileAttributeView attributes = Files.getFileAttributeView(filePath, BasicFileAttributeView.class);
        attributes.setTimes(
                FileTime.fromMillis(Long.parseLong(lastModifiedTime)), // lastModifiedTime
                FileTime.fromMillis(Long.parseLong(lastAccessTime)), // lastAccessTime
                FileTime.fromMillis(Long.parseLong(creationTime)) // creationTime
        );
    }

    // 递归遍历文件夹，获取文件
    public static void fileWalkLoop(String srcDir, List<String> filePathSet) {
        // 递归获取源目录下所有文件的路径
        File src = new File(srcDir);
        String[] srcfilePathSets = src.list();
        for (String file : srcfilePathSets) {
            String filePath = fileConcat(srcDir, file);
            File f = new File(filePath);
            filePathSet.add(filePath);
            if (f.isDirectory())
                fileWalkLoop(filePath + '/', filePathSet);
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
//        return Paths.get(dir, file).toString();
        return dir + '/' + file;
    }

    // 判断文本框是否为空
    public static boolean tfIsEmpty(TextField tf){return tf.getText().isEmpty();}
}