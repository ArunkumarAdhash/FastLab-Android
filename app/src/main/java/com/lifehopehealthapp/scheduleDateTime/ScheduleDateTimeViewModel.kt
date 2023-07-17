package com.lifehopehealthapp.scheduleDateTime

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.DateTimeResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class ScheduleDateTimeViewModel(
    private val repo: ScheduleDateTimeModel
) : ViewModel() {

    val Response: MutableLiveData<Resource<DateTimeResponse>> = MutableLiveData()


    fun getDateTime(it: String, value: JsonObject) = viewModelScope.launch {
        Response.value = repo.getDateTime(it, value)
    }


}