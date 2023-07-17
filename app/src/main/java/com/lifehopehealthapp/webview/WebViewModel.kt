package com.lifehopehealthapp.webview

import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.DoctorPaymentRequest
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class WebViewModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun confirmOrder(token: String, value: JsonObject) = safeApiCall {
        api.confirmOrder(token, value)
    }

    suspend fun confirmPayment(token: String, value: DoctorPaymentRequest) = safeApiCall {
        api.getDoctorPayment(token, value)
    }

    suspend fun confirmBulkOrder(token: String, value: JsonObject) = safeApiCall {
        api.confirmBulkOrder(token, value)
    }
}