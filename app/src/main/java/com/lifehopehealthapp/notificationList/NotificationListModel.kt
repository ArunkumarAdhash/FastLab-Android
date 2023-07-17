package com.lifehopehealthapp.notificationList

import com.google.gson.JsonObject
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class NotificationListModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {
    suspend fun getNotificationList(it: String, pageNumber: JsonObject) = safeApiCall {
        api.notificationList(it, pageNumber)
    }

    suspend fun getNotificationUpdated(token: String, req: JsonObject) = safeApiCall {
        api.notificationUpdated(token, req)
    }
}