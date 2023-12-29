package com.test;

import com.util.LZ77;
import com.util.LZ77Pro;

import java.io.File;
import java.io.IOException;

public class LZ77Test {
    public static void main(String[] args) throws IOException {
        String filePathOrig = "BackupData/20231228_203501";
        String filePathCompr = "BackupData/20231228_203501.lz77";
        String filePathUncompr = "BackupData/20231228_203501.de";
        File origFile = new File(filePathOrig);
        File enFile = new File(filePathCompr);
        File deFile = new File(filePathUncompr);


        // 5825KB -> 9160KB
        LZ77 lz = new LZ77();
        lz.compress(origFile, enFile);
        lz.unCompress(enFile, deFile);

        // 5825KB -> 8064KB
//        LZ77Pro lzPro = new LZ77Pro();
//        lzPro.compress(filePathOrig);
//        lzPro.unCompress(filePathCompr);
    }
}
