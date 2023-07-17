package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class LabResultAPIRequest {

    @SerializedName("categoryId")
    @Expose
    private var categoryId: String? = null

    @SerializedName("paginationQuery")
    @Expose
    private var paginationQuery: PaginationQuery? = null

    fun getCategoryId(): String? {
        return categoryId
    }

    fun setCategoryId(categoryId: String?) {
        this.categoryId = categoryId
    }

    fun getPaginationQuery(): PaginationQuery? {
        return paginationQuery
    }

    fun setPaginationQuery(paginationQuery: PaginationQuery) {
        this.paginationQuery = paginationQuery
    }

    class PaginationQuery {
        @SerializedName("pageNumber")
        @Expose
        var pageNumber: Int? = null

        @SerializedName("pageSize")
        @Expose
        var pageSize: Int? = null

    }

}
