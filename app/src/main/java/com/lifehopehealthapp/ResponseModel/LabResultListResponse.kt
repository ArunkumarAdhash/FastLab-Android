package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

@Keep
class LabResultListResponse {

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
        @SerializedName("categoryId")
        @Expose
        private var categoryId: String? = null

        @SerializedName("categoryName")
        @Expose
        internal var categoryName: String? = null

        @SerializedName("imagePath")
        @Expose
        internal var imagePath: String? = null

        @SerializedName("iosImagePath")
        @Expose
        private var iosImagePath: String? = null

        @SerializedName("orderBy")
        @Expose
        private var orderBy: Int? = null

        fun getCategoryId(): String? {
            return categoryId
        }

        fun setCategoryId(categoryId: String?) {
            this.categoryId = categoryId
        }

        fun getCategoryName(): String? {
            return categoryName
        }

        fun setCategoryName(categoryName: String?) {
            this.categoryName = categoryName
        }

        fun getImagePath(): String? {
            return imagePath
        }

        fun setImagePath(imagePath: String?) {
            this.imagePath = imagePath
        }

        fun getIosImagePath(): String? {
            return iosImagePath
        }

        fun setIosImagePath(iosImagePath: String?) {
            this.iosImagePath = iosImagePath
        }

        fun getOrderBy(): Int? {
            return orderBy
        }

        fun setOrderBy(orderBy: Int?) {
            this.orderBy = orderBy
        }
    }

    class Value {
        @SerializedName("data")
        @Expose
        var data: List<Datum>? = null

        @SerializedName("status")
        @Expose
        var status: Int? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

    }

}
