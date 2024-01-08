package com.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.min;

public class FileToolUtil {

    // 工具类，禁止创建对象
    private FileToolUtil() {
    }

    // 获取文件元数据，包括创建时间，最后修改时间访问时间和最后修改时间
    public static String[] getMetaData(File file, boolean lastAccess) throws IOException {
        String owner, creationTime, lastAccessTime, lastModifiedTime, filePermission;

        // 获取属性表
        Path path = file.toPath();
        FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(path, FileOwnerAttributeView.class);
        BasicFileAttributeView basicFileAttributeView = Files.getFileAttributeView(path, BasicFileAttributeView.class);
        BasicFileAttributes attributes = basicFileAttributeView.readAttributes();

        // 时间，包括创建时间、最后访问时间和最后修改时间
        lastAccessTime = String.valueOf(attributes.lastAccessTime().toMillis());
        creationTime = String.valueOf(attributes.creationTime().toMillis());
        lastModifiedTime = String.valueOf(attributes.lastModifiedTime().toMillis());
        // 属主
        owner = ownerAttributeView.getOwner().getName();
        // 权限
        int filePermissionInt = 0;
        if(file.canExecute()) filePermissionInt += 1;
        if(file.canRead()) filePermissionInt += 2;
        if(file.canWrite()) filePermissionInt += 4;
        filePermission = String.valueOf(filePermissionInt);

        if(lastAccess) // 读取最后访问时间
            return new String[]{owner, creationTime, lastAccessTime, lastModifiedTime, filePermission};
        else // 不读取最后访问时间
            return new String[]{owner, creationTime, lastModifiedTime, filePermission};
    }

    // 设置文件元数据
    public static void setMetaData(File file, String[] metaData) throws IOException{
        String owner=metaData[0], creationTime=metaData[1], lastAccessTime=metaData[2], lastModifiedTime=metaData[3], filePermission=metaData[4];

        Path filePath = Paths.get(file.getAbsolutePath());
        // owner
        UserPrincipalLookupService upls = FileSystems.getDefault().getUserPrincipalLookupService();
        UserPrincipal newOwner = upls.lookupPrincipalByName(owner);
        try{
            Files.setOwner(filePath, newOwner);
        }catch(IOException ade){
            // 无权限，一般是在Windows上且使用Idea直接运行
//            System.out.println("无权限");
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
        File[] srcFileSets = src.listFiles();

        for (File file : srcFileSets) {
            String filePath = file.getPath();
            filePathSet.add(filePath);
            if (file.isDirectory())
                fileWalkLoop(filePath + File.separator, filePathSet);
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
        else if(create){ // 确定创建文件
            // 首先确定目录是否存在，不存在则创建
            String dir = file.getParent();
            flag = dirExistEval(new File(dir));
            if(flag) // 目录存在或创建成功
                flag = file.createNewFile(); // 创建文件
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
        return dir + File.separator + file;
    }
    // 将文件数据fileData写入文件
    public static void writeFile(String filePath, byte[] fileData) throws IOException {
        File file = new File(filePath);
        fileExistEval(file, true);
        FileOutputStream os = new FileOutputStream(file);
        os.write(fileData);
        os.close();
    }
    // 以byte数组形式，读取文件中所有数据
    public static byte[] readFile(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        return is.readAllBytes();
    }

    // 删除文件或文件夹，文件直接删除，目录则只删除目录下所有文件
    public static void deleteFile(String path) {
        // 为传进来的路径参数创建一个文件对象
        File file = new File(path);
        // 如果目标路径不存在，则直接返回
        if(!file.exists())
            return;
        // 如果目标路径是一个文件，那么直接调用delete方法删除即可
        if(file.isFile())
            file.delete();
        // 如果是一个目录，那么必须把该目录下的所有文件和子目录全部删除，才能删除该目标目录，这里要用到递归函数
        // 创建一个files数组，用来存放目标目录下所有的文件和目录的file对象
        File[] files = file.listFiles();
        // 循环遍历files数组
        for(File temp : files){
            // 判断该temp对象是否为文件对象
            if (temp.isFile()) {
                temp.delete();
            }
            // 判断该temp对象是否为目录对象
            if (temp.isDirectory()) {
                // 将该temp目录的路径给delete方法（自己），达到递归的目的
                deleteFile(temp.getAbsolutePath());
                // 确保该temp目录下已被清空后，删除该temp目录
                temp.delete();
            }
        }
    }

    // 判断文本框是否为空
    public static boolean tfIsEmpty(TextField tf){return tf.getText().isEmpty();}
    // 判断文本区域是否为空
    public static boolean taIsEmpty(TextArea ta){return ta.getText().isEmpty();}
}