package com.lifehopehealthapp.symptomSearch.DiagnosisHistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifehopehealthapp.ResponseModel.DiagnosisHistoryRequestModel
import com.lifehopehealthapp.ResponseModel.DiagnosisHistoryResponse
import com.lifehopehealthapp.base.Resource
import kotlinx.coroutines.launch

class DiagnosisHistoryViewModel(
    private val repo: DiagnosisHistoryModel
) : ViewModel() {
    val Response: MutableLiveData<Resource<DiagnosisHistoryResponse>> = MutableLiveData()


    fun getDiagnosisHistory(token: String, id: DiagnosisHistoryRequestModel) =
        viewModelScope.launch {
            Response.value = repo.getDiagnosisHistory(token, id)
        }


    /*fun getDiagnosisHistoryList(
        token: String,
        diagnosisID: Int
    ): Flow<PagingData<DiagnosisHistoryResponse.Datum>> {
        return Pager(config = PagingConfig(pageSize = 20, maxSize = 200),
            pagingSourceFactory = {
                DiagnosisHistoryListDataSource(
                    token = token,
                    invoke = repo.api,
                    diagnosisID = diagnosisID
                )
            }).flow.cachedIn(viewModelScope)
    }*/
}