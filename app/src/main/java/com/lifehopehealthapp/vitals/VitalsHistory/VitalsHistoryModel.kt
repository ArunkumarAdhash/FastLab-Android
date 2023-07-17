package com.lifehopehealthapp.vitals.VitalsHistory

import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class VitalsHistoryModel(val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getVitalsList(
        value: String,
        id: Int,
        pageno: Int,
        i1: Int
    ) = safeApiCall {
        api.getVitalsHistory(value, id, pageno, i1)
    }
}