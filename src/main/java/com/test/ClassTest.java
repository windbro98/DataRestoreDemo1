package com.test;

import com.domain.BackManager;
import com.domain.ResManager;
import com.domain.SrcManager;

import java.io.IOException;
import java.util.ArrayList;
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
}
