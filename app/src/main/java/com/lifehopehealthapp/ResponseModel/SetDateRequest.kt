package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class SetDateRequest {
    @SerializedName("date")
    @Expose
    private var date: String? = null

    @SerializedName("doctorId")
    @Expose
    private var doctorId: String? = null

    fun getDate(): String? {
        return date
    }

    fun setDate(date: String?) {
        this.date = date
    }

    fun getDoctorId(): String? {
        return doctorId
    }

    fun setDoctorId(doctorId: String?) {
        this.doctorId = doctorId
    }
}