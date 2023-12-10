package com.util;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.util.DataUtil.*;
import static com.util.FileToolUtil.*;
import static java.lang.Math.min;

public class PageManagerUtil {
    private static Page tmpPage = new Page(); // 正在处理的页面
    private static boolean pageStatus = true; // 判断文件是否损坏

    // 工具类，禁止创建对象
    private PageManagerUtil(){
    }

    /*
        第一部分：文件备份相关函数
        对外接口：
        filePaged - 文件页面化，并提取到备份文件
        cirPaged - 目录页面化，并提取到备份文件
     */

    // 文件页面化，提取到备份文件
    public static void filePaged(InputStream is, OutputStream os, String fileName, File inFile) throws IOException {
        int fileType=1;
        int pageStart;

        // dataStart：代表可以在该页面中写数据的初始索引位置
        pageStart = namePaged(os, fileName, fileType); // 文件名
        // todo: 元数据的pageStart处理有问题，得到的数值大于256
        pageStart = metaPaged(os, inFile, pageStart, fileType); // 元数据
        dataPaged(is, os, pageStart); // 数据
    }

    // 目录页面化，提取到备份文件
    public static void dirPaged(OutputStream os, String fileName, File inFile) throws IOException {
        int fileType=0;
        int pageStart;

        // dataStart：代表可以在该页面中写数据的初始索引位置
        pageStart = namePaged(os, fileName, fileType); // 文件名
        metaPaged(os, inFile, pageStart, fileType); // 元数据
    }

    // 将文件名写入页面
    // 返回值表示当前页面可写入的第一个索引位置
    public static int namePaged(OutputStream os, String name, int fileType) throws IOException {
        int dataLen=0, metaLen=0, idxStart=0, tailPage=0;
        int copyLen;
        byte[] dirBytes = name.getBytes();

        // 写入文件名
        while(idxStart >= 0){
            copyLen = min(dirBytes.length-idxStart, Page.pageDataLen);

            idxStart = copyByteArray(dirBytes, idxStart, 0, copyLen);
            // 生成tmpHead
            buildHead(fileType, tailPage, copyLen, metaLen, dataLen);
            if(idxStart>=0) //
                writePage(os);
        }

        return -idxStart;
    }

    // 将文件元数据写入页面
    // 返回值表示当前页面可写入的第一个索引位置
    public static int metaPaged(OutputStream os, File file, int pageStart, int fileType) throws IOException {
        String[] metaData = getMetaData(file);
        byte[] metaBytes = enByteArray(metaData);
        int metaLen = metaBytes.length, idxStart=0;
        boolean firstFlag = true; // 代表是否是第一次写入文件元数据
        int copyLen, tailPage, idxStartAbs;

        while(idxStart >= 0){
            copyLen = min(metaLen-idxStart, Page.pageDataLen-pageStart);
            idxStart = copyByteArray(metaBytes, idxStart, pageStart, copyLen);
            idxStartAbs = Math.abs(idxStart);
            tailPage = ((fileType==0) && (metaLen-idxStartAbs)<=Page.pageDataLen) ? 1 : 0;
            if(firstFlag){
                tmpPage.metaLen = (byte)copyLen;
                tmpPage.setTailPage(tailPage);
                pageStart = 0;
                firstFlag = false;
            }
            else{
                buildHead(fileType, tailPage, 0, copyLen, 0);
            }
            if(idxStart>=0 || fileType==0)
                writePage(os);
        }

        return -idxStart;
    }

    // 将文件数据页面化
    public static void dataPaged(InputStream is, OutputStream os, int dataStart ) throws IOException {
        int tailPage, dataLen;
        boolean firstFlag=true;

        while(is.available()>0){
            dataLen = copyData(is, dataStart);
            tailPage = (is.available()>0) ? 0 : 1;
            if(firstFlag){
                tmpPage.setTailPage(tailPage);
                tmpPage.dataLen = (byte)dataLen;
                firstFlag = false;
                dataStart = 0;
            }
            else{
                buildHead(1, tailPage, 0, 0, dataLen);
            }
            writePage(os);
        }
    }

    /*
        第二部分：文件恢复相关函数
        对外接口：
        getResFileName - 获取恢复文件名
        getResFileMeta - 获取恢复文件元数据
        dataRestore - 恢复文件数据
     */
    // 获取恢复文件名
    public static String getResFileName(FileInputStream is) throws IOException {
        byte[] fileNameByte=null;
        byte[] tmpFileNameByte;

        readPage(is);
        while(tmpPage.getNameLen()>0){
            // tmpPage.getNameLen() -> headLen
            if(fileNameByte != null){
                int prevLen = fileNameByte.length;
                tmpFileNameByte = fileNameByte;
                fileNameByte = new byte[prevLen+tmpPage.getNameLen()];
                System.arraycopy(tmpFileNameByte, 0, fileNameByte, 0, prevLen);
                System.arraycopy(tmpPage.pageData, 0, fileNameByte, prevLen, tmpPage.getNameLen());
            }
            else{
                fileNameByte = new byte[tmpPage.getNameLen()];
                System.arraycopy(tmpPage.pageData, 0, fileNameByte, 0, tmpPage.getNameLen());
            }
            if(tmpPage.getNameLen()==Page.pageDataLen) // headLen==Page.pageDataLen, 表示此时标题仍然没有读尽
                readPage(is);
            else
                break;
        }

        return new String(fileNameByte);
    }

    // 获取恢复文件元数据
    public static String[] getResMeta(FileInputStream is) throws IOException, ClassNotFoundException {
        byte[] metaByte=null;
        byte[] tmpMetaByte;

        while(tmpPage.getMetaLen()>0){
            // tmpPage.getNameLen() -> headLen
            if(metaByte != null){
                int prevLen = metaByte.length;
                tmpMetaByte = metaByte;
                metaByte = new byte[prevLen+tmpPage.getMetaLen()];
                System.arraycopy(tmpMetaByte, 0, metaByte, 0, prevLen);
                System.arraycopy(tmpPage.pageData, 0, metaByte, prevLen, tmpPage.getMetaLen());
            }
            else{
                metaByte = new byte[tmpPage.getMetaLen()];
                System.arraycopy(tmpPage.pageData, tmpPage.getNameLen(), metaByte, 0, tmpPage.getMetaLen());
            }
            if(tmpPage.getMetaLen()==(Page.pageDataLen-tmpPage.getNameLen())) // headLen==Page.pageDataLen, 表示此时标题仍然没有读尽
                readPage(is);
            else
                break;
        }

        try{
            String[] demo = (String[]) deByteArray(metaByte);
        }
        catch (EOFException e){
            System.out.println("here");
        }

        return (String[]) deByteArray(metaByte);
    }

    // 数据恢复
    public static void dataRestore(FileInputStream is,  File resFile) throws IOException {
        FileOutputStream os = new FileOutputStream(resFile);
        while(tmpPage.getTailPage()!=1){ // 非尾页面
            os.write(tmpPage.pageData, tmpPage.getNameLen()+tmpPage.getMetaLen(), tmpPage.getDataLen());
            readPage(is);
        };
        // 尾页面
        // 尾页面的所有数据由三部分组成：文件名+文件数据+空白
        os.write(tmpPage.pageData, tmpPage.getNameLen()+tmpPage.getMetaLen(), tmpPage.getDataLen());
        os.close();
    }

    /*
        第三部分：其他工具函数
     */

    // tmpData和大部分的tmpHead准备完成，写入page
    public static void writePage(OutputStream os) throws IOException {
        tmpPage.setCrcCode();;
        // 计算循环冗余校验码
        os.write(tmpPage.getHead(), 0, Page.pageHeadLen);
        os.write(tmpPage.pageData, 0, Page.pageDataLen);
        tmpPage.reset();
    }

    // 构建tmpHead, tmpHead[3]的位置留给了crcCode，但是这是在最后生成的
    public static void buildHead(int fileType, int tailPage, int nameLen, int metaLen, int dataLen) throws IOException {
        // 0位-是否是头页面；1位-是否是文件；2位-是否是尾页面
        byte headPrefix = (byte)(fileType + (tailPage<<1));
        tmpPage.headPrefix = headPrefix;
        tmpPage.nameLen = (byte)nameLen;
        tmpPage.metaLen = (byte)metaLen;
        tmpPage.dataLen = (byte)dataLen;
    }

    // 复制文件数据，返回值realDataLen为本次实际写入的数据大小
    public static int copyData(InputStream is, int dataStart) throws IOException {
        // 读取页面数据
        int realDataLen;
        if(dataStart>0){
            byte[] tmpDataRemain = new byte[Page.pageDataLen-dataStart];
            realDataLen = is.read(tmpDataRemain);
            System.arraycopy(tmpDataRemain, 0, tmpPage.pageData, dataStart, Page.pageDataLen-dataStart);
        }
        else{
            realDataLen = is.read(tmpPage.pageData);
        }
        return realDataLen;
    }

    // 复制文件名&元数据到缓存帧(即把byte[]赋值到缓存帧)
    // 若文件名&元数据在本次未读完，则返回下一次文件名需要开始的位置；如果文件名在本次读完，则返回该帧下一个空的字节索引
    public static int copyByteArray(byte[] byteArray, int nameStart, int pageStart, int copyLen) throws IOException {
        int arrayLen = byteArray.length;
        if(arrayLen-nameStart <= Page.pageDataLen-pageStart){
            System.arraycopy(byteArray, nameStart, tmpPage.pageData, pageStart, copyLen);
            // 当文件名已读完的时候，返回-(当前帧第一个空闲位置坐标)
            return -(arrayLen-nameStart)-pageStart;
        }
        else{
            System.arraycopy(byteArray, nameStart, tmpPage.pageData, pageStart, Page.pageDataLen-pageStart);
            // 当文件名未读
            return nameStart+Page.pageDataLen-pageStart;
        }
    }

    // 读取页面
    public static void readPage(FileInputStream is) throws IOException {
        byte[] tmpHead = new byte[Page.pageHeadLen];
        is.read(tmpHead);
        tmpPage.setHead(tmpHead);
        is.read(tmpPage.pageData);

//        // 文件损坏测试
//        if(Math.random()<0.1)
//            tmpPage.pageData[78] = (byte)(1-tmpPage.pageData[78]);

        // 确认循环校验码：
        pageStatus = (pageStatus && Page.crc.judge(tmpPage.pageData, tmpPage.crcCode));
    }

    // 检查页面是否损坏
    public static String pageCheck(String resFileName){
        if(pageStatus)
            return "";
        else{
            pageStatus = true;
            return resFileName;
        }
    }

    // 获取当前文件类型
    public static int getFileType(){
        return tmpPage.getFileType();
    }
}
