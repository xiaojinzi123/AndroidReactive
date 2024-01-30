## 前言

[AndroidReactive](https://github.com/xiaojinzi123/AndroidReactive) 是一个开源的 Android 响应式的业务框架. 帮助你在项目开始之初, 快速的搭建可以编写业务代码的响应式基础业务层的架构

项目结构解释

- reactive 响应式的业务架构的核心组件, 需要在项目中进行依赖
- reactive-template 核心组件之外的通用的业务. 在你的新项目创建之后, 复制此模块的代码到你的项目中, 比如 Base 业务模块中
- reactive-demo 是为了展示各种场景以及演示响应式的书写方式写的一些 Demo 代码
- build-logic 是为了简化每个模块的 gradle 配置. **此模块你不需要理会, 只是我这里项目中 Gradle 的一种配置方式, 你自己用你自己熟悉的即可**

## 架构图

![image](https://github.com/xiaojinzi123/AndroidReactive/assets/12975743/5f9a8bbb-6a7f-4c73-9bf2-fee15dd8a348)

![image](https://github.com/xiaojinzi123/AndroidReactive/assets/12975743/3c2b92d9-bb75-4996-af5e-581a2e62c710)

## 使用

### 项目中依赖 

添加仓库

```groovy
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    // 这行
    maven { url 'https://jitpack.io' }
  }
}
```

添加依赖 [![](https://jitpack.io/v/xiaojinzi123/AndroidReactive.svg)](https://jitpack.io/#xiaojinzi123/AndroidReactive)

```groovy
dependencies {
	implementation 'com.github.xiaojinzi123:AndroidReactive:<versionTag>'
}
```

### 复制 reactive-template 模块中的代码及其资源到项目

这步省略啦

### 使用范例

待完善......
