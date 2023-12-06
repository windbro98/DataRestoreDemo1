package com.test;

import com.entity.BackManager;
import com.entity.ResManager;
import com.entity.SrcManager;

import java.io.IOException;
import java.util.ArrayList;

public class ClassTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String srcDir = "OriginalData";
        String backDir = "BackupData";
        String resDir = "RestoreData";
        String compType = "";
        String encryType = "";

        SrcManager srcM = new SrcManager();
        BackManager backM = new BackManager();
        ResManager resM = new ResManager();
        srcM.initSrcManager(srcDir);
        backM.initBackManager(backDir, compType, encryType);
        resM.initResManager(resDir, compType, encryType);
        System.out.println("所有源文件相对路径：");
        System.out.println(srcM.getSelFilePath());
//
        // 备份
        backM.fileExtract(srcM.getSelFilePath(), srcM.getsrcDir());
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
