package com.xiaojinzi.demo.module.base.spi

import com.xiaojinzi.demo.lib.res.user.UserInfoDto
import com.xiaojinzi.support.annotation.StateHotObservable
import kotlinx.coroutines.flow.Flow

interface UserSpi {

    @StateHotObservable
    val userInfoStateOb: Flow<UserInfoDto?>

    /**
     * 登录
     */
    suspend fun login(name: String, password: String)

}