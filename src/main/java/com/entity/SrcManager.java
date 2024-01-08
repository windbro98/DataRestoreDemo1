package com.entity;

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

// 源目录管理器，主要进行源文件的遍历和筛选
public class SrcManager {
    // 单例模式
    private final static SrcManager INSTANCE = new SrcManager();
    private SrcManager(){}
    public static SrcManager getInstance(){
        return INSTANCE;
    }
    /*
        属性
     */
    private String srcDir; // 源目录路径
    private List<String> filePathSet; // 源目录中所有筛选后的文件的路径集合
    private ArrayList<FileFilter> fileFilters = new ArrayList<>(); // 筛选器集合
    private FileFilter comFilter; // 将所有筛选器整合后得到的最终筛选器

    // 初始化
    public void initSrcManager(String srcDir){
        this.srcDir = srcDir; // 源目录
        initComFilter(); // 初始化文件筛选器
        filePathSet = fileWalk(srcDir, comFilter); // 根据文件筛选器，遍历并筛选源文件
    }
    // 属性获取
    // 获取源目录
    public String getSrcDir() {
        return srcDir;
    }
    // 获取源目录中筛选后的所有文件路径
    public List<String> getFilePathSet(){
        return filePathSet;
    }
    // 文件筛选器，根据文件路径排除文件
    public void setFilterFile(String filterFileStr) {
        String[] ignoreFiles = filterFileStr.split("\n");
        FileFilter fileFilter = pathname -> !Arrays.asList(ignoreFiles).contains(pathname.getAbsolutePath());
        fileFilters.add(fileFilter);
    }
    // 目录筛选器，根据目录路径排除目录
    // 这里在排除时，将该目录下的所有文件也一并排除
    public void setFilterDir(String filterDirStr) {
        String[] ignoreDirs = filterDirStr.split("\n");
        FileFilter dirFilter = pathname -> {
            for(String ignoreDir : ignoreDirs){
                // 排除该目录下的所有文件
                if(pathname.getAbsolutePath().contains(ignoreDir))
                    return false;
            }
            return true;
        };
        fileFilters.add(dirFilter);
    }
    // 文件类型筛选器，根据文件类型（后缀）包含或排除文件
    public void setFilterFormat(String filterFormatStr, String choice) {
        String[] ignoreFormats = filterFormatStr.split("\n");
        boolean choiceFlag;

        // choice为"包含": 仅备份该类型的文件
        // choice为"排除": 不备份该类型的文件
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
    // 文件名筛选器，根据文件名包含或排除文件
    public void setFilterName(String filterNameStr, String choice) {
        String[] ignoreNames = filterNameStr.split("\n");
        boolean choiceFlag;
        // choice为"包含": 仅备份对应文件名的文件
        // choice为"排除": 不备份对应文件名的文件
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
    // 文件大小过滤器，根据文件大小备份文件
    // 这里的文件大小以KB为单位
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
    // 时间过滤器，根据文件的时间（创建时间、最后修改时间、最后访问时间）包含或排除文件
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
                    // 最后修改时间
                    case "modified" -> time = LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), TimeZone.getDefault().toZoneId());
                    // 最后访问时间
                    case "access" -> time = LocalDateTime.ofInstant(attrs.lastAccessTime().toInstant(), TimeZone.getDefault().toZoneId());
                    // 创建时间
                    case "create" -> time = LocalDateTime.ofInstant(attrs.creationTime().toInstant(), TimeZone.getDefault().toZoneId());
                }
                return (time.isAfter(dateStart) && time.isBefore(dateEnd)) == choiceFlag;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        fileFilters.add(timeFilter);
    }
    // 整合所有文件过滤器，将其整合为comFilter
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

        // 遍历得到源目录下所有文件，并存储在filePathSet中
        fileWalkLoop(srcDir, filePathSet);
        // 对filePathSet中的文件再次遍历，查看其是否符合筛选条件，符合则去除
        if(comFilter != null)
            filePathSet.removeIf(filePath -> !comFilter.accept(new File(filePath)));
        // 去除文件名中的源目录，仅保留相对路径
        filePathSet.replaceAll(s -> s.replace(srcDir+File.separator, ""));
        return filePathSet;
    }
}



