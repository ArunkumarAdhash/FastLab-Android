package com.lifehopehealthapp.bulkBooking.organizationDetails

import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.VerifyEmailRequest
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class BulkBookingModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getOrganizationList(value: String) = safeApiCall {
        api.getOrganizationList(value)
    }

    suspend fun getOrganizationDetailList(
        value: String,
        request: JsonObject
    ) = safeApiCall {
        api.getOrganizationDetailList(value, request)
    }

    suspend fun getUserList(
        value: String,
        request: VerifyEmailRequest
    ) = safeApiCall {
        api.getVerifyEmail(value, request)
    }
}