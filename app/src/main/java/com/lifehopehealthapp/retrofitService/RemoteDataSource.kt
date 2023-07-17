package com.lifehopehealthapp.retrofitService

import com.google.gson.GsonBuilder
import com.lifehopehealthapp.retrofitService.crypto.DecryptionImpl
import com.lifehopehealthapp.retrofitService.crypto.EncryptionImpl
import com.lifehopehealthapp.retrofitService.interceptor.DecryptionInterceptor
import com.lifehopehealthapp.retrofitService.interceptor.EncryptionInterceptor
import com.lifehopehealthapp.utils.NoConnectivityInterceptor
import com.lifehopehealthapp.utils.NullStringToEmptyAdapterFactory
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


val mainModule = module {

    single { RemoteDataSource() }

}

class RemoteDataSource {
    //TODO companion object is a singleton. & its members can be accessed directly via the name of the containing class. thread-safe manner
    companion object {
        //internal const val BASE_URL = "https://mobiletest.lifehopeseniors.com/api/"

        //const val BASE_URL = "https://mobileapi.lifehopeseniors.com/api/"
        const val BASE_URL = "http://3.140.76.244/api/"

      //  const val BASE_URL = "https://mobiledev.lifehopeseniors.com/api/"

        const val BASEURL = "http://api.openweathermap.org/"
        const val GOOGLE_API ="https://maps.googleapis.com/maps/api/geocode/"
        val encryptionInterceptor = EncryptionInterceptor(EncryptionImpl())
        val decryptionInterceptor = DecryptionInterceptor(DecryptionImpl())
        var aClientGet: OkHttpClient.Builder? =null
        var client: OkHttpClient? =null

    }

    fun <Api> BuildAPI(api: Class<Api>): Api {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
        val aClient: OkHttpClient =
            OkHttpClient.Builder().addInterceptor(NoConnectivityInterceptor())
                .addInterceptor(logging)
                .connectTimeout(0, TimeUnit.MINUTES)
                .readTimeout(0, TimeUnit.MINUTES).build()
        val gson =
            GsonBuilder().registerTypeAdapterFactory(NullStringToEmptyAdapterFactory()).create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(aClient)
            .build()
            .create(api)
    }

    fun <Api> BuildGoogleAPI(api: Class<Api>): Api {

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        var aClient: OkHttpClient = OkHttpClient()

        aClient = OkHttpClient.Builder().addInterceptor(NoConnectivityInterceptor())
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10,TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.MINUTES).build()

        val gson =
            GsonBuilder().registerTypeAdapterFactory(NullStringToEmptyAdapterFactory()).create()

        return Retrofit.Builder()
            .baseUrl(GOOGLE_API)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(aClient)
            .build()
            .create(api)
    }

    //TODO Generic class
    fun <Api> buildApi(
        api: Class<Api>
    ): Api {


        //Encryption Interceptor
        /**
         * injection of interceptors to handle encryption and decryption
         */

       /* val encryptionInterceptor = EncryptionInterceptor(EncryptionImpl())
        val decryptionInterceptor = DecryptionInterceptor(DecryptionImpl())*/

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val dispatcher = Dispatcher()
        dispatcher.maxRequests=1
        if (aClientGet==null){
            aClientGet = OkHttpClient.Builder()
            aClientGet!!.addInterceptor(NoConnectivityInterceptor())
                 .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(encryptionInterceptor)
                .dispatcher(dispatcher)
                .cache(null)
                .addInterceptor(decryptionInterceptor)
                .connectTimeout(0, TimeUnit.MINUTES)
                .readTimeout(0, TimeUnit.MINUTES).build()
             client = aClientGet!!.build()
        }


        val gson = GsonBuilder().registerTypeAdapterFactory(NullStringToEmptyAdapterFactory()).create()


       /* mRetrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(aClient)
            .build()
            .create(api)*/
        //return mRetrofit.newBuilder()
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(api)
    }




    //TODO Generic class
    fun <Api> buildApiWOEncry(
        api: Class<Api>
    ): Api {


        //Encryption Interceptor
        /**
         * injection of interceptors to handle encryption and decryption
         */

        //val encryptionInterceptor = EncryptionInterceptor(EncryptionImpl())
        val decryptionInterceptor = DecryptionInterceptor(DecryptionImpl())

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        var aClient: OkHttpClient = OkHttpClient()

        aClient = OkHttpClient.Builder().addInterceptor(NoConnectivityInterceptor())
            .addInterceptor(httpLoggingInterceptor)
            //.addInterceptor(encryptionInterceptor)
            .addInterceptor(decryptionInterceptor)
            .connectTimeout(0, TimeUnit.MINUTES)
            .readTimeout(0, TimeUnit.MINUTES).build()

        val gson =
            GsonBuilder().registerTypeAdapterFactory(NullStringToEmptyAdapterFactory()).create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(aClient)
            .build()
            .create(api)
    }
}

