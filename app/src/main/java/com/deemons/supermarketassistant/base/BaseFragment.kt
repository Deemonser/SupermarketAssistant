//package com.deemons.supermarketassistant.base
//
//import android.databinding.DataBindingUtil
//import android.databinding.ViewDataBinding
//import android.os.Bundle
//import android.support.annotation.LayoutRes
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.followme.basiclib.expand.qmui.createTipDialog
//import com.followme.basiclib.expand.qmui.showTime
//import com.deemons.supermarketassistant.base.IPresenter
//import com.deemons.supermarketassistant.base.IView
//import com.followme.basiclib.utils.ToastUtils
//import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
//import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
//
///**
// * author： deemons
// * date:    2018/4/26
// * desc:
// */
//abstract class WFragment<P : IPresenter, B : ViewDataBinding> : BaseFragment<P>(), IView {
//
//    var mRootView: View? = null
//    lateinit var mBinding: B
//
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?): View? {
//        mRootView = initView(inflater, container)
//        return mRootView
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//        initComponent()
//
//        initPresenter()
//
//        initEventAndData()
//    }
//
//
//
//    override fun showMessage(msg: String) {
//        ToastUtils.show(msg)
//    }
//    override fun showSuccessDialog(msg: String, during: Long, dismissListener: () -> Unit?) {
//        mActivity.createTipDialog(msg, QMUITipDialog.Builder.ICON_TYPE_SUCCESS).showTime(during,
//            dismissListener)
//    }
//
//    override fun showErrorDialog(msg: String, during: Long, dismissListener: () -> Unit?) {
//        mActivity.createTipDialog(msg, QMUITipDialog.Builder.ICON_TYPE_FAIL).showTime(during,
//            dismissListener)
//    }
//
//
//    override fun isEventBusRun(): Boolean {
//        return false
//    }
//
//    override fun generatePresenter(): P? {
//        return null
//    }
//
//    open fun initView(inflater: LayoutInflater, container: ViewGroup?): View {
//        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
//        return mBinding.root
//    }
//
//    private fun initPresenter() {
//        presenter?.attachView(this)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mRootView = null
//    }
//
//    @LayoutRes
//    protected abstract fun getLayoutId(): Int
//
//    abstract fun initComponent()
//
//    abstract fun initEventAndData()
//
//    override fun getContext(): RxAppCompatActivity = mActivity
//
//}