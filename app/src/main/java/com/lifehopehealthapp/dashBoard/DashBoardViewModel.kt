package com.lifehopehealthapp.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class DashBoardViewModel(
    private val repo: DashBoardModel
) : ViewModel() {

    val Response: MutableLiveData<Resource<HomeCategoriesResponse>> = MutableLiveData()

    fun getHomeCategories(value: String) = viewModelScope.launch {
        Response.value = repo.getCategoriesList(value)
    }

}