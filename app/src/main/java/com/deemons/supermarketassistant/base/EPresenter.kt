package com.deemons.supermarketassistant.base

import javax.inject.Inject

/**
 * author： deemons
 * date:    2018/4/25
 * desc:
 */
class EPresenter @Inject constructor() : BasePresenter<EPresenter.View>(){

    interface View : IView
}