package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class ProductListResponse {



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

     class Datum {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("imagePath")
        @Expose
        var imagePath: String? = null

        @SerializedName("title")
        @Expose
        var title: String? = null

        @SerializedName("category")
        @Expose
        var category: Int? = null

        @SerializedName("amount")
        @Expose
        var amount: Int? = null

        @SerializedName("limit")
        @Expose
        var limit: Int? = null

        @SerializedName("totalPrice")
        @Expose
        var totalPrice: Int? = null

        @SerializedName("totalQuantity")
        @Expose
        var totalQuantity: Int? = null

        @SerializedName("description")
        @Expose
        var description: String? = null

         var isSelected : Boolean =false
    }

    class Value {
        @SerializedName("data")
        @Expose
        var data: List<Datum>? = null

        @SerializedName("pageNumber")
        @Expose
        var pageNumber: Int? = null

        @SerializedName("pageSize")
        @Expose
        var pageSize: Int? = null

        @SerializedName("totalPages")
        @Expose
        var totalPages: Int? = null

        @SerializedName("nextPage")
        @Expose
        var nextPage: Any? = null

        @SerializedName("previousPage")
        @Expose
        var previousPage: Any? = null
    }



}