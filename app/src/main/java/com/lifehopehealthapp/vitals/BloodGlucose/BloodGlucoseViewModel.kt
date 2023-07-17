package com.lifehopehealthapp.vitals.BloodGlucose


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.BloodGlucoseResponse
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.ResponseModel.ShareVitalsResponse
import kotlinx.coroutines.launch

class BloodGlucoseViewModel(
    private val repo: BloodGlucoseModel
) : ViewModel() {
    val Response: MutableLiveData<Resource<BloodGlucoseResponse>> = MutableLiveData()

    fun getBloodGlucoseData(it: String, id: JsonObject) = viewModelScope.launch {
        Response.value = repo.getBloodGlucoseData(it, id)
    }

    val Response1: MutableLiveData<Resource<ShareVitalsResponse>> = MutableLiveData()

    fun shareVitalsData(it: String, id: JsonObject) = viewModelScope.launch {
        Response1.value = repo.shareVitalsData(it, id)
    }
}