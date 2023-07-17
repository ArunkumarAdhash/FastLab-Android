package com.lifehopehealthapp.covidstatus

import com.lifehopehealthapp.ResponseModel.CovidStatusAPIRequest
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class CovidStatusModel(internal val api: APIManager, private val preferences: PreferenceHelper):BaseRepository() {

    suspend fun getCovidStatusList(token: String,orderId: CovidStatusAPIRequest) = safeApiCall {
        api.getCovidStatusDetails(token,orderId)
    }
}