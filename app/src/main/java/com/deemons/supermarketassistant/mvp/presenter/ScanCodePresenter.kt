package com.deemons.supermarketassistant.mvp.presenter

import android.text.TextUtils
import cn.bmob.v3.BmobQuery
import com.deemons.supermarketassistant.base.BasePresenter
import com.deemons.supermarketassistant.base.IView
import com.deemons.supermarketassistant.expand.bindLoadingDialog
import com.deemons.supermarketassistant.mvp.activity.ScanCodeActivity
import com.deemons.supermarketassistant.net.NetService
import com.deemons.supermarketassistant.sql.bmob.GoodsModel
import com.deemons.supermarketassistant.sql.bmob.ScanCodeModel
import com.deemons.supermarketassistant.tools.*
import io.reactivex.Observable
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * author： deemons
 * date:    2018/4/25
 * desc:
 */
class ScanCodePresenter @Inject constructor(val netService: NetService) :
    BasePresenter<ScanCodePresenter.View>() {

    val bmobQuery by lazy { BmobQuery<GoodsModel>() }

    fun decode(code: String) {

        Observable.just(code)
            .flatMap { bmobQuery.addWhereEqualTo("barCode", code).findObjectsObservable(GoodsModel::class.java) }
            .onErrorReturn { mutableListOf() }
            ._io()
            .flatMap {
                if (it.size == 0) {
                    netService.getGoodsDetail2(code)
                        .map { it.data.convert() }
                        .doOnNext { it.saveSync() }
                        .doOnError {
                            val model = GoodsModel()
                            model.barCode = code
                            model.name = ""
                            model.image = ""
                            model.price = ""
                            model
                        }
                } else {
                    Observable.just(it.first())
                }
            }
            .map {
                val model = ScanCodeModel()
                model.code = code
                model.createTime = DateTime.now().millis
                val id = model.saveSync()

                ScanCodeActivity.ScanCodeViewBean(
                    id,
                    it.barCode,
                    it.image,
                    it.name,
                    if (TextUtils.isEmpty(it.price)) "" else it.price,
                    DateTime.now().toString(TimeUtils.TIME_HMS)
                )
            }
            .io_main()
            .bindLoadingDialog(mView)
            .subscribe({
                mView?.handleDecode(it)
            }, { it.printStackTrace() })
            .bind(mDisposable)
    }

    fun deleteCode(id: String) {
        BmobQuery<ScanCodeModel>().getObjectObservable(ScanCodeModel::class.java, id)
            .flatMap { it.deleteObservable(it.objectId) }
            .subscribeSafe {}
            .bind(mDisposable)
    }


    interface View : IView {
        fun handleDecode(result: ScanCodeActivity.ScanCodeViewBean)
    }
}