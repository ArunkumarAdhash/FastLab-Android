package com.lifehopehealthapp.bulkBooking.organizationType

import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class OrganizationTypeModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getOrganizationList(value: String) = safeApiCall {
        api.getOrganizationList(value)
    }

}