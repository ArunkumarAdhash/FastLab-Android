package com.lifehopehealthapp.labResultList

import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class LabResultListModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getLabResultList(value: String) = safeApiCall {
        api.getLabResultList(value)
    }
}