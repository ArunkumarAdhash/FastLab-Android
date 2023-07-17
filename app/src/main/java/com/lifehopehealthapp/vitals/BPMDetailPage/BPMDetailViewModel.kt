package com.lifehopehealthapp.vitals.BPMDetailPage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.ResponseModel.ShareVitalsResponse
import com.lifehopehealthapp.ResponseModel.TemperatureDetailResponse
import kotlinx.coroutines.launch

class BPMDetailViewModel(
    private val repo: BPMDetailModel
) : ViewModel() {

    val Response: MutableLiveData<Resource<TemperatureDetailResponse>> = MutableLiveData()

    fun getTemperatureList(it: String, id: JsonObject) = viewModelScope.launch {
        Response.value = repo.getTemperatureData(it, id)
    }

    val Response1: MutableLiveData<Resource<ShareVitalsResponse>> = MutableLiveData()

    fun shareVitalsData(it: String, id: JsonObject) = viewModelScope.launch {
        Response1.value = repo.shareVitalsData(it, id)
    }
}