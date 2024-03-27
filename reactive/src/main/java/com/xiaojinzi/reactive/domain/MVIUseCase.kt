package com.xiaojinzi.reactive.domain

import androidx.annotation.CallSuper
import androidx.annotation.Keep
import androidx.annotation.MainThread
import com.xiaojinzi.reactive.anno.IntentProcess
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.support.ktx.NormalMutableSharedFlow
import com.xiaojinzi.support.ktx.launchIgnoreError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.callSuspend
import kotlin.reflect.jvm.isAccessible

private class IntentMethodException(
    method: KCallable<*>,
    intentProcess: IntentProcess,
) : RuntimeException("方法 $method 必须是 suspend 标记的, 并且方法参数只能是一个")

@Keep
private sealed class IntentProcessResult(
    open val intent: Any,
) {

    @Keep
    data class Success(
        override val intent: Any,
    ) : IntentProcessResult(intent = intent)

    @Keep
    data class Fail(
        override val intent: Any,
        val error: Throwable,
    ) : IntentProcessResult(intent = intent)

}

interface IntentAddResult {

    /**
     * 等待完成
     */
    @Throws(Exception::class)
    suspend fun await()

    /**
     * 等待完成
     */
    suspend fun awaitIgnoreError() {
        runCatching {
            await()
        }
    }

}

/**
 * 从 [BaseUseCase] 扩展了 [addIntent] 方法, 来实现 MVI 意图的唯一入口
 * 不可以用代理的模式去使用, 比如 BaseUseCase by mviUseCase
 */
interface MVIUseCase : BaseUseCase {

    companion object {
        const val TAG = "MVIUseCase"
    }

    /**
     * 添加一个意图, 返回一个 [IntentAddResult] 对象, 通过这个对象可以等待意图处理完成
     */
    fun addIntent(intent: Any): IntentAddResult

}

/**
 * 必须使用继承才可以生效
 */
open class MVIUseCaseImpl : BaseUseCaseImpl(), MVIUseCase {

    private val intentEvent = Channel<Any>(
        capacity = Channel.Factory.UNLIMITED,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )

    private val intentProcessResultEvent = NormalMutableSharedFlow<IntentProcessResult>()

    private val intentProcessMethodMap = mutableMapOf<KClass<*>, KCallable<*>>()

    private val taskScope = MainScope()

    override fun addIntent(intent: Any): IntentAddResult {
        intentEvent.trySend(element = intent)
        return object : IntentAddResult {

            private val targetIntent: Any = intent

            override suspend fun await() {

                val intentProcessResult = withContext(context = Dispatchers.IO) {

                    val intentSuccess = async {
                        intentProcessResultEvent
                            .filterIsInstance<IntentProcessResult.Success>()
                            .filter { it.intent == targetIntent }
                            .first()
                    }

                    val intentFail = async {
                        intentProcessResultEvent
                            .filterIsInstance<IntentProcessResult.Fail>()
                            .filter { it.intent == targetIntent }
                            .first()
                    }

                    select {
                        intentSuccess.onAwait {
                            it
                        }
                        intentFail.onAwait {
                            it
                        }
                    }

                }

                when (intentProcessResult) {
                    is IntentProcessResult.Fail -> {
                        throw intentProcessResult.error
                    }

                    is IntentProcessResult.Success -> {
                        // nothing
                    }
                }

            }

        }
    }

    @MainThread
    protected open fun onIntentProcessError(
        intent: Any, error: Throwable,
    ) {
        // empty
    }

    @CallSuper
    @Throws(Exception::class)
    protected open suspend fun onIntentProcess(
        kCallable: KCallable<*>,
        intent: Any,
    ) {
        kCallable.isAccessible = true
        kCallable.callSuspend(
            this@MVIUseCaseImpl, intent
        )
    }

    override fun destroy() {
        super.destroy()
        taskScope.cancel()
    }

    /**
     * 处理意图
     */
    private fun intentProcess(intent: Any) {
        taskScope.launchIgnoreError(context = Dispatchers.IO) {
            LogSupport.d(
                tag = MVIUseCase.TAG,
                content = "准备处理意图：$intent",
            )
            val intentProcessResult = runCatching {
                intentProcessMethodMap.get(
                    key = intent::class
                )?.run {
                    onIntentProcess(
                        kCallable = this,
                        intent = intent,
                    )
                }
            }.apply {
                this.exceptionOrNull()?.let {
                    withContext(context = Dispatchers.Main) {
                        onIntentProcessError(
                            intent = intent,
                            error = it,
                        )
                    }
                }
            }
            LogSupport.d(
                tag = MVIUseCase.TAG,
                content = "意图处理结果: ${intentProcessResult.isSuccess}",
            )
            intentProcessResultEvent.add(
                value = intentProcessResult.exceptionOrNull()?.let {
                    IntentProcessResult.Fail(
                        intent = intent,
                        error = it,
                    )
                } ?: IntentProcessResult.Success(
                    intent = intent,
                )
            )
            LogSupport.d(
                tag = MVIUseCase.TAG,
                content = "处理完毕意图：$intent",
            )
        }
    }

    init {

        // 收集意图
        this@MVIUseCaseImpl::class
            .members
            .forEach { method ->

                if (!method.isSuspend) {
                    return@forEach
                }

                val intentProcessAnno = method
                    .annotations
                    .find {
                        it is IntentProcess
                    } as? IntentProcess

                intentProcessAnno?.let {
                    if (method.parameters.size == 2) {
                        val intentClassifier = method.parameters[1].type.classifier as? KClass<*>
                        /*if (intentClassifier != intentProcessAnno.value) {
                            throw IntentMethodException(
                                method = method,
                                intentProcess = intentProcessAnno,
                            )
                        }*/
                        intentClassifier?.let {
                            intentProcessMethodMap[it] = method
                        }
                    } else {
                        throw IntentMethodException(
                            method = method,
                            intentProcess = intentProcessAnno,
                        )
                    }
                }

            }

        // 处理意图
        intentEvent
            .consumeAsFlow()
            .onEach { intent ->
                intentProcess(
                    intent = intent,
                )
            }
            .flowOn(context = Dispatchers.IO)
            .launchIn(scope = scope)

    }

}
