package com.lifehopehealthapp.myProfile

import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.UpdateProfileModel
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.utils.PreferenceHelper

class MyProfileModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {


    suspend fun getUserProfileDetails(value: String) = safeApiCall {
        api.getUserProfileDetails(value)
    }

    suspend fun updatedProfile(value: UpdateProfileModel, token: String) = safeApiCall {
        api.updatedProfile(token, value)
    }

    suspend fun uploadImage(token: String, aJsonObject: JsonObject) = safeApiCall {
        api.uploadImage(token,aJsonObject)
    }

    /*suspend fun getsettings(request: SettingsRequest) = safeApiCall {
        api.getSettingsData(request)
    }*/
}