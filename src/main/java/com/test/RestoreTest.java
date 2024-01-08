package com.test;

import com.entity.BackManager;
import com.entity.ResManager;
import com.entity.SrcManager;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static com.test.testUtil.*;
import static com.util.FileToolUtil.*;

// 整体功能测试
public class RestoreTest {
    @Test // 文件类型检验，检验不同类型的文件恢复的内容是否与源文件一致
    public void fileTypeTest() throws ReedSolomonException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException, ClassNotFoundException {
        String srcDir = "OriginalDataWin\\文件类型";
        String backDir = "BackupData";
        String resDir = "RestoreDataWin";

        // 文件恢复
        fileRestore(srcDir, backDir, resDir);
        // 文件类型检验
        fileTypeAssert(srcDir, resDir);
    }

    @Test // 文件的元数据恢复检验
    public void fileMetaTest() throws ReedSolomonException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException, ClassNotFoundException {
        // 源文件、备份文件和恢复文件目录
        String srcDir = "OriginalDataWin\\filter\\fileFilter";
        String backDir = "BackupData";
        String resDir = "RestoreDataWin";

        // 恢复文件
        fileRestore(srcDir, backDir, resDir);
        metaAssert(srcDir, resDir);
    }

    @Test // 文件筛选检验
    public void filterTest() throws IOException, ClassNotFoundException, ParseException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException, ReedSolomonException, InterruptedException {
        // 源目录、备份目录和恢复目录
        String srcDir = "OriginalDataWin\\filter";
        String backDir = "BackupData";
        String resDir = "RestoreDataWin";

        // 提前处理源文件时间，将对应文件的创建时间、最近修改时间和最近访问时间都修改为1998-10-10 10:01:00
        // 这些时间对应的文件都是被排除的文件
        setTime();
        // 筛选类型，0代表排除，1代表包含
        int filterType;
        // 排除
        Thread.sleep(2000); // 延时，防止与其他两个测试类冲突
        filterType = 0;
        setFilter(filterType); // 设置筛选器
        fileRestore(srcDir, backDir, resDir); // 文件恢复
        verifyFilter(filterType); // 验证筛选效果
        // 包含
        filterType = 1;
        // 6种设置了"包含"的筛选器
        for (int i = 0; i < 6; i++) {
            setFilter(filterType, i);
            Thread.sleep(1100); // 休眠1s，防止备份文件名称相同
            fileRestore(srcDir, backDir, resDir);
            verifyFilter(filterType, i);
        }
   }

}
