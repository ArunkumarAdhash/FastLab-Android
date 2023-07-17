package com.lifehopehealthapp.editProfile

import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class EditProfileModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getUserProfileDetails(value: String) = safeApiCall {
        api.getUserProfileDetails(value)
    }
}