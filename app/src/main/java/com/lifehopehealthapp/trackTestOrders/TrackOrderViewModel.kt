package com.lifehopehealthapp.trackTestOrders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.TrackOrderResponse
import com.lifehopehealthapp.ResponseModel.UpdateShipmentResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class TrackOrderViewModel(
    private val repo: TrackOrderModel
) : ViewModel() {

    val responseData: MutableLiveData<Resource<TrackOrderResponse>> = MutableLiveData()
    val response: MutableLiveData<Resource<UpdateShipmentResponse>> = MutableLiveData()

    fun getOrderTrackListData(token: String,orderId: JsonObject) = viewModelScope.launch {
        responseData.value = repo.getOrderTrackList(token, orderId)
    }

    fun getUpdateShippment(orderId: JsonObject, token: String) = viewModelScope.launch {
        response.value = repo.updateShippment(token, orderId)
    }

}