package com.lifehopehealthapp.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.ProductListResponse
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val repo: ProductListModel
) : ViewModel() {


    var Response: SingleLiveEvent<Resource<ProductListResponse>>? = null

    init {
        Response = SingleLiveEvent()
    }
    fun getProductList(it: String, pageNumber: JsonObject) = viewModelScope.launch {
        Response!!.value = repo.getProductList(it, pageNumber)
    }

}