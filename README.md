![helperLogo](src\main\resources\META-INF\pluginIcon.svg "helperLogo")

## IfsSnowHelper

### 简介

本插件为个人兴趣使然在业余时间开发，对idea插件开发的一次简单尝试与学习。

公司自研框架在开发过程中，搜索dtst、jsp以及对应的java方法时比较麻烦，所以就写了这个插件，用来简化开发。

#### 功能特点

- [x] Java方法跳转到DTST中引用处
- [x] dtst跳转到引用的方法处
- [x] dtst跳转到Jsp `<snow:dataset>` 标签引用出
- [x] Jsp`<snow:dataset>`标签跳转到对应dtst中
- [x] rqlx文件注入sql语法提示，需要在idea设置中配置方言，如果需要数据库表名、字段提示需要在idea中连接数据库，并且配置SQL
  Resolution Scopes中设置对应的映射

##### 1.40 新增功能

- [x] Java代码跳转到rqlx文件sql处
- [x] 识别dtst中的datasource属性并支持跳转到引用的dtst中
- [x] Jsp中的<code>&lt;snow:button&gt;</code>标签现已支持跳转到dtst中

##### 2.00 新增功能

- [x] 移除Java跳转到Dtst的箭头,改为ctrl+左键点击方法名跳转,在dtst中点击flowId跳转
- [x] 移除Java跳转到Rqlx文件的箭头,改为引用点击即可跳转（仅支持显式赋值的字符串，不支持拼接的字符串跳转）
- [x] 添加Rqlx文件中,点击id跳转到对应sql使用处（仅支持显式赋值的字符串，不支持拼接的字符串跳转）
- [x] 添加Rqlx文件中,点击箭头跳转到对应sql使用处(保留用于显示引用的java类)

##### 2.14 新增功能

- [x] 优化rqlx key缓存机制
- [x] 修复文件修改监听器导致的BUG
- [x] 添加项目开启时的rqlx key缓存加载提示框，期间不允许操作

##### 计划新增

- [ ] Jsp文件dataset标签采用引用方式跳转dtst
- [ ] 移除Jsp文件dataset标签箭头跳转dtst
- [ ] Jsp文件button标签采用引用方式跳转dtst
- [ ] 移除Jsp文件button标签箭头跳转dtst
- [ ] jsp文件sform标签采用引用方式跳转jsp
- [ ] dtst datasource属性使用引用方式跳转dtst,移除箭头跳转

#### 随缘更新



---

### 快速开始

#### 如何下载

本插件不会发布在idea的插件市场中，目前仅在本文附件中下载。

#### 如何安装

1. 下载插件
2. 无需解压将zip文件拖入idea界面（也可以在idea插件管理界面使用安装本地插件功能选择zip文件安装）
3. 在idea设置中为xml文件类型添加三个文件后缀`*.dtst`,`*.dtmd`,`*.rqlx`
   ![](src\main\resources\img\fileType.png)
4. 至此重启idea后即安装成功！

#### 如何使用

插件安装成功后在jsp、dtst、java文件的编辑区域左侧会出现箭头点击箭头即可转向对应文件。

![](src\main\resources\img\arrow.png)

在使用过程中，如果项目比较复杂dtst、jsp文件较多的情况下索引较慢，箭头可能会等待一段时间才会出现

##### 箭头分为三种颜色分别对应不同文件：

- ![](src\main\resources\icons\go-blue.svg) 对应java文件，点击后跳转对应方法；

- ![](src\main\resources\icons\go-green.svg) 对应dtst文件，点击后跳转到对应dtst文件；

- ![](src\main\resources\icons\go-yellow.svg)
  对应jsp文件，点击后跳转到对应jsp文件；（只可能显示在dtst文件的`<Data>`标签左侧）
