package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class OrganizationTitleNewResponse {
    @SerializedName("ContentTypes")
    @Expose
    private var contentTypes: List<Any?>? = null

    @SerializedName("DeclaredType")
    @Expose
    private var declaredType: Any? = null

    @SerializedName("Formatters")
    @Expose
    private var formatters: List<Any?>? = null

    @SerializedName("StatusCode")
    @Expose
    private var statusCode: Int? = null

    @SerializedName("Value")
    @Expose
    private var value: Value? = null

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

    fun getFormatters(): List<Any?>? {
        return formatters
    }

    fun setFormatters(formatters: List<Any?>?) {
        this.formatters = formatters
    }

    fun getStatusCode(): Int? {
        return statusCode
    }

    fun setStatusCode(statusCode: Int?) {
        this.statusCode = statusCode
    }

    fun getValue(): Value? {
        return value
    }

    fun setValue(value: Value?) {
        this.value = value
    }
    class Value {
        @SerializedName("data")
        @Expose
        var data: Data? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

        @SerializedName("status")
        @Expose
        var status: Int? = null
    }

    class Title {
        @SerializedName("Id")
        @Expose
        var id: String? = null

        @SerializedName("OrderBy")
        @Expose
        var orderBy: Int? = null

        @SerializedName("TitleName")
        @Expose
        var titleName: String? = null

        @SerializedName("Value")
        @Expose
        var value: Int? = null

        var isSelected : Boolean = false
    }

    class TestType {
        @SerializedName("categoryDescription")
        @Expose
        var categoryDescription: String? = null

        @SerializedName("categoryName")
        @Expose
        var categoryName: String? = null

        @SerializedName("imagePath")
        @Expose
        var imagePath: String? = null

        @SerializedName("iosImagePath")
        @Expose
        var iosImagePath: String? = null

        @SerializedName("orderBy")
        @Expose
        var orderBy: Int? = null

        @SerializedName("price")
        @Expose
        var price: Double = 0.0

        @SerializedName("surfacePrice")
        @Expose
        var surfacePrice: Double = 0.0

        @SerializedName("testCategoryId")
        @Expose
        var testCategoryId: String? = null

        @SerializedName("testTypeId")
        @Expose
        var testTypeId: String? = null

        @SerializedName("type")
        @Expose
        var type: Int? = null


    }

    class Surface {
        @SerializedName("Id")
        @Expose
        var id: String? = null

        @SerializedName("OrderBy")
        @Expose
        var orderBy: Int? = null

        @SerializedName("TitleName")
        @Expose
        var titleName: String? = null

        @SerializedName("Value")
        @Expose
        var value: Int? = null

        var isSelected : Boolean =false
    }

    class Person {
        @SerializedName("Id")
        @Expose
        var id: String? = null

        @SerializedName("OrderBy")
        @Expose
        var orderBy: Int? = null

        @SerializedName("TitleName")
        @Expose
        var titleName: String? = null

        @SerializedName("Value")
        @Expose
        var value: Int? = null

        var isSelected : Boolean =false

    }

    class Data {
        @SerializedName("contractLists")
        @Expose
        var contractLists: List<ContractList>? = null

        @SerializedName("people")
        @Expose
        var people: List<Person>? = null

        @SerializedName("surface")
        @Expose
        var surface: List<Surface>? = null

        @SerializedName("testType")
        @Expose
        var testType: List<TestType>? = null

        @SerializedName("titleList")
        @Expose
        var titleList: List<Title>? = null
    }

    class ContractList {
        @SerializedName("description")
        @Expose
        var description: String? = null

        @SerializedName("discount")
        @Expose
        var discount: String? = null

        @SerializedName("Id")
        @Expose
        var id: Int? = null

        @SerializedName("month")
        @Expose
        var month: Int? = null

        @SerializedName("titleName")
        @Expose
        var titleName: String? = null
    }
}