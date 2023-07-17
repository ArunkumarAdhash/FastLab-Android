package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class TestListResponse {
    /*@SerializedName("data")
    @Expose
    private var data: List<Datum?>? = null

    @SerializedName("status")
    @Expose
    private var status: Int? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    fun getData(): List<Datum?>? {
        return data
    }

    fun setData(data: List<Datum?>?) {
        this.data = data
    }

    fun getStatus(): Int? {
        return status
    }

    fun setStatus(status: Int?) {
        this.status = status
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }
*/
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

        @SerializedName("status")
        @Expose
        var status: Int? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

    }

    class TestList {
        @SerializedName("testTypeId")
        @Expose
        internal var testTypeId: String? = null

        @SerializedName("testCategoryId")
        @Expose
        internal var testCategoryId: String? = null

        @SerializedName("imagePath")
        @Expose
        internal var imagePath: String? = null

        @SerializedName("iosImagePath")
        @Expose
        private var iosImagePath: String? = null

        @SerializedName("categoryName")
        @Expose
        internal var categoryName: String? = null

        @SerializedName("categoryDescription")
        @Expose
        internal var categoryDescription: String? = null

        @SerializedName("price")
        @Expose
        internal var price: Int? = null

        @SerializedName("orderBy")
        @Expose
        internal var orderBy: Int? = null

        @SerializedName("type")
        @Expose
        internal var type: Int? = null

        @SerializedName("IsBookingType")
        @Expose
        internal var isBookingType: Int? = null

        @SerializedName("ExpeditedPricing")
        @Expose
        internal var expeditedPricing: Int? = null

        @SerializedName("actualDescription")
        @Expose
        internal var actualDescription: String? = ""

        @SerializedName("expeditedDescription")
        @Expose
        internal var expeditedDescription: String? = ""

        fun getTestTypeId(): String? {
            return testTypeId
        }

        fun setTestTypeId(testTypeId: String?) {
            this.testTypeId = testTypeId
        }

        fun getTestCategoryId(): String? {
            return testCategoryId
        }

        fun setTestCategoryId(testCategoryId: String?) {
            this.testCategoryId = testCategoryId
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

        fun getCategoryName(): String? {
            return categoryName
        }

        fun setCategoryName(categoryName: String?) {
            this.categoryName = categoryName
        }

        fun getCategoryDescription(): String? {
            return categoryDescription
        }

        fun setCategoryDescription(categoryDescription: String?) {
            this.categoryDescription = categoryDescription
        }

        fun getPrice(): Int? {
            return price
        }

        fun setPrice(price: Int?) {
            this.price = price
        }

        fun getOrderBy(): Int? {
            return orderBy
        }

        fun setOrderBy(orderBy: Int?) {
            this.orderBy = orderBy
        }

        fun getType(): Int? {
            return type
        }

        fun setType(type: Int?) {
            this.type = type
        }

    }

    class Datum {
        @SerializedName("type1")
        @Expose
        internal var type1: String? = null

        @SerializedName("type1Notes")
        @Expose
        internal var type1Notes: String? = null

        @SerializedName("type2")
        @Expose
        internal var type2: String? = null

        @SerializedName("type2Notes")
        @Expose
        internal var type2Notes: String? = null

        @SerializedName("shippingCharge")
        @Expose
        internal var shippingCharge: Int? = null

        @SerializedName("tax")
        @Expose
        internal var tax: Double? = null

        @SerializedName("extraInfo")
        @Expose
        internal var extraInfo: String? = null

        @SerializedName("imagePath")
        @Expose
        internal var imagePath: String? = null

        @SerializedName("iosImagePath")
        @Expose
        internal var iosImagePath: String? = null


        @SerializedName("list")
        @Expose
        internal var list: List<TestList?>? = null

        fun getExtraInfo(): String? {
            return extraInfo
        }

        fun getImagePath(): String? {
            return imagePath
        }

        fun setImagePath(imagePath: String?) {
            this.imagePath = imagePath
        }

        fun getiosImagePath(): String? {
            return iosImagePath
        }

        fun setiosImagePath(imagePath: String?) {
            this.iosImagePath = imagePath
        }

        fun setExtraInfo(extraInfo: String?) {
            this.extraInfo = extraInfo
        }

        fun getType1(): String? {
            return type1
        }

        fun setType1(type1: String?) {
            this.type1 = type1
        }

        fun getType1Notes(): String? {
            return type1Notes
        }

        fun setType1Notes(type1Notes: String?) {
            this.type1Notes = type1Notes
        }

        fun getType2(): String? {
            return type2
        }

        fun setType2(type2: String?) {
            this.type2 = type2
        }

        fun getType2Notes(): String? {
            return type2Notes
        }

        fun setType2Notes(type2Notes: String?) {
            this.type2Notes = type2Notes
        }

        fun getShippingCharge(): Int? {
            return shippingCharge
        }

        fun setShippingCharge(shippingCharge: Int?) {
            this.shippingCharge = shippingCharge
        }

        fun getTax(): Double? {
            return tax
        }

        fun setTax(tax: Double?) {
            this.tax = tax
        }

        fun getList(): List<TestList?>? {
            return list
        }

        fun setList(list: List<TestList?>?) {
            this.list = list
        }
    }
}