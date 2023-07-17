package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class BloodPressureDetailResponse {
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
        var data: BloodPressureData? = null

        @SerializedName("status")
        @Expose
        var status: Int? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

    }
}

class BarColorSystolic {
    @SerializedName("color_hexcode")
    @Expose
    var colorHexcode: String? = null

}

class BarColorDiastolic {
    @SerializedName("color_hexcode")
    @Expose
    var colorHexcode: String? = null

}

class SystolicBarDatum {
    @SerializedName("bar_value")
    @Expose
    var barValue: Float? = 0F

}

class DiastolicBarDatum {
    @SerializedName("bar_value")
    @Expose
    var barValue: Float? = 0F

}

class GraphTypess {
    @SerializedName("graph_type_data")
    @Expose
    var graphTypeData: String? = null

}

class BloodPressureData {
    @SerializedName("graph_name")
    @Expose
    private var graphName: String? = null

    @SerializedName("graph_desc")
    @Expose
    internal var graphDesc: String? = null

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
    private var graphType: GraphTypess? = null

    @SerializedName("systolic_bar_data")
    @Expose
    internal var systolicBarData: List<SystolicBarDatum?>? = null

    @SerializedName("diastolic_bar_data")
    @Expose
    internal var diastolicBarData: List<DiastolicBarDatum?>? = null

    @SerializedName("bar_timestamp")
    @Expose
    internal var barTimestamp: List<BPBarTimestamp?>? = null

    @SerializedName("bar_color_systolic")
    @Expose
    private var barColorSystolic: List<BarColorSystolic?>? = null

    @SerializedName("bar_color_diastolic")
    @Expose
    private var barColorDiastolic: List<BarColorDiastolic?>? = null

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

    fun getGraphType(): GraphTypess? {
        return graphType
    }

    fun setGraphType(graphType: GraphTypess?) {
        this.graphType = graphType
    }

    fun getSystolicBarData(): List<SystolicBarDatum?>? {
        return systolicBarData
    }

    fun setSystolicBarData(systolicBarData: List<SystolicBarDatum?>?) {
        this.systolicBarData = systolicBarData
    }

    fun getDiastolicBarData(): List<DiastolicBarDatum?>? {
        return diastolicBarData
    }

    fun setDiastolicBarData(diastolicBarData: List<DiastolicBarDatum?>?) {
        this.diastolicBarData = diastolicBarData
    }

    fun getBarTimestamp(): List<BPBarTimestamp?>? {
        return barTimestamp
    }

    fun setBarTimestamp(barTimestamp: List<BPBarTimestamp?>?) {
        this.barTimestamp = barTimestamp
    }

    fun getBarColorSystolic(): List<BarColorSystolic?>? {
        return barColorSystolic
    }

    fun setBarColorSystolic(barColorSystolic: List<BarColorSystolic?>?) {
        this.barColorSystolic = barColorSystolic
    }

    fun getBarColorDiastolic(): List<BarColorDiastolic?>? {
        return barColorDiastolic
    }

    fun setBarColorDiastolic(barColorDiastolic: List<BarColorDiastolic?>?) {
        this.barColorDiastolic = barColorDiastolic
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

class BPBarTimestamp {
    @SerializedName("timestamp")
    @Expose
    internal var timestamp: Int? = null

    fun getTimestamp(): Int? {
        return timestamp
    }

    fun setTimestamp(timestamp: Int?) {
        this.timestamp = timestamp
    }
}
