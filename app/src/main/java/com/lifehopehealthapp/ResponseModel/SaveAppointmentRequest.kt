package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class SaveAppointmentRequest {

    @SerializedName("doctorId")
    @Expose
    private var doctorId: String? = null

    @SerializedName("patientId")
    @Expose
    private var patientId: String? = null

    @SerializedName("slotId")
    @Expose
    private var slotId: String? = null

    @SerializedName("date")
    @Expose
    private var date: Int? = null

    @SerializedName("startTime")
    @Expose
    private var startTime: Long? = null

    @SerializedName("endTime")
    @Expose
    private var endTime: Long? = null

    @SerializedName("description")
    @Expose
    private var description: String? = null

    @SerializedName("amount")
    @Expose
    private var amount: Double? = null

    fun getDoctorId(): String? {
        return doctorId
    }

    fun setDoctorId(doctorId: String?) {
        this.doctorId = doctorId
    }

    fun getPatientId(): String? {
        return patientId
    }

    fun setPatientId(patientId: String?) {
        this.patientId = patientId
    }

    fun getSlotId(): String? {
        return slotId
    }

    fun setSlotId(slotId: String?) {
        this.slotId = slotId
    }

    fun getDate(): Int? {
        return date
    }

    fun setDate(date: Int?) {
        this.date = date
    }

    fun getStartTime(): Long? {
        return startTime
    }

    fun setStartTime(startTime: Long?) {
        this.startTime = startTime
    }

    fun getEndTime(): Long? {
        return endTime
    }

    fun setEndTime(endTime: Long?) {
        this.endTime = endTime
    }

    fun getDescription(): String? {
        return description
    }

    fun setDescription(description: String?) {
        this.description = description
    }

    fun getAmount(): Double? {
        return amount
    }

    fun setAmount(amount: Double?) {
        this.amount = amount
    }
}