package com.lifehopehealthapp.vaccineReport

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.security.MessageDigest

class VaccineReportViewModel(
    private val repo: VaccineReportModel
) : ViewModel() {

    private val FILE_BUFFER_SIZE_BYTES = 1024
    private var currentResult: Flow<PagingData<VaccinationListDatum>>? = null

    //val response: MutableLiveData<Resource<VaccinationListResponse>> = MutableLiveData()
    val response1: MutableLiveData<Resource<UploadVaccinationDocumentResponse>> = MutableLiveData()


    var Response: SingleLiveEvent<Resource<VaccinationListResponse>>? = null

    init {
        Response = SingleLiveEvent()
    }

    fun getVaccinationList(model: VaccinationListRequest, token: String) = viewModelScope.launch {
        Response!!.value = repo.vaccinationList(token, model)
    }

    fun uploadVaccinationListDocument(model: UploadVaccinationDocumentRequest, token: String) =
        viewModelScope.launch {
            response1.value = repo.uploadVaccineDocument(token, model)
        }

    fun deleteVaccinationListDocument(model: DeleteVaccinationDocumentRequest, token: String) =
        viewModelScope.launch {
            response1.value = repo.deleteVaccineDocument(token, model)
        }

    /*fun getVaccinationList(
        token: String
    ): Flow<PagingData<VaccinationListDatum>> {
        return Pager(config = PagingConfig(pageSize = 20, maxSize = 200),
            pagingSourceFactory = {
                VaccineListDataSource(
                    token = token,
                    invoke = repo.api
                )
            }).flow.cachedIn(viewModelScope)
    }*/
    /*@ExperimentalPagingApi
    fun getVaccinationList(token: String): Flow<PagingData<VaccinationListResponse.VaccinationListDatum>> {
        val newResult: Flow<PagingData<VaccinationListResponse.VaccinationListDatum>> =
            repo.getPlayers().cachedIn(viewModelScope)
        currentResult = newResult
        return newResult
    }*/

    suspend fun openDocumentExample(inputStream: InputStream): String {
        @Suppress("BlockingMethodInNonBlockingContext")
        return withContext(Dispatchers.IO) {
            inputStream.use { stream ->
                val messageDigest = MessageDigest.getInstance("SHA-256")

                val buffer = ByteArray(FILE_BUFFER_SIZE_BYTES)
                var bytesRead = stream.read(buffer)
                while (bytesRead > 0) {
                    messageDigest.update(buffer, 0, bytesRead)
                    bytesRead = stream.read(buffer)
                }
                val hashResult = messageDigest.digest()
                hashResult.joinToString(separator = ":") { "%02x".format(it) }
            }
        }
    }
}