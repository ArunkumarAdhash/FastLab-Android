package com.lifehopehealthapp.myProfile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.ResponseModel.GoogleAddresResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class MapViewViewModel(
    private val repo: MapViewModel
) : ViewModel() {
    val Response: MutableLiveData<Resource<GoogleAddresResponse>> = MutableLiveData()

    fun callGoogleAddress(data: String, key: String) = viewModelScope.launch {
        Response.value = repo.googleAddress(data, key)
    }
}