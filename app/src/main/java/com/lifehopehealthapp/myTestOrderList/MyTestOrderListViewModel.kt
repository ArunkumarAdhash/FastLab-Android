package com.lifehopehealthapp.myTestOrderList

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import com.lifehopehealthapp.ResponseModel.MyTestOrderResponse
import com.lifehopehealthapp.ResponseModel.OrderListData
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.retrofitService.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MyTestOrderListViewModel(
    private val repo: MyTestOrderListModel
) : ViewModel() {
    lateinit var retroService: APIManager
    protected val remoteDataSource = RemoteDataSource()

    init {
        retroService = remoteDataSource.buildApi(APIManager::class.java)
    }


    val Response: MutableLiveData<Resource<MyTestOrderResponse>> = MutableLiveData()

    fun getTestList(it: String, pageNumber: JsonObject) = viewModelScope.launch {
        Response.value = repo.getTestList(it, pageNumber)
        val data: String = Response.value.toString()
        Log.e("myorderVM", data)
    }


    fun getOrderListItem(token: String): Flow<PagingData<OrderListData>> {

        return Pager(
            config = PagingConfig(pageSize = 100, maxSize = 500),
            pagingSourceFactory = {
                OrderPagingSource(
                    retroService,
                    token
                )
            }).flow.cachedIn(viewModelScope)
    }

}