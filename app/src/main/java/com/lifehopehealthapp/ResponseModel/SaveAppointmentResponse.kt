package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class SaveAppointmentResponse {

    @SerializedName("Value")
    @Expose
    private var value: SaveData? = null

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

    fun getValue(): SaveData? {
        return value
    }

    fun setValue(value: SaveData?) {
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

}

class SaveData {
    @SerializedName("appointmentId")
    @Expose
    private var appointmentId: String? = null

    @SerializedName("totalAmount")
    @Expose
    private var totalAmount: Double? = null

    @SerializedName("link")
    @Expose
    private var link: String? = null

    fun getAppointmentId(): String? {
        return appointmentId
    }

    fun setAppointmentId(appointmentId: String?) {
        this.appointmentId = appointmentId
    }

    fun getTotalAmount(): Double? {
        return totalAmount
    }

    fun setTotalAmount(totalAmount: Double?) {
        this.totalAmount = totalAmount
    }

    fun getLink(): String? {
        return link
    }

    fun setLink(link: String?) {
        this.link = link
    }
}