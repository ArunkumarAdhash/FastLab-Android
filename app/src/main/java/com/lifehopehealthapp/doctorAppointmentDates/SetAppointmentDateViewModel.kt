package com.lifehopehealthapp.doctorAppointmentDates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.ResponseModel.SaveAppointmentRequest
import com.lifehopehealthapp.ResponseModel.SaveAppointmentResponse
import com.lifehopehealthapp.ResponseModel.SetDateRequest
import com.lifehopehealthapp.ResponseModel.SetDateResponse
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class SetAppointmentDateViewModel(
    private val repo: SetAppointmentDateModel
) : ViewModel() {

    private val Response: MutableLiveData<Resource<SetDateResponse>> = MutableLiveData()

    val dataResponse: LiveData<Resource<SetDateResponse>>
        get() = Response

    fun getDetails(token: String, request: SetDateRequest) = viewModelScope.launch {
        Response.value = repo.getDateList(token, request)
    }

    //private val Response1: MutableLiveData<Resource<SaveAppointmentResponse>> = MutableLiveData()
    var Response1: SingleLiveEvent<Resource<SaveAppointmentResponse>>? = null

    init {
        Response1 = SingleLiveEvent()
    }

    fun saveAppointment(token: String, request: SaveAppointmentRequest) = viewModelScope.launch {
        Response1!!.value = repo.saveAppointment(token, request)
    }
}