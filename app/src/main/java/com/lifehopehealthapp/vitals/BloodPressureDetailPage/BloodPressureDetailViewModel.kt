package com.lifehopehealthapp.vitals.BloodPressureDetailPage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.BloodPressureDetailResponse
import com.lifehopehealthapp.ResponseModel.GlucoseListResponse
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.ResponseModel.ShareVitalsResponse
import com.lifehopehealthapp.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class BloodPressureDetailViewModel(
    private val repo: BloodPressureDetailModel
) : ViewModel() {

    var Response: SingleLiveEvent<Resource<BloodPressureDetailResponse>>? = null
    var Response2: SingleLiveEvent<Resource<BloodPressureDetailResponse>>? = null

    init {
        Response = SingleLiveEvent()
        Response2 = SingleLiveEvent()
    }

    fun getBloodPressureData(it: String, id: JsonObject) = viewModelScope.launch {
        Response!!.value = repo.getBloodPressureData(it, id)
    }

    fun getBloodPressure(it: String, id: JsonObject) = viewModelScope.launch {
        Response2!!.value = repo.getBloodPressureData(it, id)
    }

    val Response1: MutableLiveData<Resource<ShareVitalsResponse>> = MutableLiveData()

    fun shareVitalsData(it: String, id: JsonObject) = viewModelScope.launch {
        Response1.value = repo.shareVitalsData(it, id)
    }
}