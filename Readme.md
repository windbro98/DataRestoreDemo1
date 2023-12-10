### 参考代码链接
https://blog.csdn.net/CSDN___LYY/article/details/79461026

**定时**备份参考

https://blog.csdn.net/fish332/article/details/114178510

（建议搜索：java文件备份）文件备份参考

![img.png](D:\learning_programs\java_programs\DataRestoreDemo1\images\Readme\img.png)

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

   meta-data read：https://21yi.com/java/java.nio.file.Files.getLastModifiedTime_9661.html（已经获取属主、时间、windows权限）

   meta-data write: https://www.w3cschool.cn/java/java-nio-file-owner-permissions.html#:~:text=%E6%9C%89%E4%B8%89%E7%A7%8D%E6%96%B9%E6%B3%95%E5%8F%AF%E4%BB%A5%E7%AE%A1%E7%90%86%E6%96%87%E4%BB%B6%E6%89%80%E6%9C%89%E8%80%85%3A%201%20%E4%BD%BF%E7%94%A8Files.getOwner%20%28%29%E5%92%8CFiles.setOwner%20%28%29%E6%96%B9%E6%B3%95%E3%80%82%202,%E4%BD%BF%E7%94%A8%E2%80%9Cowner%E2%80%9D%E4%BD%9C%E4%B8%BA%E5%B1%9E%E6%80%A7%E5%90%8D%E7%A7%B0%E7%9A%84Files.getAttribute%20%28%29%20%E5%92%8CFiles.setAttribute%20%28%29%E6%96%B9%E6%B3%95%E3%80%82%203%20%E4%BD%BF%E7%94%A8FileOwnerAttributeView%E3%80%82

   只要获取了文件的元数据，就可以根据元数据进行filter

7.文件筛选：类型；名字；时间；尺寸；（路径？）

```
import java.util.regex.*;
 
class RegexExample1{
   public static void main(String[] args){
      String content = "I am noob " +
        "from runoob.com.";
 
      String pattern = ".*runoob.*";
 
      boolean isMatch = Pattern.matches(pattern, content);
      System.out.println("字符串中是否包含了 'runoob' 子字符串? " + isMatch);
   }
}
```

8. 压缩解压：Huffman编码和LZ77算法（可以调库）

   Huffman编码：https://blog.csdn.net/weixin_37610397/article/details/80222991

   https://blog.csdn.net/kaerbuka/article/details/90762178 （考虑使用这个，先将byte[]转化为String，应该并不困难）

   https://www.cnblogs.com/yu-kunpeng/p/10087415.html

   LZ77算法：（外网）

   https://gist.github.com/fogus/5404660 (考虑使用)

   https://commons.apache.org/proper/commons-compress/jacoco/org.apache.commons.compress.compressors.lz77support/LZ77Compressor.java.html

9. 加密解密：使用openssl库（不建议使用openssl的底层库，推荐使用最新的EVP API），也可以尝试手写实现AES的CBC模式

10. 数值区域设置

    https://stackoverflow.com/questions/40472668/numeric-textfield-for-integers-in-javafx-8-with-textformatter-and-or-unaryoperat

    

### 开发进度
1. v1: 基本的GUI界面、备份和恢复函数 done

   v1.1: GUI界面的优化: 

2. v2: 实现打包过程的页面化，同时实现了对长文件名的支持（超过一个页面大小）done

   页面大小：256B (head 8B + data 248B)

3. v3
   
   v3.1: 实现循环校验码crc。如果文件损坏（仅限文件的bit修改），则在通知框中提示损坏的文件名
   
   v3.5: 基本将meta-data加入到恢复过程中，但是存在两个漏洞：i. owner只能通过cmd进行设置，否则无权限；ii. accessTime恢复问题

4. v4

   v4.1: GUI代码重构

   v4.2: 添加了对format, name, file, type的筛选支持

### 代码注意
1. 在FileToolUtil中，有readPage函数，其中含有测试文件损坏部分
1. 输入时间格式：yyyy-MM-dd HH:mm:ss
1. 对于lastAccessTime, 现在的windows版本并不支持自动更新lastAccessTime（一小时之内不更新），但是java中获取的是更新后的lastAccessTime，因此出现了不对齐。

![image-20231209201009536](D:\learning_programs\java_programs\DataRestoreDemo1\images\Readme\image-20231209201009536.png)

https://www.tenforums.com/tutorials/139015-enable-disable-ntfs-last-access-time-stamp-updates-windows-10-a.html

4. 筛选测试：

   nameFilter: filteredName

   sizeFilter: 56-57

   modifiedTimeFilter: 1998-10-10 10:00:00 - 1998-10-10 10:02:00

   createTimeFilter: 1998-10-10 10:00:00 - 1998-10-10 10:02:00

   accessTimeFilter: 1998-10-10 10:00:00 - 1998-10-10 10:02:00

   

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

2. 对lastAccessTime放弃解决，有StackOverFlow帖子称java上不可解决

3. 待办：

   i. 对输入的错误格式进行警告（比如某个空为空等、输入的路径格式错误等）

   ii. 进行错误文件的整理，将文件按照时间、大小等筛选格式进行各自的文件夹处理，从而使得最终的测试更加有序

4. 现在的待办

5. 压缩与解压

6. 软硬链接

代码细节：

1. 已经读取到了文件时间等元数据，之后需要验证String能否直接写入，同时还需要验证能否将数据写入

### 附加要求
1. 完成cyc循环校验（必备）
2. 打包解包：注意**目录也是文件！！！** 一定要实现分块化处理。
3. 压缩：实现Huffman编码和LZ77编码。其中Huffman编码可以在压缩过程中使用字符串的形式，但是在存储和恢复过程中要使用byte
建议直接存储哈夫曼树用于后续解码。