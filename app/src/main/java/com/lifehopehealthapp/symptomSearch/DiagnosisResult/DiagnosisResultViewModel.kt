package com.lifehopehealthapp.symptomSearch.DiagnosisResult

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.DiagnosisResultResponse
import com.lifehopehealthapp.ResponseModel.DiagnosisResultSaveResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class DiagnosisResultViewModel(
    private val repo: DiagnosisResultModel
) : ViewModel() {

    val Response: MutableLiveData<Resource<DiagnosisResultResponse>> = MutableLiveData()

    val SaveResponse: MutableLiveData<Resource<DiagnosisResultSaveResponse>> = MutableLiveData()


    fun getDiagnosisResult(it: String, request: JsonObject) = viewModelScope.launch {
        Response.value = repo.getDiagnosisResult(it, request)
    }

    fun saveDiagnosisResult(it: String, request: JsonObject) = viewModelScope.launch {
        SaveResponse.value = repo.saveDiagnosisResult(it, request)
    }
}