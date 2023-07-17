package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class SetDateResponse {
    @SerializedName("Value")
    @Expose
    private var value: Values? = null

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
    private var statusCode: Long? = null

    fun getValue(): Values? {
        return value
    }

    fun setValue(value: Values?) {
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

    fun getStatusCode(): Long? {
        return statusCode
    }

    fun setStatusCode(statusCode: Long?) {
        this.statusCode = statusCode
    }
}

class Values {
    @SerializedName("profilePic")
    @Expose
    private var profilePic: String? = null

    @SerializedName("doctorName")
    @Expose
    private var doctorName: String? = null

    @SerializedName("categroyName")
    @Expose
    private var categroyName: String? = null

    @SerializedName("experience")
    @Expose
    private var experience: String? = null

    @SerializedName("education")
    @Expose
    private var education: String? = null

    @SerializedName("price")
    @Expose
    private var price: Double? = null

    @SerializedName("isAvailable")
    @Expose
    private var isAvailable: Boolean? = null

    @SerializedName("doctorId")
    @Expose
    private var doctorId: String? = null

    @SerializedName("specialisation")
    @Expose
    internal var specialisation: String? = ""

    @SerializedName("listOfDates")
    @Expose
    private var listOfDates: List<ListOfDate?>? = null

    fun getProfilePic(): String? {
        return profilePic
    }

    fun setProfilePic(profilePic: String?) {
        this.profilePic = profilePic
    }

    fun getDoctorName(): String? {
        return doctorName
    }

    fun setDoctorName(doctorName: String?) {
        this.doctorName = doctorName
    }

    fun getCategroyName(): String? {
        return categroyName
    }

    fun setCategroyName(categroyName: String?) {
        this.categroyName = categroyName
    }

    fun getExperience(): String? {
        return experience
    }

    fun setExperience(experience: String?) {
        this.experience = experience
    }

    fun getEducation(): String? {
        return education
    }

    fun setEducation(education: String?) {
        this.education = education
    }

    fun getPrice(): Double? {
        return price
    }

    fun setPrice(price: Double?) {
        this.price = price
    }

    fun getIsAvailable(): Boolean? {
        return isAvailable
    }

    fun setIsAvailable(isAvailable: Boolean?) {
        this.isAvailable = isAvailable
    }

    fun getDoctorId(): String? {
        return doctorId
    }

    fun setDoctorId(doctorId: String?) {
        this.doctorId = doctorId
    }

    fun getListOfDates(): List<ListOfDate?>? {
        return listOfDates
    }

    fun setListOfDates(listOfDates: List<ListOfDate?>?) {
        this.listOfDates = listOfDates
    }

}

class ListOfDate {
    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("morningSlot")
    @Expose
    var morningSlot: List<MorningSlot>? = null

    @SerializedName("eveningSlot")
    @Expose
    var eveningSlot: List<EveningSlot>? = null
}

class MorningSlot {
    @SerializedName("startTime")
    @Expose
    var startTime: String? = null

    @SerializedName("endTime")
    @Expose
    var endTime: String? = null

    @SerializedName("isActive")
    @Expose
    var isActive: Boolean? = null

    @SerializedName("slotId")
    @Expose
    var slotId: String? = ""
}

class EveningSlot {
    @SerializedName("startTime")
    @Expose
    var startTime: String? = null

    @SerializedName("endTime")
    @Expose
    var endTime: String? = null

    @SerializedName("isActive")
    @Expose
    var isActive: Boolean? = null

    @SerializedName("slotId")
    @Expose
    var slotId: String? = ""
}