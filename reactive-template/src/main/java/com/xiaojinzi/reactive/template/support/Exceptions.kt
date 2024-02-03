package com.xiaojinzi.reactive.template.support

import android.content.Context
import android.widget.Toast
import com.xiaojinzi.reactive.template.ReactiveTemplate
import com.xiaojinzi.support.bean.StringItemDto
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.contentWithContext
import com.xiaojinzi.support.ktx.toStringItemDto

/**
 * 表示业务异常
 */
open class ReactiveTemplateBusinessException(
    val messageStringItem: StringItemDto? = null,
    cause: Throwable? = null,
) : RuntimeException(messageStringItem?.contentWithContext() ?: "", cause)

fun Throwable.getCommonHandleMessage(
    defString: StringItemDto? = null,
): StringItemDto? {

    var currentThrowable: Throwable = this

    do {

        when {

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
fun Throwable.commonHandle(
    context: Context = app,
    defString: StringItemDto? = ReactiveTemplate.errorDefault,
) {
    this.getCommonHandleMessage(
        defString = defString,
    )?.run {
        Toast.makeText(
            context,
            this.contentWithContext(),
            Toast.LENGTH_SHORT
        ).show()
    }
}