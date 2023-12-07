package com.entity;
/**
 * todo: 过滤器的构建、使用
 */

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.nio.file.DirectoryStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        FileFilter fileFilter = pathname -> !Arrays.asList(ignoreFiles).contains(pathname.getPath());
        fileFilters.add(fileFilter);
    }

    public void setFilterDir(String filterDirStr) {
        String[] ignoreDirs = filterDirStr.split("\n");
        FileFilter dirFilter = pathname -> {
            for(String ignoreDir : ignoreDirs){
                if(pathname.getPath().contains(ignoreDir))
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
                    return true&&choiceFlag;
            }
            return false&&choiceFlag;
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
                    return true&&choiceFlag;
            }
            return false&&choiceFlag;
        };
        fileFilters.add(nameFilter);
    }

    private void initComFilter(){
        // 总的filter
        if(fileFilters.size()>0){
            comFilter = (FileFilter) pathname -> {
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
        filePathSet.removeIf(filePath -> !comFilter.accept(new File(filePath)));
        filePathSet.replaceAll(s -> s.replace(srcDir+File.separator, ""));
        return filePathSet;
    }
}



