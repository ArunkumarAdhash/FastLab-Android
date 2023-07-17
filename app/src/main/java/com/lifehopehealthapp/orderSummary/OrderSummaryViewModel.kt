package com.lifehopehealthapp.orderSummary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.ResponseModel.OrderBookingResponse
import kotlinx.coroutines.launch

class OrderSummaryViewModel(
    private val repo: OrderSummaryModel
) : ViewModel() {

    val Response1: MutableLiveData<Resource<OrderBookingResponse>> = MutableLiveData()

    fun getOrderBooking(it: String, value: JsonObject) = viewModelScope.launch {
        Response1.value = repo.orderBookinge(it, value)
    }

}