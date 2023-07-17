package com.lifehopehealthapp.trackTestOrders

import com.google.gson.JsonObject
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class TrackOrderModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getOrderTrackList(token: String, orderId: JsonObject) = safeApiCall {
        api.getTrackOrderDetails(token, orderId)
    }

    suspend fun updateShippment(token: String, orderId: JsonObject) = safeApiCall {
        api.updateShipment(token, orderId)
    }
}