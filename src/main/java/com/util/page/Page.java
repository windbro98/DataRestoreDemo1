package com.util.page;

import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import com.util.redundancyCheck.CRC;
import com.util.redundancyCheck.ReedSolomon;

import java.util.Arrays;

// 数据页面类
public class Page {
    final static int PAGE_HEAD_LEN = 8; // 头长度
    final static int PAGE_DATA_LEN = 248; // 数据长度
    final static int RS_LEN = 2; // RS纠删码的长度
    final static int SIGNAL_NUM = 2; // 控制信号数量, 从低到搞依次为fileType, tailPage
    final static int LEN_NUM = 3; // 长度数量
    final static CRC PAGE_CRC = new CRC();
    final static ReedSolomonEncoder REED_SOLOMON_ENCODER = new ReedSolomonEncoder(GenericGF.DATA_MATRIX_FIELD_256);
    final static ReedSolomonDecoder REED_SOLOMON_DECODER = new ReedSolomonDecoder(GenericGF.DATA_MATRIX_FIELD_256);
    public byte headPrefix; // 控制信号，
    public byte nameLen; // 该页面中文件名长度
    public byte metaLen; // 该页面中文件元数据长度
    public byte dataLen; // 该页面中文件数据长度
    public byte crcCode; // 循环校验码
    public byte[] RSCode = new byte[RS_LEN]; // 2字节的RS纠删码
    public byte pad1; // 头部为了凑足8字节的填充
    public byte[] pageData = new byte[PAGE_DATA_LEN]; // 也买你数据

    /*
        属性获取
     */
    // 获取该页面文件名长度
    public int getNameLen(){
        return this.nameLen<0 ? (256+this.nameLen) : this.nameLen;
    }
    // 获取该页面数据长度
    public int getDataLen(){
        return this.dataLen<0 ? (256+this.dataLen) : this.dataLen;
    }
    // 获取该页面文件元数据长度
    public int getMetaLen(){
        return this.metaLen<0 ? (256+this.metaLen) : this.metaLen;
    }
    // 获取该页面文件类型
    public int getFileType(){
        return this.headPrefix%2;
    }
    // 获取该页面是否为为也买你
    public int getTailPage(){
        return (this.headPrefix>>1)%2;
    }
    // 获取该页面文件的head信息，包括控制信号、文件名长度、文件元数据长度、循环校验码和RS纠删码
    public byte[] getHead(){
        byte[] head = new byte[PAGE_HEAD_LEN];
        head[0] = this.headPrefix;
        head[1] = this.nameLen;
        head[2] = this.metaLen;
        head[3] = this.dataLen;
        head[4] = this.crcCode;
        System.arraycopy(this.RSCode, 0, head, 5, RS_LEN);
        head[7] = this.pad1;
        return head;
    }
    /*
        属性设置
     */
    // 设置该页面是否为尾页面
    public void setTailPage(int tailPage){
        int tailPagePrev = this.getTailPage();
        if(tailPagePrev != tailPage){
            this.headPrefix += (byte) ((tailPage-tailPagePrev)<<1);
        }
    }
    // 设置页面head信息
    public void setHead(byte[] tmpHead){
        this.headPrefix = tmpHead[0];
        this.nameLen = tmpHead[1];
        this.metaLen = tmpHead[2];
        this.dataLen = tmpHead[3];
        this.crcCode = tmpHead[4];
        System.arraycopy(tmpHead, 5, this.RSCode, 0, RS_LEN);
        this.pad1 = tmpHead[7];
    }
    // 设置循环校验码
    public void setCrcCode() {
        this.crcCode = PAGE_CRC.getFCS(this.pageData);
    }
    // 设置RS纠删码
    public void setRSCode(){
        System.arraycopy(
                ReedSolomon.encodeData(this.pageData, RS_LEN, REED_SOLOMON_ENCODER),
                PAGE_DATA_LEN,
                this.RSCode,
                0,
                RS_LEN);
    }
    // 根据纠删码，对原页面进行矫正
    public void revisePageData() throws ReedSolomonException {
        byte[] dataWithECC = new byte[PAGE_DATA_LEN + RS_LEN];
        System.arraycopy(this.pageData, 0, dataWithECC, 0, PAGE_DATA_LEN);
        System.arraycopy(this.RSCode, 0, dataWithECC, PAGE_DATA_LEN, RS_LEN);
        System.arraycopy(
                ReedSolomon.decodeData(dataWithECC, RS_LEN, REED_SOLOMON_DECODER),
                0,
                this.pageData,
                0,
                PAGE_DATA_LEN
        );
    }

    // 将素有属性重置
    public void reset(){
        this.headPrefix = 0;

        this.nameLen = 0;
        this.metaLen = 0;
        this.dataLen = 0;

        this.crcCode = 0;
        Arrays.fill(this.RSCode, (byte) 0);

        this.pad1 = 0;
    }
}
