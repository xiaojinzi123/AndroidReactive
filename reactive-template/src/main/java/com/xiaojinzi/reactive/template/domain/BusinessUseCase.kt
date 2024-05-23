package com.xiaojinzi.reactive.template.domain

import androidx.annotation.CallSuper
import androidx.annotation.Keep
import androidx.annotation.MainThread
import com.xiaojinzi.reactive.domain.MVIUseCase
import com.xiaojinzi.reactive.domain.MVIUseCaseImpl
import com.xiaojinzi.reactive.template.ReactiveTemplate
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.support.ktx.MutableSharedStateFlow
import com.xiaojinzi.support.ktx.launchIgnoreError
import com.xiaojinzi.support.ktx.timeAtLeast
import kotlin.reflect.KCallable

interface BusinessUseCase : MVIUseCase, CommonUseCase {

    companion object {
        const val TAG = "BusinessUseCase"
    }

    @Retention(value = AnnotationRetention.RUNTIME)
    @Target(
        AnnotationTarget.FUNCTION,
    )
    annotation class AutoLoading

    /**
     * 会忽略错误, 不会抛出异常
     */
    @Retention(value = AnnotationRetention.RUNTIME)
    @Target(
        AnnotationTarget.FUNCTION,
    )
    annotation class ErrorIgnore

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
    val pageInitStateObservableDto: MutableSharedStateFlow<ViewState>

    /**
     * 初始化数据
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
) : MVIUseCaseImpl(),
    BusinessUseCase,
    CommonUseCase by commonUseCase {

    override val pageInitStateObservableDto =
        MutableSharedStateFlow(initValue = BusinessUseCase.ViewState.STATE_INIT)

    @MainThread
    override fun onIntentProcessError(
        intent: Any, error: Throwable,
    ) {
        ReactiveTemplate.errorHandle.invoke(error)
    }

    protected suspend fun <R> withLoading(block: suspend () -> R): R  {
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

    /**
     * 自定义拦截处理, 判断是否有注解 AutoLoading 注解, 然后执行前后加上 loading 的显示和隐藏
     */
    @CallSuper
    @Throws(Exception::class)
    final override suspend fun onIntentProcess(kCallable: KCallable<*>, intent: Any) {
        // 判断是否有注解 AutoLoading
        val isAutoLoading = kCallable.annotations.any {
            it is BusinessUseCase.AutoLoading
        }
        // 判断是否有注解 ErrorIgnore
        val isErrorIgnore = kCallable.annotations.any {
            it is BusinessUseCase.ErrorIgnore
        }
        if (isAutoLoading) {
            showLoading()
        }
        try {
            super.onIntentProcess(
                kCallable = kCallable,
                intent = intent,
            )
        } catch (e: Exception) {
            if (LogSupport.logAble) {
                e.printStackTrace()
            }
            if (!isErrorIgnore) {
                throw e
            }
        } finally {
            if (isAutoLoading) {
                hideLoading()
            }
        }
    }

    @Throws(Exception::class)
    override suspend fun initData() {
    }

    final override fun retryInit() {
        scope.launchIgnoreError {
            try {
                pageInitStateObservableDto.value = BusinessUseCase.ViewState.STATE_LOADING
                timeAtLeast {
                    initData()
                }
                pageInitStateObservableDto.emit(
                    value = BusinessUseCase.ViewState.STATE_SUCCESS
                )
            } catch (e: Exception) {
                if (ReactiveTemplate.isDebug) {
                    e.printStackTrace()
                }
                pageInitStateObservableDto.emit(
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
