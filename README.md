![helperLogo](D:\WorkSpace\Project\personal\IfsSnowHelper\src\main\resources\META-INF\pluginIcon.svg "helperLogo")

## IfsSnowHelper
### 简介
本插件为个人兴趣使然在业余时间开发，对idea插件开发的一次简单尝试与学习。

公司自研框架在开发过程中，搜索dtst、jsp以及对应的java方法时比较麻烦，所以就写了这个插件，用来简化开发。

#### 功能特点

- [x] Java方法跳转到DTST中引用处
- [x] dtst跳转到引用的方法处
- [x] dtst跳转到Jsp `<snow:dataset>` 标签引用出
- [x] Jsp`<snow:dataset>`标签跳转到对应dtst中
- [x] rqlx文件注入sql语法提示，需要在idea设置中配置方言，如果需要数据库表名、字段提示需要在idea中连接数据库，并且配置SQL Resolution Scopes中设置对应的映射

#### 待开发功能

- [ ] 支持Ctrl + 左键点击dtst连接、java方法等跳转转到对应文件
- [ ] 支持idea重构功能
- [ ] 引用文件不存在报红功能
- [ ] ~~代码提示功能、包括JavaScript中的代码提示~~
- [ ] ~~rqlx与Java代码中相互跳转~~
- [ ] ~~集成公司自研代码生成工具~~
- [ ] ~~alt+enter 生成不存在的java方法、dtst和rqlx~~

划线为随缘功能，因个人实力原因有可能写不出来

#### 更新频率

随缘更新，业余时间有空就写写

---

### 快速开始


#### 如何下载

出于对公司框架的保密考虑，本插件不会发布在idea的插件市场中，目前仅在本文附件中下载。

#### 如何安装

1. 下载插件
2. 无需解压将zip文件拖入idea界面（也可以在idea插件管理界面使用安装本地插件功能选择zip文件安装）
3. 在idea设置中为xml文件类型添加三个文件后缀`*.dtst`,`*.dtmd`,`*.rqlx`
   ![](D:\WorkSpace\Project\personal\IfsSnowHelper\src\main\resources\img\fileType.png)
4. 至此重启idea后即安装成功！

#### 如何使用
插件安装成功后在jsp、dtst、java文件的编辑区域左侧会出现箭头点击箭头即可转向对应文件。

![](D:\WorkSpace\Project\personal\IfsSnowHelper\src\main\resources\img\arrow.png)

在使用过程中，如果项目比较复杂dtst、jsp文件较多的情况下索引较慢，箭头可能会等待一段时间才会出现

##### 箭头分为三种颜色分别对应不同文件：

- ![](D:\WorkSpace\Project\personal\IfsSnowHelper\src\main\resources\icons\go-blue.svg) 对应java文件，点击后跳转对应方法；

- ![](D:\WorkSpace\Project\personal\IfsSnowHelper\src\main\resources\icons\go-green.svg) 对应dtst文件，点击后跳转到对应dtst文件；

- ![](D:\WorkSpace\Project\personal\IfsSnowHelper\src\main\resources\icons\go-yellow.svg) 
  对应jsp文件，点击后跳转到对应jsp文件；（只可能显示在dtst文件的`<Data>`标签左侧）