package com.xiaojinzi.reactive.view

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider

open class BaseAct<VM : BaseViewModel> : AppCompatActivity() {

    /*上下文*/
    protected lateinit var mContext: FragmentActivity

    protected var mViewModel: VM? = null

    protected open fun getViewModelClass(): Class<VM>? {
        return null
    }

    fun requiredViewModel(): VM {
        return mViewModel!!
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        val viewModelClass = getViewModelClass()
        if (viewModelClass != null) {
            mViewModel = ViewModelProvider(this)[viewModelClass]
        }
    }

}