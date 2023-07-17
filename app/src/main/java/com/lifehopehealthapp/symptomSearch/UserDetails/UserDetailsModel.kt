package com.lifehopehealthapp.symptomSearch.UserDetails

import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class UserDetailsModel(val api: APIManager, val preferences: PreferenceHelper) : BaseRepository() {
    /*suspend fun uploadImage(token: String, part: MultipartBody.Part, type: Int) = safeApiCall {
        api.uploadImage(token, part, type)
    }*/
}