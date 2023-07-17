package com.lifehopehealthapp.retrofitService.interceptor;

import android.util.Log;

import com.google.gson.Gson;
import com.lifehopehealthapp.retrofitService.crypto.CryptoStrategy;
import com.lifehopehealthapp.ResponseModel.APIRequest;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class EncryptionInterceptor implements Interceptor {
    private static final String TAG = EncryptionInterceptor.class.getSimpleName();
    private CryptoStrategy mEncryptionStrategy = null;

    public EncryptionInterceptor(CryptoStrategy mEncryptionStrategy) {
        this.mEncryptionStrategy = mEncryptionStrategy;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        String encryptedBody = "";

        Request request = chain.request();
        RequestBody rawBody = request.body();
        Buffer buffer = new Buffer();
        if (rawBody != null) {
            rawBody.writeTo(buffer);
        }
        String strOldBody = buffer.readUtf8();

        MediaType mediaType = MediaType.parse("application/json");
        try {
            encryptedBody = mEncryptionStrategy.encrypt(strOldBody);
            APIRequest apiRequest = new APIRequest();
            apiRequest.setType(encryptedBody);
            Gson gson = new Gson();
            String json = "";
            json = gson.toJson(apiRequest);
            Log.e("Model-->", json);
            RequestBody body = RequestBody.create(mediaType, json);
            request = request.
                    newBuilder()
                    .header("Content-Type", body.contentType().toString())
                    .header("Content-Length", String.valueOf(body.contentLength()))
                    .method(request.method(), body).build();
            Log.e("encrypted BODY=> %s", encryptedBody);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return chain.proceed(request);
    }
}