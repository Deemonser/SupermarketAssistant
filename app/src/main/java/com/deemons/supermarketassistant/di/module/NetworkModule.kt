package com.deemons.supermarketassistant.di.module

import android.os.Build
import com.blankj.utilcode.util.LogUtils
import com.deemons.supermarketassistant.BuildConfig
import com.deemons.supermarketassistant.net.api.Api
import com.deemons.supermarketassistant.net.interceptor.HeaderInterceptor
import com.deemons.supermarketassistant.net.ssl.Tls12SocketFactory
import com.deemons.supermarketassistant.tools.schedulerIO
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext

/**
 * author： deemons
 * date:    2018/8/8
 * desc:   网络层
 */
@Module
class NetworkModule {

    private val CONNECT_TIMEOUT = 30L
    private val READ_TIMEOUT = 30L


    @Singleton
    @Provides
    fun provideGson(): Gson = Gson()


    @Singleton
    @Provides
    fun provideOkHttpClientBuilder(): OkHttpClient.Builder = OkHttpClient.Builder()


    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)


    /**
     * 添加请求头
     *
     */
    @Singleton
    @Provides
    fun provideHeaderInterceptor(): HeaderInterceptor {
        val map = hashMapOf(
            Pair("Connection", "Keep-Alive"),
            Pair("UserModel-Agent", "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1")
        )
        return HeaderInterceptor(map) { url, map -> }
    }


    @Singleton
    @Provides
    fun provideOkHttpClient(
        builder: OkHttpClient.Builder,
        headerInterceptor: HeaderInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)

        setSSL(builder)

        return builder.build()
    }

    // 设置 ssl
    private fun setSSL(builder: OkHttpClient.Builder) {
        if (Build.VERSION.SDK_INT in 16..21) {
            try {
                val sc = SSLContext.getInstance("TLSv1.2")
                sc.init(null, null, null)
                builder.sslSocketFactory(Tls12SocketFactory(sc.socketFactory))

                val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .build()

                val specs = ArrayList<ConnectionSpec>()
                specs.add(cs)
                specs.add(ConnectionSpec.COMPATIBLE_TLS)
                specs.add(ConnectionSpec.CLEARTEXT)

                builder.connectionSpecs(specs)
            } catch (exc: Exception) {
                LogUtils.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc)
            }
        }
    }


    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit.Builder {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(schedulerIO))
    }


    @Singleton
    @Provides
    fun provideApi(retrofitBuilder: Retrofit.Builder): Api {
        return retrofitBuilder.baseUrl(Api.BASE_HOST)
            .build().create(Api::class.java)
    }


    companion object {

    }

}

