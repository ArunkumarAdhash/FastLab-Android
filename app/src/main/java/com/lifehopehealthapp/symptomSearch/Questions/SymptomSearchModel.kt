package com.lifehopehealthapp.symptomSearch.Questions

import com.google.gson.JsonObject
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class SymptomSearchModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getQuestionList(token: String, id: JsonObject) = safeApiCall {
        api.getQuestionList(token, id)
    }
}