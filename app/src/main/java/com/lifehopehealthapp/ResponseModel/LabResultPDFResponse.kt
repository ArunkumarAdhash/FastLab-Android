package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

@Keep
class LabResultPDFResponse {
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
        var data: List<Datum>? = null

        @SerializedName("pageNumber")
        @Expose
        var pageNumber: Int? = 0

        @SerializedName("pageSize")
        @Expose
        var pageSize: Int? = 0

        @SerializedName("totalPages")
        @Expose
        var totalPages: Int? = 0

        @SerializedName("nextPage")
        @Expose
        var nextPage: String? = ""

        @SerializedName("previousPage")
        @Expose
        var previousPage: String? = ""

    }

    class Datum {
        @SerializedName("orderNo")
        @Expose
        internal var orderNo: Int? = null

        @SerializedName("patientName")
        @Expose
        private var patientName: String? = null

        @SerializedName("orderDate")
        @Expose
        internal var orderDate: Double? = null

        @SerializedName("imagePath")
        @Expose
        private var imagePath: String? = null

        @SerializedName("orderId")
        @Expose
        private var orderId: String? = null

        fun getOrderNo(): Int? {
            return orderNo
        }

        fun setOrderNo(orderNo: Int?) {
            this.orderNo = orderNo
        }

        fun getPatientName(): String? {
            return patientName
        }

        fun setPatientName(patientName: String?) {
            this.patientName = patientName
        }

        fun getOrderDate(): Double? {
            return orderDate
        }

        fun setOrderDate(orderDate: Double?) {
            this.orderDate = orderDate
        }

        fun getImagePath(): String? {
            return imagePath
        }

        fun setImagePath(imagePath: String?) {
            this.imagePath = imagePath
        }

        fun getOrderId(): String? {
            return orderId
        }

        fun setOrderId(orderId: String?) {
            this.orderId = orderId
        }

    }
}