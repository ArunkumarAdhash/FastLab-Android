package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class DoctorPaymentRequest {
    @SerializedName("appointmentId")
    @Expose
    private var appointmentId: String? = null

    @SerializedName("paymentStatus")
    @Expose
    private var paymentStatus: Int? = null

    fun getAppointmentId(): String? {
        return appointmentId
    }

    fun setAppointmentId(appointmentId: String?) {
        this.appointmentId = appointmentId
    }

    fun getPaymentStatus(): Int? {
        return paymentStatus
    }

    fun setPaymentStatus(paymentStatus: Int?) {
        this.paymentStatus = paymentStatus
    }
}