package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

@Keep
class TemperatureDetailResponse {
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
        var data: TemperatureData? = null

        @SerializedName("status")
        @Expose
        var status: Int? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

    }
}

class Graphtype {
    @SerializedName("graph_type_data")
    @Expose
    var graphTypeData: String? = null

}
class BarTimestamp {
    @SerializedName("timestamp")
    @Expose
    var timestamp: Int? = null

}
class TemperatureData {
    @SerializedName("graph_name")
    @Expose
    private var graphName: String? = null

    @SerializedName("graph_desc")
    @Expose
    private var graphDesc: String? = null

    @SerializedName("graph_min_val")
    @Expose
    private var graphMinVal: Int? = null

    @SerializedName("graph_max_val")
    @Expose
    private var graphMaxVal: Int? = null

    @SerializedName("graph_metrics")
    @Expose
    private var graphMetrics: String? = null

    @SerializedName("graph_date_format")
    @Expose
    private var graphDateFormat: String? = null

    @SerializedName("graph_type")
    @Expose
    private var graphType: Graphtype? = null

    @SerializedName("bar_data")
    @Expose
    private var barData: List<BarDatum?>? = null

    @SerializedName("bar_timestamp")
    @Expose
    private var barTimestamp: List<BarTimestamp?>? = null

    @SerializedName("bar_color_schemes")
    @Expose
    private var barColorSchemes: List<BarColorScheme?>? = null

    @SerializedName("start_date")
    @Expose
    private var startDate: Int? = null

    @SerializedName("end_date")
    @Expose
    private var endDate: Int? = null

    fun getGraphName(): String? {
        return graphName
    }

    fun setGraphName(graphName: String?) {
        this.graphName = graphName
    }

    fun getGraphDesc(): String? {
        return graphDesc
    }

    fun setGraphDesc(graphDesc: String?) {
        this.graphDesc = graphDesc
    }

    fun getGraphMinVal(): Int? {
        return graphMinVal
    }

    fun setGraphMinVal(graphMinVal: Int?) {
        this.graphMinVal = graphMinVal
    }

    fun getGraphMaxVal(): Int? {
        return graphMaxVal
    }

    fun setGraphMaxVal(graphMaxVal: Int?) {
        this.graphMaxVal = graphMaxVal
    }

    fun getGraphMetrics(): String? {
        return graphMetrics
    }

    fun setGraphMetrics(graphMetrics: String?) {
        this.graphMetrics = graphMetrics
    }

    fun getGraphDateFormat(): String? {
        return graphDateFormat
    }

    fun setGraphDateFormat(graphDateFormat: String?) {
        this.graphDateFormat = graphDateFormat
    }

    fun getGraphType(): Graphtype? {
        return graphType
    }

    fun setGraphType(graphType: Graphtype?) {
        this.graphType = graphType
    }

    fun getBarData(): List<BarDatum?>? {
        return barData
    }

    fun setBarData(barData: List<BarDatum?>?) {
        this.barData = barData
    }

    fun getBarTimestamp(): List<BarTimestamp?>? {
        return barTimestamp
    }

    fun setBarTimestamp(barTimestamp: List<BarTimestamp?>?) {
        this.barTimestamp = barTimestamp
    }

    fun getBarColorSchemes(): List<BarColorScheme?>? {
        return barColorSchemes
    }

    fun setBarColorSchemes(barColorSchemes: List<BarColorScheme?>?) {
        this.barColorSchemes = barColorSchemes
    }

    fun getStartDate(): Int? {
        return startDate
    }

    fun setStartDate(startDate: Int?) {
        this.startDate = startDate
    }

    fun getEndDate(): Int? {
        return endDate
    }

    fun setEndDate(endDate: Int?) {
        this.endDate = endDate
    }

}

class BarDatum {
    @SerializedName("bar_value")
    @Expose
    var barValue: Float? = 0F

}

class BarColorScheme {
    @SerializedName("color_hexcode")
    @Expose
    var colorHexcode: String? = null

}
