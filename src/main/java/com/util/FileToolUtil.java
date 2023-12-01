package com.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import javafx.scene.control.TextField;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.min;

public class FileToolUtil {
    // 工具类，禁止创建对象
    private FileToolUtil() {
    }
    
    public static int tmpHeadLen = 8;
    public static int tmpDataLen = 248;
    // 3个signal，依次为headPage, fileType, tailPage
    public static int signalNum = 3;
    public static int lenNum = 2;
    public static byte[] tmpHead = new byte[tmpHeadLen];
    public static byte[] tmpData = new byte[tmpDataLen];
    public static CRC crc = new CRC();
    public static boolean pageStatus = true;

    // 递归遍历文件夹，获取文件
    public static void fileWalkLoop(String srcDir, List<String> filePathSet) {
        // 递归获取源目录下所有文件的路径
        File src = new File(srcDir);
        String[] srcfilePathSets = src.list();
        for (String file : srcfilePathSets) {
            String filePath = fileConcat(srcDir, file);
            File f = new File(filePath);
            filePathSet.add(filePath);
            if (f.isDirectory())
                fileWalkLoop(filePath + '/', filePathSet);
        }
    }

    // 文件恢复
    public static int fileCopy(InputStream is, OutputStream os, String fileName) throws IOException {
        int headPage=1, tailPage=0, fileType=1;
        int dataLen;

        int dataStart = dirCopy(os, fileName, fileType);

        // dataStart: 代表开始写data的索引位置
        while(is.available()>0){
            dataLen = copyData(is, dataStart);
            tailPage = (is.available()>0) ? 0 : 1;
            buildHead(headPage, fileType, tailPage, dataStart, dataLen);
            writePage(os);
            headPage = 0;
            dataStart = 0;
        }
        return 0;
    }

    // 对(相对)路径名进行复制，文件或目录均可调用
    // 返回值realDataLen，表示在完成目录的复制后，该页面中可以写入的数据大小
    public static int dirCopy(OutputStream os, String dirName, int fileType) throws IOException {
        int headPage=1, dataLen=0, fileNameStart=0, tailPage=0;
        int copyLen, crcCode;
        byte[] dirBytes = dirName.getBytes();
        int dirLen = dirBytes.length;

        while(fileNameStart >= 0){
            copyLen = min(dirBytes.length-fileNameStart, tmpDataLen);
            // 只有dir的时候需要判断，而文件的时候不需要判断
            tailPage = ((fileType==0) && (dirLen-fileNameStart)<=tmpDataLen) ? 1 : 0;
            // 生成tmpHead
            fileNameStart = copyFileName(dirBytes, fileNameStart, copyLen);
            buildHead(headPage, fileType, tailPage, copyLen, dataLen);
            if(!(fileType==1 && fileNameStart<0))
                writePage(os);
        }

        // 返回该帧下一个空字节的索引
        return -fileNameStart;
     }

     // tmpData和大部分的tmpHead准备完成，写入page
    public static void writePage(OutputStream os) throws IOException {
        // 计算循环冗余校验码
        tmpHead[3] = crc.getFCS(tmpData);
        os.write(tmpHead, 0, tmpHeadLen);
        os.write(tmpData, 0, tmpDataLen);
        Arrays.fill(tmpHead, (byte)0);
        Arrays.fill(tmpData, (byte)0);
    }

    // 构建tmpHead, tmpHead[3]的位置留给了crcCode，但是这是在最后生成的
    public static void buildHead(int headPage, int fileType, int tailPage, int fileNameLen, int dataLen) throws IOException {
        // 0位-是否是头页面；1位-是否是文件；2位-是否是尾页面
        byte headPrefix = (byte)(headPage + (fileType<<1) + (tailPage<<2));
        tmpHead[0] = headPrefix;
        tmpHead[1] = (byte)fileNameLen;
        tmpHead[2] = (byte)dataLen;
     }

    // 返回值realDataLen为本次实际写入的数据大小
    // todo: 添加crc校验码计算
    public static int copyData(InputStream is, int dataStart) throws IOException {
        // 读取页面数据
        int realDataLen;
        if(dataStart>0){
            byte[] tmpDataRemain = new byte[tmpDataLen-dataStart];
            realDataLen = is.read(tmpDataRemain);
            System.arraycopy(tmpDataRemain, 0, tmpData, dataStart, tmpDataLen-dataStart);
        }
        else{
            realDataLen = is.read(tmpData);
        }
        return realDataLen;
    }

    // 复制文件名，返回下一次文件名需要开始的位置；如果文件名在本次读完，则返回该帧下一个空的字节索引
    public static int copyFileName(byte[] fileName, int nameStart, int copyLen) throws IOException {
        int fileNameLen = fileName.length;
        if(fileNameLen-nameStart <= tmpDataLen){
            System.arraycopy(fileName, nameStart, tmpData, 0, copyLen);
            // 当文件名已读完的时候，返回-(当前帧第一个空闲位置坐标)
            return -(fileNameLen-nameStart);
        }
        else{
            System.arraycopy(fileName, nameStart, tmpData, 0, tmpDataLen);
            return nameStart+tmpDataLen;
        }
    }

    // 单个文件恢复
    public static String fileRestoreSingle(FileInputStream is, String resRoot) throws IOException {
        // 读取head, headDecode中的信号依次为headPage, fileType, tailType
        int[] headDecode = readPage(is);
        byte[] fileNameByte=null;
        byte[] tmpFileNameByte;

        // 获取恢复文件名
        while(headDecode[0]==1){
            // headDecode[signalNum] -> headLen
            if(fileNameByte != null){
                int prevLen = fileNameByte.length;
                tmpFileNameByte = fileNameByte;
                fileNameByte = new byte[prevLen+headDecode[signalNum]];
                System.arraycopy(tmpFileNameByte, 0, fileNameByte, 0, prevLen);
                System.arraycopy(tmpData, 0, fileNameByte, prevLen, headDecode[signalNum]);
                // notice: headDecode is deleted here
            }
            else{
                fileNameByte = new byte[headDecode[signalNum]];
                System.arraycopy(tmpData, 0, fileNameByte, 0, headDecode[signalNum]);
            }
            if(headDecode[signalNum]==tmpDataLen) // headLen==tmpDataLen, 表示此时标题仍然没有读尽
                headDecode = readPage(is);
            else
                break;
        }
        String resFileName = new String(fileNameByte);

        String resFilePath = fileConcat(resRoot, resFileName);
        File resFile = new File(resFilePath);
        // 如果该对象是目录，直接创建并退出
        if(headDecode[1]==0){
            dirExistEval(resFile);
            if(pageStatus)
                return "";
            else{
                pageStatus = true;
                return resFileName;
            }
        }
        // 如果该对象是文件，则准备读取数据
        fileExistEval(resFile, true);
        FileOutputStream os = new FileOutputStream(resFile);
        while(headDecode[2]!=1){ // 非尾页面
            os.write(tmpData, headDecode[signalNum], headDecode[signalNum+1]);
            headDecode = readPage(is);
        };
        // 尾页面
        // 尾页面的所有数据由三部分组成：文件名+文件数据+空白
        os.write(tmpData, headDecode[signalNum], headDecode[signalNum+1]);

        if(pageStatus)
            return "";
        else{
            pageStatus = true;
            return resFileName;
        }
    }

    public static int[] readPage(FileInputStream is) throws IOException {
        is.read(tmpHead);
        is.read(tmpData);
        int[] headDecode = new int[signalNum+lenNum];
        int headPrefix = tmpHead[0];
        // 读取控制信号
        for (int i = 0; i < signalNum; i++) {
            headDecode[i] = (headPrefix>>i)%2;
        }
        // 读取长度信息
        for (int i = 0; i < lenNum; i++) {
            headDecode[signalNum+i] = (tmpHead[i+1]<0) ? (tmpHeadLen+tmpDataLen+tmpHead[i+1]) : tmpHead[i+1];
        }
        // 错误信息
        tmpData[34] = (byte)0;
        // 确认循环校验码：
        pageStatus = (pageStatus && crc.judge(tmpData, tmpHead[3]));

//        // 文件损坏测试
//        if(Math.random()<0.1)
//            tmpData[78] = (byte)(1-tmpData[78]);

        return headDecode;
    }

    // 将map<String, String>写入json文件
    public static void writeJson(String jsonPath, Map<String, String> inMap){
        String data = JSON.toJSONString(inMap);
        File jsonFile = new File(jsonPath);
        try {
            // 验证json文件是否存在，不存在且创建失败则直接返回
            if(!fileExistEval(jsonFile, true))
                return;

            // 写入json文件
            FileWriter fw = new FileWriter(jsonFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data);
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 将json文件内容读取到map<String, String>
    public static JSONObject readJson(String jsonPath) throws IOException {
        File jsonFile = new File(jsonPath);
        String jsonString = Files.readString(jsonFile.toPath());
        JSONObject jo = JSONObject.parseObject(jsonString);
        return jo;
    }

    // 判断文件是否存在，不存在则创建
    public static boolean fileExistEval(File file, boolean create) throws IOException {
        boolean flag=true;
        if(file.exists()){
            return true;
        }
        else if(create){
            String dir = file.getParent();
            flag = dirExistEval(new File(dir));
            if(flag)
                flag = file.createNewFile();;
            return flag;
        }
        else{
            return false;
        }
    }

    // 判断目录是否存在，不存在则创建
    public static boolean dirExistEval(File dir){
        boolean flag=true;
        if(!dir.exists())
            flag = dir.mkdirs();
        return flag;
    }

    // 目录文件与文件名拼接
    public static String fileConcat(String dir, String file){
//        return Paths.get(dir, file).toString();
        return dir + '/' + file;
    }

    // 判断文本框是否为空
    public static boolean tfIsEmpty(TextField tf){return tf.getText().isEmpty();}
}