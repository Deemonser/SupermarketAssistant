package com.deemons.supermarketassistant.di.module

import com.deemons.supermarketassistant.base.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * author： deemons
 * date:    2018/4/25
 * desc:    提供全局依赖
 */
@Module
class AppModule constructor(private val app: App) {

    @Singleton
    @Provides
    fun provideApplication(): App = app




}


