package com.lifehopehealthapp.login

import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.ResponseModel.LogInDataModel
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class LogInModel(private val api: APIManager, val preferences: PreferenceHelper) : BaseRepository() {

    suspend fun getLogInDetails(fname: LogInDataModel) = safeApiCall {
        api.getLogInDetails(fname)
    }

}