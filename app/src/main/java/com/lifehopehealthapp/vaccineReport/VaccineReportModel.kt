package com.lifehopehealthapp.vaccineReport

import com.lifehopehealthapp.ResponseModel.DeleteVaccinationDocumentRequest
import com.lifehopehealthapp.ResponseModel.UploadVaccinationDocumentRequest
import com.lifehopehealthapp.ResponseModel.VaccinationListRequest
import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class VaccineReportModel(
    val api: APIManager,
    val preferences: PreferenceHelper
) : BaseRepository() {

    suspend fun vaccinationList(token: String, orderId: VaccinationListRequest) = safeApiCall {
        api.getVaccinationReportList(token, orderId)
    }

    suspend fun uploadVaccineDocument(token: String, orderId: UploadVaccinationDocumentRequest) =
        safeApiCall {
            api.uploadVaccineDocument(token, orderId)
        }

    suspend fun deleteVaccineDocument(token: String, orderId: DeleteVaccinationDocumentRequest) =
        safeApiCall {
            api.deleteVaccineDocument(token, orderId)
        }

    /*@ExperimentalPagingApi
    fun getPlayers(): Flow<PagingData<VaccinationListResponse.VaccinationListDatum>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = PlayersRemoteMediator(
                api
            ),
            pagingSourceFactory = PagingSource<Int, VaccinationListResponse.VaccinationListDatum>
        ).flow
    }*/

    companion object {
        private const val NETWORK_PAGE_SIZE = 25
    }


}