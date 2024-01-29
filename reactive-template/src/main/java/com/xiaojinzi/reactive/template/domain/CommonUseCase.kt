package com.xiaojinzi.reactive.template.domain

import android.widget.Toast
import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.xiaojinzi.reactive.domain.BaseUseCase
import com.xiaojinzi.reactive.domain.BaseUseCaseImpl
import com.xiaojinzi.reactive.template.support.CommonBusinessException
import com.xiaojinzi.support.annotation.PublishHotObservable
import com.xiaojinzi.support.annotation.StateHotObservable
import com.xiaojinzi.support.bean.StringItemDto
import com.xiaojinzi.support.ktx.launchIgnoreError
import com.xiaojinzi.support.ktx.toStringItemDto
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

interface DialogUseCase : BaseUseCase {

    /**
     * 对话框的返回类型
     */
    enum class ConfirmDialogResultType {
        CONFIRM,
        CANCEL,
    }

    @Keep
    data class ConfirmDialogModel(
        val title: StringItemDto? = "提示".toStringItemDto(),
        val content: StringItemDto? = null,
        val negative: StringItemDto? = "取消".toStringItemDto(),
        val positive: StringItemDto? = "确定".toStringItemDto(),
    )

    /**
     * 显示确认对话框的
     */
    @StateHotObservable
    val confirmDialogStateOb: MutableStateFlow<ConfirmDialogModel?>

    /**
     * 确认的事件
     */
    @PublishHotObservable
    val confirmDialogResultEventOb: MutableSharedFlow<ConfirmDialogResultType>

    suspend fun confirmDialog(
        title: StringItemDto? = null,
        content: StringItemDto,
        negative: StringItemDto? = "取消".toStringItemDto(),
        positive: StringItemDto? = "确认".toStringItemDto(),
    ): ConfirmDialogResultType {
        // 显示对话框
        confirmDialogStateOb.emit(
            value = ConfirmDialogModel(
                title = title,
                content = content,
                negative = negative,
                positive = positive,
            )
        )
        return confirmDialogResultEventOb.first().apply {
            confirmDialogStateOb.emit(
                value = null,
            )
        }
    }

    suspend fun confirmDialogOrError(
        title: StringItemDto? = null,
        content: StringItemDto,
        negative: StringItemDto? = "取消".toStringItemDto(),
        positive: StringItemDto? = "确认".toStringItemDto(),
    ) {
        confirmDialog(
            title = title,
            content = content,
            negative = negative,
            positive = positive,
        ).apply {
            if (this != ConfirmDialogResultType.CONFIRM) {
                throw CommonBusinessException()
            }
        }
    }

    fun postConfirmDialog(
        title: StringItemDto? = null,
        content: StringItemDto,
        negative: StringItemDto? = null,
        positive: StringItemDto? = null,
    )

}

class DialogUseCaseImpl : BaseUseCaseImpl(), DialogUseCase {

    override val confirmDialogStateOb: MutableStateFlow<DialogUseCase.ConfirmDialogModel?> =
        MutableStateFlow(value = null)

    override val confirmDialogResultEventOb: MutableSharedFlow<DialogUseCase.ConfirmDialogResultType> =
        MutableSharedFlow(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    override fun postConfirmDialog(
        title: StringItemDto?,
        content: StringItemDto,
        negative: StringItemDto?,
        positive: StringItemDto?
    ) {
        scope.launchIgnoreError {
            confirmDialogStateOb.emit(
                value = DialogUseCase.ConfirmDialogModel(
                    title = title,
                    content = content,
                    negative = negative,
                    positive = positive,
                )
            )
        }
    }

}

/**
 * 常用的一个业务 UseCase
 * 比如 toast 一个消息, 显示一个 loading, 等等
 */
interface CommonUseCase : BaseUseCase, DialogUseCase {

    enum class TipType {
        Toast
    }

    @Keep
    data class TipBean(
        val type: TipType = TipType.Toast,
        val toastLength: Int = Toast.LENGTH_SHORT,
        val content: StringItemDto,
    )

    @StateHotObservable
    val isLoadingOb: Flow<Boolean>

    @PublishHotObservable
    val tipEventOb: Flow<TipBean>

    @PublishHotObservable
    val activityFinishEventOb: Flow<Unit>

    /**
     * 显示 loading
     */
    fun showLoading()

    /**
     * 隐藏 loading
     */
    fun hideLoading()

    /**
     * 提示
     */
    fun tip(value: TipBean)

    fun toast(@StringRes contentResId: Int, toastLength: Int = Toast.LENGTH_SHORT) {
        tip(
            TipBean(
                type = TipType.Toast,
                content = contentResId.toStringItemDto(),
                toastLength = toastLength,
            )
        )
    }

    fun toast(content: String, toastLength: Int = Toast.LENGTH_SHORT) {
        tip(
            TipBean(
                type = TipType.Toast,
                content = content.toStringItemDto(),
                toastLength = toastLength,
            )
        )
    }

    /**
     * 投递界面销毁的事件
     */
    fun postActivityFinishEvent()

}

/**
 * 不能设置为 open, 最好不要继承来用
 */
class CommonUseCaseImpl(
    private val dialogUseCase: DialogUseCase = DialogUseCaseImpl(),
) : BaseUseCaseImpl(),
    CommonUseCase,
    DialogUseCase by dialogUseCase {

    override val isLoadingOb: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val tipEventOb: MutableSharedFlow<CommonUseCase.TipBean> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val activityFinishEventOb: MutableSharedFlow<Unit> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun showLoading() {
        isLoadingOb.value = true
    }

    override fun hideLoading() {
        isLoadingOb.value = false
    }

    override fun tip(value: CommonUseCase.TipBean) {
        tipEventOb.tryEmit(value)
    }

    override fun postActivityFinishEvent() {
        activityFinishEventOb.tryEmit(Unit)
    }

    override fun destroy() {
        super.destroy()
        dialogUseCase.destroy()
    }

}
