package com.test;

import com.util.LZ77;
import com.util.LZ77Pro;

import java.io.IOException;

public class LZ77Test {
    public static void main(String[] args) throws IOException {
        String filePathOrig = "BackupData/20231217_200748";
        String filePathCompr = "BackupData/20231217_200748.lz77";

        // 5825KB -> 9160KB
        LZ77 lz = new LZ77();
        lz.compress(filePathOrig);
        lz.unCompress(filePathCompr);

        // 5825KB -> 8064KB
//        LZ77Pro lzPro = new LZ77Pro();
//        lzPro.compress(filePathOrig);
//        lzPro.unCompress(filePathCompr);
    }
}
