package com.deemons.supermarketassistant.di.component

import com.deemons.supermarketassistant.di.scope.FragmentScope
import dagger.Component

/**
 * author： deemons
 * date:    2018/4/25
 * desc:
 */
@FragmentScope
@Component(dependencies = [ActivityComponent::class])
interface FragmentComponent {


}