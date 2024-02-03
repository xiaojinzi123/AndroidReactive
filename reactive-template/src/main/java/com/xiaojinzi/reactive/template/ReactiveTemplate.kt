package com.xiaojinzi.reactive.template

import android.widget.Toast
import com.xiaojinzi.reactive.template.support.reactiveTemplateHandle
import com.xiaojinzi.support.bean.StringItemDto
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.contentWithContext
import com.xiaojinzi.support.ktx.toStringItemDto

object ReactiveTemplate {

    private val TipHandleDefault: (StringItemDto) -> Unit = {
        Toast.makeText(
            app,
            it.contentWithContext(context = app),
            Toast.LENGTH_SHORT,
        ).show()
    }
    private val ErrorHandleDefault: (Throwable) -> Unit = {
        it.reactiveTemplateHandle()
    }
    private val ErrorDefaultDefault: StringItemDto = "未知错误".toStringItemDto()
    private val ErrorCustomDefault: (Throwable) -> StringItemDto? = { null }

    private var _tipHandle: (StringItemDto) -> Unit = TipHandleDefault
    private var _errorHandle: (Throwable) -> Unit = ErrorHandleDefault
    private var _errorDefault: StringItemDto = ErrorDefaultDefault
    private var _errorCustom: (Throwable) -> StringItemDto? = ErrorCustomDefault

    /**
     * 可选的初始化, 参数也都是可选的!!!
     */
    fun config(
        tipHandle: (StringItemDto) -> Unit = TipHandleDefault,
        errorHandle: (Throwable) -> Unit = ErrorHandleDefault,
        errorDefault: StringItemDto = ErrorDefaultDefault,
        errorCustom: (Throwable) -> StringItemDto? = ErrorCustomDefault,
    ) {
        _tipHandle = tipHandle
        _errorHandle = errorHandle
        _errorDefault = errorDefault
        _errorCustom = errorCustom
    }

    val tipHandle: (StringItemDto) -> Unit
        get() = _tipHandle

    val errorHandle: (Throwable) -> Unit
        get() = _errorHandle

    val errorDefault: StringItemDto
        get() = _errorDefault

    val errorCustom: (Throwable) -> StringItemDto?
        get() = _errorCustom


}