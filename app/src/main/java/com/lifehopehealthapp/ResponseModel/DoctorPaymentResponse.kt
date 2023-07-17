package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class DoctorPaymentResponse {
    @SerializedName("Value")
    @Expose
    private var value: DoctorPaymentData? = null

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

    fun getValue(): DoctorPaymentData? {
        return value
    }

    fun setValue(value: DoctorPaymentData?) {
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

class DoctorPaymentData {
    @SerializedName("status")
    @Expose
    internal var status: Int? = null

    @SerializedName("description")
    @Expose
    internal var description: String? = null

    @SerializedName("emailContent")
    @Expose
    internal var emailContent: String? = null

    fun getStatus(): Int? {
        return status
    }

    fun setStatus(status: Int?) {
        this.status = status
    }

    fun getDescription(): String? {
        return description
    }

    fun setDescription(description: String?) {
        this.description = description
    }

    fun getEmailContent(): String? {
        return emailContent
    }

    fun setEmailContent(emailContent: String?) {
        this.emailContent = emailContent
    }
}