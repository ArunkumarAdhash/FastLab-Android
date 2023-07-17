package com.lifehopehealthapp.signUp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.ResponseModel.SettingsRequest
import com.lifehopehealthapp.ResponseModel.SettingsResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val repo: SignupModel
) : ViewModel() {

    private val Response: MutableLiveData<Resource<SettingsResponse>> = MutableLiveData()

    val dataResponse: LiveData<Resource<SettingsResponse>>
        get() = Response

    fun getDetails(request: SettingsRequest) = viewModelScope.launch {
        Response.value = repo.getSettingsDetails(request)
    }
}