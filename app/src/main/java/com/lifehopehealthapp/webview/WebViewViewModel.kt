package com.lifehopehealthapp.webview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.ConfirmOrderResponse
import com.lifehopehealthapp.ResponseModel.DoctorPaymentRequest
import com.lifehopehealthapp.ResponseModel.DoctorPaymentResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class WebViewViewModel(
    private val repo: WebViewModel
) : ViewModel() {
    /*suspend fun getKey(): String? {
        val token = runBlocking { repo.preferences.authToken.first() }
        return token
    }*/

    val Response1: MutableLiveData<Resource<ConfirmOrderResponse>> = MutableLiveData()
    val Response2: MutableLiveData<Resource<ConfirmOrderResponse>> = MutableLiveData()

    fun getOrderConfirm(it: String, value: JsonObject) = viewModelScope.launch {
        Response1.value = repo.confirmOrder(it, value)
    }

    val Response: MutableLiveData<Resource<DoctorPaymentResponse>> = MutableLiveData()

    fun getPaymentConfirm(it: String, value: DoctorPaymentRequest) = viewModelScope.launch {
        Response.value = repo.confirmPayment(it, value)
    }


    fun getBulkOrderConfirm(it: String, value: JsonObject) = viewModelScope.launch {
        Response2.value = repo.confirmBulkOrder(it, value)
    }
}