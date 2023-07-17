package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class DoctorListResponse {

    @SerializedName("Value")
    @Expose
    private var value: Value? = null

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
    private var statusCode: Int? = null

    fun getValue(): Value? {
        return value
    }

    fun setValue(value: Value?) {
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

    fun getStatusCode(): Int? {
        return statusCode
    }

    fun setStatusCode(statusCode: Int?) {
        this.statusCode = statusCode
    }

    class Value {
        @SerializedName("data")
        @Expose
        var data: List<Datum>? = null

        @SerializedName("pageNumber")
        @Expose
        var pageNumber: Int? = null

        @SerializedName("pageSize")
        @Expose
        var pageSize: Int? = null

        @SerializedName("totalPages")
        @Expose
        var totalPages: Int? = null

        @SerializedName("nextPage")
        @Expose
        var nextPage: String? = null

        @SerializedName("previousPage")
        @Expose
        var previousPage: Int? = null
    }

    class Datum {
        @SerializedName("id")
        @Expose
        private var id: String? = null

        @SerializedName("doctorName")
        @Expose
        private var doctorName: String? = null

        @SerializedName("categoryName")
        @Expose
        private var categoryName: String? = null

        @SerializedName("location")
        @Expose
        private var location: String? = null

        @SerializedName("education")
        @Expose
        private var education: String? = null

        @SerializedName("yearOfExperience")
        @Expose
        private var yearOfExperience: String = "0"

        @SerializedName("experienceMonth")
        @Expose
        var experienceMonth: String = "0"

        @SerializedName("profilePic")
        @Expose
        private var profilePic: String? = null

        @SerializedName("aboutUs")
        @Expose
        private var aboutUs: String? = null

        @SerializedName("status")
        @Expose
        private var status: Long? = null

        @SerializedName("patientsCount")
        @Expose
        private var patientsCount: Long? = null

        @SerializedName("isMessage")
        @Expose
        private var isMessage: Boolean? = null

        @SerializedName("isCall")
        @Expose
        private var isCall: Boolean? = null

        @SerializedName("iVideoCall")
        @Expose
        private var iVideoCall: Boolean? = null

        fun getId(): String? {
            return id
        }

        fun setId(id: String?) {
            this.id = id
        }

        fun getDoctorName(): String? {
            return doctorName
        }

        fun setDoctorName(doctorName: String?) {
            this.doctorName = doctorName
        }

        fun getCategoryName(): String? {
            return categoryName
        }

        fun setCategoryName(categoryName: String?) {
            this.categoryName = categoryName
        }

        fun getLocation(): String? {
            return location
        }

        fun setLocation(location: String?) {
            this.location = location
        }

        fun getEducation(): String? {
            return education
        }

        fun setEducation(education: String?) {
            this.education = education
        }

        fun getYearOfExperience(): String {
            return yearOfExperience
        }

        fun setYearOfExperience(yearOfExperience: String) {
            this.yearOfExperience = yearOfExperience
        }


        fun getMonthOfExperience(): String {
            return experienceMonth
        }

        fun setMonthOfExperience(monthOfExperience: String) {
            this.experienceMonth = monthOfExperience
        }

        fun getProfilePic(): String? {
            return profilePic
        }

        fun setProfilePic(profilePic: String?) {
            this.profilePic = profilePic
        }

        fun getAboutUs(): String? {
            return aboutUs
        }

        fun setAboutUs(aboutUs: String?) {
            this.aboutUs = aboutUs
        }

        fun getStatus(): Long? {
            return status
        }

        fun setStatus(status: Long?) {
            this.status = status
        }

        fun getPatientsCount(): Long? {
            return patientsCount
        }

        fun setPatientsCount(patientsCount: Long?) {
            this.patientsCount = patientsCount
        }

        fun getIsMessage(): Boolean? {
            return isMessage
        }

        fun setIsMessage(isMessage: Boolean?) {
            this.isMessage = isMessage
        }

        fun getIsCall(): Boolean? {
            return isCall
        }

        fun setIsCall(isCall: Boolean?) {
            this.isCall = isCall
        }

        fun getiVideoCall(): Boolean? {
            return iVideoCall
        }

        fun setiVideoCall(iVideoCall: Boolean?) {
            this.iVideoCall = iVideoCall
        }

    }
}