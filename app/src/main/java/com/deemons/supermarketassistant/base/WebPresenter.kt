package com.deemons.supermarketassistant.base

import com.google.gson.Gson
import javax.inject.Inject

/**
 * author： deemons
 * date:    2018/4/25
 * desc:
 */
open class WebPresenter<V : WebPresenter.View> @Inject constructor(val mGson: Gson) : BasePresenter<V>() {



    interface View : IView {
        fun loadUrl(url: String)

        fun setMainTitle(title: String)

    }

}