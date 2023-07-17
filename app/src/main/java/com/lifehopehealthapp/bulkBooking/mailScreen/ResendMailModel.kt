package com.lifehopehealthapp.bulkBooking.mailScreen

import com.google.gson.JsonObject
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class ResendMailModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun confirmOrder(token: String, value: JsonObject) = safeApiCall {
        api.confirmOrder(token, value)
    }

    suspend fun confirmBulkOrder(token: String, value: JsonObject) = safeApiCall {
        api.confirmBulkOrder(token, value)
    }
}