## 1. 参考代码链接
https://blog.csdn.net/CSDN___LYY/article/details/79461026

**定时**备份参考

https://blog.csdn.net/fish332/article/details/114178510

（建议搜索：java文件备份）文件备份参考

## 2. 参考库

1. 前端GUI：swing
2. 定时：Timer.SheduleAtFixedRate
3. 读取文件：io
4. 

## 3. 开发进度
1. v1: 基本的GUI界面、备份和恢复函数 done

   v1.1: GUI界面的优化
2. 

## 4. 技术栈
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

## 5. 现存问题
1. 各种exception之间的逻辑关系（基本放弃解决）
