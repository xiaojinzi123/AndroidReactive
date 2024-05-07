package com.xiaojinzi.reactive.template.support

import android.content.Context
import android.widget.Toast
import com.xiaojinzi.reactive.template.ReactiveTemplate
import com.xiaojinzi.support.bean.StringItemDto
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.contentWithContext
import com.xiaojinzi.support.ktx.orNull
import com.xiaojinzi.support.ktx.toStringItemDto

/**
 * 表示业务异常
 */
open class ReactiveTemplateBusinessException(
    val messageStringItem: StringItemDto? = null,
    cause: Throwable? = null,
) : RuntimeException(messageStringItem?.contentWithContext() ?: "", cause)

fun Throwable.getReactiveTemplateHandleMessage(
    defString: StringItemDto? = null,
    custom: (Throwable) -> StringItemDto? = ReactiveTemplate.errorCustom,
): StringItemDto? {

    var currentThrowable: Throwable = this

    var customResult: StringItemDto? = null

    do {

        // 如果需要忽略, 那就返回 null
        if (ReactiveTemplate.errorCustomIgnore.invoke(currentThrowable)) {
            return null
        }

        // 自定义处理的返回 null 表示没匹配到
        customResult = custom.invoke(currentThrowable)

        when {

            customResult != null -> {
                return customResult
            }

            currentThrowable is ReactiveTemplateBusinessException -> {
                return currentThrowable.messageStringItem
                    ?: currentThrowable.message?.toStringItemDto()
            }

            currentThrowable is kotlinx.coroutines.CancellationException -> {
                return null
            } // ignore

            currentThrowable::class.java in listOf(
                java.net.SocketTimeoutException::class.java,
                java.net.UnknownHostException::class.java,
                javax.net.ssl.SSLHandshakeException::class.java,
                java.net.SocketException::class.java,
                java.net.ConnectException::class.java,
                javax.net.ssl.SSLProtocolException::class.java,
            ) -> {
                return "网络错误".toStringItemDto()
            }

            else -> {
                currentThrowable = currentThrowable.cause ?: break
            }

        }

    } while (true)

    return defString

}

/**
 * 错误的常见处理
 */
fun Throwable.reactiveTemplateHandle(
    context: Context = app,
    defString: StringItemDto? = ReactiveTemplate.errorDefault,
    custom: (Throwable) -> StringItemDto? = ReactiveTemplate.errorCustom,
) {
    this.getReactiveTemplateHandleMessage(
        defString = defString,
        custom = custom,
    )?.contentWithContext().orNull()?.let { content ->
        Toast.makeText(
            context,
            content,
            Toast.LENGTH_SHORT
        ).show()
    }
}