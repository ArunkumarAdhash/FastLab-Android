package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


@Keep
class VitalsHistoryResponse {

    @SerializedName("data")
    @Expose
    internal var data: ArrayList<VitalsDatum>? = null

    @SerializedName("pageNumber")
    @Expose
    private var pageNumber: Int? = null

    @SerializedName("pageSize")
    @Expose
    private var pageSize: Int? = null

    @SerializedName("totalPages")
    @Expose
    internal var totalPages: Int? = null

    @SerializedName("nextPage")
    @Expose
    private var nextPage: String? = null

    @SerializedName("previousPage")
    @Expose
    private var previousPage: Any? = null

    fun getData(): ArrayList<VitalsDatum>? {
        return data
    }

    fun setData(data: ArrayList<VitalsDatum>?) {
        this.data = data
    }

    fun getPageNumber(): Int? {
        return pageNumber
    }

    fun setPageNumber(pageNumber: Int?) {
        this.pageNumber = pageNumber
    }

    fun getPageSize(): Int? {
        return pageSize
    }

    fun setPageSize(pageSize: Int?) {
        this.pageSize = pageSize
    }

    fun getTotalPages(): Int? {
        return totalPages
    }

    fun setTotalPages(totalPages: Int?) {
        this.totalPages = totalPages
    }

    fun getNextPage(): String? {
        return nextPage
    }

    fun setNextPage(nextPage: String?) {
        this.nextPage = nextPage
    }

    fun getPreviousPage(): Any? {
        return previousPage
    }

    fun setPreviousPage(previousPage: Any?) {
        this.previousPage = previousPage
    }
}

class VitalsDatum {
    @SerializedName("date")
    @Expose
    internal var date: Int? = 0

    @SerializedName("value")
    @Expose
    internal var value: String? = ""

    @SerializedName("vitalId")
    @Expose
    private var vitalId: Int? = 0

    @SerializedName("Id")
    @Expose
    private var id: Int? = 0

    @SerializedName("imagePath")
    @Expose
    internal var imagePath: String? = ""

    @SerializedName("iosImagePath")
    @Expose
    private var iosImagePath: String? = ""

    @SerializedName("name")
    @Expose
    internal var name: String? = ""

    @SerializedName("shortName")
    @Expose
    private var shortName: String? = ""

    @SerializedName("lastRecordDate")
    @Expose
    private var lastRecordDate: Int? = 0

    @SerializedName("lastData")
    @Expose
    private var lastData: String? = ""

    fun getDate(): Int? {
        return date
    }

    fun setDate(date: Int?) {
        this.date = date
    }

    fun getValue(): String? {
        return value
    }

    fun setValue(value: String?) {
        this.value = value
    }

    fun getVitalId(): Int? {
        return vitalId
    }

    fun setVitalId(vitalId: Int?) {
        this.vitalId = vitalId
    }

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