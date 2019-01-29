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
import com.vondear.rxtool.view.RxToast
import io.reactivex.Observable
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * author： deemons
 * date:    2018/4/25
 * desc:
 */
class ProductDetailPresenter @Inject constructor(val netService: NetService) :
    BasePresenter<ProductDetailPresenter.View>() {

    val bmobQuery by lazy { BmobQuery<GoodsModel>() }

    fun getData(code: String) {
        Observable.just(code)
            .flatMap { bmobQuery.addWhereEqualTo("barCode", code).findObjectsObservable(GoodsModel::class.java) }
            .filter { it.size == 1 }
            .map { it.first() }
            .io_main()
            .bindLoadingDialog(mView)
            .subscribeSafe {
                mView?.setViewData(it)
            }
            .bind(mDisposable)
    }


    fun saveData(barCode: String, realPrice: Float, sellingPrice: Float, count: Int) {
        Observable.just(barCode)
            .flatMap { bmobQuery.addWhereEqualTo("barCode", barCode).findObjectsObservable(GoodsModel::class.java) }
            .filter { it.size == 1 }
            .map { it.first() }
            .doOnNext {
                it.realPrice = realPrice
                it.sellingPrice = sellingPrice
                it.count = count

            }
            .flatMap { it.saveObservable() }
            .io_main()
            .bindLoadingDialog(mView)
            .subscribeSafe {
                RxToast.showToast("保存成功")
            }
            .bind(mDisposable)
    }


    interface View : IView {
        fun setViewData(data: GoodsModel)
    }
}