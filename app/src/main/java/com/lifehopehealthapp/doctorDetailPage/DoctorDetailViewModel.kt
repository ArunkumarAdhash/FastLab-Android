package com.lifehopehealthapp.doctorDetailPage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.ResponseModel.DoctorDetailRequest
import com.lifehopehealthapp.ResponseModel.DoctorDetailResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class DoctorDetailViewModel(
    private val repo: DoctorDetailModel
) : ViewModel() {
    val Response: MutableLiveData<Resource<DoctorDetailResponse>> = MutableLiveData()

    fun getDoctorDetail(it: String, data: DoctorDetailRequest) =
        viewModelScope.launch {
            Response.value = repo.getDoctorDetail(it, data)
        }
}