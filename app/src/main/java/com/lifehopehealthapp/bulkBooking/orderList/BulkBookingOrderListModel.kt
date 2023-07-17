package com.lifehopehealthapp.bulkBooking.orderList

import com.google.gson.JsonObject
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class BulkBookingOrderListModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun orderBookinge(token: String, value: JsonObject) = safeApiCall {
        api.orderBook(token, value)
    }
}