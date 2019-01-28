package com.deemons.supermarketassistant.mvp.presenter

import com.deemons.supermarketassistant.base.BasePresenter
import com.deemons.supermarketassistant.base.IView
import com.deemons.supermarketassistant.mvp.activity.ScanCodeActivity
import com.deemons.supermarketassistant.sql.model.ScanCodeModel
import com.deemons.supermarketassistant.tools.TimeUtils
import com.deemons.supermarketassistant.tools._main
import com.deemons.supermarketassistant.tools.bind
import io.objectbox.BoxStore
import io.reactivex.Observable
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * author： deemons
 * date:    2018/4/25
 * desc:
 */
class ScanCodePresenter @Inject constructor(val box: BoxStore) : BasePresenter<ScanCodePresenter.View>() {

    val scanCodeStore by lazy { box.boxFor(ScanCodeModel::class.java) }

    fun decode(code: String) {
        Observable.just(code)
            .map {
                val model = ScanCodeModel()
                model.code = code
                model.createTime = DateTime.now().millis
                val id = scanCodeStore.put(model)
                ScanCodeActivity.ScanCodeViewBean(id, it, DateTime.now().toString(TimeUtils.TIME_HMS))
            }
            ._main()
            .subscribe({
                mView?.handleDecode(it)
            }, { it.printStackTrace() })
            .bind(mDisposable)
    }

    fun deleteCode(id: Long) {
        scanCodeStore.remove(id)
    }

    interface View : IView {
        fun handleDecode(result: ScanCodeActivity.ScanCodeViewBean)
    }
}