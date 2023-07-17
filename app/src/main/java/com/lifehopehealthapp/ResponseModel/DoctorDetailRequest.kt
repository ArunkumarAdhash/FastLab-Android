package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class DoctorDetailRequest {
    @SerializedName("doctorId")
    @Expose
    private var doctorId: String? = null

    fun getDoctorId(): String? {
        return doctorId
    }

    fun setDoctorId(doctorId: String?) {
        this.doctorId = doctorId
    }
}