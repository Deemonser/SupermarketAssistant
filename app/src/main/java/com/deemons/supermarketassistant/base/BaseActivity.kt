package com.deemons.supermarketassistant.base

import android.content.pm.ActivityInfo
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import com.alibaba.android.arouter.launcher.ARouter
import com.deemons.supermarketassistant.di.component.ActivityComponent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


/**
 * author： deemons
 * date:    2018/4/25
 * desc:
 */
abstract class BaseActivity<P : IPresenter, B : ViewDataBinding> : RxAppCompatActivity(), IView {

    protected lateinit var mBinding: B

    @Inject
    lateinit var mPresenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateBefore()

        mBinding = DataBindingUtil.setContentView(this, getLayout())

        onCreateAfter()

        componentInject((application as App).activityComponent)

        mPresenter.attachView(this)

        ARouter.getInstance().inject(this)

        initEventAndData()

        if (isEventBusRun()) EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isEventBusRun()) EventBus.getDefault().unregister(this)
        mPresenter.onDestroy()
    }

    final override fun getContext(): RxAppCompatActivity = this

    open fun onCreateAfter() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    open fun onCreateBefore() {}

    final override fun showMessage(msg: String) {}

    final override fun showSuccessDialog(msg: String, during: Long, dismissListener: () -> Unit?) {}

    final override fun showErrorDialog(msg: String, during: Long, dismissListener: () -> Unit?) {}

    open fun isEventBusRun(): Boolean = false


    @LayoutRes
    protected abstract fun getLayout(): Int

    abstract fun componentInject(activityComponent: ActivityComponent)

    abstract fun initEventAndData()
}