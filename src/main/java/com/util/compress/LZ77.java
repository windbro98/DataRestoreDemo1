package com.util.compress;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.util.DataUtil.idxSubArray;
import static com.util.FileToolUtil.fileExistEval;

public class LZ77 {
    public static final int DEFAULT_BUFF_SIZE = 256; // 默认字典长度
    public byte[] searchList; // 字典
    public ByteBuffer currentBuffer; // 当前区域
    public int listLen; // 实际字典长度

    // LZ77默认初始化
    public LZ77() {
        this(DEFAULT_BUFF_SIZE);
    }
    // LZ77自定义字典长度初始化
    public LZ77(int buffSize) {
        searchList = new byte[buffSize];
        currentBuffer = ByteBuffer.allocate(buffSize);
    }

    // 解压
    public void unCompress(File enFile, File deFile) throws IOException {
        // 压缩和解码文件
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(enFile));
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(deFile));
        // 当前的编码
        byte[] code = new byte[3];

        while(is.read(code)>0){
            if(code[1]==0){ // 匹配失败，直接写入
                os.write(code[2]);
                currentBuffer.put(code[2]);
            }
            else{ // 匹配成功，将匹配到的字符串写入解压文件
                int pairStart = code[0]&0xFF;
                int pairLen = code[1]&0xFF;
                if(is.available()>0){ // 非最后一次匹配
                    byte[] tmpArray = new byte[pairLen+1];
                    System.arraycopy(searchList, pairStart, tmpArray, 0, pairLen);
                    tmpArray[pairLen] = code[2];
                    os.write(tmpArray);
                    currentBuffer.put(tmpArray);
                }
                else{ // 最后一次匹配
                    byte[] tmpArray = new byte[pairLen];
                    System.arraycopy(searchList, pairStart, tmpArray, 0, pairLen);
                    os.write(tmpArray);
                    currentBuffer.put(tmpArray);
                }
            }
            updateSearchList();
            currentBuffer.clear();
        }

        // 善后工作
        listLen = 0;
        Arrays.fill(searchList, (byte) 0);
        currentBuffer.clear();
        is.close();
        os.close();
    }

    // 压缩
    public void compress(File origFile, File enFile) throws IOException {
        // 原文件和压缩文件
        fileExistEval(enFile, true);
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(origFile));
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(enFile));
        // 匹配到的字符序号，下一个字符和临时序号
        int matchIndex = 0, nextInt, tmpIndex;
        // 编码，分别为匹配序号、匹配长度和下一个字符
        byte[] code = new byte[3];

        // 读取并压缩文件
        // readNum为读取的字节数, writeNum为写入的字节数，它们均在debug中使用
        int readNum = 0;
        int writeNum = 0;
        boolean debug = false;
        while ((nextInt = is.read()) != -1) {
            readNum ++;
            // 在字典中查找配对
            byte nextByte = (byte) nextInt;
            currentBuffer.put(nextByte);
            tmpIndex = idxSubArray(searchList, Arrays.copyOf(currentBuffer.array(), currentBuffer.position()));
            // 在字典中匹配成功
            if (tmpIndex != -1) {
                matchIndex = tmpIndex;
                // 已经是最后一个字节，则准备写入
                if(is.available()==0){
                    os.write(matchIndex);
                    os.write(currentBuffer.position());
                    writeNum += 2;
                }
            } else {// 匹配失败，准备写入
                writeNum += 3;
                // 准备写入的字符
                code[0] = (byte) matchIndex; // 匹配位置
                code[1] = (byte) (currentBuffer.position()-1); // 匹配长度
                code[2] = nextByte; // 未匹配字符
                // 写入字符
                os.write(code);
                // 更新字典，清空code与currentBuffer，准备下一次匹配
                updateSearchList();
                Arrays.fill(code, (byte) 0);
                currentBuffer.clear();
            }
        }
        if(debug){
            System.out.println("实际读取字节数"+readNum);
            System.out.println("理论写入字节数"+writeNum);
        }
        // 善后工作
        listLen = 0;
        currentBuffer.clear();
        Arrays.fill(searchList, (byte) 0);
        is.close();
        os.close();
    }

    // 更新字典
    private void updateSearchList(){
        // 全局listLen: 代表目前searchList的长度
        int bufferLen = currentBuffer.position(); // 本次写入的长度
        int searchLen = searchList.length; // 常量，即searchList的总长度
        int overflow = listLen+bufferLen-searchLen; //溢出字节数，负数或0表示不溢出
        // 不溢出
        if(overflow<=0){
            System.arraycopy(currentBuffer.array(), 0, searchList, listLen, bufferLen);
            listLen += bufferLen;
        }
        else { // 溢出
            System.arraycopy(searchList, overflow, searchList, 0, searchLen-bufferLen);
            System.arraycopy(currentBuffer.array(), 0, searchList, searchLen-bufferLen, bufferLen);
            listLen = searchLen;
        }
    }

}
