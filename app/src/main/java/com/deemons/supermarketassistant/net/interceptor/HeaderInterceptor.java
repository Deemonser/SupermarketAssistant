package com.deemons.supermarketassistant.net.interceptor;

import android.support.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * author： deemons
 * date:    2018/8/8
 * desc:
 */
public class HeaderInterceptor implements Interceptor {

    private HashMap<String, String> headers;
    private HeaderCallback mCallback;

    public HeaderInterceptor(@NonNull HashMap<String, String> headers, @NonNull HeaderCallback headerCallback) {
        mCallback = headerCallback;
        this.headers = headers;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request().newBuilder();

        if (mCallback != null) {
            mCallback.addHeader(chain.request().url().toString(), headers);
        }


        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

        return chain.proceed(builder.build());
    }

    public interface HeaderCallback {
        void addHeader(String url, HashMap map);
    }

}