package com.lifehopehealthapp.symptomSearch.DiagnosisList

import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class DiagnosisListModel (private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getDiagnosisList(token: String) = safeApiCall {
        api.getDiagnosisList(token)
    }
}