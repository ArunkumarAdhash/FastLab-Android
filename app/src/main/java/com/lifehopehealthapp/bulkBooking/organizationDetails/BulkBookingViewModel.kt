package com.lifehopehealthapp.bulkBooking.organizationDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.OrganizationListResponse
import com.lifehopehealthapp.ResponseModel.OrganizationTitleResponse
import com.lifehopehealthapp.ResponseModel.VerifyEmailRequest
import com.lifehopehealthapp.ResponseModel.VerifyEmailResponse
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class BulkBookingViewModel(
    private val repo: BulkBookingModel
) : ViewModel() {
    val Response: MutableLiveData<Resource<OrganizationListResponse>> = MutableLiveData()

    val Response1: MutableLiveData<Resource<OrganizationTitleResponse>> = MutableLiveData()

    var Response2: SingleLiveEvent<Resource<VerifyEmailResponse>>? = null

    fun getTestList(it: String) = viewModelScope.launch {
        Response.value = repo.getOrganizationList(it)
    }

    fun getTitle(it: String, request: JsonObject) = viewModelScope.launch {
        Response1.value = repo.getOrganizationDetailList(it, request)
    }

    init {
        Response2 = SingleLiveEvent()
    }

    fun getUserDetails(it: String, request: VerifyEmailRequest) = viewModelScope.launch {
        Response2!!.value = repo.getUserList(it, request)
    }
}