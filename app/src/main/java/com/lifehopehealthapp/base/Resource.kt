package com.lifehopehealthapp.base

import com.lifehopehealthapp.ResponseModel.MyError
import com.lifehopehealthapp.ResponseModel.NewError

sealed class Resource<out T> {
    data class Success<out T>(val value: T) : Resource<T>()
    data class GenericError(val code: Int? = null, val error: MyError? = null) : Resource<Nothing>()

    data class GenericNewError(val code: Int? = null, val error: NewError? = null) :
        Resource<Nothing>()
    object NetworkError : Resource<Nothing>()

    /*data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: ResponseBody?
    ) : Resource<Nothing>()*/
}