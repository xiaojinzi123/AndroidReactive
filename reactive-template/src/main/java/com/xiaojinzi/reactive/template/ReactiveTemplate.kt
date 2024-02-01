package com.xiaojinzi.reactive.template

import android.widget.Toast
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import com.xiaojinzi.reactive.template.support.commonHandle
import com.xiaojinzi.reactive.template.view.TemplateAlertDialog
import com.xiaojinzi.reactive.template.view.TemplateErrorView
import com.xiaojinzi.reactive.template.view.TemplateInitView
import com.xiaojinzi.reactive.template.view.TemplateLoadingView
import com.xiaojinzi.support.bean.StringItemDto
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.contentWithContext

object ReactiveTemplate {

    const val EnableInitDefault: Boolean = true
    val TipHandleDefault: (StringItemDto) -> Unit = {
        Toast.makeText(
            app,
            it.contentWithContext(context = app),
            Toast.LENGTH_SHORT,
        ).show()
    }

    val ErrorHandleDefault: (Throwable) -> Unit = {
        it.commonHandle()
    }

    private var _enableInit: Boolean = EnableInitDefault
    private var _initView: @Composable (BoxScope.() -> Unit)? = null
    private var _errorView: @Composable (BoxScope.() -> Unit)? = null
    private var _loadingView: @Composable (() -> Unit)? = null
    private var _alertDialogView: @Composable (
        (
        title: StringItemDto?, text: StringItemDto?, cancelText: StringItemDto?, confirmText: StringItemDto?,
        onDismissCallback: () -> Unit, onConfirmCallback: () -> Unit
    ) -> Unit
    )? = null
    private var _tipHandle: (StringItemDto) -> Unit = TipHandleDefault
    private var _errorHandle: (Throwable) -> Unit = ErrorHandleDefault

    /**
     * 可选的初始化, 参数也都是可选的!!!
     */
    fun init(
        enableInit: Boolean = EnableInitDefault,
        initView: @Composable (BoxScope.() -> Unit)? = null,
        errorView: @Composable (BoxScope.() -> Unit)? = null,
        loadingView: @Composable (() -> Unit)? = null,
        alertDialogView: @Composable (
            (
            title: StringItemDto?, text: StringItemDto?, cancelText: StringItemDto?, confirmText: StringItemDto?,
            onDismissCallback: () -> Unit, onConfirmCallback: () -> Unit
        ) -> Unit
        )? = null,
        tipHandle: (StringItemDto) -> Unit = TipHandleDefault,
        errorHandle: (Throwable) -> Unit = ErrorHandleDefault,
    ) {
        this._enableInit = enableInit
        this._initView = initView
        this._errorView = errorView
        this._loadingView = loadingView
        this._alertDialogView = alertDialogView
        _tipHandle = tipHandle
        _errorHandle = errorHandle
    }

    val enableInit: Boolean
        get() = _enableInit

    val initView: @Composable (BoxScope.() -> Unit) = _initView ?: {
        TemplateInitView()
    }

    val errorView: @Composable (BoxScope.() -> Unit) = _errorView ?: {
        TemplateErrorView()
    }

    val loadingView: @Composable (() -> Unit) = _loadingView ?: {
        TemplateLoadingView()
    }

    val alertDialogView: @Composable (
        (
        title: StringItemDto?, text: StringItemDto?, cancelText: StringItemDto?, confirmText: StringItemDto?,
        onDismissCallback: () -> Unit, onConfirmCallback: () -> Unit
    ) -> Unit
    ) = _alertDialogView
        ?: { title, text, cancelText, confirmText, onDismissCallback, onConfirmCallback ->
            TemplateAlertDialog(
                title = title,
                text = text,
                cancelText = cancelText,
                confirmText = confirmText,
                onDismissClick = onDismissCallback,
                onConfirmClick = onConfirmCallback
            )
        }

    val tipHandle: (StringItemDto) -> Unit
        get() = _tipHandle

    val errorHandle: (Throwable) -> Unit
        get() = _errorHandle


}