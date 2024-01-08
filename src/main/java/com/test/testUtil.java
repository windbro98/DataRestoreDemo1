package com.test;

import com.entity.BackManager;
import com.entity.ResManager;
import com.entity.SrcManager;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import org.junit.Assert;

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

import static com.util.FileToolUtil.*;
import static com.util.FileToolUtil.deleteFile;

public class testUtil {
    // 工具类
    private testUtil(){}

    // 设置三个对应文件的时间
    public static void setTime() throws IOException, ParseException {
        // 三次设置时间，分别为设置创建最后修改时间、最后访问时间和创建时间
        for (int i = 0; i < 3; i++) {
            setTimeWin(i);
        }
    }

    // 设置源文件时间，0 - modified, 1 - access, 2 - create
    public static void setTimeWin(int timeType) throws IOException, ParseException {
        String filePath = null;
        // 预设时间
        FileTime setTime = setFileTime("1998-10-10 10:01:00");

        switch (timeType){
            // 最后修改时间
            case 0 -> filePath = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\OriginalDataWin\\filter\\timeFilter\\modified\\filteredFile.txt";
            // 最后访问时间
            case 1 -> filePath = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\OriginalDataWin\\filter\\timeFilter\\access\\filteredFile.txt";
            // 创建时间
            case 2 -> filePath = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\OriginalDataWin\\filter\\timeFilter\\create\\filteredFile.txt";
            default -> {
                return;
            }
        }

        // 获取文件元数据
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

    // 文件时间设置，用于调整时间格式
    public static FileTime setFileTime(String fileTimeStr) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return FileTime.fromMillis(timeFormat.parse(fileTimeStr).getTime());
    }

    // 不同类型的文件内容恢复的完整性检验
    public static void fileTypeAssert(String srcDir, String resDir) throws IOException {
        File srcDirFile = new File(srcDir);
        File[] files = srcDirFile.listFiles();

        for(File file : files){
            String fileName = file.getName();
            File srcFile = new File(fileConcat(srcDir, fileName));
            File resFile = new File(fileConcat(resDir, fileName));
            Assert.assertArrayEquals(readFile(resFile), readFile(srcFile));
        }
    }

    // 文件元数据恢复检验
    public static void metaAssert(String srcDir, String resDir) throws IOException {
        File srcDirFile = new File(srcDir);
        File[] files = srcDirFile.listFiles();

        for(File file : files){
            String fileName = file.getName();
            File srcFile = new File(fileConcat(srcDir, fileName));
            File resFile = new File(fileConcat(resDir, fileName));
            // 获取文件元数据
            String[] srcMeta = getMetaData(srcFile, false);
            String[] resMeta = getMetaData(resFile, false);
            // 去除最近访问时间，因为它不太靠谱
            Assert.assertArrayEquals(srcMeta, resMeta);
        }
    }

    // 设置筛选器，这里设置的是排除，包括文件、目录、格式、名称、时间、大小
    public static void setFilter(int filterType){
        SrcManager srcM = SrcManager.getInstance();
        // 筛选类型，包含还是排除
        String choice = (filterType==1) ? "包含" : "排除";

        // 时间格式
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 筛选的起始时间和终止时间
        LocalDateTime timeStart = LocalDateTime.from(timeFormat.parse("1998-10-10 10:00:00"));
        LocalDateTime timeEnd = LocalDateTime.from(timeFormat.parse("1998-10-10 10:02:00"));

        // 设置筛选器
        srcM.setFilterFormat(".png\n", choice); // 文件类型
        srcM.setFilterName("filteredName\n", choice); // 文件名
        srcM.setFilterTime(timeStart, timeEnd, "create", choice); // 创建时间
        srcM.setFilterTime(timeStart, timeEnd, "modified", choice); // 最后修改时间
        srcM.setFilterTime(timeStart, timeEnd, "access", choice); // 最后访问时间
        srcM.setFilterSize(56, 57, choice); // 文件大小
        // 排除文件
        srcM.setFilterFile("D:\\learning_programs\\java_programs\\DataRestoreDemo1\\OriginalDataWin\\filter\\fileFilter\\filteredFile.txt");
        // 排除目录
        srcM.setFilterDir("D:\\learning_programs\\java_programs\\DataRestoreDemo1\\OriginalDataWin\\filter\\dirFilter\\filteredDir");
    }

    // 设置文件筛选器，这里设置的是包含
    // filterClass: 0-名称，1-大小，2-类型，3-最近修改时间，4-最近访问时间，5-创建时间
    public static void setFilter(int filterType, int filterClass){
        String choice = (filterType==1) ? "包含" : "排除";
        SrcManager srcM = SrcManager.getInstance();

        // 时间格式
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 筛选的起始时间和终止时间
        LocalDateTime timeStart = LocalDateTime.from(timeFormat.parse("1998-10-10 10:00:00"));
        LocalDateTime timeEnd = LocalDateTime.from(timeFormat.parse("1998-10-10 10:02:00"));
        // 设置筛选器
        switch (filterClass){
            case 0 -> srcM.setFilterFormat(".png", choice); // 文件类型
            case 1 -> srcM.setFilterName("filteredName", choice); // 文件名称
            case 2 -> srcM.setFilterSize(56, 57, choice); // 文件大小
            case 3 -> srcM.setFilterTime(timeStart, timeEnd, "modified", choice); // 最后修改时间
            case 4 -> srcM.setFilterTime(timeStart, timeEnd, "access", choice); // 最后访问时间
            case 5 -> srcM.setFilterTime(timeStart, timeEnd, "create", choice); // 创建时间
            default -> {
                return;
            }
        }
    }

    // 验证筛选效果，这里主要验证的是排除的效果
    public static void verifyFilter(int filterType){
        // 被筛选的文件
        String filteredFile = "fileFilter\\filteredFile.txt"; // 文件筛选
        String filteredDir = "dirFilter\\filteredDir"; // 目录筛选
        String filteredFormat = "formatFilter\\zhihu.png"; // 文件格式筛选
        String filteredName = "nameFilter\\filteredName.txt"; // 文件名称筛选
        String filteredSize = "sizeFilter\\filteredSize.txt"; // 文件大小筛选
        String filteredCreateTime = "timeFilter\\create\\filteredFile.txt"; // 创建时间筛选
        String filteredAccessTime = "timeFilter\\access\\filteredFile.txt"; // 最后访问时间筛选
        String filteredModifiedTime = "timeFilter\\modified\\filteredFile.txt"; // 最后修改时间筛选
        // 设计了排除与包含两种筛选方式的文件
        String[] doubleFilterFiles = {filteredFormat, filteredSize, filteredName, filteredCreateTime, filteredAccessTime, filteredModifiedTime};
        String[] excludeFiles = {filteredFile, filteredDir};
        // 对具有两种文件筛选效果进行检验
        // 若筛选类型为排除，则正常判断；反之，则取相反的结果
        for(String doubleFilterFile : doubleFilterFiles){
            fileAssert(doubleFilterFile, filterType);
        }
        // 对于仅排除类型的筛选效果进行检验
        for(String excludeFile : excludeFiles){
            fileAssert(excludeFile, filterType);
        }
    }

    // 验证筛选效果，这里验证的是包含
    public static void verifyFilter(int filterType, int filterClass){
        // 被筛选的文件
        String filteredFormat = "formatFilter\\zhihu.png"; // 文件类型
        String filteredName = "nameFilter\\filteredName.txt"; // 文件名称
        String filteredSize = "sizeFilter\\filteredSize.txt"; // 文件大小
        String filteredModifiedTime = "timeFilter\\modified\\filteredFile.txt"; // 最后修改时间
        String filteredAccessTime = "timeFilter\\access\\filteredFile.txt"; // 最后访问时间
        String filteredCreateTime = "timeFilter\\create\\filteredFile.txt"; // 创建时间
        switch (filterClass){
            case 0 -> fileAssert(filteredFormat, filterType);
            case 1 -> fileAssert(filteredName, filterType);
            case 2 -> fileAssert(filteredSize, filterType);
            case 3 -> fileAssert(filteredModifiedTime, filterType);
            case 4 -> fileAssert(filteredAccessTime, filterType);
            case 5 -> fileAssert(filteredCreateTime, filterType);
        }

    }

    // 文件检验，检验筛选文件是否存在
    public static void fileAssert(String fileName, int filterType){
        // 恢复目录
        String resRoot = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\RestoreDataWin";
        boolean filterFlag = (filterType == 1);
        boolean resFlag = new File(fileConcat(resRoot, fileName)).exists();
        Assert.assertTrue(resFlag == filterFlag);
    }

    // 文件恢复
    public static void fileRestore(String srcDir, String backDir, String resDir) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException, ReedSolomonException, ClassNotFoundException {
        // 获取源文件、备份文件和恢复文件管理器
        SrcManager srcM = SrcManager.getInstance();
        BackManager backM = BackManager.getInstance();
        ResManager resM = ResManager.getInstance();
        // 恢复文件夹清空
        deleteFile(resDir);
        // 三大管理器初始化
        srcM.initSrcManager(srcDir);
        backM.initBackManager(backDir);
        System.out.println("所有源文件相对路径：");
        System.out.println(srcM.getFilePathSet());
        // 压缩方式和加密方式初始化
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
        resM.initResManager(resDir, backM.getBackFilePath());
        resM.setPassword("hello");
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
        // 文件筛选器重置
        srcM.resetFilter();
    }
}
