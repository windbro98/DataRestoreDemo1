package com.domain;
/**
 * todo: 过滤器的构建、使用
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.util.FileToolUtil.fileWalkLoop;

// 源目录管理器
public class SrcManager {
    private String srcDir; // 源目录路径
    private int[] srcSize; // 源目录各个文件的大小

    private List<String> filePathSet; // 源目录中所有文件的路径集合
    private filter fileFilter; // 源目录文件过滤器
    private List<String> selFilePath; // 筛选后的源目录文件路径集合

    // 源目录管理器初始化
    public SrcManager(String srcDir) {
        this.srcDir = srcDir;
        this.filePathSet = fileWalk(srcDir);
        // todo
        this.fileFilter = new filter();
        this.selFilePath = fileSelect(this.filePathSet, this.fileFilter);
        this.srcSize = new int[this.selFilePath.size()];
    }

    public SrcManager() {
    }

    // 获取源目录管理器的属性
    public int[] getSrcSize() {
        return srcSize;
    }


    public String getSrcDir() {
        return srcDir;
    }

    public List<String> getFilePathSet() {
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
    private static List<String> fileWalk(String srcDir){
        List<String> filePathSet = new ArrayList<String>();
        File src = new File(srcDir);
        // 源目录存在性验证
        if (!src.isDirectory())
            return filePathSet;

        fileWalkLoop(srcDir, filePathSet);
        String rDir;
<<<<<<< HEAD
        if(srcDir.endsWith("/") || srcDir.endsWith("/"))
=======
        // 文件路径处理
        if(srcDir.endsWith("/") || srcDir.endsWith("\\"))
>>>>>>> v1
            rDir = srcDir;
        else
            rDir = srcDir+'/';
        filePathSet.replaceAll(s -> s.replace(rDir, ""));
        return filePathSet;
    }

    // todo: 过滤器的具体设计
    // 源目录文件筛选
    private static List<String> fileSelect(List<String> filePathSet, filter fileFilter){
        List<String> selFilePath;
        selFilePath = filePathSet;
        return selFilePath;
    }

}



