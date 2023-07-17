package com.lifehopehealthapp.retrofitService.interceptor;

import android.app.Application;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lifehopehealthapp.LifeHopeApplication;
import com.lifehopehealthapp.retrofitService.crypto.CryptoStrategy;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DecryptionInterceptor implements Interceptor {

    private final CryptoStrategy mDecryptionStrategy;

    public DecryptionInterceptor(CryptoStrategy mDecryptionStrategy) {
        this.mDecryptionStrategy = mDecryptionStrategy;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());


           /*if (response.isSuccessful()) {
        }*/
        Response.Builder newResponse = response.newBuilder();
        String contentType = response.header("Content-Type");
        if (TextUtils.isEmpty(contentType)) contentType = "application/json";
//            InputStream cryptedStream = response.body().byteStream();
        String responseStr = Objects.requireNonNull(response.body()).string();


        String decryptedString = null;
        if (mDecryptionStrategy != null) {
            try {
                decryptedString = mDecryptionStrategy.decrypt(responseStr);
                Log.e("Decrypted BODY=> %s", ""+decryptedString);
                Log.e("Response string => %s",""+ responseStr);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            throw new IllegalArgumentException("No decryption strategy!");
        }

        if(response.code()==500){
            newResponse.body(ResponseBody.create(MediaType.parse(contentType), String.valueOf(response.code())));
        }else {
            if (decryptedString != null) {
                if (contentType != null) {
                    newResponse.body(ResponseBody.create(MediaType.parse(contentType), decryptedString));
                }
            }
        }

        Log.e("","qrerror1--->"+ newResponse.build());
        return newResponse.build();

        //return response;
    }


}