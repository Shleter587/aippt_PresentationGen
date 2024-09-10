# PrensentationGen

A SpringBoot web application that generates PPT files using a llm.

<div align=center href="https://github.com/Shleter587/aippt_PresentationGen"><img src="https://github.com/user-attachments/assets/12fba3ae-8f88-4df7-9dce-f22e724f9543"></div>

  <h1 align="center" style="font-weight:bold">AI生成PPT-PrensentationGen-Java实现</h1>



## 目录

- [功能演示](#功能介绍)
- [部署](#部署)
- [文件目录说明](#文件目录说明)
- [开发的架构](#开发的架构)
- [技术栈](#技术栈)
- [版本控制](#版本控制)
- [联系作者](#作者)

---

### 功能演示视频

https://github.com/user-attachments/assets/117e2d22-9618-4e97-ab03-9fe3c9fac485

<video src="./show.mp4"></video>

### 部署

- 后端：
  1. ​	配置application.yml中MySQL地址(spring.datasource)
  2. ​    配置application.yml中文心一言参数，先在文心一言创建自己的应用，再设置应用的appkey和secretkey。并且要开通boturl中对应的模型，可以自己  在模型广场选择模型并修改boturl。(ernie.*)
  3. ​    配置application.yml中对象储存参数，需要在阿里云oss创建桶并填入相关参数。(aliyun.oss.*)
  4. ​    （可选）配置unplash等开源图库apikey，根据具体图库，配置src/main/java/com/solocongee/presentationgen_back_end/utils/MergePPT.java中Futrue数组中请求图片方式。
  5. ​    配置文件路径，如果在windows本地运行请把配置application.yml中（# 设置全局路径Windows段）取消注释，并注释掉（#设置全局路径Linux段）。
  6. ​    下载完整资源文件，解压并覆盖掉data文件夹下的templates文件夹。
     ​     链接：https://pan.baidu.com/s/1Fp_uIF6eJKvQcgdq6_3s2A?pwd=jo63 
     ​     提取码：jo63
  7. ​    准备数据库数据，使用提供的PGen.sql还原数据库。
  7. ​    Meaven依赖加载。
- 前端
  1.    npm install -f
  2.    修改src/main.js下axios.defaults.baseURL为后端地址。
  3.    修改src/StreamDisplay.vue内url为后端地址，本地运行请修改process.env.NODE_ENV === 'development'分支语句内的url。

### 文件目录说明

```
filetree 
├── LICENSE.txt
├── README.md
├── 设计说明书.pdf  设计文档
├── /PresentationGen_Back_End/  后端SpringBoot项目
├── /PresentationGen_Front_End/  前端Vue2项目
├── /bbs/
├── /data/ 数据文件
│  ├── /markdown/ 储存中间markdown文件
│  ├── /record/ 储存用户生成历史
│  ├── /templates/ 小部分预处理的模板
│  │  ├── /chap/ 小部分预处理章节模板
│  │  ├── /cover/ 小部分预处理全局模板
│  │  ├── background.png 缺省背景图片
│  │  └── license.xml Aposed学习证书
└──  PGen.sql  SQLdump备份文件，包含已有模板所需记录

```





### 开发的架构 (详见文档)

主要思想是通过预处理出大量单页模板（屎山），并在使用时根据用户选择实时组合出PPTX文件，并对PPTX文件进行文本替换，实现了在后端生成PPT文件。

<div align=center><img src="https://github.com/user-attachments/assets/efd53298-5ce2-43e0-a8f8-154411432df2"></div>



### 技术栈

<div align=center><img src="https://github.com/user-attachments/assets/b55f6545-1fac-42ea-93d7-d612b8d18152"></div>

​													**SpringBoot+MyBatis+MySQL+Redis+WebFlux+Apache POI+Aspose Slides+OSS+Vue2**



### 联系作者

2790226109@qq.com 

### 版权说明

该项目签署了MIT 授权许可，详情请参阅 LICENSE.txt
