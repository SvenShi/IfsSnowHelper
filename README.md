<img src="src/main/resources/META-INF/pluginIcon.svg"  alt="helperLogo" width="200">

## IfsSnowHelper

### 简介

本插件是个人在业余时间开发的一款针对公司框架的特性，提供各种方便的功能，也作为一次对idea插件开发的简单尝试与学习。

**如果本插件对你有帮助，可以到Github中star来支持本项目开发。**

[![star](https://gitee.com/svenshi/IfsSnowHelper/badge/star.svg?theme=dark)](https://gitee.com/svenshi/IfsSnowHelper/stargazers)
  <a href="https://github.com/SvenShi/IfsSnowHelper" style="display: inline-block; margin-right: 10px;">
    <img src="https://img.shields.io/badge/GitHub-%23121011.svg?style=for-the-badge&logo=github&logoColor=white" alt="GitHub" style="border-radius: 7px;">
  </a>
    <a href="https://plugins.jetbrains.com/plugin/21046-ifssnowhelper" style="display: inline-block;">
    <img src="https://img.shields.io/badge/IntelliJ%20IDEA%20Marketplace-000000.svg?style=flat&logo=intellij-idea&logoColor=white" alt="IntelliJ IDEA Marketplace">
  </a>

### 快速开始

#### 版本要求

仅支持idea 2021.2及以上版本

#### 如何下载

插件市场已发布！搜索IfsSnowHelper安装即可。

#### 功能特点

##### 引用跳转

###### Java

- 方法跳转到dtst文件中的flowId和method属性或Jsp文件的sform标签

- 代码中dao的select语句中的rqlxKey跳转到rqlx文件

###### dtst

- flowid、method属性跳转到对应的java方法
- datasource属性跳转到对应的dtst文件
- Find Usages功能跳转到使用该dtst文件的datasource或Jsp文件中的dataset标签

###### rqlx

- 各标签的id属性跳转到对应Java代码的引用处

###### Jsp

- dataset标签的path属性跳转到对应的dtst文件

- 各标签的dataset属性跳转到当前页面的dataset声明处

- 各标签的fieldstr属性中的各个字段跳转到对应的dtst文件field声明处

- gird标签paginationbar属性的各个按钮跳转到对应dtst文件command声明处

- button标签的id属性跳转到对应dtst文件的command声明处

- sform标签的flowid属性跳转到对应的Java方法

###### JavaScript

- dataset对象跳转到对应的dataset标签处
- interface_dataset对象跳转到对应的query标签处
- dataset对象调用setValue、getValue等方法中使用的field跳转到对应dtst文件的field声明处

##### 代码重构

使用idea文件的右键菜单`Refactor | rename`或快捷键`shift + F6`功能时可以帮助快速的重构相关代码，以下列出的各项均可反向操作

- 修改Java方法名时自动修改dtst文件的flowid、method属性值
- 修改dtst文件名时自动修改Jsp文件dataset标签的path属性值或dtst文件中的datasource属性值
- 修改dtst文件的field标签的id时自动修改Jsp文件中的fieldstr属性值、JavaScript中的setValue、getValue等方法中使用的fieldId
- 修改dtst文件的command标签的id时自动修改Jsp文件的button标签id以及gird标签aginationbar的属性值
- 修改rqlx文件的标签id时自动修改Java代码中的rqlxKey的值
- 修改Jsp文件dataset标签的id时自动修改其他标签的dataset属性值、JavaScript中的dataset对象名

##### 代码检查

在编写代码时如果出现未找到对应的引用元素，就会报红警告作为提醒。下面列出各个场景：

- Java代码中的rqlxKey未找到对应rqlx文件的标签时
- dtst文件中的flowid、method属性未找到对应的Java方法时
- Jsp文件dataset标签的path属性未找到对应的dtst文件时
- Jsp文件sform标签的flowid属性未找到对应的Java方法时
- Jsp文件button标签未找到对应dtst文件的command标签时
- Jsp文件gird标签的paginationbar属性未找到对应dtst文件的command标签时
- JavaScript中dataset对象未找到对应dataset标签时
- JavaScript中的interface_dataset对象未找到对应的query标签时
- JavaScript中dataset对象的getValue、setValue等方法中的field id未找到对应dtst文件的Field标签时

##### 自动补全

- Java代码中的rqlx key自动补全
- dtst文件中的flowid、method、datasource属性自动补全
- Jsp文件path、fieldstr、paginationbar、dataset属性自动补全
- JavaScript中的dataset对象自动补全
- JavaScript中dataset对象的getValue、setValue方法参数的自动补全

##### 快捷复制
- 方法名右键 Copy / Paste Special 菜单中添加 Copy Flow Id 选项点击后复制为flowId
- rqlx文件中的id右键 Copy / Paste Special 菜单中添加 Copy Rqlx Key 选项点击后复制为rqlxKey
- 选中dtst文件右键 Copy Path/Reference 菜单中添加 Copy DataSet Path 选项点击后复制为datasetPath

##### 语言注入

需要在idea设置中配置方言，如果需要数据库表名、字段提示需要在idea中连接数据库，并且配置SQL Resolution Scopes中设置对应的映射
idea SQL方言配置路径 `File | Settings | Languages & Frameworks | SQL Dialects`
idea SQL解析范围配置路径 `File | Settings | Languages & Frameworks | SQL Resolution Scopes`

- dtst、dtmd、rqlx文件关联为xml文件
- rqlx文件注入Sql语言

