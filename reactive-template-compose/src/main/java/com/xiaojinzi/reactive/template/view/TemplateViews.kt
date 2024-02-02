package com.xiaojinzi.reactive.template.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.xiaojinzi.reactive.template.R
import com.xiaojinzi.reactive.template.ReactiveTemplate
import com.xiaojinzi.reactive.template.ReactiveTemplateCompose
import com.xiaojinzi.reactive.template.domain.BusinessUseCase
import com.xiaojinzi.reactive.template.domain.CommonUseCase
import com.xiaojinzi.reactive.template.domain.DialogUseCase
import com.xiaojinzi.support.bean.StringItemDto
import com.xiaojinzi.support.compose.util.circleClip
import com.xiaojinzi.support.compose.util.clickableNoRipple
import com.xiaojinzi.support.compose.util.contentWithComposable
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.ktx.toStringItemDto
import com.xiaojinzi.support.ktx.tryFinishActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun TemplateAlertDialog(
    cancelText: StringItemDto? = "取消".toStringItemDto(),
    confirmText: StringItemDto? = "确认".toStringItemDto(),
    title: StringItemDto? = null,
    text: StringItemDto? = null,
    onDismissClick: () -> Unit = {},
    onConfirmClick: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismissClick.invoke() },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .nothing(),
            contentAlignment = Alignment.Center
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.85F)
                    .wrapContentHeight()
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                    )
                    .padding(horizontal = 24.dp)
                    .nothing(),
            ) {
                val (titleText, content, row) = createRefs()

                Text(
                    modifier = Modifier
                        .constrainAs(ref = titleText) {
                            this.visibility = if (title == null) {
                                Visibility.Gone
                            } else {
                                Visibility.Visible
                            }
                            this.centerHorizontallyTo(other = parent)
                            this.top.linkTo(anchor = parent.top, margin = 30.dp)
                        }
                        .nothing(),
                    text = title?.contentWithComposable().orEmpty(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    textAlign = TextAlign.Center,
                )
                Text(
                    modifier = Modifier
                        .constrainAs(content) {
                            if (title == null) {
                                top.linkTo(parent.top, margin = 32.dp)
                            } else {
                                top.linkTo(titleText.bottom, margin = 16.dp)
                            }
                            width = Dimension.fillToConstraints
                            start.linkTo(parent.start, margin = 0.dp)
                            end.linkTo(parent.end, margin = 0.dp)
                        }
                        .nothing(),
                    text = text?.contentWithComposable().orEmpty(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    textAlign = TextAlign.Center,
                )

                Row(
                    modifier = Modifier
                        .constrainAs(row) {
                            width = Dimension.fillToConstraints
                            start.linkTo(parent.start, margin = 0.dp)
                            end.linkTo(parent.end, margin = 0.dp)
                            top.linkTo(
                                content.bottom, margin = 30.dp
                            )
                            bottom.linkTo(
                                parent.bottom, margin = 24.dp,
                            )
                        }
                        .nothing(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (cancelText != null) {
                        Text(
                            modifier = Modifier
                                .weight(1F)
                                .circleClip()
                                .background(color = MaterialTheme.colorScheme.primaryContainer)
                                .clickable {
                                    onDismissClick()
                                }
                                .padding(vertical = 10.dp)
                                .nothing(),
                            text = cancelText.contentWithComposable(),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(
                            modifier = Modifier
                                .width(24.dp)
                                .nothing()
                        )
                    }
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .circleClip()
                            .background(color = MaterialTheme.colorScheme.primary)
                            .clickable {
                                onConfirmClick()
                            }
                            .padding(vertical = 10.dp)
                            .nothing(),
                        text = (confirmText
                            ?: "确认".toStringItemDto()).contentWithComposable(),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onPrimary,
                        ),
                        textAlign = TextAlign.Center,
                    )

                }


            }
        }

    }
}

@Composable
fun BoxScope.TemplateInitView(
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.res_loading1)
    )
    LottieAnimation(
        modifier = Modifier
            .size(60.dp)
            .nothing(),
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
}

@Composable
fun BoxScope.TemplateErrorView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nothing(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.res_error1)
        )
        LottieAnimation(
            modifier = Modifier
                .width(200.dp)
                .aspectRatio(ratio = 1f)
                .nothing(),
            composition = composition,
            iterations = LottieConstants.IterateForever,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "页面开小差了, 点击空白处重试",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onBackground,
            ),
        )
    }
}

@Composable
fun TemplateLoadingView() {
    Dialog(
        onDismissRequest = {
        },
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.res_loading1)
        )
        LottieAnimation(
            modifier = Modifier
                .size(60.dp)
                .nothing(),
            composition = composition,
            iterations = LottieConstants.IterateForever,
        )
    }
}

@Composable
inline fun <reified VM : ViewModel> BusinessContentView(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .nothing(),
    // null 表示走配置
    needInit: Boolean? = null,
    contentAlignment: Alignment = Alignment.Center,
    noinline content: @Composable BoxScope.(vm: VM) -> Unit,
) {
    val needInitReal = needInit ?: ReactiveTemplateCompose.enableInit
    val context = LocalContext.current
    val vm: VM = viewModel()
    val viewState = when (vm) {
        is BusinessUseCase -> {
            val pageInitState by vm.pageInitStateObservableDto.collectAsState(initial = BusinessUseCase.ViewState.STATE_INIT)
            pageInitState
        }

        else -> {
            BusinessUseCase.ViewState.STATE_SUCCESS
        }
    }
    val dialogContent = when (vm) {
        is DialogUseCase -> {
            val dialogContent by vm.confirmDialogStateOb.collectAsState(initial = null)
            dialogContent
        }

        else -> {
            null
        }
    }
    var isLoading by remember {
        mutableStateOf(value = false)
    }
    if (isLoading) {
        ReactiveTemplateCompose.loadingView.invoke()
    }
    // 对 ui 控制的一些监听
    LaunchedEffect(key1 = Unit) {
        when (vm) {
            is CommonUseCase -> {
                vm.activityFinishEventOb
                    .onEach {
                        context.tryFinishActivity()
                    }
                    .launchIn(scope = this)
                vm.isLoadingOb
                    .onEach { isShow ->
                        isLoading = isShow
                    }
                    .launchIn(scope = this)
                vm.tipEventOb
                    .onEach { content ->
                        ReactiveTemplate.tipHandle.invoke(content)
                    }
                    .launchIn(scope = this)
            }
        }
    }
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {
        if (needInitReal) {
            when (viewState) {
                BusinessUseCase.ViewState.STATE_INIT, BusinessUseCase.ViewState.STATE_LOADING -> {
                    ReactiveTemplateCompose.initView.invoke(this)
                }

                BusinessUseCase.ViewState.STATE_ERROR -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickableNoRipple {
                                (vm as? BusinessUseCase)?.retryInit()
                            }
                            .nothing(),
                        contentAlignment = Alignment.Center,
                    ) {
                        ReactiveTemplateCompose.errorView.invoke(this)
                    }
                }

                BusinessUseCase.ViewState.STATE_SUCCESS -> {
                    content(vm)
                }
            }
        } else {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                content(vm)
            }
        }
    }
    dialogContent?.let {
        ReactiveTemplateCompose.alertDialogView.invoke(
            dialogContent.title,
            dialogContent.content,
            dialogContent.negative,
            dialogContent.positive,
            {
                (vm as? BusinessUseCase)?.confirmDialogResultEventOb?.tryEmit(
                    value = DialogUseCase.ConfirmDialogResultType.CANCEL
                )
            }, {
                (vm as? BusinessUseCase)?.confirmDialogResultEventOb?.tryEmit(
                    value = DialogUseCase.ConfirmDialogResultType.CONFIRM
                )
            }
        )
    }
}