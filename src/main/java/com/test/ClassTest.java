package com.test;

import com.domain.BackManager;
import com.domain.ResManager;
import com.domain.SrcManager;

import java.io.IOException;
import java.util.Arrays;

public class ClassTest {
    public static void main(String[] args) throws IOException {
        String srcDir = "OriginalData";
        String backDir = "BackupData";
        String resDir = "RestoreData";
        String compType = "";
        String encryType = "";

        SrcManager srcM = new SrcManager(srcDir);
        BackManager backM = new BackManager(backDir, compType, encryType);
        ResManager resM = new ResManager(resDir, compType, encryType);
        System.out.println("所有源文件相对路径：");
        System.out.println(srcM.getSelFilePath());
//
        // 备份
        backM.fileExtract(srcM.getFilePathSet(), srcM.getSrcDir());
        String backFilePath = backM.getBackFilePath();
        backM.setBackFilePath(backFilePath);
        System.out.println("备份文件位置：");
        System.out.println(backFilePath);

        // 恢复
        resM.fileRestore(backFilePath);
    }
}
