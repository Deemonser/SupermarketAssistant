package com.deemons.supermarketassistant.base

import android.app.Application
import com.deemons.supermarketassistant.di.component.DaggerActivityComponent
import com.deemons.supermarketassistant.di.component.DaggerAppComponent
import com.deemons.supermarketassistant.di.module.AppModule
import com.deemons.supermarketassistant.di.module.NetworkModule
import com.deemons.supermarketassistant.tools.InitUtils

/**
 * author： deemons
 * date:    2019/1/27
 * desc:
 */
class App : Application() {


    /**
     * 依赖注入
     */
    val appComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .networkModule(NetworkModule())
            .build()
    }


    val activityComponent by lazy {
        DaggerActivityComponent.builder()
            .appComponent(appComponent)
            .build()
    }


    override fun onCreate() {
        super.onCreate()

        InitUtils.init(this)
    }
}
