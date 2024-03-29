package com.entity;
/**
 * todo: 过滤器的构建、使用
 */

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import static com.util.FileToolUtil.fileWalkLoop;

// 源目录管理器
public class SrcManager {
    // 单例模式
    private final static SrcManager INSTANCE = new SrcManager();
    private SrcManager(){}
    public static SrcManager getInstance(){
        return INSTANCE;
    }
    private String srcDir; // 源目录路径

    private List<String> filePathSet; // 源目录中所有文件的路径集合
    private ArrayList<FileFilter> fileFilters = new ArrayList<>();
    private FileFilter comFilter;

    public void initSrcManager(String srcDir){
        this.srcDir = srcDir;
        initComFilter();
        filePathSet = fileWalk(srcDir, comFilter);
    }

    public String getSrcDir() {
        return srcDir;
    }

    public List<String> getFilePathSet(){
        return filePathSet;
    }

    public void setFilterFile(String filterFileStr) {
        String[] ignoreFiles = filterFileStr.split("\n");
        FileFilter fileFilter = pathname -> !Arrays.asList(ignoreFiles).contains(pathname.getAbsolutePath());
        fileFilters.add(fileFilter);
    }

    public void setFilterDir(String filterDirStr) {
        String[] ignoreDirs = filterDirStr.split("\n");
        FileFilter dirFilter = pathname -> {
            for(String ignoreDir : ignoreDirs){
                if(pathname.getAbsolutePath().contains(ignoreDir))
                    return false;
            }
            return true;
        };
        fileFilters.add(dirFilter);
    }

    public void setFilterFormat(String filterFormatStr, String choice) {
        String[] ignoreFormats = filterFormatStr.split("\n");
        boolean choiceFlag;

        if(choice.equals("包含"))
            choiceFlag = true;
        else {
            choiceFlag = false;
        }
        FileFilter formatFilter = pathname -> {
            for(String format : ignoreFormats){
                if(pathname.getPath().endsWith(format))
                    return choiceFlag;
            }
            return !choiceFlag;
        };
        fileFilters.add(formatFilter);
    }

    public void setFilterName(String filterNameStr, String choice) {
        String[] ignoreNames = filterNameStr.split("\n");
        boolean choiceFlag;

        if(choice.equals("包含"))
            choiceFlag = true;
        else {
            choiceFlag = false;
        }
        FileFilter nameFilter = pathname -> {
            for(String name : ignoreNames){
                if(pathname.getName().contains(name))
                    return choiceFlag;
            }
            return !choiceFlag;
        };
        fileFilters.add(nameFilter);
    }

    public void setFilterSize(long sizeMin, long sizeMax, String choice){
        boolean choiceFlag;

        if(choice.equals("包含"))
            choiceFlag = true;
        else {
            choiceFlag = false;
        }
        FileFilter sizeFilter = pathname -> {
            long fileSize = pathname.length() / 1024;
            return ((fileSize >= sizeMin) && (fileSize <= sizeMax)) == choiceFlag;
        };
        fileFilters.add(sizeFilter);
    }

    // todo: 主要修改时间格式问题
    public void setFilterTime(LocalDateTime dateStart, LocalDateTime dateEnd, String timeType, String choice){
        boolean choiceFlag;

        if(choice.equals("包含"))
            choiceFlag = true;
        else {
            choiceFlag = false;
        }
        FileFilter timeFilter = pathname -> {
            try {
                BasicFileAttributes attrs = Files.readAttributes(pathname.toPath(), BasicFileAttributes.class);
                LocalDateTime time=null;
                switch (timeType) {
                    case "modified" -> time = LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), TimeZone.getDefault().toZoneId());
                    case "access" -> time = LocalDateTime.ofInstant(attrs.lastAccessTime().toInstant(), TimeZone.getDefault().toZoneId());
                    case "create" -> time = LocalDateTime.ofInstant(attrs.creationTime().toInstant(), TimeZone.getDefault().toZoneId());
                }
                return (time.isAfter(dateStart) && time.isBefore(dateEnd)) == choiceFlag;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        fileFilters.add(timeFilter);
    }

    private void initComFilter(){
        // 总的filter
        if(fileFilters.size()>0){
            comFilter =  pathname -> {
                boolean flag = true;
                for(FileFilter filter:fileFilters){
                    flag = flag && filter.accept(pathname);
                }
                return flag;
            };
        }
        else
            comFilter = null;
    };

    // 遍历源目录中所有文件
    private List<String> fileWalk(String srcDir, FileFilter comFilter){
        List<String> filePathSet = new ArrayList<>();

        fileWalkLoop(srcDir, filePathSet);
        if(comFilter != null)
            filePathSet.removeIf(filePath -> !comFilter.accept(new File(filePath)));
        filePathSet.replaceAll(s -> s.replace(srcDir+File.separator, ""));
        return filePathSet;
    }
}



