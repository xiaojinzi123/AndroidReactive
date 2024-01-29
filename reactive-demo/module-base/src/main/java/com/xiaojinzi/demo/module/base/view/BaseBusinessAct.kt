package com.xiaojinzi.demo.module.base.view

import android.os.Bundle
import androidx.annotation.CallSuper
import com.xiaojinzi.component.Component
import com.xiaojinzi.reactive.view.BaseAct
import com.xiaojinzi.reactive.view.BaseViewModel

open class BaseBusinessAct<VM : BaseViewModel> : BaseAct<VM>() {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Component.inject(this)
    }

}