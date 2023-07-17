package com.lifehopehealthapp.symptomSearch.DiagnosisHistoryList

import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class DiagnosisHistoryListModel (val api: APIManager, val preferences: PreferenceHelper) : BaseRepository() {
    suspend fun getDiagnosisList(token: String) = safeApiCall {
        api.getDiagnosisList(token)
    }
}