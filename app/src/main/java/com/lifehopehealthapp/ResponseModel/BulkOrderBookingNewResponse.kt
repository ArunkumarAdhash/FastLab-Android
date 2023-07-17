package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName





@Keep
class BulkOrderBookingNewResponse {
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

    class Data {
        @SerializedName("orderID")
        @Expose
        var orderID: String? = null

        @SerializedName("totalAmount")
        @Expose
        var totalAmount: Double? = null

        @SerializedName("link")
        @Expose
        var link: String? = null

        @SerializedName("bookingType")
        @Expose
        var bookingType: Int? = null

        @SerializedName("confirmResponse")
        @Expose
        var confirmResponse: ConfirmResponse? = null
    }

    class ConfirmResponse {
        @SerializedName("status")
        @Expose
        var status: Int? = null

        @SerializedName("description")
        @Expose
        var description: String? = null

        @SerializedName("emailContent")
        @Expose
        var emailContent: Any? = null
    }

}