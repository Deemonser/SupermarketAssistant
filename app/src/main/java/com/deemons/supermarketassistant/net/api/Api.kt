package com.deemons.supermarketassistant.net.api

import io.reactivex.Observable
import retrofit2.http.*

/**
 * author： deemons
 * date:    2019/1/27
 * desc:
 */
interface Api {

    companion object {
        const val BASE_HOST = "http://webapi.chinatrace.org/"
    }

    /**
     * http://webapi.chinatrace.org/api/getProductData?productCode=6954306880269&mac=8BF8634ABC27F2A21B66D9148F7D080BE0C2BF712E57A568D071B612397E6392
     */
    @GET("api/getProductData")
    abstract fun getProductData(
        @Query("productCode") productCode: String,
        @Query("mac") mac: String
    ): Observable<String>


    @FormUrlEncoded
    @PUT("v1/account/users/{userId}/nickname")
    abstract fun setUserNickname(
        @Path("userId") userId: Long,
        @Field("nickname") nickName: String
    ): Observable<String>


}
