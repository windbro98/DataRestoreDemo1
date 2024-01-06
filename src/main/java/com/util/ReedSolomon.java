package com.util;

import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;

public class ReedSolomon {
    public static byte[] encodeData(byte[] originalData, int ecBytes, ReedSolomonEncoder encoder) {
        int dataLen = originalData.length;
        int[] dataAsInts = new int[dataLen+ecBytes];

        System.arraycopy(convertBytesToInts(originalData), 0, dataAsInts, 0, dataLen);
        encoder.encode(dataAsInts, ecBytes);
        return convertIntsToBytes(dataAsInts);
    }

    public static byte[] decodeData(byte[] dataWithECC, int ecBytes, ReedSolomonDecoder decoder) throws ReedSolomonException {
        int[] dataAsInts = convertBytesToInts(dataWithECC);
        decoder.decode(dataAsInts, ecBytes);
        return convertIntsToBytes(dataAsInts);
    }

    private static int[] convertBytesToInts(byte[] data) {
        int[] result = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i] & 0xFF;
        }
        return result;
    }

    private static byte[] convertIntsToBytes(int[] data) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) data[i];
        }
        return result;
    }

    private static void simulateErrors(byte[] data, int numErrors) {
        // Simulate errors by changing some bytes
        for (int i = 0; i < numErrors; i++) {
            int index = (int) (Math.random() * data.length);
            data[index] ^= 0x01; // Flip the bit
        }
    }

    public static void main(String[] args) throws ReedSolomonException {
        // Example parameters

        // Original data
        byte[] originalData = "HelloWorld".getBytes();
        int ecBytes = 2;
        System.out.println("Original data: " + new String(originalData));

        // Encode the data
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(GenericGF.DATA_MATRIX_FIELD_256);
        byte[] dataWithECC = encodeData(originalData, ecBytes,  encoder);
        System.out.println("Encoded data: " + new String(dataWithECC));

        // Simulate errors (you can replace this with your error-handling logic)
        simulateErrors(dataWithECC, 1);
        System.out.println("Error data: "+new String(dataWithECC));

        // Decode the data
        ReedSolomonDecoder decoder = new ReedSolomonDecoder(GenericGF.DATA_MATRIX_FIELD_256);
        byte[] decodedData = decodeData(dataWithECC, ecBytes, decoder);
        System.out.println("Decoded data: " + new String(decodedData));
    }
}
