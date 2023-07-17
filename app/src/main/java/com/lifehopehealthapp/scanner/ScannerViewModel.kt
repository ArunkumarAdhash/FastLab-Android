package com.lifehopehealthapp.scanner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.ResponseModel.APIRequest
import com.lifehopehealthapp.ResponseModel.ScannerResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val repo: ScannerModel
) : ViewModel() {
    val Response: MutableLiveData<Resource<ScannerResponse>> = MutableLiveData()

    fun checkQRStatus(it: String, value: APIRequest) = viewModelScope.launch {
        Response.value = repo.checkQRStatus(it, value)
    }

}