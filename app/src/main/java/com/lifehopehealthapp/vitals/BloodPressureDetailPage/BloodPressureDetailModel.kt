package com.lifehopehealthapp.vitals.BloodPressureDetailPage

import com.google.gson.JsonObject
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class BloodPressureDetailModel(val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getBloodPressureData(value: String, id: JsonObject) = safeApiCall {
        api.getBloodPressureData(value, id)
    }

    suspend fun shareVitalsData(value: String, id: JsonObject) = safeApiCall {
        api.shareVitalsData(value, id)
    }
}