package com.lifehopehealthapp.covidstatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.ResponseModel.CovidStatusAPIRequest
import com.lifehopehealthapp.ResponseModel.CovidStatusResponse
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class CovidStatusViewModel(private val repo: CovidStatusModel) : ViewModel() {

    var Response: SingleLiveEvent<Resource<CovidStatusResponse>>? = null

    init {
        Response = SingleLiveEvent()
    }

    fun getCovidStatusList(token: String, orderId: CovidStatusAPIRequest) = viewModelScope.launch {
        Response!!.value = repo.getCovidStatusList(token, orderId)
    }

    /*fun getCovidListData(
        token: String): Flow<PagingData<CovidStatusResponse.Data>> {
        return Pager(config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                CovidStatusListDataSource(
                    token = token,
                    invoke = repo
                )
            }).flow.cachedIn(viewModelScope)
    }*/

}