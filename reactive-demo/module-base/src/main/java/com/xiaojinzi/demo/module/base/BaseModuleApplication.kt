package com.xiaojinzi.demo.module.base

import android.app.Application
import com.xiaojinzi.component.anno.ModuleAppAnno
import com.xiaojinzi.component.application.IApplicationLifecycle

@ModuleAppAnno
class BaseModuleApplication: IApplicationLifecycle {

    override fun onCreate(app: Application) {
    }

    override fun onDestroy() {
    }

}