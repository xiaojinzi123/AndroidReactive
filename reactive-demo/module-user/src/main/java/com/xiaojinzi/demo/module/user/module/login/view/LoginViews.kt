package com.xiaojinzi.demo.module.user.module.login.view

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojinzi.demo.module.base.view.compose.AppbarNormalM3
import com.xiaojinzi.demo.module.user.module.login.domain.LoginIntent
import com.xiaojinzi.reactive.template.view.BusinessContentView
import com.xiaojinzi.support.compose.util.clickableNoRipple
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.ktx.toStringItemDto
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(ExperimentalComposeUiApi::class)
@InternalCoroutinesApi
@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun LoginView(
    needInit: Boolean? = null,
    previewDefault: LoginPreviewDefault? = null,
) {
    val context = LocalContext.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    BusinessContentView<LoginViewModel>(
        needInit = needInit,
    ) { vm ->

        val name by vm.nameStateOb.collectAsState(initial = "")
        val isNameError by vm.isNameErrorStateOb.collectAsState(initial = false)
        val password by vm.passwordStateOb.collectAsState(initial = "")
        val isPasswordError by vm.isPasswordErrorStateOb.collectAsState(initial = false)
        val canSubmit by vm.canSubmitStateOb.collectAsState(initial = false)

        Column(
            modifier = Modifier
                .clickableNoRipple {
                    softwareKeyboardController?.hide()
                }
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 38.dp, vertical = 0.dp)
                .nothing(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(
                modifier = Modifier
                    .height(height = 100.dp)
                    .nothing()
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                value = previewDefault?.name ?: name,
                isError = isNameError,
                onValueChange = {
                    vm.nameStateOb.value = it.trim()
                },
                placeholder = {
                    Text(
                        style = TextStyle(
                            fontSize = 14.sp,
                        ),
                        text = "用户名: admin"
                    )
                },
            )

            Spacer(
                modifier = Modifier
                    .height(height = 24.dp)
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                value = previewDefault?.password ?: password,
                isError = isPasswordError,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                ),
                onValueChange = {
                    vm.passwordStateOb.value = it.trim()
                },
                placeholder = {
                    Text(
                        style = TextStyle(
                            fontSize = 14.sp,
                        ),
                        text = "密码: 123",
                    )
                },
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 0.dp, vertical = 26.dp),
                enabled = previewDefault?.canSubmit ?: canSubmit,
                onClick = {
                    vm.addIntent(
                        intent = LoginIntent.Submit(
                            context = context,
                        )
                    )
                },
            ) {
                Text(text = "登录")
            }

        }
    }
}

@InternalCoroutinesApi
@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun LoginViewWrap() {
    Scaffold(
        topBar = {
            AppbarNormalM3(
                title = "登录".toStringItemDto(),
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .nothing(),
        ) {
            LoginView()
        }
    }
}

private data class LoginPreviewDefault(
    val name: String = "admin",
    val password: String = "123",
    val canSubmit: Boolean = true,
)

@InternalCoroutinesApi
@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Preview(
    showBackground = true,
)
@Composable
private fun LoginViewPreview() {
    LoginView(
        needInit = false,
        previewDefault = LoginPreviewDefault()
    )
}