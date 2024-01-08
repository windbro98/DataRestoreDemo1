package com.test;

import com.entity.SrcManager;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import static com.test.testUtil.fileRestore;
import static com.util.FileToolUtil.fileConcat;

public class OverallTest {
    @Test // 整体测试
    public void overallTest() throws ReedSolomonException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException, ClassNotFoundException {
        String srcDir = "OriginalDataWin";
        String backDir = "BackupData";
        String resDir = "RestoreDataWin";

        // 文件恢复
        fileRestore(srcDir, backDir, resDir);
        // 获取源目录下所有文件
        List<String> fileNames = SrcManager.getInstance().getFilePathSet();
        // 检验源目录下所有文件在备份目录下是否存在
        for(String fileName : fileNames){
            String resPath = fileConcat(resDir, fileName);
            File resFile = new File(resPath);
            Assert.assertTrue(resFile.exists());
        }

    }
}
