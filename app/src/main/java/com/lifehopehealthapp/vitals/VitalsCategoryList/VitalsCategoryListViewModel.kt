package com.lifehopehealthapp.vitals.VitalsCategoryList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.VitalsCategoryListResponse
import com.lifehopehealthapp.ResponseModel.VitalsSaveDataResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class VitalsCategoryListViewModel(
    private val repo: VitalsCategoryListModel
) : ViewModel() {
    private var heroList: MutableLiveData<List<VitalsCategoryListResponse>>? = null

    fun getHeroes(): MutableLiveData<List<VitalsCategoryListResponse>>? {
        //if the list is null
        if (heroList == null) {
            heroList = MutableLiveData<List<VitalsCategoryListResponse>>()
            //we will load it asynchronously from server in this method
            loadHeroes()
        }

        //finally we will return the list
        return heroList
    }

    private fun loadHeroes() {
        TODO("Not yet implemented")
    }


    val Response: MutableLiveData<Resource<VitalsCategoryListResponse>> = MutableLiveData()

    fun getVitalsList(it: String) = viewModelScope.launch {
        Response.value = repo.getVitalsList(it)
    }

    val Response1: MutableLiveData<Resource<VitalsSaveDataResponse>> = MutableLiveData()

    fun saveVitals(it: String, request: JsonObject) = viewModelScope.launch {
        Response1.value = repo.saveVitalsData(it, request)
    }
}