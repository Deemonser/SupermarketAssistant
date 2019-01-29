package com.deemons.supermarketassistant.base

import io.reactivex.disposables.CompositeDisposable

/**
 * author： deemons
 * date:    2018/4/25
 * desc:
 */
open class BasePresenter<V : IView> : IPresenter {

    var mView: V? = null

    private val mDisposableDelegate = lazy { CompositeDisposable() }
    val mDisposable by mDisposableDelegate

    @Suppress("UNCHECKED_CAST")
    override fun attachView(view: IView) {
        mView = view as V
    }


    override fun onDestroy() {
        if (mDisposableDelegate.isInitialized() && !mDisposable.isDisposed) {
            mDisposable.dispose()
        }
    }

}