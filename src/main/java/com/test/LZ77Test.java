package com.test;

import com.util.compress.LZ77;

import java.io.File;
import java.io.IOException;

// LZ77测试
public class LZ77Test {
    public static void main(String[] args) throws IOException {
        // 源文件
        String filePathOrig = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\src\\main\\java\\com\\test\\testFile\\文本1.txt";
        // 备份文件
        String filePathCompr = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\src\\main\\java\\com\\test\\testFile\\文本1_lz77.txt";
        // 恢复文件
        String filePathUncompr = "D:\\learning_programs\\java_programs\\DataRestoreDemo1\\src\\main\\java\\com\\test\\testFile\\文本1_lz77_res.txt";
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
