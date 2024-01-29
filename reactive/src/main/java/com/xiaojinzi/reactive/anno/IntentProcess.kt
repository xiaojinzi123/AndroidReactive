package com.xiaojinzi.reactive.anno

import kotlin.reflect.KClass

/**
 * 表示一个 String 的参数是 Json 格式的
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
)
annotation class IntentProcess(
    val value: KClass<out Any>,
)