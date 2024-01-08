package com.test;

import com.util.compress.Huffman;
import com.util.compress.LZ77;
import com.util.compress.LZ77Pro;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.util.FileToolUtil.readFile;

public class CompressTest {
    @Test
    public void huffmanTest() throws IOException, ClassNotFoundException {
        Huffman hm = new Huffman();

        // 源文件
        File inFile = new File("testFile\\文本1.txt");
        // 压缩文件
        File enFile = new File("testFile\\文本1_huffman.txt");
        // 恢复文件
        File deFile = new File("testFile\\文本1_huffman_res.txt");

        // 压缩
        hm.encode(inFile, enFile);
        // 解压
        hm.decode(enFile, deFile);
        // 检验恢复的文件是否与源文件内容相同
        Assert.assertArrayEquals(readFile(inFile), readFile(deFile));
    }
    @Test
    public void LZ77Test() throws IOException {
        // 源文件
        String filePathOrig = "testFile\\文本1.txt";
        // 备份文件
        String filePathCompr = "testFile\\文本1_lz77.txt";
        // 恢复文件
        String filePathUncompr = "testFile\\文本1_lz77_res.txt";
        File origFile = new File(filePathOrig);
        File enFile = new File(filePathCompr);
        File deFile = new File(filePathUncompr);

        // LZ77压缩
        LZ77 lz = new LZ77();
        lz.compress(origFile, enFile); // 压缩
        lz.unCompress(enFile, deFile); // 解压
        // 检验是否成功恢复
        Assert.assertArrayEquals(readFile(origFile), readFile(deFile));
    }

    @Test
    public void LZ77ProTest() throws IOException {
        // 源文件
        String filePathOrig = "testFile\\文本1.txt";
        // 备份文件
        String filePathCompr = "testFile\\文本1_lz77pro.txt";
        // 恢复文件
        String filePathUncompr = "testFile\\文本1_lz77pro_res.txt";
        File inFile = new File(filePathOrig);
        File enFile = new File(filePathCompr);
        File deFile = new File(filePathUncompr);

        // LZ77pro压缩
        LZ77Pro lzPro = new LZ77Pro();
        lzPro.compress(inFile, enFile); // 压缩
        lzPro.unCompress(enFile, deFile); // 解压
        // 检验是否成功恢复
        Assert.assertArrayEquals(readFile(inFile), readFile(deFile));
    }
}
