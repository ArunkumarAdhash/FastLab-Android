package com.lifehopehealthapp.bookingList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.ResponseModel.TestListResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class TestBookingViewModel(
    private val repo: TestBookingModel
) : ViewModel() {

    val Response: MutableLiveData<Resource<TestListResponse>> = MutableLiveData()


    fun getTestList(it: String) = viewModelScope.launch {
        Response.value = repo.getTestList(it)
    }

}