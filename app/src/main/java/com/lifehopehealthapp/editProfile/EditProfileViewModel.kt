package com.lifehopehealthapp.editProfile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.ResponseModel.MyProfileResponse
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val repo: EditProfileModel
) : ViewModel() {

    val Response: MutableLiveData<Resource<MyProfileResponse>> = MutableLiveData()


    fun getUserDetails(it: String) = viewModelScope.launch {
        Response.value = repo.getUserProfileDetails(it)
    }
}