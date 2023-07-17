package com.lifehopehealthapp.vitals.TemperatureDetailPage

import com.google.gson.JsonObject
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class TemperatureDetailModel(val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getTemperatureData(value: String, id: JsonObject) = safeApiCall {
        api.getTemperatureData(value, id)
    }

    suspend fun shareVitalsData(value: String, id: JsonObject) = safeApiCall {
        api.shareVitalsData(value, id)
    }

    suspend fun saveVitalsData(value: String, request: JsonObject) = safeApiCall {
        api.saveVitalsData(value, request)
    }

}