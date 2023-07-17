package com.lifehopehealthapp.vitals.AddVitals

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.GlucoseListResponse
import com.lifehopehealthapp.ResponseModel.OrderBookingResponse
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.ResponseModel.VitalsSaveDataResponse
import com.lifehopehealthapp.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class AddVitalsViewModel(
    private val repo: AddVitalsModel
) : ViewModel() {

    var Response1: SingleLiveEvent<Resource<VitalsSaveDataResponse>>? = null
    var Response: SingleLiveEvent<Resource<GlucoseListResponse>>? = null


    init {
        Response = SingleLiveEvent()
        Response1 = SingleLiveEvent()
    }

    fun saveVitals(it: String, request: JsonObject) = viewModelScope.launch {
        Response1!!.value = repo.saveVitalsData(it, request)
    }

    fun getGlucoseList(it: String, request: JsonObject) = viewModelScope.launch {
        Response!!.value = repo.getGlucoseList(it, request)
    }

}