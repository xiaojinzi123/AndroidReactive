package com.xiaojinzi.demo.module.base.support

import com.xiaojinzi.component.impl.service.ServiceManager
import com.xiaojinzi.demo.module.base.spi.UserSpi

object AppServices {

    val userSpi: UserSpi?
        get() = ServiceManager.get(tClass = UserSpi::class)

}