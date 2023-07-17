package com.lifehopehealthapp.mobileOtpVerify

import com.lifehopehealthapp.ResponseModel.LogInDataModel
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class MobileOTPVerifyModel(
    private val api: APIManager,
    val preferences: PreferenceHelper
) : BaseRepository() {

    suspend fun getLogInDetails(fname: LogInDataModel) = safeApiCall {
        api.getLogInDetails(fname)
    }
}