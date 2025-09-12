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

interface BusinessMVIUseCase : MVIUseCase, CommonUseCase {

    companion object Companion {
        const val TAG = "BusinessMVIUseCase"
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
    val pageInitState: MutableSharedStateFlow<ViewState>

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

open class BusinessMVIUseCaseImpl(
    private val commonUseCase: CommonUseCase = CommonUseCaseImpl(),
) : MVIUseCaseImpl(),
    BusinessMVIUseCase,
    CommonUseCase by commonUseCase {

    override val pageInitState =
        MutableSharedStateFlow(initValue = BusinessMVIUseCase.ViewState.STATE_INIT)

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
            it is BusinessMVIUseCase.AutoLoading
        }
        // 判断是否有注解 ErrorIgnore
        val isErrorIgnore = kCallable.annotations.any {
            it is BusinessMVIUseCase.ErrorIgnore
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
                pageInitState.value = BusinessMVIUseCase.ViewState.STATE_LOADING
                timeAtLeast {
                    initData()
                }
                pageInitState.emit(
                    value = BusinessMVIUseCase.ViewState.STATE_SUCCESS
                )
            } catch (e: Exception) {
                if (ReactiveTemplate.isDebug) {
                    e.printStackTrace()
                }
                pageInitState.emit(
                    value = BusinessMVIUseCase.ViewState.STATE_ERROR
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
