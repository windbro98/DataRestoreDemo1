package com.util;

public class Page {
    final static int pageHeadLen = 8;
    final static int pageDataLen = 248;
    final static int signalNum = 2; // 控制信号数量, 从低到搞依次为fileType, tailPage
    final static int lenNum = 3; // 长度数量
    final static CRC crc = new CRC();
    public byte headPrefix;
    public byte nameLen;
    public byte metaLen;
    public byte dataLen;
    public byte crcCode;
    public byte pad1, pad2, pad3; // 头部为了凑足8字节的填充
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
        return new byte[]{
                this.headPrefix,
                this.nameLen,
                this.metaLen,
                this.dataLen,
                this.crcCode,
                this.pad1,
                this.pad2,
                this.pad3
        };
    }

    // todo: tailPage此时设置为[2]，之后可能设置为[1]，需要修改
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
        this.pad1 = tmpHead[5];
        this.pad2 = tmpHead[6];
        this.pad3 = tmpHead[7];
    }

    public void setCrcCode() {
        this.crcCode = crc.getFCS(this.pageData);
    }

    // 将所有变量重置为0
    public void reset(){
        this.headPrefix = 0;

        this.nameLen = 0;
        this.metaLen = 0;
        this.dataLen = 0;

        this.crcCode = 0;

        this.pad1 = 0;
        this.pad2 = 0;
        this.pad3 = 0;
    }
}
