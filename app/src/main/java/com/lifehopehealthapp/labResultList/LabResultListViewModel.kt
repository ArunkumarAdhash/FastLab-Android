package com.lifehopehealthapp.labResultList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.ResponseModel.LabResultListResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class LabResultListViewModel(
    private val repo: LabResultListModel
) : ViewModel() {

    val Response: MutableLiveData<Resource<LabResultListResponse>> = MutableLiveData()

    fun getLabResultList(it: String) = viewModelScope.launch {
        Response.value = repo.getLabResultList(it)
    }
}