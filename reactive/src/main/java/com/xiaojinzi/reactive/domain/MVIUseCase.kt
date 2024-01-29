package com.xiaojinzi.reactive.domain

import androidx.annotation.CallSuper
import androidx.annotation.Keep
import com.xiaojinzi.reactive.anno.IntentProcess
import com.xiaojinzi.support.ktx.NormalMutableSharedFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
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
) : RuntimeException("方法 $method 必须是 suspend 标记的, 并且方法参数只能是一个, 类型必须是：${intentProcess.value.java}")

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

    /**
     * 添加一个意图, 返回一个 [IntentAddResult] 对象, 通过这个对象可以等待意图处理完成
     */
    fun addIntent(intent: Any): IntentAddResult

}

/**
 * 必须使用继承才可以生效
 */
open class MVIUseCaseImpl : BaseUseCaseImpl(), MVIUseCase {

    private val intentEvent = NormalMutableSharedFlow<Any>()

    private val intentProcessResultEvent = NormalMutableSharedFlow<IntentProcessResult>()

    private val intentProcessMethodMap = mutableMapOf<KClass<*>, KCallable<*>>()

    override fun addIntent(intent: Any): IntentAddResult {
        intentEvent.tryEmit(
            value = intent,
        )
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

    @CallSuper
    protected open suspend fun onIntentProcess(
        kCallable: KCallable<*>,
        intent: Any,
    ) {
        kCallable.isAccessible = true
        kCallable.callSuspend(
            this@MVIUseCaseImpl, intent
        )
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
                        if (method.parameters[1].type.classifier != intentProcessAnno.value) {
                            throw IntentMethodException(
                                method = method,
                                intentProcess = intentProcessAnno,
                            )
                        }
                        intentProcessMethodMap[
                            intentProcessAnno.value
                        ] = method
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
            .onEach { intent ->
                println("准备处理意图：$intent")
                val intentProcessResult = runCatching {
                    intentProcessMethodMap.get(
                        key = intent::class
                    )?.run {
                        onIntentProcess(
                            kCallable = this,
                            intent = intent,
                        )
                    }
                }
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
                println("处理完毕意图：$intent")
            }
            .launchIn(scope = scope)

    }

}
