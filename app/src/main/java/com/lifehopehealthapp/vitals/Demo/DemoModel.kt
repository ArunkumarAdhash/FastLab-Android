package com.lifehopehealthapp.vitals.Demo

import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class DemoModel(val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getVitalsList(value: String, id: Int, pageno: Int, i1: Int) = safeApiCall {
        api.getVitalsHistory(value, id, pageno, i1)
    }

    suspend fun getTestList(value: String, pageNumber: Int, pageSize: Int) = safeApiCall {
        //api.getOrderTestList(value, pageNumber, pageSize)
    }
}