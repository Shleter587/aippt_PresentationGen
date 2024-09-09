<div align=center><img src="https://github.com/user-attachments/assets/12fba3ae-8f88-4df7-9dce-f22e724f9543"></div>

  <h1 align="center" style="font-weight:bold">AI生成PPT-PrensentationGen-Java实现</h1>



## 目录

- [功能演示](#功能介绍)
- [开发前的配置要求](#开发前的配置要求)
- [文件目录说明](#文件目录说明)
- [开发的架构](#开发的架构)
- [部署](#部署)
- [技术栈](#技术栈)
- [版本控制](#版本控制)
- [联系作者](#作者)

---

### 功能演示视频

https://github.com/user-attachments/assets/117e2d22-9618-4e97-ab03-9fe3c9fac485

<video src="./show.mp4"></video>

### 开发前的配置要求

1. 从千帆大模型平台获得访问语言模型文心一言的Access Token
2. AI配图功能需要自备任意开源图库API KEY，作者使用的是pixabay
2. OSS服务

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



### 部署

1. 后端：配置application.yml中缺省参数，补充MergePPT.java中图库api等，加载Mevan依赖。
2. 后端资源：data中的template文件限于仓库大小删减了绝大部分模板文件，功能会有异常，数据库中的文件会找不到。
3. 前端: npm install --force

### 技术栈

<div align=center><img src="https://github.com/user-attachments/assets/b55f6545-1fac-42ea-93d7-d612b8d18152"></div>

​													**SpringBoot+MyBatis+MySQL+Redis+WebFlux+Apache POI+Aspose Slides+OSS+Vue2**



### 联系作者

2790226109@qq.com 

### 版权说明

该项目签署了MIT 授权许可，详情请参阅 LICENSE.txt





