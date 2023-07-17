package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class DateTimeResponse {
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
}

class Datum {
    @SerializedName("list")
    @Expose
    var list: List<ListData>? = null

}

class ListData {
    @SerializedName("day")
    @Expose
    var day: String? = null

    @SerializedName("dayText")
    @Expose
    var dayText: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("morning")
    @Expose
    var morning: List<Morning>? = null

    @SerializedName("evening")
    @Expose
    var evening: List<Evening>? = null


}

class Evening {
    @SerializedName("time")
    @Expose
    var time: String? = null

    @SerializedName("isAvailable")
    @Expose
    var isAvailable: Int? = null

}

class Morning {
    @SerializedName("time")
    @Expose
    var time: String? = null

    @SerializedName("isAvailable")
    @Expose
    var isAvailable: Int? = null

}

