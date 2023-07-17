package com.lifehopehealthapp.notificationList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.NotificationListResponse
import com.lifehopehealthapp.ResponseModel.NotificationUpdateResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class NotificationListViewModel(private val repo: NotificationListModel) : ViewModel() {
    val Response: MutableLiveData<Resource<NotificationListResponse>> = MutableLiveData()
    val Response1: MutableLiveData<Resource<NotificationUpdateResponse>> = MutableLiveData()

    fun getNotificationList(it: String, pageNumber: JsonObject) = viewModelScope.launch {
        Response.value = repo.getNotificationList(it, pageNumber)
    }

    fun getNotificationUpdated(it: String, pageNumber: JsonObject) = viewModelScope.launch {
        Response1.value = repo.getNotificationUpdated(it, pageNumber)
    }

}