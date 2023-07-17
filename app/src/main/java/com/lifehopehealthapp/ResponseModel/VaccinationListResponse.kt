package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class VaccinationListResponse {
    @SerializedName("Value")
    @Expose
    private var value: VaccinationListValue? = null

    @SerializedName("Formatters")
    @Expose
    private var formatters: List<Any?>? = null

    @SerializedName("ContentTypes")
    @Expose
    private var contentTypes: List<Any?>? = null

    @SerializedName("DeclaredType")
    @Expose
    private var declaredType: Any? = null

    @SerializedName("StatusCode")
    @Expose
    private var statusCode: Int? = null

    fun getValue(): VaccinationListValue? {
        return value
    }

    fun setValue(value: VaccinationListValue?) {
        this.value = value
    }

    fun getFormatters(): List<Any?>? {
        return formatters
    }

    fun setFormatters(formatters: List<Any?>?) {
        this.formatters = formatters
    }

    fun getContentTypes(): List<Any?>? {
        return contentTypes
    }

    fun setContentTypes(contentTypes: List<Any?>?) {
        this.contentTypes = contentTypes
    }

    fun getDeclaredType(): Any? {
        return declaredType
    }

    fun setDeclaredType(declaredType: Any?) {
        this.declaredType = declaredType
    }

    fun getStatusCode(): Int? {
        return statusCode
    }

    fun setStatusCode(statusCode: Int?) {
        this.statusCode = statusCode
    }


    class VaccinationListValue {
        @SerializedName("data")
        @Expose
        internal var data: List<VaccinationListDatum>? = null

        @SerializedName("pageNumber")
        @Expose
        private var pageNumber: Int? = null

        @SerializedName("pageSize")
        @Expose
        private var pageSize: Int? = null

        @SerializedName("totalPages")
        @Expose
        internal var totalPages: Int? = null

        @SerializedName("nextPage")
        @Expose
        internal var nextPage: String? = ""

        @SerializedName("previousPage")
        @Expose
        private var previousPage: String? = null

        fun getData(): List<VaccinationListDatum?>? {
            return data
        }

        fun setData(data: List<VaccinationListDatum>?) {
            this.data = data
        }

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

        fun getTotalPages(): Int? {
            return totalPages
        }

        fun setTotalPages(totalPages: Int?) {
            this.totalPages = totalPages
        }

        fun getNextPage(): String? {
            return nextPage
        }

        fun setNextPage(nextPage: String?) {
            this.nextPage = nextPage
        }

        fun getPreviousPage(): String? {
            return previousPage
        }

        fun setPreviousPage(previousPage: String?) {
            this.previousPage = previousPage
        }
    }
}

class VaccinationListDatum {
    @SerializedName("Id")
    @Expose
    private var id: String? = null

    @SerializedName("name")
    @Expose
    internal var name: String? = null

    @SerializedName("fileUrl")
    @Expose
    internal var fileUrl: String? = null

    @SerializedName("createdDate")
    @Expose
    internal var createdDate: Int? = 0

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getFileUrl(): String? {
        return fileUrl
    }

    fun setFileUrl(fileUrl: String?) {
        this.fileUrl = fileUrl
    }
}