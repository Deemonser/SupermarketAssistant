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
