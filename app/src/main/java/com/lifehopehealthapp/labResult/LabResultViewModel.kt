package com.lifehopehealthapp.labResult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.ResponseModel.LabResultAPIRequest
import com.lifehopehealthapp.ResponseModel.LabResultPDFResponse
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class LabResultViewModel(
    private val repo: LabResultModel
) : ViewModel() {

    var Response: SingleLiveEvent<Resource<LabResultPDFResponse>>? = null

    init {
        Response = SingleLiveEvent()
    }

    fun getLabResult(it: String, data: LabResultAPIRequest) =
        viewModelScope.launch {
            Response!!.value = repo.getLabResult(it, data)
        }


    /*fun getLabResultList(
        token: String,
        categoryID: String
    ): Flow<PagingData<LabResultPDFResponse.Datum>> {
        return Pager(config = PagingConfig(pageSize = 20, maxSize = 200),
            pagingSourceFactory = {
                LabResultsListDataSource(
                    token = token,
                    categoryID = categoryID,
                    invoke = repo.api
                )
            }).flow.cachedIn(viewModelScope)
    }*/
}