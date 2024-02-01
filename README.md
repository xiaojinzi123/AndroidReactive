## 前言

[AndroidReactive](https://github.com/xiaojinzi123/AndroidReactive) 是一个开源的 Android 响应式的业务框架. 帮助你在项目开始之初, 快速的搭建可以编写业务代码的响应式基础业务层的架构

使用前提：

- Kotlin
- Compose

项目结构解释

- reactive 响应式的业务架构的核心组件, 需要在项目中进行依赖
- reactive-template 核心组件之外的通用的业务. 
  - 使用方式1: 直接依赖此模板模块, 通过调用 ReactiveTemplate.init() 方法来自定义一些默认的视图和事件
  - 使用方式2: 在你的新项目创建之后, 复制此模块的代码到你的项目中, 比如 Base 业务模块中

- reactive-demo 是为了展示各种场景以及演示响应式的书写方式写的一些 Demo 代码
- build-logic 是为了简化每个模块的 gradle 配置. **此模块你不需要理会, 只是我这里项目中 Gradle 的一种配置方式, 你自己用你自己熟悉的即可**

## 架构图

### 响应式架构图

<img src="https://github.com/xiaojinzi123/AndroidReactive/assets/12975743/5f9a8bbb-6a7f-4c73-9bf2-fee15dd8a348" alt="image" style="zoom:50%;" />

### MVI 架构图

<img src="https://github.com/xiaojinzi123/AndroidReactive/assets/12975743/3c2b92d9-bb75-4996-af5e-581a2e62c710" alt="image" style="zoom:50%;" />

## 使用方式1: (核心架构 + 对应架构模板)

### 添加依赖

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
	implementation 'com.github.xiaojinzi123:android-reactive-core:<versionTag>'
	implementation 'com.github.xiaojinzi123:android-reactive-template:<versionTag>'
}
```

### 自定义(方法是可选的, 里面的每个参数也都是可选的!!!)

比如我在 App 中通过 init 方法设置了几个视图和几个通用场景的处理, 比如 提示 和 错误处理

```kotlin
class App: Application {
  override fun oncreate() {
    //......
    ReactiveTemplate.init(
      enableInit = true,
      initView = {
          // ....
      },
      errorView = {
          // ....
      },
      loadingView = {
          // ....
      },
      alertDialogView = { title, text, cancelText, confirmText, onDismissCallback, onConfirmCallback ->
          // ....
      },
      tipHandle = {
        	// ....
      },
      errorHandle = {
      		// ....
      },
    )
  }
}
```

### 愉快使用

1. 创建一个业务类接口, 比如 OrderDetailUseCase, 继承 BusinessUseCase 接口

   ```Kotlin
   interface OrderDetailUseCase : BusinessUseCase {
     	val xxxStateFlow: Flow<String>
   		// ......
   }
   ```

2. 创建对应的业务实现接口, 继承 BusinessUseCaseImpl 类, 并且实现上面的 OrderDetailUseCase 接口

   ```kotlin
   class OrderDetailUseCaseImpl(
   ) : BusinessUseCaseImpl(), OrderDetailUseCase {
     	val xxxStateFlow = MutableStateFlow(value = "")
   	  // ......
   }
   ```

3. 创建对应的 ViewModel 这个基本属于模板代码, 里面基本不写其他代码. 除非有一些 UI 相关的, 可以处理下

   ```kotlin
   @ViewLayer
   class OrderDetailViewModel(
       private val useCase: OrderDetailUseCase = OrderDetailUseCaseImpl(),
   ) : BaseViewModel(),
       OrderDetailUseCase by useCase {
   }
   ```

4. 然后在你的 Compose 代码的根节点上, 使用 BusinessContentView

   ```kotlin
   BusinessContentView<OrderDetailViewModel> { vm ->
   	// 从 VM 代理的 OrderDetailUseCase 中订阅数据
   	val xxx by vm.xxxStateFlow.collectAsState(initial = "")
     // 你自己的视图代码, xxx 可以被使用了
   }
   ```

## 使用方式2: (核心架构 + 自定义模板架构)

### 添加依赖

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
	implementation 'com.github.xiaojinzi123:android-reactive-core:<versionTag>'
}
```

### 自行复制  [AndroidReactive](https://github.com/xiaojinzi123/AndroidReactive) reactive-template 模块中的代码到项目. 自行修改

自行修改, 你可以完全放飞自我. 飞翔吧, 少年

## MVI 的支持

BusinessUseCase 接口继承了 MVIUseCase 接口. 拥有 MVI 处理的能力.

先看看 MVIUseCase 的 addIntent 方法

```kotlin
interface MVIUseCase : BaseUseCase {
    /**
     * 添加一个意图, 返回一个 [IntentAddResult] 对象, 通过这个对象可以等待意图处理完成
     */
    fun addIntent(intent: Any): IntentAddResult
}
```

比如我们处理一个登录意图, 在点击登录的时候, 添加一个意图, 并且处理

```kotlin
sealed class LoginIntent {
    data class Submit(
        @UiContext val context: Context
    ) : LoginIntent()
}
```

```kotlin
vm.addIntent(
    intent = LoginIntent.Submit(
    		context = context,
    )
)
```

```kotlin
@BusinessUseCase.AutoLoading
@IntentProcess(LoginIntent.Submit::class)
private suspend fun login(
    intent: LoginIntent.Submit,
) {
    delay(1000)
    val name = nameStateOb.first()
    val password = passwordStateOb.first()
    AppServices
        .userSpi
        ?.login(
            name = name,
            password = password,
        )
    confirmDialogOrError(
        content = "登录成功".toStringItemDto(),
        negative = null,
    )
}
```

对应的效果视频为：

https://github.com/xiaojinzi123/AndroidReactive/assets/12975743/638dca9a-ee6f-4b04-a9ed-7f174258fa3e
