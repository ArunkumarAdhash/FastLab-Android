package com.lifehopehealthapp.bookingList

import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper


class TestBookingModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getTestList(value: String) = safeApiCall {
        api.getTestList(value)
    }
}