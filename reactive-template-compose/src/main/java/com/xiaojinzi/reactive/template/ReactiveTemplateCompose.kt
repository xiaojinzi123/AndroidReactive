package com.xiaojinzi.reactive.template

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import com.xiaojinzi.reactive.template.view.TemplateAlertDialog
import com.xiaojinzi.reactive.template.view.TemplateErrorView
import com.xiaojinzi.reactive.template.view.TemplateInitView
import com.xiaojinzi.reactive.template.view.TemplateLoadingView
import com.xiaojinzi.support.bean.StringItemDto

object ReactiveTemplateCompose {

    const val EnableInitDefault: Boolean = true

    private var _initView: @Composable (BoxScope.() -> Unit)? = null
    private var _errorView: @Composable (BoxScope.() -> Unit)? = null
    private var _loadingView: @Composable (() -> Unit)? = null
    private var _alertDialogView: @Composable (
        (
        title: StringItemDto?, text: StringItemDto?, cancelText: StringItemDto?, confirmText: StringItemDto?,
        onDismissCallback: () -> Unit, onConfirmCallback: () -> Unit
    ) -> Unit
    )? = null
    private var _enableInit: Boolean = EnableInitDefault

    /**
     * 可选的初始化, 参数也都是可选的!!!
     */
    fun config(
        initView: @Composable (BoxScope.() -> Unit)? = null,
        errorView: @Composable (BoxScope.() -> Unit)? = null,
        loadingView: @Composable (() -> Unit)? = null,
        alertDialogView: @Composable (
            (
            title: StringItemDto?, text: StringItemDto?, cancelText: StringItemDto?, confirmText: StringItemDto?,
            onDismissCallback: () -> Unit, onConfirmCallback: () -> Unit
        ) -> Unit
        )? = null,
        enableInit: Boolean = EnableInitDefault,
    ) {
        this._initView = initView
        this._errorView = errorView
        this._loadingView = loadingView
        this._alertDialogView = alertDialogView
        this._enableInit = enableInit
    }
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

    val enableInit: Boolean
        get() = _enableInit

}