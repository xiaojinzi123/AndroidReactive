package com.xiaojinzi.demo.module.user.module.login.domain

import android.content.Context
import androidx.annotation.UiContext
import com.xiaojinzi.demo.module.base.support.AppServices
import com.xiaojinzi.reactive.anno.IntentProcess
import com.xiaojinzi.reactive.template.domain.BusinessUseCase
import com.xiaojinzi.reactive.template.domain.BusinessUseCaseImpl
import com.xiaojinzi.support.annotation.StateHotObservable
import com.xiaojinzi.support.ktx.toStringItemDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

sealed class LoginIntent {

    data class Submit(
        @UiContext val context: Context
    ) : LoginIntent()

}

interface LoginUseCase : BusinessUseCase {

    @StateHotObservable
    val nameStateOb: MutableStateFlow<String>

    @StateHotObservable
    val isNameErrorStateOb: Flow<Boolean>

    @StateHotObservable
    val passwordStateOb: MutableStateFlow<String>

    @StateHotObservable
    val isPasswordErrorStateOb: Flow<Boolean>

    @StateHotObservable
    val canSubmitStateOb: Flow<Boolean>

}

class LoginUseCaseImpl(
) : BusinessUseCaseImpl(), LoginUseCase {

    override val nameStateOb = MutableStateFlow(value = "")

    override val isNameErrorStateOb = nameStateOb.map {
        it.length < 6
    }

    override val passwordStateOb = MutableStateFlow(value = "")

    override val isPasswordErrorStateOb = passwordStateOb.map {
        it.isEmpty()
    }

    override val canSubmitStateOb = combine(
        nameStateOb,
        passwordStateOb,
    ) { name, password ->
        name.isNotBlank() && password.isNotBlank()
    }

    @BusinessUseCase.AutoLoading
    @IntentProcess(LoginIntent.Submit::class)
    private suspend fun login(
        intent: LoginIntent.Submit,
    ) {
        delay(1000)
        val name = nameStateOb.first()
        val password = passwordStateOb.first()
        AppServices
            .userSpi
            ?.login(
                name = name,
                password = password,
            )
        confirmDialog(
            // title = "提示".toStringItemDto(),
            content = "登录成功".toStringItemDto(),
            negative = null,
        )
    }

    override suspend fun initData() {
        super.initData()
        delay(1000)
    }

}

