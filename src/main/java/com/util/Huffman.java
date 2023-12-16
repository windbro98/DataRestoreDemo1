package com.util;

import java.io.*;
import java.util.*;

import static com.util.DataUtil.*;

public class Huffman {
    final int byteNum = 256;
    final int pageSize = 256;
    String[] encodeMap = new String[byteNum];
    int maxEncodeLen;


    //内部类 二叉树节点
    private class TreeNode implements Comparable<TreeNode> {
        public TreeNode() { }
        public TreeNode(int idx, int val, long freq, TreeNode left, TreeNode right) {
            this.idx = idx;
            this.val = val;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }
        int idx;
        int val;
        long freq;
        TreeNode left;
        TreeNode right;

        @Override
        public int compareTo(TreeNode o) {
            long res = this.freq - o.freq;
            if (res > 0) return 1;
            else if (res < 0) return -1;
            else return 0;
        }
    }

    private void constructFreqMap(FileInputStream is, long[] freqMap) throws IOException {
        byte[] bufferArray = new byte[byteNum];
        int readNum;

        while((readNum=is.read(bufferArray))!=-1){
            for (int i = 0; i < readNum; i++) {
                freqMap[bufferArray[i]&0xFF]++;
            }
        }
    }

    //编码方法，返回Object[]，大小为2,Object[0]为编码后的字符串，Object[1]为编码对应的码表
    public void encode(File origFile, File comprFile) throws IOException {
        FileInputStream is = new FileInputStream(origFile);
        long[] freqMap=new long[byteNum];

        // 构建频率表
        constructFreqMap(is, freqMap);
        // 构建哈希树
        TreeNode tree = constructTree(freqMap);
        // 获取编码表
        constructEncodeMap(tree, new StringBuilder());
        // 将原本的string转为byte[]，并写入压缩文件comprFile
        is.close();
        is = new FileInputStream(origFile);
        compressFile(is, comprFile);
        // 计算压缩比
        double comprRadio = getCompressionRadio(freqMap);
        System.out.println("当前的压缩比为："+ comprRadio);
    }

     private double getCompressionRadio(long[] freqMap){
        long bitsPrev=0, bitsNow=0;
        for (int i = 0; i < byteNum; i++) {
             bitsPrev += freqMap[i] * 8;
             bitsNow += freqMap[i] * encodeMap[i].length();
        }
         System.out.println("原本的字节数为："+bitsPrev/8);
         System.out.println("当前压缩后理论字节数为："+bitsNow/8);
        return (double) bitsNow / bitsPrev;
     }

    // 将源文件进行压缩
    private void compressFile(FileInputStream is, File comprFile) throws IOException {
        byte[] bufferArray = new byte[pageSize];
        FileOutputStream os = new FileOutputStream(comprFile);
        // 编码后的0-1字符串
        StringBuilder sb = new StringBuilder();
        int readNum;

        while((readNum=is.read(bufferArray))!=-1){
            for (int i = 0; i < readNum; i++) {
                sb.append(encodeMap[bufferArray[i]&0xFF]);
            }
            // 将现有的数据按照字节，写入压缩文件
            int validLen = sb.length()-sb.length()%8;
            os.write(binaryToByteArray(sb.substring(0, validLen)));
            sb.delete(0, validLen);
        }
        // 将最后一个字节（不满8bit）写入压缩文件
        if(!sb.isEmpty())
            os.write(binaryToByteArray(sb.toString()));
    }

    /*
     * 根据字符串建立二叉树
     * @param s：要编码的源字符串
     */
    private TreeNode constructTree(long[] freqMap) {
        // 新类：有序链表
        class OrderedList<T extends Comparable<T>> extends LinkedList<T> {
            @Serial
            private static final long serialVersionUID = 1L;
            public void orderedAdd(T element) {
                ListIterator<T> itr = listIterator();
                while(true) {
                    if (!itr.hasNext()) {
                        itr.add(element);
                        return;
                    }

                    T elementInList = itr.next();
                    if (elementInList.compareTo(element) > 0) {
                        itr.previous();
                        itr.add(element);
                        return;
                    }
                }
            }
        }

        //遍历dataMap,初始化二叉树节点，并将所有初始化后的节点放到有序集合nodeList中
        OrderedList<TreeNode> nodeList = new OrderedList<>();

        for (int i = 0; i < byteNum; i++)
            nodeList.orderedAdd(new TreeNode(i, 0, freqMap[i], null, null));

        //size==1,代表字符串只包含一种类型的字母
        if(nodeList.size()==1){
            TreeNode t = nodeList.getFirst();
            return new TreeNode(-1,0,t.freq,t,null);
        }

        //利用排序好的节点建立二叉树，root为初始化根节点
        TreeNode root = null;
        while(!nodeList.isEmpty()){
            //因为nodeList在前面已经排好序，所以直接取出前两个节点，他们的和肯定为最小
            TreeNode t1 = nodeList.removeFirst();
            TreeNode t2 = nodeList.removeFirst();
            //左子树的val赋值为0，右子树的val赋值为1
            t1.val = 0;
            t2.val = 1;
            //将取出的两个节点进行合并
            if(nodeList.isEmpty()){
                //此时代表所有节点合并完毕，返回结果
                root = new TreeNode(-1,0,t1.freq+t2.freq,t1,t2);
            }else {
                //此时代表还有可以合并的节点
                TreeNode tmp = new TreeNode(-1,0,t1.freq+t2.freq,t1,t2);
                nodeList.orderedAdd(tmp);
            }
        }
        //返回建立好的二叉树根节点
        return root;
    }

    //对已经建立好的二叉树进行遍历，得到每个字符的编码
    private void constructEncodeMap(TreeNode root, StringBuilder path) {
        if (root.left == null && root.right == null) {
            path.append(root.val);
            encodeMap[root.idx] = path.substring(1);
            path.deleteCharAt(path.length()-1);
            // 计算最长编码长度
            maxEncodeLen = Math.max(path.length(), maxEncodeLen);
            return;
        }
        path.append(root.val);
        if (root.left != null) constructEncodeMap(root.left, path);
        if (root.right != null) constructEncodeMap(root.right, path);
        path.deleteCharAt(path.length() - 1);
    }

    //对字符串进行解码，解码时需要编码码表
    public void decode(File comprFile, File origFile) throws IOException {
        // 缓冲处理字符串
        FileInputStream is = new FileInputStream(comprFile);
        FileOutputStream os = new FileOutputStream(origFile);
        byte[] bufferIn = new byte[pageSize];
        byte[] bufferOut = new byte[pageSize];

        int readNum;

        // 构建decodeMap
        HashMap<String, Byte> decodeMap = new HashMap<>();
        for (int i = 0; i < byteNum; i++) {
            decodeMap.put(encodeMap[i], (byte)i);
        }

        // 缓冲字符串，在读取文件的过程中不断进行处理
        String bufferStr = "";
        long recoverSize = 0;
        int idxOutNew=0;
        while((readNum=is.read(bufferIn)) != -1){
            // 将本次读取的字符串添加到bufferStr中
            // 这里的选择主要是针对最后一次读取读不满的现象
            String newBufferStr = byteArray2binary((readNum==256)? bufferIn : Arrays.copyOfRange(bufferIn, 0, readNum));
            bufferStr = bufferStr + newBufferStr;
            // 对缓冲字符串bufferStr进行解码
            while(!bufferStr.isEmpty()){
                boolean decodeFlag = false;
                for (int i = 0; i < byteNum; i++) {
                    String encodeBiStr = encodeMap[i];
                    if(bufferStr.startsWith(encodeBiStr)){
                        bufferOut[idxOutNew] = (byte) i;
                        idxOutNew++;
                        decodeFlag = true;
                        bufferStr = bufferStr.substring(encodeBiStr.length());
                        break;
                    }
                }
                // 判断本次是否解码成功，不成功则跳出解码过程，准备下一次解码
                if(!decodeFlag)
                    break;
                // 将得到的数据写入输出文件
                if(idxOutNew==readNum){
                    os.write(bufferOut, 0, idxOutNew);
                    idxOutNew = 0;
                    recoverSize += pageSize;
//                    System.out.println(recoverSize);
                }
            }
        }
        System.out.println("恢复的字节数为："+recoverSize);
    }
}