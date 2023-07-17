package com.lifehopehealthapp.symptomSearch.DiagnosisHistoryDetail

import com.google.gson.JsonObject
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class DiagnosisHistoryDetailModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getDiagnosisDetailHistory(token: String, time: JsonObject) = safeApiCall {
        api.getDiagnosisDetailHistory(token, time)
    }
}