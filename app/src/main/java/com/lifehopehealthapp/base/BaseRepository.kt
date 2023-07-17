package com.lifehopehealthapp.base

import android.util.Log
import com.lifehopehealthapp.ResponseModel.MyError
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

abstract class BaseRepository {

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                Log.e("HTTP ERROR", throwable.toString())
                when (throwable) {
                    is IOException -> Resource.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorBody(throwable)
                        Resource.GenericError(code, errorResponse)
                    }
                    else -> {
                        Resource.GenericError(null, null)
                    }
                }
            }
        }
    }

    private fun convertErrorBody(throwable: HttpException): MyError? {
        return try {
            throwable.response()?.errorBody()?.source()?.let {
                val moshiAdapter = Moshi.Builder().build().adapter(MyError::class.java)
                moshiAdapter.fromJson(it)
            }
        } catch (exception: Exception) {
            null
        }
    }
}