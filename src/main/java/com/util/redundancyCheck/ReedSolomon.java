package com.util.redundancyCheck;

import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;

import static com.util.DataUtil.convertBytesToInts;
import static com.util.DataUtil.convertIntsToBytes;

public class ReedSolomon {
    // 文件加密，ecBytes即为纠删码长度（必须为偶数，默认为2）
    public static byte[] encodeData(byte[] originalData, int ecBytes, ReedSolomonEncoder encoder) {
        int dataLen = originalData.length; // 原数据长度
        // 原数据byte数组转为对应的int数组（因为接口支持的是int数组）
        int[] dataAsInts = new int[dataLen+ecBytes];

        System.arraycopy(convertBytesToInts(originalData), 0, dataAsInts, 0, dataLen);
        // 纠删码编码，编码后的纠删码会存储在dataAsInts最后几位
        encoder.encode(dataAsInts, ecBytes);
        return convertIntsToBytes(dataAsInts);
    }

    // 解码数据, ecBytes为纠删码长度
    public static byte[] decodeData(byte[] dataWithECC, int ecBytes, ReedSolomonDecoder decoder) throws ReedSolomonException {
        int[] dataAsInts = convertBytesToInts(dataWithECC);
        decoder.decode(dataAsInts, ecBytes);
        return convertIntsToBytes(dataAsInts);
    }
    // 错误模拟, numErrors代表错误数量
    private static void simulateErrors(byte[] data, int numErrors) {
        for (int i = 0; i < numErrors; i++) {
            int index = (int) (Math.random() * data.length);
            data[index] ^= 0x01;
        }
    }
    // 测试程序
    public static void main(String[] args) throws ReedSolomonException {
        // 原数据
        byte[] originalData = "HelloWorld".getBytes();
        int ecBytes = 2;
        System.out.println("Original data: " + new String(originalData));
        // 数据编码
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(GenericGF.DATA_MATRIX_FIELD_256);
        byte[] dataWithECC = encodeData(originalData, ecBytes,  encoder);
        System.out.println("Encoded data: " + new String(dataWithECC));

        // 模拟错误
        simulateErrors(dataWithECC, 1);
        System.out.println("Error data: "+new String(dataWithECC));

        // 数据解码
        ReedSolomonDecoder decoder = new ReedSolomonDecoder(GenericGF.DATA_MATRIX_FIELD_256);
        byte[] decodedData = decodeData(dataWithECC, ecBytes, decoder);
        System.out.println("Decoded data: " + new String(decodedData));
    }
}
