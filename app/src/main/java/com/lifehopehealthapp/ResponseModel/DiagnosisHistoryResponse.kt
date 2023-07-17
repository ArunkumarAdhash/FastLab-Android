package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

@Keep
class DiagnosisHistoryResponse {
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
        @SerializedName("imagePath")
        @Expose
        internal var imagePath: String? = null

        @SerializedName("iOSImagePath")
        @Expose
        internal var iOSImagePath: String? = null

        @SerializedName("name")
        @Expose
        internal var name: String? = null

        @SerializedName("id")
        @Expose
        internal var id: String? = null

        @SerializedName("testDate")
        @Expose
        internal var testDate: Int? = null

        @SerializedName("diagnosesId")
        @Expose
        internal var diagnosesId: Int? = null

        fun getImagePath(): String? {
            return imagePath
        }

        fun setImagePath(imagePath: String?) {
            this.imagePath = imagePath
        }

        fun getIOSImagePath(): String? {
            return iOSImagePath
        }

        fun setIOSImagePath(iOSImagePath: String?) {
            this.iOSImagePath = iOSImagePath
        }

        fun getName(): String? {
            return name
        }

        fun setName(name: String?) {
            this.name = name
        }

        fun getId(): String? {
            return id
        }

        fun setId(id: String?) {
            this.id = id
        }

        fun getTestDate(): Int? {
            return testDate
        }

        fun setTestDate(testDate: Int?) {
            this.testDate = testDate
        }

        fun getDiagnosesId(): Int? {
            return diagnosesId
        }

        fun setDiagnosesId(diagnosesId: Int?) {
            this.diagnosesId = diagnosesId
        }
    }

    class Value {
        @SerializedName("data")
        @Expose
        var data: List<Datum>? =
            null

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
        var nextPage: String? = ""

        @SerializedName("previousPage")
        @Expose
        var previousPage: String? = ""

    }
}

