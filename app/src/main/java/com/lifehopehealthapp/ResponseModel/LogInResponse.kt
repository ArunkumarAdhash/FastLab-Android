package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class LogInResponse {

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
        var data: Data? = null

        @SerializedName("status")
        @Expose
        var status: Int? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

    }

    class Data {
        @SerializedName("email")
        @Expose
        internal var email: String? = null

        @SerializedName("userId")
        @Expose
        internal var userId: String? = null

        @SerializedName("mobileNumber")
        @Expose
        internal var mobileNumber: String? = null

        @SerializedName("fname")
        @Expose
        internal var fname: String? = null

        @SerializedName("lname")
        @Expose
        internal var lname: String? = null

        @SerializedName("accessToken")
        @Expose
        internal var accessToken: String? = null

        @SerializedName("refreshToken")
        @Expose
        internal var refreshToken: String? = null

        @SerializedName("deviceToken")
        @Expose
        internal var deviceToken: String? = null

        @SerializedName("deviceType")
        @Expose
        internal var deviceType: Int? = null

        @SerializedName("profilePicture")
        @Expose
        internal var profilePicture: String? = null

        @SerializedName("isRegistered")
        @Expose
        internal var isRegistered: Boolean? = null

        @SerializedName("isProfileUpdate")
        @Expose
        internal var isProfileUpdate: Boolean? = false

        @SerializedName("isVerified")
        @Expose
        internal var isVerified: Int? = null

        @SerializedName("gender")
        @Expose
        internal var gender: String? = null

        @SerializedName("dob")
        @Expose
        internal var dob: String? = null

        @SerializedName("address")
        @Expose
        internal var address: String? = null

        @SerializedName("isAgree")
        @Expose
        internal var isAgree: Boolean? = null

        @SerializedName("locality")
        @Expose
        internal var locality: String? = null

        fun getEmail(): String? {
            return email
        }

        fun setEmail(email: String?) {
            this.email = email
        }

        fun getUserId(): String? {
            return userId
        }

        fun setUserId(userId: String?) {
            this.userId = userId
        }

        fun getMobileNumber(): String? {
            return mobileNumber
        }

        fun setMobileNumber(mobileNumber: String?) {
            this.mobileNumber = mobileNumber
        }

        fun getFname(): String? {
            return fname
        }

        fun setFname(fname: String?) {
            this.fname = fname
        }

        fun getLname(): String? {
            return lname
        }

        fun setLname(lname: String?) {
            this.lname = lname
        }

        fun getAccessToken(): String? {
            return accessToken
        }

        fun setAccessToken(accessToken: String?) {
            this.accessToken = accessToken
        }

        fun getRefreshToken(): String? {
            return refreshToken
        }

        fun setRefreshToken(refreshToken: String?) {
            this.refreshToken = refreshToken
        }

        fun getDeviceToken(): String? {
            return deviceToken
        }

        fun setDeviceToken(deviceToken: String?) {
            this.deviceToken = deviceToken
        }

        fun getDeviceType(): Int? {
            return deviceType
        }

        fun setDeviceType(deviceType: Int?) {
            this.deviceType = deviceType
        }

        fun getProfilePicture(): String? {
            return profilePicture
        }

        fun setProfilePicture(profilePicture: String?) {
            this.profilePicture = profilePicture
        }

        fun getIsRegistered(): Boolean? {
            return isRegistered
        }

        fun setIsRegistered(isRegistered: Boolean?) {
            this.isRegistered = isRegistered
        }

        fun getIsProfileUpdate(): Boolean? {
            return isProfileUpdate
        }

        fun setIsProfileUpdate(isProfileUpdate: Boolean?) {
            this.isProfileUpdate = isProfileUpdate
        }

        fun getIsVerified(): Int? {
            return isVerified
        }

        fun setIsVerified(isVerified: Int?) {
            this.isVerified = isVerified
        }

        fun getGender(): String? {
            return gender
        }

        fun setGender(gender: String?) {
            this.gender = gender
        }

        fun getDob(): String? {
            return dob
        }

        fun setDob(dob: String?) {
            this.dob = dob
        }

        fun getAddress(): String? {
            return address
        }

        fun setAddress(address: String?) {
            this.address = address
        }

        fun getIsAgree(): Boolean? {
            return isAgree
        }

        fun setIsAgree(isAgree: Boolean?) {
            this.isAgree = isAgree
        }

        fun getLocality(): String? {
            return locality
        }

        fun setLocality(locality: String?) {
            this.locality = locality
        }

    }
}