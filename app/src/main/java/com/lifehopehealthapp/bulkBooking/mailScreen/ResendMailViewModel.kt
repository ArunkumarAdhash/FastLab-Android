package com.lifehopehealthapp.bulkBooking.mailScreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.ConfirmOrderResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch


class ResendMailViewModel(
    private val repo: ResendMailModel
) : ViewModel() {
    val Response1: MutableLiveData<Resource<ConfirmOrderResponse>> = MutableLiveData()

    val Response2: MutableLiveData<Resource<ConfirmOrderResponse>> = MutableLiveData()

    fun getOrderConfirm(it: String, value: JsonObject) = viewModelScope.launch {
        Response1.value = repo.confirmOrder(it, value)
    }

    fun getBulkOrderConfirm(it: String, value: JsonObject) = viewModelScope.launch {
        Response2.value = repo.confirmBulkOrder(it, value)
    }
}