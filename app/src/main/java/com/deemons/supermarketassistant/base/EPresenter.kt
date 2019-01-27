package com.followme.basiclib.mvp.base

import com.deemons.supermarketassistant.base.IView
import com.deemons.supermarketassistant.base.BasePresenter
import javax.inject.Inject

/**
 * author： deemons
 * date:    2018/4/25
 * desc:
 */
class EPresenter @Inject constructor() : BasePresenter<EPresenter.View>(){

    interface View : IView
}