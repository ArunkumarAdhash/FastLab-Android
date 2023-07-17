package com.lifehopehealthapp.scheduleDateTime

import com.google.gson.JsonObject
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class ScheduleDateTimeModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getDateTime(token: String, value: JsonObject) = safeApiCall {
        api.getDateTime(token, value)
    }
}