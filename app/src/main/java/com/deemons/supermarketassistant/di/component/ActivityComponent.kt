package com.deemons.supermarketassistant.di.component

import com.deemons.supermarketassistant.di.scope.ActivityScope
import dagger.Component

/**
 * author： deemons
 * date:    2018/4/25
 * desc:
 */
@ActivityScope
@Component(dependencies = [AppComponent::class])
interface ActivityComponent {

}