package com.lifehopehealthapp.doctorAppointmentDates

import com.lifehopehealthapp.ResponseModel.SaveAppointmentRequest
import com.lifehopehealthapp.ResponseModel.SetDateRequest
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class SetAppointmentDateModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getDateList(token: String, request: SetDateRequest) = safeApiCall {
        api.getAvailableDates(token, request)
    }

    suspend fun saveAppointment(token: String, request: SaveAppointmentRequest) = safeApiCall {
        api.saveDoctorAppointment(token, request)
    }
}