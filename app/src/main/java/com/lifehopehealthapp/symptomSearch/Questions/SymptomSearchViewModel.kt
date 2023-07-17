package com.lifehopehealthapp.symptomSearch.Questions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.SymptomSearchQuestionResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class SymptomSearchViewModel(
    private val repo: SymptomSearchModel
) : ViewModel() {
  /*  suspend fun getKey(): String? {
        val token = runBlocking { repo.preferences.authToken.first() }
        return token
    }*/

    val Response: MutableLiveData<Resource<SymptomSearchQuestionResponse>> = MutableLiveData()


    fun getQuestionList(it: String, id: JsonObject) = viewModelScope.launch {
        Response.value = repo.getQuestionList(it, id)
    }
}