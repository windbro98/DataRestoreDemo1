package com.entity;
/**
 * todo: 过滤器的构建、使用
 */

import java.io.File;
import java.io.FileFilter;
import java.nio.file.DirectoryStream;
import java.util.ArrayList;
import java.util.List;

import static com.util.FileToolUtil.fileWalkLoop;

// 源目录管理器
public class SrcManager {
    private String srcDir; // 源目录路径

    private List<File> filePathSet; // 源目录中所有文件的路径集合
    private filter fileFilter; // 源目录文件过滤器
    private List<String> selFilePath; // 筛选后的源目录文件路径集合
    private List<Integer> srcDirLenCum;


    // 源目录管理器初始化
    public SrcManager() {
    }

    public void initSrcManager(String srcDir){
        this.srcDir = srcDir;
        this.filePathSet = fileWalk(srcDir);
        this.fileFilter = new filter();
        this.selFilePath = fileSelect(this.filePathSet, this.fileFilter);
    }

    public List<Integer> getSrcDirLenCum() {
        return srcDirLenCum;
    }

    public void setSrcDirLenCum(List<Integer> srcDirLenCum) {
        this.srcDirLenCum = srcDirLenCum;
    }

    public String getsrcDir() {
        return srcDir;
    }

    public List<File> getFilePathSet() {
        return filePathSet;
    }

    public filter getFileFilter() {
        return fileFilter;
    }

    public List<String> getSelFilePath() {
        return selFilePath;
    }

    // 过滤器
    private static class filter{
        String[] type;
        String[] name;
        int maxSize;
        int minSize;
        String Time;
    }

    // 遍历源目录中所有文件
    private List<File> fileWalk(String srcDir){
        List<File> filePathSet = new ArrayList<File>();

        fileWalkLoop(srcDir, filePathSet);

        return filePathSet;
    }

    // todo: 过滤器的具体设计
    // 源目录文件筛选
    // todo: 这里可以将文件的输入改为file类型，从输入上改而不是在这里转换
    private List<String> fileSelect(List<File> filePathSet, filter fileFilter){
        List<File> selFiles = filePathSet;
        List<String> selFilePath = new ArrayList<>();

        // 筛选器
        // 文件格式
        FileFilter formatFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return false;
            }
        };

        // 大小
        FileFilter sizeFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return false;
            }
        };

        // 名字
        FileFilter nameFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return false;
            }
        };

        // 路径
        FileFilter pathFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return false;
            }
        };

        // 时间
        FileFilter timeFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return false;
            }
        };

        for(File file:selFiles){
            selFilePath.add(file.getPath());
        }

        // 文件路径处理
        String rDir = srcDir+File.separator;
        selFilePath.replaceAll(s -> s.replace(rDir, ""));

        return selFilePath;
    }

}



