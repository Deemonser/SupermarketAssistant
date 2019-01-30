package com.deemons.supermarketassistant.di.component

import com.deemons.supermarketassistant.base.SplashActivity
import com.deemons.supermarketassistant.di.scope.ActivityScope
import com.deemons.supermarketassistant.mvp.activity.*
import dagger.Component

/**
 * author： deemons
 * date:    2018/4/25
 * desc:
 */
@ActivityScope
@Component(dependencies = [AppComponent::class])
interface ActivityComponent {

    fun inject(scanCodeActivity: ScanCodeActivity)
    fun inject(scanCodeActivity: ScanCodeHistoryActivity)
    fun inject(productDetailActivity: ProductDetailActivity)
    fun inject(pandianActivity: PandianActivity)
    fun inject(splashActivity: SplashActivity)
    fun inject(tongjiActivity: TongjiActivity)

}