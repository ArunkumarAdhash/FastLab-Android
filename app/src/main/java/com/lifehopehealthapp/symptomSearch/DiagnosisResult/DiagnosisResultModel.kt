package com.lifehopehealthapp.symptomSearch.DiagnosisResult

import com.google.gson.JsonObject
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class DiagnosisResultModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getDiagnosisResult(token: String, request: JsonObject) = safeApiCall {
        api.getDiagnosisResult(token, request)
    }

    suspend fun saveDiagnosisResult(token: String, request: JsonObject) = safeApiCall {
        api.saveDiagnosisResult(token, request)
    }
}