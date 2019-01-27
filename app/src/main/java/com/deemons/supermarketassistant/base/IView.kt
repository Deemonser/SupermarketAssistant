package com.deemons.supermarketassistant.base

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

/**
 * author： deemons
 * date:    2018/4/25
 * desc:
 */
interface IView {
    fun getContext(): RxAppCompatActivity

    fun showMessage(msg: String)

    fun showSuccessDialog(msg: String, during: Long = 1500, dismissListener: () -> Unit?)

    fun showErrorDialog(msg: String, during: Long = 1500, dismissListener: () -> Unit?)

}
