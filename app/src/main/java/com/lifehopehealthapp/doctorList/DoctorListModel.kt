package com.lifehopehealthapp.doctorList

import com.lifehopehealthapp.ResponseModel.DoctorListRequest
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class DoctorListModel(internal val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getDoctorList(value: String, data: DoctorListRequest) =
        safeApiCall {
            api.getDoctorList(value,data)
        }
}