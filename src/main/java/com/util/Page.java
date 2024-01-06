package com.util;

import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;

import java.util.Arrays;

public class Page {
    final static int pageHeadLen = 8;
    final static int pageDataLen = 248;
    final static int RSLen = 2; // RS纠删码的长度
    final static int signalNum = 2; // 控制信号数量, 从低到搞依次为fileType, tailPage
    final static int lenNum = 3; // 长度数量
    final static CRC crc = new CRC();
    final static ReedSolomonEncoder RSencoder = new ReedSolomonEncoder(GenericGF.DATA_MATRIX_FIELD_256);
    final static ReedSolomonDecoder RSdecoder = new ReedSolomonDecoder(GenericGF.DATA_MATRIX_FIELD_256);
    public byte headPrefix;
    public byte nameLen;
    public byte metaLen;
    public byte dataLen;
    public byte crcCode;
    public byte[] RSCode = new byte[RSLen]; // 2字节的RSCode作为纠删码
    public byte pad1; // 头部为了凑足8字节的填充
    public byte[] pageData = new byte[Page.pageDataLen];

    public int getNameLen(){
        return this.nameLen<0 ? (256+this.nameLen) : this.nameLen;
    }
    public int getDataLen(){
        return this.dataLen<0 ? (256+this.dataLen) : this.dataLen;
    }
    public int getMetaLen(){
        return this.metaLen<0 ? (256+this.metaLen) : this.metaLen;
    }
    public int getFileType(){
        return this.headPrefix%2;
    }
    public int getTailPage(){
        return (this.headPrefix>>1)%2;
    }
    public byte[] getHead(){
        byte[] head = new byte[pageHeadLen];
        head[0] = this.headPrefix;
        head[1] = this.nameLen;
        head[2] = this.metaLen;
        head[3] = this.dataLen;
        head[4] = this.crcCode;
        System.arraycopy(this.RSCode, 0, head, 5, RSLen);
        head[7] = this.pad1;
        return head;
    }

    public void setTailPage(int tailPage){
        int tailPagePrev = this.getTailPage();
        if(tailPagePrev != tailPage){
            this.headPrefix += (byte) ((tailPage-tailPagePrev)<<1);
        }
    }

    public void setHead(byte[] tmpHead){
        this.headPrefix = tmpHead[0];
        this.nameLen = tmpHead[1];
        this.metaLen = tmpHead[2];
        this.dataLen = tmpHead[3];
        this.crcCode = tmpHead[4];
        System.arraycopy(tmpHead, 5, this.RSCode, 0, RSLen);
        this.pad1 = tmpHead[7];
    }

    public void setCrcCode() {
        this.crcCode = crc.getFCS(this.pageData);
    }

    public void setRSCode(){
        System.arraycopy(
                ReedSolomon.encodeData(this.pageData, RSLen, RSencoder),
                pageDataLen,
                this.RSCode,
                0,
                RSLen);
    }

    public void revisePageData() throws ReedSolomonException {
        byte[] dataWithECC = new byte[pageDataLen+RSLen];
        System.arraycopy(this.pageData, 0, dataWithECC, 0, pageDataLen);
        System.arraycopy(this.RSCode, 0, dataWithECC, pageDataLen, RSLen);
        System.arraycopy(
                ReedSolomon.decodeData(dataWithECC, RSLen, RSdecoder),
                0,
                this.pageData,
                0,
                pageDataLen
        );
    }

    // 将所有变量重置为0
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
