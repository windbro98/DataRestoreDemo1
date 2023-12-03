### 参考代码链接
https://blog.csdn.net/CSDN___LYY/article/details/79461026

**定时**备份参考

https://blog.csdn.net/fish332/article/details/114178510

（建议搜索：java文件备份）文件备份参考

![img.png](img.png)

IPv4报文

### 参考库

1. 前端GUI：swing

2. 定时：Timer.SheduleAtFixedRate

3. 读取文件：io

4. 将byte[]数组转换为二进制：https://blog.51cto.com/u_16175437/7962629#:~:text=%E6%AD%A5%E9%AA%A4%E4%BA%8C%EF%BC%9A%E5%B0%86byte%E6%95%B0%E7%BB%84%E8%BD%AC%E6%8D%A2%E4%B8%BA%E4%BA%8C%E8%BF%9B%E5%88%B6%E5%AD%97%E7%AC%A6%E4%B8%B2%201%20%E6%88%91%E4%BB%AC%E4%BD%BF%E7%94%A8StringBuilder%E5%AF%B9%E8%B1%A1%E6%9D%A5%E4%BF%9D%E5%AD%98%E8%BD%AC%E6%8D%A2%E5%90%8E%E7%9A%84%E4%BA%8C%E8%BF%9B%E5%88%B6%E5%AD%97%E7%AC%A6%E4%B8%B2%EF%BC%8C%E5%9B%A0%E4%B8%BA%E5%AD%97%E7%AC%A6%E4%B8%B2%E7%9A%84%E6%8B%BC%E6%8E%A5%E6%93%8D%E4%BD%9C%E4%BC%9A%E5%AF%BC%E8%87%B4%E6%80%A7%E8%83%BD%E9%97%AE%E9%A2%98%E3%80%82%202,%E9%81%8D%E5%8E%86byte%E6%95%B0%E7%BB%84%E7%9A%84%E6%AF%8F%E4%B8%80%E4%B8%AA%E5%85%83%E7%B4%A0%EF%BC%8C%E5%B0%86%E5%85%B6%E4%B8%AD%E7%9A%84%E6%AF%8F%E4%B8%AA%E5%AD%97%E8%8A%82%E8%BD%AC%E6%8D%A2%E4%B8%BA8%E4%BD%8D%E7%9A%84%E4%BA%8C%E8%BF%9B%E5%88%B6%E5%AD%97%E7%AC%A6%E4%B8%B2%EF%BC%8C%E5%B9%B6%E8%BF%BD%E5%8A%A0%E5%88%B0StringBuilder%E4%B8%AD%E3%80%82%203%20%E6%9C%80%E5%90%8E%EF%BC%8C%E9%80%9A%E8%BF%87toString%20%28%29%E6%96%B9%E6%B3%95%E5%B0%86StringBuilder%E5%AF%B9%E8%B1%A1%E8%BD%AC%E6%8D%A2%E4%B8%BAString%E7%B1%BB%E5%9E%8B%E7%9A%84%E4%BA%8C%E8%BF%9B%E5%88%B6%E5%AD%97%E7%AC%A6%E4%B8%B2%E3%80%82

   https://blog.csdn.net/uikoo9/article/details/27980869

5. 软硬连接（套接字可以不支持）

   ![image-20231130212953291](D:\learning_programs\java_programs\DataRestoreDemo1\images\Readme\image-20231130212953291.png)

   ![image-20231130213007080](D:\learning_programs\java_programs\DataRestoreDemo1\images\Readme\image-20231130213007080.png)

   https://zhuanlan.zhihu.com/p/365239653#

   5.pdf p12

6. 获取文件元数据

   ```java
   public class TestXY {
   	public static void main(String[] args) throws Exception {
            getFileCreateTime("");
   	}
   
   
   	public static String getFileCreateTime(String filePath) {
   
   		SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss ");
   		FileTime t = null;
   		try {
   			t = Files.readAttributes(Paths.get(filePath), BasicFileAttributes.class).creationTime();
   		} catch (IOException e) {
   			e.printStackTrace();
   		}
   
   		String createTime = dateFormat.format(t.toMillis());
   		System.out.println("创建时间 ： " + createTime);
   		return createTime;
   	}
   }
   ```

   其中值得关注的属性：creationTime, lastModifiedTime

   https://www.w3cschool.cn/java/java-nio-file-attributes.html

   只要获取了文件的元数据，就可以根据元数据进行filter

### 开发进度
1. v1: 基本的GUI界面、备份和恢复函数 done

   v1.1: GUI界面的优化: 

2. v2: 实现打包过程的页面化，同时实现了对长文件名的支持（超过一个页面大小）done

   页面大小：256B (head 8B + data 248B)

3. v3
   
   v3.1: 实现循环校验码crc。如果文件损坏（仅限文件的bit修改），则在通知框中提示损坏的文件名

### 代码注意
1. 在FileToolUtil中，有readPage函数，其中含有测试文件损坏部分

### 技术栈
1. 将指定目录下的文件全部备份到**一个文件**中 (done)
2. 将备份件**还原**为原本的目录树 (done)
3. 对备份文件进行**加密、解密**
4. 对备份文件进行**打包、解包**
5. 对**元数据**进行处理
5. 对目录树下的文件进行**筛选**
6. **周期性**备份和**数据淘汰**的**设置**
7. 感知用户**文件变化**，进行自动备份
8. **jsonPath可以学习，用于筛选过程**
9. 开发GUI界面 (basically done)
10. https://blog.csdn.net/Cguoer/article/details/122588595 (java实现循环校验CRC)

### 现存问题(todo)
1. 使用cyc循环校验，如果帧的data内容错误，则仍然恢复该文件，但是返回信息提示该文件错误（如果发生在head上，则无可奈何。如果文件类型损坏，可以在设置中手动跳过，如果有时间再写）
2. 对文件进行筛选
3. 对文件的元数据进行保存
4. 压缩与解压
5. 软硬链接

代码细节：

1. 验证readPage的第一行，是否能够读到变量中

### 附加要求
1. 完成cyc循环校验（必备）
2. 打包解包：注意**目录也是文件！！！** 一定要实现分块化处理。
3. 压缩：实现Huffman编码和LZ77编码。其中Huffman编码可以在压缩过程中使用字符串的形式，但是在存储和恢复过程中要使用byte
建议直接存储哈夫曼树用于后续解码。

LZ77编码中，内存不可能一直朝一个方向无限扩展，如何处理？ -> 移动内容（丢失历史窗口且缓慢）；循环缓冲（可以加分）
4加密解密：使用AES加密，但不能是简单的异或，最好可以使用CBC模式（加分）
5封装成库：使程序的核心逻辑与界面实现分离