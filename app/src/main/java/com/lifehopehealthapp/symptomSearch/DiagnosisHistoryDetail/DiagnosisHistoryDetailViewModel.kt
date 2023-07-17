package com.lifehopehealthapp.symptomSearch.DiagnosisHistoryDetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.DiagnosisHistoryDetailResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class DiagnosisHistoryDetailViewModel(
    private val repo: DiagnosisHistoryDetailModel
) : ViewModel() {

    val Response: MutableLiveData<Resource<DiagnosisHistoryDetailResponse>> = MutableLiveData()


    fun getDiagnosisDetailHistory(it: String, time: JsonObject) = viewModelScope.launch {
        Response.value = repo.getDiagnosisDetailHistory(it, time)
    }
}