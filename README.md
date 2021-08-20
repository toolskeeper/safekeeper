<p align="center">
	<a href="https://safekeeper.cn/">
	<img src="https://images.gitee.com/uploads/images/2021/0817/173417_4ed34739_9474002.png" width="35%">
	</a>
</p>
<p align="center">
	<strong>🍬A lightweight Java permission authentication framework.</strong>
</p>
<p align="center">
	👉 <a href="https://safekeeper.cn">https://safekeeper.cn/</a> 👈
</p>

<p align="center">
    <img src="https://img.shields.io/cocoapods/p/AFNetworking?color=red" />
    <a href="https://gitee.com/skylark2020/safekeeper/stargazers"><img src="https://gitee.com/skylark2020/safekeeper/badge/star.svg"></a>
    <a href="https://gitee.com/skylark2020/safekeeper/members"><img src="https://gitee.com/skylark2020/safekeeper/badge/fork.svg"></a>
    <a target="_blank" href="https://search.maven.org/artifact/cn.safekeeper/safekeeper-plugin/0.0.2/jar">
		<img src="https://img.shields.io/maven-central/v/cn.safekeeper/safekeeper-plugin" />
	</a>
	<a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.html">
		<img src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg" />
	</a>
	<a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8+-green.svg" />
	</a>
	<a target="_blank" href="https://gitee.com/skylark2020/safekeeper">
	    <img src="https://img.shields.io/jetbrains/plugin/r/rating/11941" />
	</a>
	<a target="_blank" href="https://gitee.com/skylark2020/safekeeper">
	    <img src="https://img.shields.io/github/languages/code-size/safekeeper2020/safekeeper"/>
	</a>
    <a href="https://qm.qq.com/cgi-bin/qm/qr?k=0wwldaU0E8r-ZzHl_wma33W7420zwXYi&jump_from=webapi">
        <img src="https://img.shields.io/badge/SafeKeeper交流群-794165952-orange"/>
    </a>
    
<br/>


-------------------------------------------------------------------------------

[**🌎English Documentation**](README-EN.md)

-------------------------------------------------------------------------------

## 📚简介
<b>safekeeper是一款自研底依赖的轻量级Java权限认证框架</b><br>
主要解决的业务场景是：登录认证、权限认证、分布式Session会话、单点登录、OAuth2
微服务网关鉴权等一系列权限相关问题 框架集成简单、开箱即用、功能设计清爽
通过safekeeper这个框架，你可以用很简单的方式实现复杂的权限认证系统。

### 🎁safekeeper名称的由来

safekeeper = safe + keeper，“safe”是专注于安全，认证，授权，keeper表示守卫，守卫大家的业务。
 
## 🛠️包含组件
| 模块                |     介绍                                                                          |
| -------------------|---------------------------------------------------------------------------------- |
| safekeeper-common  |     公共组件                                                                        |
| safekeeper-core    |     核心实现                                                                        |
| safekeeper-demo    |     demo案例                                                                       |
| safekeeper-plugin  |     框架适配集成                                                                    |

-------------------------------------------------------------------------------

## 📝文档 

[📘中文文档](https://www.safekeeper.cn/)

-------------------------------------------------------------------------------

## 📦安装

### 🍊Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
    <groupId>cn.safekeeper</groupId>
    <artifactId>safekeeper-plugin</artifactId>
    <version>0.0.2</version>
</dependency>
```

### 🍐Gradle
```
compile 'cn.safekeeper:safekeeper-plugin:0.0.2'
```

### 📥下载jar

点击以下链接，下载`safekeeper-plugin-0.0.2.jar`即可：

- [Maven中央库](https://repo1.maven.org/maven2/cn/safekeeper/safekeeper-plugin/0.0.1/safekeeper-plugin-0.0.2.jar)

> 🔔️注意
> safekeeper 0.0.2支持JDK78+。

### 🚽编译安装

访问safekeeper的Gitee主页：[https://gitee.com/skylark2020/safekeeper](https://gitee.com/skylark2020/safekeeper) 下载整个项目源码（master或develop分支都可）然后进入safekeeper项目目录执行：

```sh
 mvn install
```

然后就可以使用Maven引入了。

-------------------------------------------------------------------------------

## 🏗️添砖加瓦

### 🎋分支说明

safekeeper的源码分为两个分支，功能如下：

| 分支       | 作用                                                          |
|-----------|---------------------------------------------------------------|
| master | 主分支，release版本使用的分支，与中央库提交的jar一致，不接收任何pr或修改 |
| develop | 开发分支，默认为下个版本的SNAPSHOT版本，接受修改或pr                 |

### 🐞提供bug反馈或建议

提交问题反馈请说明正在使用的JDK版本呢、Hutool版本和相关依赖库版本。

- [Gitee issue](https://gitee.com/skylark2020/safekeeper/issues)


### 🧬贡献代码的步骤

1. 在Gitee或者Github上fork项目到自己的repo
2. 把fork过去的项目也就是你的项目clone到你的本地
3. 修改代码（记得一定要修改develop分支）
4. commit后push到自己的库（develop分支）
5. 登录Gitee或Github在你首页可以看到一个 pull request 按钮，点击它，填写一些说明信息，然后提交即可。
6. 等待维护者合并

### 📐PR遵照的原则

safekeeper欢迎任何人为safekeeper添砖加瓦，贡献代码，不过维护者是一个强迫症患者，为了照顾病人，需要提交的pr（pull request）符合一些规范，规范如下：

1. 注释完备，尤其每个新增的方法应按照Java文档规范标明方法说明、参数说明、返回值说明等信息，必要时请添加单元测试，如果愿意，也可以加上你的大名。
2. 新加的方法不要使用第三方库的方法，safekeeper遵循无依赖原则。
-------------------------------------------------------------------------------

## ⭐Star safekeeper

safekeeper正在发展中，我相信后面会有很多的参与者加入进来，也会有很多的start

## 💳捐赠

如果你觉得safekeeper不错，可以捐赠请维护者吃包辣条~，在此表示感谢^_^。

点击以下链接，将页面拉到最下方点击“捐赠”即可。
(暂时不接受捐赠)

## 📌公众号

#### 🧡欢迎关注safekeeper合作的公众号

暂时没有公众号