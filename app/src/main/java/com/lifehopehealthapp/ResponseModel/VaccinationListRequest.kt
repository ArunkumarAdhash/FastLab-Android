package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class VaccinationListRequest {
    @SerializedName("PageSize")
    @Expose
    internal var pageSize: Int? = null

    @SerializedName("PageNumber")
    @Expose
    internal var pageNumber: Int? = null

    fun getPageSize(): Int? {
        return pageSize
    }

    fun setPageSize(pageSize: Int?) {
        this.pageSize = pageSize
    }

    fun getPageNumber(): Int? {
        return pageNumber
    }

    fun setPageNumber(pageNumber: Int?) {
        this.pageNumber = pageNumber
    }
}