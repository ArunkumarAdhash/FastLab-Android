package com.lifehopehealthapp.mobileOtpVerify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.ResponseModel.LogInDataModel
import com.lifehopehealthapp.ResponseModel.LogInResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class MobileOTPVerifyViewModel(
    private val repo: MobileOTPVerifyModel,
) : ViewModel() {
    private val Response: MutableLiveData<Resource<LogInResponse>> = MutableLiveData()

    val detaResponse: LiveData<Resource<LogInResponse>>
        get() = Response

    fun getLogInDetails(model: LogInDataModel) = viewModelScope.launch {
        Response.value = repo.getLogInDetails(model)
    }

}