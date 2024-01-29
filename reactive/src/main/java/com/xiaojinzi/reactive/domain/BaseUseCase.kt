package com.xiaojinzi.reactive.domain

import androidx.annotation.CallSuper
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 * 这个接口中的方法不可以新增了哦!!!
 * 这个 BaseUseCase 由于每一个 UseCase 都要继承
 * 但是 UseCase 之间又可能随意组合使用. 比如下面的例子：
 * class UseCase1: BaseUseCase
 * class UseCase2: BaseUseCase
 * // 逻辑类 UseCase3 组合 UseCase1 和 UseCase2 的功能
 * class UseCase3: BaseUseCase, UseCase1, UseCase2
 *
 * class UseCase3Impl(
 *      private val useCase1: UseCase1 = UseCase1Impl(),
 *      private val useCase2: UseCase2 = UseCase2Impl(),
 * ): BaseUseCaseImpl(), UseCase3,
 * UseCase1 by useCase1,
 * UseCase2 by useCase2 {
 *      // some code
 *      override fun destroy() {
 *          super.destroy()
 *          useCase1.destroy()
 *          useCase2.destroy()
 *      }
 * }
 *
 * 所有其他的基础功能, 都应该扩展此接口来实现. 不能在这个接口上新增方法了
 */
interface BaseUseCase {

    /**
     * 销毁
     */
    fun destroy()

}

/**
 * 这个类需要用继承的方式来使用
 */
open class BaseUseCaseImpl : BaseUseCase {

    val scope = MainScope()

    @CallSuper
    override fun destroy() {
        scope.cancel()
    }

}
