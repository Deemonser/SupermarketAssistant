package com.deemons.supermarketassistant.expand

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContextWrapper
import android.support.annotation.StringRes
import com.deemons.supermarketassistant.base.IView
import com.deemons.supermarketassistant.tools.ResUtils
import com.deemons.supermarketassistant.tools._main
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

/**
 * author： deemons
 * date:    2019/1/29
 * desc:    提示相关的辅助类
 */


/**
 *  创建仅提示文案的 dialog
 */
fun Activity.createTipDialog(
    msg: CharSequence,
    @QMUITipDialog.Builder.IconType iconType: Int = QMUITipDialog.Builder.ICON_TYPE_NOTHING
): QMUITipDialog {
    return QMUITipDialog.Builder(this)
        .setIconType(iconType)
        .setTipWord(msg)
        .create()
}


@SuppressLint("CheckResult")
fun Dialog.showTime(during: Long = 2000, dismissListener: () -> Unit? = {}) {
    val activity: RxAppCompatActivity? = when {
        context is RxAppCompatActivity -> context as RxAppCompatActivity
        context is ContextWrapper && (context as ContextWrapper).baseContext is RxAppCompatActivity -> (context as ContextWrapper).baseContext as RxAppCompatActivity
        else -> return
    }


    Observable.timer(during, TimeUnit.MILLISECONDS)
        .compose(activity!!.bindToLifecycle())
        .doOnSubscribe { show() }
        ._main()
        .doFinally {
            if (this.isShowing) {
                dismiss()
            }
        }
        .subscribe({
            if (this.isShowing) {
                dismiss()
            }
            dismissListener()
        }, { it.printStackTrace() })

}


/**
 *  绑定 loading dialog
 *  在订阅时显示，结束时自动消失
 *  在 2s 后可取消弹窗，同时取消订阅
 */
fun <T> Observable<T>.bindLoadingDialog(view: IView?, @StringRes stringRes: Int = 0): Observable<T> {

    val dialog = view?.getContext()?.createTipDialog(
        if (stringRes == 0) "" else ResUtils.getString(stringRes),
        QMUITipDialog.Builder.ICON_TYPE_LOADING
    )
    dialog?.setCancelable(false)
    dialog?.setCanceledOnTouchOutside(false)
    dialog?.showTime(Long.MAX_VALUE)

    Observable.timer(2, TimeUnit.SECONDS)
        .compose(view?.getContext()?.bindToLifecycle())
        .subscribe({
            dialog?.setCancelable(true)
            dialog?.setCanceledOnTouchOutside(true)
        }, { it.printStackTrace() })

    val dialogCancelSubject = BehaviorSubject.create<Boolean>()
    dialog?.setOnCancelListener { dialogCancelSubject.onNext(true) }

    return this.doOnSubscribe { dialog?.show() }
        .doFinally { dialog?.dismiss() }
        .takeUntil(dialogCancelSubject)
}


