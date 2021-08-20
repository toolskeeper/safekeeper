<p align="center">
	<a href="https://safekeeper.cn/">
	<img src="http://safekeeper-cdn.mymlsoft.com/safekeeper.png" width="35%">
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
</p>

<br/>


-------------------------------------------------------------------------------

[**🌎English Documentation**](README-EN.md)

-------------------------------------------------------------------------------

## 📚Introductory
<b> SafeKeeper is a lightweight Java permission authentication framework for self-developed and dependent </b><br>  
The service scenarios are login authentication, permission authentication, distributed Session Session, single sign-on (SSO), and OAuth2  
Micro service network customs authentication and a series of permissions related issues framework integration is simple, out of the box, clean functional design  
Safekeeper is a framework that allows you to implement complex permission authentication systems in a very simple way.  

### 🎁SafeKeeper Origin of the name

SafeKeeper = Safe + Keeper, "Safe" is focused on security, authentication, authorization, Keeper means guard, guard everyone's business.  

## 🛠️Contains the components
| module                |     Introduce                                                                 |
| -------------------|---------------------------------------------------------------------------------- |
| safekeeper-common  |     Common components                                                           |
| safekeeper-core    |     The core to realize                                                           |
| safekeeper-demo    |     The demo case                                                               |
| safekeeper-plugin  |     Framework adaptive integration                                               |

-------------------------------------------------------------------------------

## 📝Document

[📘Chinese Document](https://www.safekeeper.cn/)

-------------------------------------------------------------------------------

## 📦Install

### 🍊Maven
Add the following in dependencies to your project's POM.xml file: 

```xml
<dependency>
    <groupId>cn.safekeeper</groupId>
    <artifactId>safekeeper-plugin</artifactId>
    <version>0.0.1</version>
</dependency>
```

### 🍐Gradle
```
compile 'cn.safekeeper:safekeeper-plugin:0.0.1'
```

### 📥Download

Click the following link to download 'safekeeper-plugin-0.0.1.jar' :  

- [Maven central Repository](https://repo1.maven.org/maven2/cn/safekeeper/safekeeper-plugin/0.0.1/safekeeper-plugin-0.0.1.jar)

> 🔔️attention
> safekeeper 0.0.1 supports JDK78+。

### 🚽Compile the installation

[https://gitee.com/skylark2020/safekeeper](https://gitee.com/skylark2020/safekeeper) Download the entire project source code (either master or Develop) and go to the SafeKeeper project directory to execute:  

```sh
 mvn install
```

You can then use Maven to introduce。

-------------------------------------------------------------------------------

## 🏗️Building blocks

### 🎋Branch instructions

The safeKeeper source code is divided into two branches with the following functions:

| branch         | effect                                                          |
|-----------|---------------------------------------------------------------|
| master | The main branch, the branch used by the release version, is consistent with the JAR submitted by the central library and does not receive any PR or modifications  |
| develop | The development branch, which defaults to the SNAPSHOT version of the next version, accepts modifications or PR                  |

### 🐞Provide bug feedback or suggestions

To submit feedback, please indicate the JDK version, Hutool version, and related dependency library version you are using.  

- [Gitee issue](https://gitee.com/skylark2020/safekeeper/issues)


### 🧬Steps to contribute code  

1. Fork the project to your own repo on Gitee or Github  
2. Clone past projects from fork to your local site  
3. Modify the code (make sure to modify the Develop branch)  
4. Push to your own library after commit (develop branch)  
5. Go to Gitee or Github and you'll see a Pull Request button on your home page. Click on it, fill out some instructions, and submit.  
6. Wait for maintainers to merge  

### 📐The principles of PR

Everyone is welcome to contribute code to SafeKeeper, but the maintainer is an ocD patient. In order to care for the patient, pr (pull request) needs to be submitted in accordance with the following specifications:  
 
1. Complete comments. In particular, each new method should be marked with method description, parameter description, return value description and other information according to the Java document specification.  
2. Do not use the methods of third-party libraries to add new methods. Safekeeper follows the no-dependency principle.  
-------------------------------------------------------------------------------

## ⭐Star safekeeper

Safekeeper is in development, and I believe that there will be a lot of participants to join in, and there will be a lot of start  

## 💳Donate
If you think safekeeper is good, you can donate to the maintainer to eat latiao ~, thank you ^_^ here.  
 
Click on the link below, pull to the bottom of the page and click "Donate".  
(No donation for now)  

## 📌Official Accounts

#### 🧡Welcome to the public account of SafeKeeper cooperation  

There is no public account