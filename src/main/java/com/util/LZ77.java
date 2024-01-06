package com.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.util.DataUtil.idxSubArray;
import static com.util.FileToolUtil.fileExistEval;

public class LZ77 {
    public static final int DEFAULT_BUFF_SIZE = 256;
    public byte[] searchList;
    public ByteBuffer currentBuffer;
    public int listLen;

    public LZ77() {
        this(DEFAULT_BUFF_SIZE);
    }

    public LZ77(int buffSize) {
        searchList = new byte[buffSize];
        currentBuffer = ByteBuffer.allocate(buffSize);
    }

    public void unCompress(File enFile, File deFile) throws IOException {
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(enFile));
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(deFile));
        byte[] code = new byte[3];

        while(is.read(code)>0){
            if(code[1]==0){ // 匹配失败，直接写入
                os.write(code[2]);
                currentBuffer.put(code[2]);
            }
            else{ // 匹配成功
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

    /**
     * Compress method
     *
     * @param origFile the name of the file to compress. Automatically appends
     * a ".lz77" extension to inFilePath name when creating the output file
     * @exception IOException if an error occurs
     */
    public void compress(File origFile, File enFile) throws IOException {
        // set up input and output
        fileExistEval(enFile, true);
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(origFile));
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(enFile));

        int matchIndex = 0, nextInt, tmpIndex;
        byte[] code = new byte[3];

        // while there are more characters - read a character
        int readNum = 0;
        int writeNum = 0;
        while ((nextInt = is.read()) != -1) {
            readNum ++;
            // look in our search buffer for a match
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
                System.out.println("写入字节数"+writeNum);
            }
            System.out.println("读取字节数"+readNum);
        }
        System.out.println("实际读取字节数"+readNum);
        System.out.println("理论写入字节数"+writeNum);
        // 善后工作
        listLen = 0;
        currentBuffer.clear();
        Arrays.fill(searchList, (byte) 0);
        is.close();
        os.close();
    }

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
