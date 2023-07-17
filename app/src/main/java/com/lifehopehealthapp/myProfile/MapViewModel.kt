package com.lifehopehealthapp.myProfile

import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class MapViewModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun googleAddress(data: String, key: String) = safeApiCall {
        api.googleAddress(data, key)
    }
}