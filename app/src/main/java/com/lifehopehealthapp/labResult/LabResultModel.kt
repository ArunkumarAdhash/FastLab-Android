package com.lifehopehealthapp.labResult

import com.lifehopehealthapp.ResponseModel.LabResultAPIRequest
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class LabResultModel(internal val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getLabResult(value: String, data: LabResultAPIRequest) =
        safeApiCall {
            api.getLabResult(value, data)
        }
}