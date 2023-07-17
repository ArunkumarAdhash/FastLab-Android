package com.lifehopehealthapp.symptomSearch.DiagnosisHistoryList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.ResponseModel.DiagnosisListResponse
import kotlinx.coroutines.launch

class DiagnosisHistoryListViewModel(
    private val repo: DiagnosisHistoryListModel
) : ViewModel() {
    val Response: MutableLiveData<Resource<DiagnosisListResponse>> = MutableLiveData()


    fun getDiagnosisList(it: String) = viewModelScope.launch {
        Response.value = repo.getDiagnosisList(it)
    }
}