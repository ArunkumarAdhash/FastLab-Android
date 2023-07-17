package com.lifehopehealthapp.symptomSearch.DiagnosisHistory

import com.lifehopehealthapp.ResponseModel.DiagnosisHistoryRequestModel
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class DiagnosisHistoryModel(internal val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getDiagnosisHistory(token: String, id: DiagnosisHistoryRequestModel) =
        safeApiCall {
            api.getDiagnosisHistory(token, id)
        }
}