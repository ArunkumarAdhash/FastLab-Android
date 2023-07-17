package com.lifehopehealthapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.ResponseModel.LogInDataModel
import com.lifehopehealthapp.ResponseModel.LogInResponse
import kotlinx.coroutines.launch

class LogInViewModel(
    private val repo: LogInModel
) : ViewModel() {

    private val Response: MutableLiveData<Resource<LogInResponse>> = MutableLiveData()

    val detaResponse: LiveData<Resource<LogInResponse>>
        get() = Response

    fun getLogInDetails(model: LogInDataModel) = viewModelScope.launch {
        Response.value = repo.getLogInDetails(model)
    }

}