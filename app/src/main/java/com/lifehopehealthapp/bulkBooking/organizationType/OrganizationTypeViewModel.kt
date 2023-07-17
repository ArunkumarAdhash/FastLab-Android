package com.lifehopehealthapp.bulkBooking.organizationType

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.ResponseModel.OrganizationListResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class OrganizationTypeViewModel(
    private val repo: OrganizationTypeModel
) : ViewModel() {
    val Response: MutableLiveData<Resource<OrganizationListResponse>> = MutableLiveData()

    fun getTestList(it: String) = viewModelScope.launch {
        Response.value = repo.getOrganizationList(it)
    }

}