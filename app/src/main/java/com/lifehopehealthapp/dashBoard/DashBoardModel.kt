package com.lifehopehealthapp.dashboard

import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class DashBoardModel(
    private val api: APIManager,
    val preferences: PreferenceHelper
) : BaseRepository() {

    suspend fun getCurrentWeather(lat: Double, lng: Double, value: String) = safeApiCall {
        api.getCurrentWeatherData(lat, lng, value)
    }

    suspend fun getCategoriesList(token: String) = safeApiCall {
        api.getHomeCategories(token)
    }
}