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
        String filter = ".png\n.jpg";
        String compType = "";
        String encryType = "";

        SrcManager srcM = SrcManager.getInstance();
        BackManager backM = BackManager.getInstance();
        ResManager resM = ResManager.getInstance();
        srcM.setFilterFormat(filter, "包含");
        srcM.initSrcManager(srcDir);
        backM.initBackManager(backDir, compType, encryType);
        resM.initResManager(resDir, compType, encryType);
        System.out.println("所有源文件相对路径：");
        System.out.println(srcM.getFilePathSet());
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
