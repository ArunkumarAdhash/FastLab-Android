package com.lifehopehealthapp.vitals.TemperatureDetailPage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.TemperatureDetailResponse
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.ResponseModel.ShareVitalsResponse
import com.lifehopehealthapp.ResponseModel.VitalsSaveDataResponse
import kotlinx.coroutines.launch

class TemperatureDetailViewModel(
    private val repo: TemperatureDetailModel
) : ViewModel() {

    val Response: MutableLiveData<Resource<TemperatureDetailResponse>> = MutableLiveData()

    fun getTemperatureList(it: String, id: JsonObject) = viewModelScope.launch {
        Response.value = repo.getTemperatureData(it, id)
    }

    val Response1: MutableLiveData<Resource<ShareVitalsResponse>> = MutableLiveData()

    fun shareVitalsData(it: String, id: JsonObject) = viewModelScope.launch {
        Response1.value = null
        Response1.value = repo.shareVitalsData(it, id)
    }

    val Response2: MutableLiveData<Resource<VitalsSaveDataResponse>> = MutableLiveData()

    fun saveVitals(it: String, request: JsonObject) = viewModelScope.launch {
        Response2.value = repo.saveVitalsData(it, request)
    }

}