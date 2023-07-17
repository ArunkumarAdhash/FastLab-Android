package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

@Keep
class ECGDetailResponse {
    @SerializedName("Value")
    @Expose
    private var value: Value1? = null

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

    fun getValue(): Value1? {
        return value
    }

    fun setValue(value: Value1?) {
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

class GraphType1 {
    @SerializedName("graph_type_data")
    @Expose
    var graphTypeData: String? = null

}

class Value1 {
    @SerializedName("data")
    @Expose
    var data: Data1? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

}

class Data1 {
    @SerializedName("graph_name")
    @Expose
    var graphName: String? = null

    @SerializedName("graph_desc")
    @Expose
    var graphDesc: String? = null

    @SerializedName("graph_min_val")
    @Expose
    var graphMinVal: Double? = null

    @SerializedName("graph_max_val")
    @Expose
    var graphMaxVal: Double? = null

    @SerializedName("graph_metrics")
    @Expose
    var graphMetrics: String? = null

    @SerializedName("graph_date_format")
    @Expose
    var graphDateFormat: String? = null

    @SerializedName("graph_type")
    @Expose
    var graphType: GraphType1? = null

    @SerializedName("color_hexcode")
    @Expose
    var colorHexcode: String? = null

    @SerializedName("bar_data")
    @Expose
    var barData: List<Double>? = null

}