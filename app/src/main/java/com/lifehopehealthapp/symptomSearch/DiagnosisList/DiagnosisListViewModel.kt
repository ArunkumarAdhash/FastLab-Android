package com.lifehopehealthapp.symptomSearch.DiagnosisList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.ResponseModel.DiagnosisListResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class DiagnosisListViewModel(
    private val repo: DiagnosisListModel
) : ViewModel() {

    val Response: MutableLiveData<Resource<DiagnosisListResponse>> = MutableLiveData()


    fun getDiagnosisList(it: String) = viewModelScope.launch {
        Response.value = repo.getDiagnosisList(it)
    }
}