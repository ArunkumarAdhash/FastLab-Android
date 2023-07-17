package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class LogInDataModel {
    constructor(
        fname: String,
        lname: String,
        email: String,
        userId: String,
        mobileNumber: String,
        accessToken: String,
        refreshToken: String,
        deviceToken: String,
        deviceType: Int,
        profilePicture: String,
        isVerified: Int,
        fcmtoken: String
    ) {
        this.fname = fname
        this.lname = lname
        this.email = email
        this.userId = userId
        this.mobileNumber = mobileNumber
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.deviceToken = deviceToken
        this.deviceType = deviceType
        this.profilePicture = profilePicture
        this.isVerified = isVerified
        this.fcmtoken = fcmtoken
    }

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("userId")
    @Expose
    var userId: String? = null

    @SerializedName("mobileNumber")
    @Expose
    var mobileNumber: String? = null

    @SerializedName("fname")
    @Expose
    var fname: String? = null

    @SerializedName("lname")
    @Expose
    var lname: String? = null

    @SerializedName("accessToken")
    @Expose
    var accessToken: String? = null

    @SerializedName("refreshToken")
    @Expose
    var refreshToken: String? = null

    @SerializedName("deviceToken")
    @Expose
    var deviceToken: String? = null

    @SerializedName("deviceType")
    @Expose
    var deviceType: Int? = null

    @SerializedName("profilePicture")
    @Expose
    var profilePicture: String? = null

    @SerializedName("isRegistered")
    @Expose
    var isRegistered: Boolean? = null

    @SerializedName("isVerified")
    @Expose
    var isVerified: Int? = null

    @SerializedName("fcmtoken")
    @Expose
    var fcmtoken: String? = ""


    fun LogInDataModel(
        email: String?,
        userId: String?,
        mobileNumber: String?,
        fname: String?,
        lname: String?,
        accessToken: String?,
        refreshToken: String?,
        deviceToken: String?,
        deviceType: Int?,
        profilePicture: String?,
        isVerified: Int?,
        fcmtoken: String?
    ) {
        this.fname = fname
        this.lname = lname
        this.email = email
        this.userId = userId
        this.mobileNumber = mobileNumber
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.deviceToken = deviceToken
        this.deviceType = deviceType
        this.profilePicture = profilePicture
        this.isVerified = isVerified
        this.fcmtoken = fcmtoken
    }
}