package com.lifehopehealthapp.productlist

import com.google.gson.JsonObject
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class ProductListModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {

    suspend fun getProductList(it: String, pageNumber: JsonObject) = safeApiCall {
        api.productList(it, pageNumber)
    }

}