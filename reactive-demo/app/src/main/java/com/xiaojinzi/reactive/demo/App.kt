package com.xiaojinzi.reactive.demo

import android.app.Application
import android.content.Context
import com.xiaojinzi.component.Component
import com.xiaojinzi.component.Config
import com.xiaojinzi.reactive.template.ReactiveTemplate
import com.xiaojinzi.reactive.template.ReactiveTemplateCompose
import com.xiaojinzi.support.init.AppInstance
import com.xiaojinzi.support.ktx.LogSupport

class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        AppInstance.app = this
        LogSupport.logAble = BuildConfig.DEBUG
    }

    override fun onCreate() {
        super.onCreate()

        // 初始化组件化
        Component.init(
            application = this,
            isDebug = BuildConfig.DEBUG,
            config = Config.Builder()
                .initRouterAsync(true)
                .errorCheck(true)
                .optimizeInit(true)
                .autoRegisterModule(true)
                .build(),
        )

        ReactiveTemplate.config(
            isDebug = BuildConfig.DEBUG,
        )
        ReactiveTemplateCompose.config()

    }

}