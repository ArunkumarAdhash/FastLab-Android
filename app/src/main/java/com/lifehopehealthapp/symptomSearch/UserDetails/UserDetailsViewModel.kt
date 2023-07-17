package com.lifehopehealthapp.symptomSearch.UserDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.ResponseModel.ProfileImageResponse

class UserDetailsViewModel(
    private val repo: UserDetailsModel
) : ViewModel() {

    private val uploadImageResponse: MutableLiveData<Resource<ProfileImageResponse>> =
        MutableLiveData()

    val updateImageResponse: LiveData<Resource<ProfileImageResponse>>
        get() = uploadImageResponse

    /*fun uploadImage(token: String, part: MultipartBody.Part, type: Int) = viewModelScope.launch {
        uploadImageResponse.value = repo.uploadImage(token, part, type)
    }*/
}