package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class OrganizationListResponse {
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
        @SerializedName("organizationType")
        @Expose
        var organizationType: List<OrganizationType>? = null

        @SerializedName("testType")
        @Expose
        var testType: List<TestType>? = null

    }

    class OrganizationType {
        @SerializedName("id")
        @Expose
        internal var id: Int? = 0

        @SerializedName("categoryName")
        @Expose
        private var categoryName: String? = null

        @SerializedName("imagePath")
        @Expose
        internal var imagePath: String? = null

        @SerializedName("iosImagePath")
        @Expose
        private var iosImagePath: String? = null

        @SerializedName("imagePathGrey")
        @Expose
        private var imagePathGrey: String? = null

        @SerializedName("iosImagePathGrey")
        @Expose
        private var iosImagePathGrey: String? = null

        @SerializedName("isAvailable")
        @Expose
        internal var isAvailable: Int? = 0

        @SerializedName("isPayment")
        @Expose
        internal var isPayment : Boolean =true

        fun getId(): Int? {
            return id
        }

        fun setId(id: Int?) {
            this.id = id
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

        fun getImagePathGrey(): String? {
            return imagePathGrey
        }

        fun setImagePathGrey(imagePathGrey: String?) {
            this.imagePathGrey = imagePathGrey
        }

        fun getIosImagePathGrey(): String? {
            return iosImagePathGrey
        }

        fun setIosImagePathGrey(iosImagePathGrey: String?) {
            this.iosImagePathGrey = iosImagePathGrey
        }

        fun getIsAvailable(): Int? {
            return isAvailable
        }

        fun setIsAvailable(isAvailable: Int?) {
            this.isAvailable = isAvailable
        }
    }

    class TestType {
        @SerializedName("testTypeId")
        @Expose
        var testTypeId: String? = null

        @SerializedName("testCategoryId")
        @Expose
        var testCategoryId: String? = null

        @SerializedName("imagePath")
        @Expose
        var imagePath: String? = null

        @SerializedName("iosImagePath")
        @Expose
        var iosImagePath: String? = null

        @SerializedName("categoryName")
        @Expose
        var categoryName: String? = null

        @SerializedName("categoryDescription")
        @Expose
        var categoryDescription: String? = null

        @SerializedName("price")
        @Expose
        var price: Double? = null

        @SerializedName("orderBy")
        @Expose
        var orderBy: Int? = null

        @SerializedName("type")
        @Expose
        var type: Int? = null

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