package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class DoctorDetailResponse {

    @SerializedName("Value")
    @Expose
    private var value: Value? = null

    @SerializedName("StatusCode")
    @Expose
    private var statusCode: Long? = null

    fun getValue(): Value? {
        return value
    }

    fun setValue(value: Value?) {
        this.value = value
    }

    fun getStatusCode(): Long? {
        return statusCode
    }

    fun setStatusCode(statusCode: Long?) {
        this.statusCode = statusCode
    }

    class Value {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("doctorName")
        @Expose
        var doctorName: String? = null

        @SerializedName("categoryName")
        @Expose
        var categoryName: String? = null

        @SerializedName("location")
        @Expose
        var location: String? = null

        @SerializedName("education")
        @Expose
        var education: String? = null

        @SerializedName("yearOfExperience")
        @Expose
        var yearOfExperience: String = "0"

        @SerializedName("experienceMonth")
        @Expose
        var experienceMonth: String = "0"


        @SerializedName("profilePic")
        @Expose
        var profilePic: String? = null

        @SerializedName("aboutUs")
        @Expose
        var aboutUs: String? = null

        @SerializedName("status")
        @Expose
        var status: Long? = null

        @SerializedName("patientsCount")
        @Expose
        var patientsCount: Long? = null

        @SerializedName("isMessage")
        @Expose
        var isMessage: Boolean? = null

        @SerializedName("isCall")
        @Expose
        var isCall: Boolean? = null

        @SerializedName("iVideoCall")
        @Expose
        private var iVideoCall: Boolean? = null
        fun getiVideoCall(): Boolean? {
            return iVideoCall
        }

        fun setiVideoCall(iVideoCall: Boolean?) {
            this.iVideoCall = iVideoCall
        }
    }
}