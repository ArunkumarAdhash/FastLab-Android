package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class OrganizationTitleResponse {

    @SerializedName("Value")
    @Expose
    private var value: Value? = null

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

    fun getValue(): Value? {
        return value
    }

    fun setValue(value: Value?) {
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

    class Data {
        @SerializedName("titleList")
        @Expose
        internal var titleList: List<String?>? = null

        @SerializedName("organizationLists")
        @Expose
        internal var organizationLists: List<OrganizationList?>? = null

        @SerializedName("contractLists")
        @Expose
        private var contractLists: List<ContractList?>? = null

        fun getTitleList(): List<String?>? {
            return titleList
        }

        fun setTitleList(titleList: List<String?>?) {
            this.titleList = titleList
        }

        fun getOrganizationLists(): List<OrganizationList?>? {
            return organizationLists
        }

        fun setOrganizationLists(organizationLists: List<OrganizationList?>?) {
            this.organizationLists = organizationLists
        }

        fun getContractLists(): List<ContractList?>? {
            return contractLists
        }

        fun setContractLists(contractLists: List<ContractList?>?) {
            this.contractLists = contractLists
        }
    }

    class OrganizationList {
        @SerializedName("Id")
        @Expose
        private var id: Int? = null

        @SerializedName("titleName")
        @Expose
        internal var titleName: String? = null

        @SerializedName("placeHolderList")
        @Expose
        internal var placeHolderList: List<PlaceHolder?>? = null

        fun getId(): Int? {
            return id
        }

        fun setId(id: Int?) {
            this.id = id
        }

        fun getTitleName(): String? {
            return titleName
        }

        fun setTitleName(titleName: String?) {
            this.titleName = titleName
        }

        fun getPlaceHolderList(): List<PlaceHolder?>? {
            return placeHolderList
        }

        fun setPlaceHolderList(placeHolderList: List<PlaceHolder?>?) {
            this.placeHolderList = placeHolderList
        }
    }

    class PlaceHolder {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("isAvailable")
        @Expose
        var isAvailable: Int? = null

        @SerializedName("tooltip")
        @Expose
        var tooltip: String? = ""

    }

    class ContractList {
        @SerializedName("Id")
        @Expose
        var id: Int? = null

        @SerializedName("titleName")
        @Expose
        var titleName: String? = null

        @SerializedName("description")
        @Expose
        var description: String? = null

        @SerializedName("discount")
        @Expose
        var discount: String? = null

    }

    class Value {
        @SerializedName("data")
        @Expose
        var data: Data? = null

        @SerializedName("status")
        @Expose
        var status: Int? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

    }
}