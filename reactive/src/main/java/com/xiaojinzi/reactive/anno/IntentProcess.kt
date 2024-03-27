package com.xiaojinzi.reactive.anno


/**
 * 表示一个方法是一个处理 MVI 意图的方法
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
)
annotation class IntentProcess {
}