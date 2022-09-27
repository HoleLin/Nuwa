## Nuwa
* Java 项目代码合集

### 项目基础依赖

```
spring-boot-starter-parent: 2.7.4
```



### 项目目录结构

```sh
├── README.md
├── nuwa-business
│   ├── dicom
│   └── pom.xml
├── nuwa-common
│   ├── common-base
│   ├── common-file
│   ├── common-utils
│   └── pom.xml
├── nuwa-demo
│   └── pom.xml
└── pom.xml
```

* `nuwa-business`: 存放业务相关模块代码
  * `dicom`: 影像文件处理
* `nuwa-common`: 存放通用模块代码,如工具类,基础组件,通用常量
  * `common-base`: 
  * `common-file`:
  * `common-utils`: 
* `nuwa-demo`: 存放用于测试的Demo示例