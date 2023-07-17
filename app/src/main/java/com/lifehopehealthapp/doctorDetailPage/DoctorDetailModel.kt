package com.lifehopehealthapp.doctorDetailPage

import com.lifehopehealthapp.ResponseModel.DoctorDetailRequest
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class DoctorDetailModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getDoctorDetail(value: String, data: DoctorDetailRequest) =
        safeApiCall {
            api.getDoctorDetail(value, data)
        }
}