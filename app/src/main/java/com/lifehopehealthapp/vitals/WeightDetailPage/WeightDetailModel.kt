package com.lifehopehealthapp.vitals.WeightDetailPage

import com.google.gson.JsonObject
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class WeightDetailModel(val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getTemperatureData(value: String, id: JsonObject) = safeApiCall {
        api.getTemperatureData(value, id)
    }

    suspend fun shareVitalsData(value: String, id: JsonObject) = safeApiCall {
        api.shareVitalsData(value, id)
    }
}