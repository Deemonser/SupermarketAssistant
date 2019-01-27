package com.deemons.supermarketassistant.di.component

import com.deemons.supermarketassistant.di.module.AppModule
import com.deemons.supermarketassistant.di.module.NetworkModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by deemons on 2018/5/3.
 */
@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {


}