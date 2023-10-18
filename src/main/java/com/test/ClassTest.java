package com.test;

import com.domain.BackManager;
import com.domain.ResManager;
import com.domain.SrcManager;

import java.io.IOException;
import java.util.Arrays;

public class ClassTest {
    public static void main(String[] args) throws IOException {
        String srcDir = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\OriginalData";
        String backDir = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\BackupData";
        String resDir = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\RestoreData";
        String compType = "";
        String encryType = "";

        SrcManager srcM = new SrcManager(srcDir);
        BackManager backM = new BackManager(backDir, compType, encryType);
        ResManager resM = new ResManager(resDir, compType, encryType);
        System.out.println("所有源文件相对路径：");
        System.out.println(srcM.getSelFilePath());

        String backFilePath = backM.fileExtract(srcM.getFilePathSet(), srcDir, srcM.getSrcSize());
        System.out.println("各个源文件大小：");
        System.out.println(Arrays.toString(srcM.getSrcSize()));
        backM.setBackFilePath(backFilePath);
        System.out.println("备份文件位置：");
        System.out.println(backFilePath);

        resM.fileRestore(backFilePath);
    }
}
