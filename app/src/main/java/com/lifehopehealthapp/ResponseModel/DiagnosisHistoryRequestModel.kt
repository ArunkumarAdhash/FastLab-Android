package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class DiagnosisHistoryRequestModel {
    @SerializedName("diagnosesId")
    @Expose
    private var diagnosesId: Int? = null

    @SerializedName("paginationQuery")
    @Expose
    private var paginationQuery: PaginationQuery? = null

    fun getDiagnosesId(): Int? {
        return diagnosesId
    }

    fun setDiagnosesId(diagnosesId: Int?) {
        this.diagnosesId = diagnosesId
    }

    fun getPaginationQuery(): PaginationQuery? {
        return paginationQuery
    }

    fun setPaginationQuery(paginationQuery: PaginationQuery?) {
        this.paginationQuery = paginationQuery
    }
}

class PaginationQuery {
    @SerializedName("pageNumber")
    @Expose
    var pageNumber: Int? = null

    @SerializedName("pageSize")
    @Expose
    var pageSize: Int? = null

}