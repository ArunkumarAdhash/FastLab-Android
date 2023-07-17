package com.lifehopehealthapp.vitals.ECGDetailPage

import com.google.gson.JsonObject
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class ECGDetailModel(val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getECGData(value: String, id: JsonObject) = safeApiCall {
        api.getECGData(value, id)
    }

    suspend fun shareVitalsData(value: String, id: JsonObject) = safeApiCall {
        api.shareVitalsData(value, id)
    }
}