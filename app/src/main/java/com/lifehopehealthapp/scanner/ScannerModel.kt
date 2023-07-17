package com.lifehopehealthapp.scanner

import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.APIRequest
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class ScannerModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun checkQRStatus(token: String, value: APIRequest) = safeApiCall {
        api.checkQRStatus(token, value)
    }
}