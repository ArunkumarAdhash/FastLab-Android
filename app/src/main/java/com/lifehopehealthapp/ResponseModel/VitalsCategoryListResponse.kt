package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class VitalsCategoryListResponse {
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
        var data: Datum? = null

        @SerializedName("status")
        @Expose
        var status: Int? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

    }
    class Datum {
        @SerializedName("andImagePath")
        @Expose
        private var andImagePath: String? = null

        @SerializedName("iAppleImagePath")
        @Expose
        private var iAppleImagePath: String? = null

        @SerializedName("header")
        @Expose
        private var header: String? = null

        @SerializedName("description")
        @Expose
        private var description: String? = null

        @SerializedName("vitalMenu")
        @Expose
        private var vitalMenu: List<VitalMenu?>? = null

        @SerializedName("Id")
        @Expose
        private var id: Int? = null

        @SerializedName("imagePath")
        @Expose
        private var imagePath: Any? = null

        @SerializedName("iosImagePath")
        @Expose
        private var iosImagePath: Any? = null

        @SerializedName("name")
        @Expose
        private var name: Any? = null

        @SerializedName("shortName")
        @Expose
        private var shortName: Any? = null

        @SerializedName("graphMinVal")
        @Expose
        private var graphMinVal: Int? = null

        @SerializedName("graphMaxVal")
        @Expose
        private var graphMaxVal: Int? = null

        @SerializedName("graphMetrics")
        @Expose
        private var graphMetrics: Any? = null

        @SerializedName("graphDescription")
        @Expose
        private var graphDescription: Any? = null

        @SerializedName("lastRecordDate")
        @Expose
        private var lastRecordDate: Int? = null

        @SerializedName("lastData")
        @Expose
        private var lastData: Any? = null

        fun getAndImagePath(): String? {
            return andImagePath
        }

        fun setAndImagePath(andImagePath: String?) {
            this.andImagePath = andImagePath
        }

        fun getIAppleImagePath(): String? {
            return iAppleImagePath
        }

        fun setIAppleImagePath(iAppleImagePath: String?) {
            this.iAppleImagePath = iAppleImagePath
        }

        fun getHeader(): String? {
            return header
        }

        fun setHeader(header: String?) {
            this.header = header
        }

        fun getDescription(): String? {
            return description
        }

        fun setDescription(description: String?) {
            this.description = description
        }

        fun getVitalMenu(): List<VitalMenu?>? {
            return vitalMenu
        }

        fun setVitalMenu(vitalMenu: List<VitalMenu?>?) {
            this.vitalMenu = vitalMenu
        }

        fun getId(): Int? {
            return id
        }

        fun setId(id: Int?) {
            this.id = id
        }

        fun getImagePath(): Any? {
            return imagePath
        }

        fun setImagePath(imagePath: Any?) {
            this.imagePath = imagePath
        }

        fun getIosImagePath(): Any? {
            return iosImagePath
        }

        fun setIosImagePath(iosImagePath: Any?) {
            this.iosImagePath = iosImagePath
        }

        fun getName(): Any? {
            return name
        }

        fun setName(name: Any?) {
            this.name = name
        }

        fun getShortName(): Any? {
            return shortName
        }

        fun setShortName(shortName: Any?) {
            this.shortName = shortName
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

        fun getGraphMetrics(): Any? {
            return graphMetrics
        }

        fun setGraphMetrics(graphMetrics: Any?) {
            this.graphMetrics = graphMetrics
        }

        fun getGraphDescription(): Any? {
            return graphDescription
        }

        fun setGraphDescription(graphDescription: Any?) {
            this.graphDescription = graphDescription
        }

        fun getLastRecordDate(): Int? {
            return lastRecordDate
        }

        fun setLastRecordDate(lastRecordDate: Int?) {
            this.lastRecordDate = lastRecordDate
        }

        fun getLastData(): Any? {
            return lastData
        }

        fun setLastData(lastData: Any?) {
            this.lastData = lastData
        }

    }

    class VitalMenu {
        @SerializedName("Id")
        @Expose
        internal var id: Int? = null

        @SerializedName("imagePath")
        @Expose
        internal var imagePath: String? = null

        @SerializedName("iosImagePath")
        @Expose
        private var iosImagePath: String? = null

        @SerializedName("name")
        @Expose
        private var name: String? = null

        @SerializedName("shortName")
        @Expose
        internal var shortName: String? = null

        @SerializedName("graphMinVal")
        @Expose
        private var graphMinVal: Int? = null

        @SerializedName("graphMaxVal")
        @Expose
        private var graphMaxVal: Int? = null

        @SerializedName("graphMetrics")
        @Expose
        private var graphMetrics: String? = null

        @SerializedName("graphDescription")
        @Expose
        private var graphDescription: String? = null

        @SerializedName("lastRecordDate")
        @Expose
        internal var lastRecordDate: Int? = null

        @SerializedName("lastData")
        @Expose
        internal var lastData: String? = null

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

        fun getIosImagePath(): String? {
            return iosImagePath
        }

        fun setIosImagePath(iosImagePath: String?) {
            this.iosImagePath = iosImagePath
        }

        fun getName(): String? {
            return name
        }

        fun setName(name: String?) {
            this.name = name
        }

        fun getShortName(): String? {
            return shortName
        }

        fun setShortName(shortName: String?) {
            this.shortName = shortName
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

        fun getGraphDescription(): String? {
            return graphDescription
        }

        fun setGraphDescription(graphDescription: String?) {
            this.graphDescription = graphDescription
        }

        fun getLastRecordDate(): Int? {
            return lastRecordDate
        }

        fun setLastRecordDate(lastRecordDate: Int?) {
            this.lastRecordDate = lastRecordDate
        }

        fun getLastData(): String? {
            return lastData
        }

        fun setLastData(lastData: String?) {
            this.lastData = lastData
        }
    }
}
