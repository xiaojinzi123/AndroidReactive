package com.xiaojinzi.demo.module.base.view.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojinzi.support.bean.StringItemDto
import com.xiaojinzi.support.compose.util.contentWithComposable
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.ktx.toStringItemDto
import com.xiaojinzi.support.ktx.tryFinishActivity

@Composable
fun AppbarNormalM3(
    backIcon: ImageVector = Icons.Filled.ArrowBack,
    backClickListener: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    title: StringItemDto? = null,
    titleTextAlign: TextAlign = TextAlign.Center,
    @DrawableRes
    menu1IconRsd: Int? = null,
    menu1IconTint: Color = contentColor,
    menu1TextValue: StringItemDto? = null,
    menu1TextStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Normal,
    ),
    menu1ClickListener: (() -> Unit)? = null,
    @DrawableRes
    menu2IconRsd: Int? = null,
    menu2IconTint: Color = contentColor,
    menu2TextValue: StringItemDto? = null,
    menu2TextStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Normal,
    ),
    menu2ClickListener: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val titleStr = title?.contentWithComposable()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = containerColor)
            .statusBarsPadding()
            .nothing(),
    ) {
        IconButton(
            modifier = Modifier
                .align(alignment = Alignment.CenterStart)
                .nothing(),
            onClick = {
                if (backClickListener == null) {
                    context.tryFinishActivity()
                } else {
                    backClickListener.invoke()
                }
            },
        ) {
            Icon(
                painter = rememberVectorPainter(image = backIcon),
                contentDescription = null,
                tint = contentColor,
            )
        }
        Row(
            modifier = Modifier
                .align(alignment = Alignment.CenterEnd)
                .padding(end = 8.dp)
                .nothing(),
        ) {
            menu2TextValue?.let {
                TextButton(onClick = {
                    menu2ClickListener?.invoke()
                }) {
                    Text(
                        text = menu2TextValue.contentWithComposable(),
                        style = menu2TextStyle,
                    )
                }
            }
            if (menu2IconRsd != null) {
                IconButton(onClick = {
                    menu2ClickListener?.invoke()
                }) {
                    Icon(
                        painter = painterResource(id = menu2IconRsd),
                        contentDescription = null,
                        tint = menu2IconTint,
                    )
                }
            }
            menu1TextValue?.let {
                TextButton(onClick = {
                    menu1ClickListener?.invoke()
                }) {
                    Text(
                        text = menu1TextValue.contentWithComposable(),
                        style = menu1TextStyle,
                    )
                }
            }
            if (menu1IconRsd != null) {
                IconButton(onClick = {
                    menu1ClickListener?.invoke()
                }) {
                    Icon(
                        painter = painterResource(id = menu1IconRsd),
                        contentDescription = null,
                        tint = menu1IconTint,
                    )
                }
            }
        }

        titleStr?.run {
            Text(
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .wrapContentSize()
                    .nothing(),
                text = this,
                textAlign = titleTextAlign,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = contentColor,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }

    }
}

@Preview
@Composable
private fun AppbarNormalPreviewM3() {
    AppbarNormalM3(
        title = "测试".toStringItemDto(),
    )
}