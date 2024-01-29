package com.xiaojinzi.demo.module.user.spi

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.demo.lib.res.user.UserInfoDto
import com.xiaojinzi.demo.module.base.spi.UserSpi
import com.xiaojinzi.reactive.template.support.CommonBusinessException
import com.xiaojinzi.support.ktx.MutableSharedStateFlow

@ServiceAnno(UserSpi::class)
class UserSpiImpl : UserSpi {

    override val userInfoStateOb: MutableSharedStateFlow<UserInfoDto?> = MutableSharedStateFlow(
        initValue = null,
    )

    override suspend fun login(name: String, password: String) {
        if ("admin" != name || "123" != password) {
            throw CommonBusinessException(
                message = "账号或者密码错误",
            )
        }
        userInfoStateOb.emit(
            value = UserInfoDto(
                userName = name,
            )
        )
    }

}