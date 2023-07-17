package com.lifehopehealthapp.vitals.VitalsCategoryList

import com.google.gson.JsonObject
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class VitalsCategoryListModel(val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getVitalsList(value: String) = safeApiCall {
        api.getVitalsCategories(value)
    }

    suspend fun saveVitalsData(value: String, request: JsonObject) = safeApiCall {
        api.saveVitalsData(value, request)
    }
}