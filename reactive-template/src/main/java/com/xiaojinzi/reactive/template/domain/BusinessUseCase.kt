package com.xiaojinzi.reactive.template.domain

import androidx.annotation.Keep
import com.xiaojinzi.reactive.domain.BaseUseCase
import com.xiaojinzi.reactive.domain.BaseUseCaseImpl
import com.xiaojinzi.reactive.template.ReactiveTemplate
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.ktx.MutableSharedStateFlow
import com.xiaojinzi.support.ktx.launchIgnoreError
import com.xiaojinzi.support.ktx.timeAtLeast

interface BusinessUseCase : BaseUseCase, CommonUseCase {

    companion object Companion {
        const val TAG = "BusinessUseCase"
    }

    @Keep
    enum class ViewState {
        STATE_INIT,
        STATE_LOADING,
        STATE_ERROR,
        STATE_SUCCESS,
    }

    /**
     * 页面状态
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val pageInitState: MutableSharedStateFlow<ViewState>

    /**
     * 初始化数据, 可抛出异常
     */
    @Throws(Exception::class)
    suspend fun initData()

    /**
     * 尝试初始化
     */
    fun retryInit()

}

open class BusinessUseCaseImpl(
    private val commonUseCase: CommonUseCase = CommonUseCaseImpl(),
) : BaseUseCaseImpl(),
    BusinessUseCase,
    CommonUseCase by commonUseCase {

    override val pageInitState =
        MutableSharedStateFlow(initValue = BusinessUseCase.ViewState.STATE_INIT)

    protected suspend fun <R> withLoading(block: suspend () -> R): R {
        showLoading()
        return runCatching {
            block()
        }.run {
            hideLoading()
            this.exceptionOrNull()?.run {
                throw this
            }
            this.getOrThrow()
        }
    }

    @Throws(Exception::class)
    override suspend fun initData() {
    }

    final override fun retryInit() {
        scope.launchIgnoreError {
            try {
                pageInitState.value = BusinessUseCase.ViewState.STATE_LOADING
                timeAtLeast {
                    initData()
                }
                pageInitState.emit(
                    value = BusinessUseCase.ViewState.STATE_SUCCESS
                )
            } catch (e: Exception) {
                if (ReactiveTemplate.isDebug) {
                    e.printStackTrace()
                }
                pageInitState.emit(
                    value = BusinessUseCase.ViewState.STATE_ERROR
                )
            }
        }
    }

    override fun destroy() {
        super.destroy()
        commonUseCase.destroy()
    }

    init {
        retryInit()
    }

}
