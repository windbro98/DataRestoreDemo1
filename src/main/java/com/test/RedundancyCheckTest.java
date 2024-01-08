package com.test;

import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import com.util.redundancyCheck.CRC;
import com.util.redundancyCheck.ReedSolomon;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static com.util.DataUtil.byte2binary;
import static com.util.DataUtil.byteArray2binary;
import static com.util.FileToolUtil.readFile;

public class RedundancyCheckTest {
    @Test // CRC校验码测试
    public void CRCTest() throws IOException {
        CRC crc = new CRC();
        String filePath = "testFile/文本1.txt";
        File origFile = new File(filePath);
        // 源文件数据
        byte[] origByte = readFile(origFile);
        // 计算循环校验码
        byte FCS = crc.getFCS(origByte);
        // 测试数据损坏
        byte[] mutByte = crc.setMutation(origByte);

        // 判定结果
        boolean res;
        // 正常数据，得到的判定结果res应为true
        res = crc.judge(origByte, FCS);
        Assert.assertTrue(res);
        // 错误数据，得到的判定结果res应为false
        res = crc.judge(mutByte, FCS);
        Assert.assertFalse(res);
    }

    @Test // RS纠删码测试
    public void ReedSolomonTest() throws ReedSolomonException {
        byte[] origByte = "HelloWorld".getBytes();
        int dataLen = origByte.length;
        int ecBytes = 2;
        System.out.println("Original data: " + new String(origByte));
        // 数据编码
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(GenericGF.DATA_MATRIX_FIELD_256);
        byte[] dataWithECC = ReedSolomon.encodeData(origByte, ecBytes,  encoder);
        System.out.println("Encoded data: " + new String(dataWithECC));

        // 模拟错误
        simulateErrors(dataWithECC, 1);
        System.out.println("Error data: "+new String(dataWithECC));

        // 数据解码
        ReedSolomonDecoder decoder = new ReedSolomonDecoder(GenericGF.DATA_MATRIX_FIELD_256);
        byte[] decodedData = ReedSolomon.decodeData(dataWithECC, ecBytes, decoder);
        System.out.println("Decoded data: "+new String(decodedData));
        // 判定纠正后的数据是否与元数据相同
        Assert.assertArrayEquals(Arrays.copyOfRange(decodedData, 0, dataLen), origByte);
    }

    // 错误模拟, numErrors代表错误数量
    public static void simulateErrors(byte[] data, int numErrors) {
        for (int i = 0; i < numErrors; i++) {
            int index = (int) (Math.random() * data.length);
            data[index] ^= 0x01;
        }
    }
}
