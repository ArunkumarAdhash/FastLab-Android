package com.lifehopehealthapp.bulkBooking.orderList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.OrderBookingResponse
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class BulkBookingOrderListViewModel(
    private val repo: BulkBookingOrderListModel
) : ViewModel() {
    var Response1: SingleLiveEvent<Resource<OrderBookingResponse>>? = null

    init {
        Response1 = SingleLiveEvent()
    }

    fun getOrderBooking(it: String, value: JsonObject) = viewModelScope.launch {
        Response1!!.value = repo.orderBookinge(it, value)
    }
}