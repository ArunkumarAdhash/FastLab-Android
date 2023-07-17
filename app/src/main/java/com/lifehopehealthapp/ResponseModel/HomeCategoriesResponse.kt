package com.lifehopehealthapp.dashboard

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class HomeCategoriesResponse {

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

}

class Datum {
    @SerializedName("homeList")
    @Expose
    private var homeList: List<Home?>? = null

    @SerializedName("batchCount")
    @Expose
    private var batchCount: Int? = null

    fun getHomeList(): List<Home?>? {
        return homeList
    }

    fun setHomeList(homeList: List<Home?>?) {
        this.homeList = homeList
    }

    fun getBatchCount(): Int? {
        return batchCount
    }

    fun setBatchCount(batchCount: Int?) {
        this.batchCount = batchCount
    }
}

class Home {
    @SerializedName("id")
    @Expose
    internal var id: Int? = null

    @SerializedName("imagePath")
    @Expose
    internal var imagePath: String? = null

    @SerializedName("iOSImagePath")
    @Expose
    private var iOSImagePath: String? = null

    @SerializedName("name")
    @Expose
    internal var name: String? = null

    @SerializedName("category")
    @Expose
    private var category: Int? = null

    @SerializedName("description")
    @Expose
    private var description: Any? = null

    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getImagePath(): String? {
        return imagePath
    }

    fun setImagePath(imagePath: String?) {
        this.imagePath = imagePath
    }

    fun getiOSImagePath(): String? {
        return iOSImagePath
    }

    fun setiOSImagePath(iOSImagePath: String?) {
        this.iOSImagePath = iOSImagePath
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getCategory(): Int? {
        return category
    }

    fun setCategory(category: Int?) {
        this.category = category
    }

    fun getDescription(): Any? {
        return description
    }

    fun setDescription(description: Any?) {
        this.description = description
    }

}

class Value {
    @SerializedName("data")
    @Expose
    internal var data: Datum? = null

    @SerializedName("status")
    @Expose
    private var status: Int? = null

    @SerializedName("message")
    @Expose
    internal var message: String? = null

    fun getData(): Datum? {
        return data
    }

    fun setData(data: Datum?) {
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

}