package com.xiaojinzi.reactive.view

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import com.xiaojinzi.reactive.domain.BaseUseCase

/**
 * 每一个 [ViewModel] 的基类. 定义了一些公用的方法
 * 如何创建：new ViewModelProvider(FragmentActivity).get(XxxViewModel.class);
 * 所有的 ViewModel 都应该为 [Activity] 减轻负担, 承载 UI 相关的业务逻辑和 UI 相关的数据.
 * 而数据我们可以使用"Hot Observable" 模式将数据变为具备通知能力的对象
 * 可以利用 [LiveData] 或者 [Subject] 实现.
 * [Subject] 中和 [LiveData] 对应的是 [BehaviorSubject]
 * 都是表示订阅最近发射出来的一个信号. 如果只想收到订阅之后的,
 * 请使用 [PublishSubject]. 整体其实 Google 官方的是不够的, 只有一个
 * [LiveData] 模式. Kotlin 的 Flow 出现, 弥补了 Google 响应式这块的不足
 * [SharedFlow] 和 [SharedStateFlow] 都是很好的选择
 *
 * @Note: ViewModel 不是用来承载业务逻辑的, 只是适合处理 UI 相关的数据和保存 UI 相关的数据及其需要保存的引用
 */
abstract class
BaseViewModel : ViewModel() {

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        (this as? BaseUseCase)?.destroy()
    }

}