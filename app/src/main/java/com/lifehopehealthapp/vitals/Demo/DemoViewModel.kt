package com.lifehopehealthapp.vitals.Demo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.ResponseModel.MyTestOrderResponse
import kotlinx.coroutines.launch

class DemoViewModel(
    private val repo: DemoModel
) : ViewModel() {
    /*val Response: MutableLiveData<Resource<VitalsHistoryResponse>> = MutableLiveData()

    fun getVitalsList(it: String, id: Int, pageno: Int, i1: Int) = viewModelScope.launch {
        Response.value = repo.getVitalsList(it, id, pageno, i1)
    }*/
    val Response: MutableLiveData<Resource<MyTestOrderResponse>> = MutableLiveData()

    fun getTestList(it: String, pageNumber: Int, pageSize: Int) = viewModelScope.launch {
        //Response.value = repo.getTestList(it, pageNumber, pageSize)
    }
}