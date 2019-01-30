package com.deemons.supermarketassistant.net

import com.deemons.supermarketassistant.net.api.Api
import com.deemons.supermarketassistant.net.model.GoodsBean
import com.deemons.supermarketassistant.net.model.GoodsBean2
import io.reactivex.Observable
import javax.inject.Inject

/**
 * author： deemons
 * date:    2019/1/29
 * desc:
 */
class NetService @Inject constructor(val api: Api) {


    fun getGoodsDetail(barCode: String): Observable<GoodsBean> {
        return api.getGoods("http://app.cjtecc.cn/barcode/eandetail.php", barCode)
    }


    fun getGoodsDetail2(barCode: String): Observable<GoodsBean2> {
        return api.getGoods2("http://www.mxnzp.com/api/barcode/goods/details", barCode, true)
    }

    fun getGoodsDetail3(barCode: String): Observable<String> {
        return api.getProductData(
            "http://webapi.chinatrace.org/api/getProductData",
            barCode,
            "8BF8634ABC27F2A21B66D9148F7D080BE0C2BF712E57A568D071B612397E6392"
        )
    }


}
