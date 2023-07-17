package com.lifehopehealthapp.signUp

import com.lifehopehealthapp.ResponseModel.SettingsRequest
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class SignupModel(
    private val api: APIManager,
    val preferences: PreferenceHelper
) : BaseRepository() {
    suspend fun getSettingsDetails(request: SettingsRequest) = safeApiCall {
        api.getSettingsData(request)
    }
}