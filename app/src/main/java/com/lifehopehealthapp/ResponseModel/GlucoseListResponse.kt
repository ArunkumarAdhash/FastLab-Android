package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

@Keep
class GlucoseListResponse {
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
        var data: List<Daatum>? = null

        @SerializedName("status")
        @Expose
        var status: Int? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

    }
}


class Daatum {
    @SerializedName("glucoseId")
    @Expose
    var glucoseId: Int? = null

    @SerializedName("glucoseType")
    @Expose
    var glucoseType: String? = null

    @SerializedName("isAvailable")
    @Expose
    var isAvailable: Int? = null

}