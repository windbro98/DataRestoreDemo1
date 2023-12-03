package com.util;

import java.io.*;
import java.util.HashMap;

import static com.util.FileToolUtil.*;
import static java.lang.Math.min;

public class PageManagerUtil {
    private static Page tmpPage = new Page(); // 正在处理的页面
    private static boolean pageStatus = true; // 判断文件是否损坏


    // 工具类，禁止创建对象
    private PageManagerUtil(){
    }

    // 单个文件恢复
    public static String fileRestoreSingle(FileInputStream is, String resRoot) throws IOException {
        // 读取head, headDecode中的信号依次为headPage, fileType, tailType
        readPage(is);
        byte[] fileNameByte=null;
        byte[] tmpFileNameByte;

        // 获取恢复文件名
        while(tmpPage.getHeadPage()==1){
//        while(tmpPage.getNameLen()>0){
            // tmpPage.getNameLen() -> headLen
            if(fileNameByte != null){
                int prevLen = fileNameByte.length;
                tmpFileNameByte = fileNameByte;
                fileNameByte = new byte[prevLen+tmpPage.getNameLen()];
                System.arraycopy(tmpFileNameByte, 0, fileNameByte, 0, prevLen);
                System.arraycopy(tmpPage.pageData, 0, fileNameByte, prevLen, tmpPage.getNameLen());
                // notice: headDecode is deleted here
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
        String resFileName = new String(fileNameByte);

        String resFilePath = fileConcat(resRoot, resFileName);
        File resFile = new File(resFilePath);

        // 创建目录或文件
        if(tmpPage.getFileType()==0)
            dirExistEval(resFile);
        else
            fileExistEval(resFile, true);

        // 设置文件元数据

        // 如果该对象是目录，则直接检验文件中是否有页面损坏并退出
        if(tmpPage.getFileType()==0)
            return pageCheck(resFileName);

        // 如果该对象是文件，则准备读取数据
        FileOutputStream os = new FileOutputStream(resFile);
        while(tmpPage.getTailPage()!=1){ // 非尾页面
            os.write(tmpPage.pageData, tmpPage.getNameLen(), tmpPage.getDataLen());
            readPage(is);
        };
        // 尾页面
        // 尾页面的所有数据由三部分组成：文件名+文件数据+空白
        os.write(tmpPage.pageData, tmpPage.getNameLen(), tmpPage.getDataLen());

        // 检验文件中是否有页面损坏并退出
        return pageCheck(resFileName);
        }

    // 文件恢复
    public static int fileCopy(InputStream is, OutputStream os, String fileName, File inFile) throws IOException {
        int headPage=1, tailPage=0, fileType=1;
        int dataLen;

        int dataStart = nameCopy(os, fileName, inFile, fileType);

        // dataStart: 代表开始写data的索引位置
        while(is.available()>0){
            dataLen = copyData(is, dataStart);
            tailPage = (is.available()>0) ? 0 : 1;
            // todo: 这里的metaData需要重新处理
            buildHead(headPage, fileType, tailPage, dataStart, 0, dataLen);
            writePage(os);
            headPage = 0;
            dataStart = 0;
        }
        return 0;
    }

    // 对文件名进行复制，文件或目录均可调用
    // 返回值realDataLen，表示在完成目录的复制后，该页面中可以写入的数据大小
    public static int nameCopy(OutputStream os, String name, File file, int fileType) throws IOException {
        int headPage=1, dataLen=0, metaLen=0, fileNameStart=0;
        int tailPage, copyLen;
        byte[] dirBytes = name.getBytes();
        int dirLen = dirBytes.length;

        // 写入文件名
        while(fileNameStart >= 0){
            copyLen = min(dirBytes.length-fileNameStart, Page.pageDataLen);
            // 只有dir的时候需要判断，而文件的时候不需要判断
            tailPage = ((fileType==0) && (dirLen-fileNameStart)<=Page.pageDataLen) ? 1 : 0;
            // 生成tmpHead
            fileNameStart = copyFileName(dirBytes, fileNameStart, copyLen);
            buildHead(headPage, fileType, tailPage, copyLen, metaLen, dataLen);
            if(fileNameStart<0) // 文件名已写完
                writePage(os);
        }

        // 写入元数据
        int a = 1;

        // 返回该帧下一个空字节的索引
        return -fileNameStart;
    }

    // tmpData和大部分的tmpHead准备完成，写入page
    public static void writePage(OutputStream os) throws IOException {
        tmpPage.setCrcCode();;
        // 计算循环冗余校验码
        // todo: 这里需要将pageHead先全部写入到一个byte[]中
        os.write(tmpPage.getHead(), 0, Page.pageHeadLen);
        os.write(tmpPage.pageData, 0, Page.pageDataLen);
        tmpPage.reset();
    }

    // 构建tmpHead, tmpHead[3]的位置留给了crcCode，但是这是在最后生成的
    public static void buildHead(int headPage, int fileType, int tailPage, int nameLen, int metaLen, int dataLen) throws IOException {
        // 0位-是否是头页面；1位-是否是文件；2位-是否是尾页面
        byte headPrefix = (byte)(headPage + (fileType<<1) + (tailPage<<2));
        tmpPage.headPrefix = headPrefix;
        tmpPage.nameLen = (byte)nameLen;
        tmpPage.metaLen = (byte)metaLen;
        tmpPage.dataLen = (byte)dataLen;
        return;
    }

    // 返回值realDataLen为本次实际写入的数据大小
    // todo: 添加crc校验码计算
    public static int copyData(InputStream is, int dataStart) throws IOException {
        // 读取页面数据
        int realDataLen;
        if(dataStart>0){
            byte[] tmpDataRemain = new byte[Page.pageDataLen -dataStart];
            realDataLen = is.read(tmpDataRemain);
            System.arraycopy(tmpDataRemain, 0, tmpPage.pageData, dataStart, Page.pageDataLen-dataStart);
        }
        else{
            realDataLen = is.read(tmpPage.pageData);
        }
        return realDataLen;
    }

    // 复制文件名，返回下一次文件名需要开始的位置；如果文件名在本次读完，则返回该帧下一个空的字节索引
    public static int copyFileName(byte[] fileName, int nameStart, int copyLen) throws IOException {
        int nameLen = fileName.length;
        if(nameLen-nameStart <= Page.pageDataLen){
            System.arraycopy(fileName, nameStart, tmpPage.pageData, 0, copyLen);
            // 当文件名已读完的时候，返回-(当前帧第一个空闲位置坐标)
            return -(nameLen-nameStart);
        }
        else{
            System.arraycopy(fileName, nameStart, tmpPage.pageData, 0, Page.pageDataLen);
            return nameStart+Page.pageDataLen;
        }
    }

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

    public static String pageCheck(String resFileName){
        if(pageStatus)
            return "";
        else{
            pageStatus = true;
            return resFileName;
        }
    }
}
