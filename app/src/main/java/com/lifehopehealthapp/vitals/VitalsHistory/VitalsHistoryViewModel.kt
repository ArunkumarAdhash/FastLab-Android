package com.lifehopehealthapp.vitals.VitalsHistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.ResponseModel.VitalsHistoryResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class VitalsHistoryViewModel(
    private val repo: VitalsHistoryModel
) : ViewModel() {
    val Response: MutableLiveData<Resource<VitalsHistoryResponse>> = MutableLiveData()

    fun getVitalsList(it: String, id: Int, pageno: Int, i1: Int) = viewModelScope.launch {
        Response.value = repo.getVitalsList(it, id, pageno, i1)
    }
}