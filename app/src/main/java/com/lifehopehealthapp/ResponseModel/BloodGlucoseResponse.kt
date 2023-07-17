package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

@Keep
class BloodGlucoseResponse {
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
    internal var statusCode: Int? = null

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
        var data: Dataam? = null

        @SerializedName("status")
        @Expose
        var status: Int? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

    }
}

class Dataam {
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
    private var graphType: GraphTYpes? = null

    @SerializedName("bar_data")
    @Expose
    private var barData: ArrayList<Bardatums?>? = null

    @SerializedName("bar_timestamp")
    @Expose
    private var barTimestamp: List<BarTimeStamps?>? =
        null

    @SerializedName("bar_color_schemes")
    @Expose
    private var barColorSchemes: List<BarColorschemes?>? =
        null

    @SerializedName("start_date")
    @Expose
    private var startDate: Int? = null

    @SerializedName("end_date")
    @Expose
    private var endDate: Int? = null

    @SerializedName("graphGlucoseList")
    @Expose
    private var graphGlucoseList: List<GraphGlucoseList?>? = null

    @SerializedName("bar_data_list")
    @Expose
    private var barDataList: List<BarDataList?>? = null

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

    fun getGraphType(): GraphTYpes? {
        return graphType
    }

    fun setGraphType(graphType: GraphTYpes?) {
        this.graphType = graphType
    }

    fun getBarData(): List<Bardatums?>? {
        return barData
    }

    fun setBarData(barData: ArrayList<Bardatums?>?) {
        this.barData = barData
    }

    fun getBarTimestamp(): List<BarTimeStamps?>? {
        return barTimestamp
    }

    fun setBarTimestamp(barTimestamp: List<BarTimeStamps?>?) {
        this.barTimestamp = barTimestamp
    }

    fun getBarColorSchemes(): List<BarColorschemes?>? {
        return barColorSchemes
    }

    fun setBarColorSchemes(barColorSchemes: List<BarColorschemes?>?) {
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

    fun getGraphGlucoseList(): List<GraphGlucoseList?>? {
        return graphGlucoseList
    }

    fun setGraphGlucoseList(graphGlucoseList: List<GraphGlucoseList?>?) {
        this.graphGlucoseList = graphGlucoseList
    }

    fun getBarDataList(): List<BarDataList?>? {
        return barDataList
    }

    fun setBarDataList(barDataList: List<BarDataList?>?) {
        this.barDataList = barDataList
    }
}

class GraphGlucoseList {
    @SerializedName("glucoseId")
    @Expose
    internal var glucoseId: Int? = null

    @SerializedName("glucoseType")
    @Expose
    internal var glucoseType: String? = null

    @SerializedName("isAvailable")
    @Expose
    internal var isAvailable: Int? = null

    fun getGlucoseId(): Int? {
        return glucoseId
    }

    fun setGlucoseId(glucoseId: Int?) {
        this.glucoseId = glucoseId
    }

    fun getGlucoseType(): String? {
        return glucoseType
    }

    fun setGlucoseType(glucoseType: String?) {
        this.glucoseType = glucoseType
    }

    fun getIsAvailable(): Int? {
        return isAvailable
    }

    fun setIsAvailable(isAvailable: Int?) {
        this.isAvailable = isAvailable
    }
}

class GraphTYpes {
    @SerializedName("graph_type_data")
    @Expose
    internal var graphTypeData: String? = null

    fun getGraphTypeData(): String? {
        return graphTypeData
    }

    fun setGraphTypeData(graphTypeData: String?) {
        this.graphTypeData = graphTypeData
    }
}

class BarColorschemes {
    @SerializedName("color_hexcode")
    @Expose
    var colorHexcode: String? = ""

}

class BarDataList {
    @SerializedName("current_date")
    @Expose
    var currentDate: Int? = null

    @SerializedName("detail_list")
    @Expose
    var detailList: List<DetailList>? = null

}

class Bardatums {
    @SerializedName("bar_value")
    @Expose
    var barValue: Float? = 0F

}

class BarTimeStamps {
    @SerializedName("timestamp")
    @Expose
    var timestamp: Int? = 0

}

class DetailList {
    @SerializedName("meal_type")
    @Expose
    var mealType: String? = null

    @SerializedName("meal_date")
    @Expose
    var mealDate: Int? = null

    @SerializedName("meal_Value")
    @Expose
    var mealValue: Int? = null

}
