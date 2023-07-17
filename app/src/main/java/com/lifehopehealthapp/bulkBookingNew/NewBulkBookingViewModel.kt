package com.lifehopehealthapp.bulkBookingNew

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class NewBulkBookingViewModel(
    private val repo: NewBulkBookingModel
) : ViewModel() {


    var Response1: SingleLiveEvent<Resource<OrganizationTitleNewResponse>>? = null

    var Response2: SingleLiveEvent<Resource<VerifyEmailResponse>>? = null

    var Response3: SingleLiveEvent<Resource<BulkOrderBookingNewResponse>>? = null

    fun getTitle(it: String, request: JsonObject) = viewModelScope.launch {
        Response1!!.value = repo.getOrganizationDetailList(it, request)
    }

    init {
        Response1 = SingleLiveEvent()
        Response2 = SingleLiveEvent()
        Response3 = SingleLiveEvent()
    }

    fun getUserDetails(it: String, request: VerifyEmailRequest) = viewModelScope.launch {
        Response2!!.value = repo.getUserList(it, request)
    }


    fun getOrderBooking(it: String, value: JsonObject) = viewModelScope.launch {
        Response3!!.value = repo.orderBookinge(it, value)
    }
}