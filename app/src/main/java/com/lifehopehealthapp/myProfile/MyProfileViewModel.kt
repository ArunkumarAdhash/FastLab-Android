package com.lifehopehealthapp.myProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class MyProfileViewModel(
    private val repo: MyProfileModel
) : ViewModel() {

    private val ResponseData: MutableLiveData<Resource<SettingsResponse>> = MutableLiveData()

    private val Response: MutableLiveData<Resource<MyProfileResponse>> = MutableLiveData()

    private val ProfileResponse: MutableLiveData<Resource<UpdateProfileResponse>> =
        MutableLiveData()

    private val uploadImageResponse: MutableLiveData<Resource<ProfileImageResponse>> =
        MutableLiveData()

    val DataResponse: LiveData<Resource<SettingsResponse>>
        get() = ResponseData

    val detaResponse: LiveData<Resource<MyProfileResponse>>
        get() = Response

    val updateprofileResponse: LiveData<Resource<UpdateProfileResponse>>
        get() = ProfileResponse

    val updateImageResponse: LiveData<Resource<ProfileImageResponse>>
        get() = uploadImageResponse

    /*fun getsettingsDetails(request: SettingsRequest) = viewModelScope.launch {
        ResponseData.value = repo.getsettings(request)
    }
*/
    fun getUserDetails(it: String) = viewModelScope.launch {
        Response.value = repo.getUserProfileDetails(it)
    }

    fun updatedProfile(token: String, userdata: UpdateProfileModel) = viewModelScope.launch {
        ProfileResponse.value = repo.updatedProfile(userdata, token)
    }

    fun uploadImage(token: String, aJsonObject: JsonObject, ) = viewModelScope.launch {
        uploadImageResponse.value = repo.uploadImage(token, aJsonObject)
    }
}