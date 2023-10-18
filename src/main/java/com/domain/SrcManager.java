package com.domain;
/**
 * todo: 过滤器的构建、使用
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.util.FileToolUtil.fileWalkLoop;

public class SrcManager {
    private String srcDir;
    private int[] srcSize;

    public int[] getSrcSize() {
        return srcSize;
    }

    public SrcManager(String srcDir) {
        this.srcDir = srcDir;
        this.filePathSet = fileWalk(srcDir);
        // todo
        this.fileFilter = new filter();
        this.selFilePath = fileSelect(this.filePathSet, this.fileFilter);
        this.srcSize = new int[this.selFilePath.size()];
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

    private List<String> filePathSet;
    private filter fileFilter;
    private List<String> selFilePath;

    public SrcManager() {
    }

    // 过滤器
    private static class filter{
        String[] type;
        String[] name;
        int maxSize;
        int minSize;
        String Time;
    }

    private static List<String> fileWalk(String srcDir){
        List<String> filePathSet = new ArrayList<String>();
        File src = new File(srcDir);
        // 判断源目录是否存在
        if (!src.isDirectory())
            return filePathSet;

        fileWalkLoop(srcDir, filePathSet);
        String rDir;
        if(srcDir.endsWith("/") || srcDir.endsWith("\\"))
            rDir = srcDir;
        else
            rDir = srcDir+'\\';
        filePathSet.replaceAll(s -> s.replace(rDir, ""));
        return filePathSet;
    }

    // todo: 过滤器的具体涉及
    private static List<String> fileSelect(List<String> filePathSet, filter fileFilter){
        List<String> selFilePath;
        selFilePath = filePathSet;
        return selFilePath;
    }

}



