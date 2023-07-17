package com.lifehopehealthapp.vitals.ECGDetailPage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.ECGDetailResponse
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.ResponseModel.ShareVitalsResponse
import kotlinx.coroutines.launch

class ECGDetailViewModel(
    private val repo: ECGDetailModel
) : ViewModel() {

    val Response: MutableLiveData<Resource<ECGDetailResponse>> = MutableLiveData()

    fun getECGList(it: String, id: JsonObject) = viewModelScope.launch {
        Response.value = repo.getECGData(it, id)
    }

    val Response1: MutableLiveData<Resource<ShareVitalsResponse>> = MutableLiveData()

    fun shareVitalsData(it: String, id: JsonObject) = viewModelScope.launch {
        Response1.value = repo.shareVitalsData(it, id)
    }
}