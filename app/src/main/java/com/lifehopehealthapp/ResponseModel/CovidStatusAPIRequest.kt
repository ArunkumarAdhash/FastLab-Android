package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CovidStatusAPIRequest {

    @SerializedName("PageSize")
    @Expose
    private var PageSize: Int? = null

    @SerializedName("PageNumber")
    @Expose
    private var PageNumber: Int? = null

    fun getPageSize(): Int? {
        return PageSize
    }

    fun setPageSize(PageSize: Int?) {
        this.PageSize = PageSize
    }

    fun getPageNumber(): Int? {
        return PageNumber
    }

    fun setPageNumber(PageNumber: Int?) {
        this.PageNumber = PageNumber
    }

}