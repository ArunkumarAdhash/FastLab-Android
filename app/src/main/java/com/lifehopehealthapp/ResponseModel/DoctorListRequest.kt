package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class DoctorListRequest {
    @SerializedName("text")
    @Expose
    private var text: String? = null

    @SerializedName("PaginationQuery")
    @Expose
    private var paginationQuery: Pagination? = null

    fun getText(): String? {
        return text
    }

    fun setText(text: String?) {
        this.text = text
    }

    fun getPaginationQuery(): Pagination? {
        return paginationQuery
    }

    fun setPaginationQuery(paginationQuery: Pagination?) {
        this.paginationQuery = paginationQuery
    }
}

class Pagination {
    @SerializedName("pageNumber")
    @Expose
    internal var pageNumber: Int? = 0

    @SerializedName("pageSize")
    @Expose
    internal var pageSize: Int? = 0

    @SerializedName("Count")
    @Expose
    private var count: Int? = 0

    fun getPageNumber(): Int? {
        return pageNumber
    }

    fun setPageNumber(pageNumber: Int?) {
        this.pageNumber = pageNumber
    }

    fun getPageSize(): Int? {
        return pageSize
    }

    fun setPageSize(pageSize: Int?) {
        this.pageSize = pageSize
    }

    fun getCount(): Int? {
        return count
    }

    fun setCount(count: Int?) {
        this.count = count
    }
}