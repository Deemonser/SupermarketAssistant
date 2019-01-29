package com.deemons.supermarketassistant.di.component

import com.deemons.supermarketassistant.di.module.AppModule
import com.deemons.supermarketassistant.di.module.NetworkModule
import com.deemons.supermarketassistant.net.api.Api
import dagger.Component
import io.objectbox.BoxStore
import javax.inject.Singleton

/**
 * Created by deemons on 2018/5/3.
 */
@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {

    fun boxStore(): BoxStore

    fun api(): Api

}