package com.lifehopehealthapp.bulkBookingNew

import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.VerifyEmailRequest
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class NewBulkBookingModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {


    suspend fun getOrganizationDetailList(
        value: String,
        request: JsonObject
    ) = safeApiCall {
        api.getOrganizationDetailListNew(value, request)
    }

    suspend fun getUserList(
        value: String,
        request: VerifyEmailRequest
    ) = safeApiCall {
        api.getVerifyEmail(value, request)
    }


    suspend fun orderBookinge(token: String, value: JsonObject) = safeApiCall {
        api.bookBulkOrder(token, value)
    }
}