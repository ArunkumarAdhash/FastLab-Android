package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

@Keep
class TemperatureInputModel {
    @SerializedName("vitalId")
    @Expose
    private var vitalId: Int? = null

    @SerializedName("startdate")
    @Expose
    private var startdate: Int? = null

    @SerializedName("enddate")
    @Expose
    private var enddate: Int? = null

    fun getVitalId(): Int? {
        return vitalId
    }

    fun setVitalId(vitalId: Int?) {
        this.vitalId = vitalId
    }

    fun getStartdate(): Int? {
        return startdate
    }

    fun setStartdate(startdate: Int?) {
        this.startdate = startdate
    }

    fun getEnddate(): Int? {
        return enddate
    }

    fun setEnddate(enddate: Int?) {
        this.enddate = enddate
    }
}