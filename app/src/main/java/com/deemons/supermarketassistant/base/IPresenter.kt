package com.deemons.supermarketassistant.base

/**
 * author： deemons
 * date:    2018/4/25
 * desc:
 */
interface IPresenter {
    fun attachView(view: IView)
    fun onDestroy()
}