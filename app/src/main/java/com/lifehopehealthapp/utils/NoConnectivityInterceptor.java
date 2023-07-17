package com.lifehopehealthapp.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NoConnectivityInterceptor implements Interceptor {

    public NoConnectivityInterceptor() {

    }

    /**
     * connection intercept method
     *
     * @param chain Chain data
     * @return response value
     * @throws IOException error
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }

    /**
     * Check connection
     */
    public class NoConnectivityException extends IOException {

        @Override
        public String getMessage() {
            return "Connect Internet";
        }
    }
}
