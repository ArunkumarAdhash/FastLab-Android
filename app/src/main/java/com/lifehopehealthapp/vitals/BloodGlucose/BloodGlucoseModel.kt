package com.lifehopehealthapp.vitals.BloodGlucose

import com.google.gson.JsonObject
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class BloodGlucoseModel(val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getBloodGlucoseData(value: String, id: JsonObject) = safeApiCall {
        api.getBloodGlucoseData(value, id)
    }

    suspend fun shareVitalsData(value: String, id: JsonObject) = safeApiCall {
        api.shareVitalsData(value, id)
    }
}