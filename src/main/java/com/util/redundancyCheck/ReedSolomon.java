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
}
