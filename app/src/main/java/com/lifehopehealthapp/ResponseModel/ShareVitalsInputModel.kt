package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

@Keep
class ShareVitalsInputModel {
    @SerializedName("fromDate")
    @Expose
    private var fromDate: Int? = null

    @SerializedName("toDate")
    @Expose
    private var toDate: Int? = null

    @SerializedName("vitalId")
    @Expose
    private var vitalId: Int? = null

    @SerializedName("type")
    @Expose
    private var type: Int? = null

    fun getFromDate(): Int? {
        return fromDate
    }

    fun setFromDate(fromDate: Int?) {
        this.fromDate = fromDate
    }

    fun getToDate(): Int? {
        return toDate
    }

    fun setToDate(toDate: Int?) {
        this.toDate = toDate
    }

    fun getVitalId(): Int? {
        return vitalId
    }

    fun setVitalId(vitalId: Int?) {
        this.vitalId = vitalId
    }

    fun getType(): Int? {
        return type
    }

    fun setType(type: Int?) {
        this.type = type
    }
}