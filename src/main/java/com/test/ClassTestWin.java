package com.test;

import com.entity.BackManager;
import com.entity.ResManager;
import com.entity.SrcManager;
import com.google.zxing.common.reedsolomon.ReedSolomonException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ClassTestWin {
    public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException, ReedSolomonException {
        String srcDir = "OriginalDataWin";
        String backDir = "BackupData";
        String resDir = "RestoreDataWin";
        String compType = "";
        String encryType = "";
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timeStart = LocalDateTime.from(timeFormat.parse("1998-10-10 10:00:00"));
        LocalDateTime timeEnd = LocalDateTime.from(timeFormat.parse("1998-10-10 10:02:00"));
        SrcManager srcM = SrcManager.getInstance();
        BackManager backM = BackManager.getInstance();
        ResManager resM = ResManager.getInstance();

        // 提前处理源文件时间
        for (int i = 0; i < 3; i++) {
            setTimeWin(i);
        }
        // 清空文件夹
        delete(resDir);
        // 设置筛选器
        srcM.setFilterFormat(".png\n", "排除");
        srcM.setFilterName("filteredName\n", "排除");
        srcM.setFilterTime(timeStart, timeEnd, "create", "排除");
        srcM.setFilterTime(timeStart, timeEnd, "modified", "排除");
        srcM.setFilterTime(timeStart, timeEnd, "access", "排除");
        srcM.setFilterSize(56, 57, "排除");
        srcM.setFilterFile("D:\\learning_programs\\java_programs\\DataRestoreDemo1\\OriginalDataWin\\filter\\fileFilter\\filteredFile.txt");
        srcM.setFilterDir("D:\\learning_programs\\java_programs\\DataRestoreDemo1\\OriginalDataWin\\filter\\dirFilter\\filteredDir");
        // 三大管理器初始化
        srcM.initSrcManager(srcDir);
        backM.initBackManager(backDir);
        resM.initResManager(resDir);
        System.out.println("所有源文件相对路径：");
        System.out.println(srcM.getFilePathSet());
        // 压缩方式和加密方式初始化
        // todo: 将压缩和加密的方式应用在恢复文件上
        backM.setCompressType("Huffman");
        backM.setEncryptType("AES256");
        backM.setPassword("hello");
        // 备份
        backM.fileExtract(srcM.getFilePathSet(), srcM.getSrcDir());
        String backFilePath = backM.getBackFilePath();
        backM.setBackFilePath(backFilePath);
        System.out.println("备份文件位置：");
        System.out.println(backFilePath);
        // 恢复
        resM.setPassword("hello");
        resM.initHead(backFilePath);
        ArrayList<String> errorFileList = resM.fileRestore(backFilePath);
        if(errorFileList.isEmpty())
            System.out.println("恢复成功");
        else{
            System.out.println("出现文件损坏！损坏文件为：");
            for (String errorFile :
                    errorFileList) {
                System.out.println(errorFile);
            }
        }
    }

    public static FileTime transFileTime(String fileTimeStr) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return FileTime.fromMillis(timeFormat.parse(fileTimeStr).getTime());
    }

    // 设置源文件时间，0 - modified, 1 - access, 2 - create
    public static void setTimeWin(int timeType) throws IOException, ParseException {
        String filePath = null;
        FileTime setTime = transFileTime("1998-10-10 10:01:00");

        switch (timeType){
            case 0 -> filePath = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\OriginalDataWin\\filter\\timeFilter\\modified\\filteredFile.txt";
            case 1 -> filePath = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\OriginalDataWin\\filter\\timeFilter\\access\\filteredFile.txt";
            case 2 -> filePath = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\OriginalDataWin\\filter\\timeFilter\\create\\filteredFile.txt";
            default -> {
                return;
            }
        }

        File file = new File(filePath);
        BasicFileAttributeView attributeView = Files.getFileAttributeView(file.toPath(), BasicFileAttributeView.class);
        BasicFileAttributes attributes = attributeView.readAttributes();
        // 获取文件时间
        FileTime creationTime = attributes.creationTime();
        FileTime lastAccessTime = attributes.lastAccessTime();
        FileTime lastModifiedTime = attributes.lastModifiedTime();

        // 时间设置
        switch (timeType){
            case 0 -> lastModifiedTime = setTime;
            case 1 -> lastAccessTime = setTime;
            case 2 -> creationTime = setTime;
        }
        attributeView.setTimes(lastModifiedTime, lastAccessTime, creationTime);
    }

    // 删除文件或文件夹
    public static void delete(String path) {
        // 为传进来的路径参数创建一个文件对象
        File file = new File(path);
        // 如果目标路径是一个文件，那么直接调用delete方法删除即可
        // file.delete();
        // 如果是一个目录，那么必须把该目录下的所有文件和子目录全部删除，才能删除该目标目录，这里要用到递归函数
        // 创建一个files数组，用来存放目标目录下所有的文件和目录的file对象
        File[] files = new File[50];
        // 将目标目录下所有的file对象存入files数组中
        files = file.listFiles();
        // 循环遍历files数组
        for(File temp : files){
            // 判断该temp对象是否为文件对象
            if (temp.isFile()) {
                temp.delete();
            }
            // 判断该temp对象是否为目录对象
            if (temp.isDirectory()) {
                // 将该temp目录的路径给delete方法（自己），达到递归的目的
                delete(temp.getAbsolutePath());
                // 确保该temp目录下已被清空后，删除该temp目录
                temp.delete();
            }
        }

    }
}
