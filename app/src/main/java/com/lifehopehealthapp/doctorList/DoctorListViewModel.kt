package com.lifehopehealthapp.doctorList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.ResponseModel.DoctorListRequest
import com.lifehopehealthapp.ResponseModel.DoctorListResponse
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class DoctorListViewModel(
    val repo: DoctorListModel
) : ViewModel() {

    var Response: SingleLiveEvent<Resource<DoctorListResponse>>? = null

    init {
        Response = SingleLiveEvent()
    }

    fun getDoctorList(it: String, data: DoctorListRequest) =
        viewModelScope.launch {
            Response!!.value = repo.getDoctorList(it, data)
        }


    /*fun getDoctorListData(
        token: String,
        search: String
    ): Flow<PagingData<DoctorListResponse.Datum>> {
        return Pager(config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                DoctorListDataSource(
                    token = token,
                    searchData = search,
                    invoke = repo
                )
            }).flow.cachedIn(viewModelScope)
    }*/

}