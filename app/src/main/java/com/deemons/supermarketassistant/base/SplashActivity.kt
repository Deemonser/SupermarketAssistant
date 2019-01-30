package com.deemons.supermarketassistant.base

import android.content.Intent
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import cn.bmob.v3.BmobQuery
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.deemons.supermarketassistant.R
import com.deemons.supermarketassistant.databinding.ActivitySplashBinding
import com.deemons.supermarketassistant.di.component.ActivityComponent
import com.deemons.supermarketassistant.mvp.activity.MainActivity
import com.deemons.supermarketassistant.net.NetService
import com.deemons.supermarketassistant.net.model.GoodsBean
import com.deemons.supermarketassistant.sql.bmob.GoodsModel
import com.deemons.supermarketassistant.sql.bmob.Tmp_Goods
import com.deemons.supermarketassistant.tools.bind
import com.deemons.supermarketassistant.tools.schedulerIO
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SplashActivity : BaseActivity<EPresenter, ActivitySplashBinding>() {

    @Inject
    lateinit var netService: NetService

    val tGoodsQuery by lazy { BmobQuery<Tmp_Goods>() }
    val goodsQuery by lazy { BmobQuery<GoodsModel>() }

    override fun getLayout(): Int = R.layout.activity_splash

    override fun componentInject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun initEventAndData() {


        startAnimation()

//        initAppData()

        Observable.timer(1500, TimeUnit.MILLISECONDS).subscribe { toMain() }.bind(mPresenter.mDisposable)
    }

    private fun startAnimation() {
        val animation =
            ScaleAnimation(1f, 2f, 1f, 2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        animation.duration = 800
        animation.fillAfter = true
        mBinding.splashIv.startAnimation(animation)

        val alphaAnimation = AlphaAnimation(0f, 1f)
        alphaAnimation.duration = 800
        mBinding.splashTv.text = "Powered by Deemons ${AppUtils.getAppVersionName()}© 20019-2020"
        mBinding.splashTv.startAnimation(alphaAnimation)
    }


    var page = 0
    /**
     * 初始化 APP 的数据
     */
    private fun initAppData() {
        var count = 0
        var index = 0

        Observable.just(tGoodsQuery)
            .flatMap { it.setLimit(500).setSkip(page * 500 + 4574).findObjectsObservable(Tmp_Goods::class.java) }
            .doOnNext {
                count = it.size
                LogUtils.d("总计：${count}   page:$page")
            }
            .flatMap { Observable.fromIterable(it) }
            .doOnNext {
                index++
                LogUtils.d("进度：${index / count}")
            }
            .flatMap {
                netService.getGoodsDetail(it.barCode)
                    .map { t: GoodsBean -> Pair(t, it) }
                    .onErrorReturn { e -> Pair(GoodsBean(), it) }
                    .filter { it.first != null && it.first.ean != null }
            }
            .map {

                val model = GoodsModel()
                model.barCode = it.first.ean
                model.name = it.first.name
                model.image = it.first.img
                model.price = it.first.price
                model.standard = it.first.standard
                model.supplier = it.first.supplier
                model.brand = it.first.brand
                model.realPrice = it.second.realPrice.replace("￥", "").replace(",", "").toFloat()
                model.sellingPrice = it.second.sellingPrice.replace("￥", "").replace(",", "").toFloat()
                model.count = it.second.count
                try {
                    model.saveSync()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.doOnError { GoodsModel() }
            .toList()
            .subscribeOn(schedulerIO)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtils.d("完成======================= page:$page")

                page++
                if (page * 500 < 11000) {
                    initAppData()
                }
            }, { it.printStackTrace() })


    }


    fun toMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


}
