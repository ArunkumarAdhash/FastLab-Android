package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

@Keep
class BloodPressureInputModel {
    @SerializedName("vitalId")
    @Expose
    private var vitalId: Int? = null

    @SerializedName("startdate")
    @Expose
    private var startdate: Int? = null

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
}