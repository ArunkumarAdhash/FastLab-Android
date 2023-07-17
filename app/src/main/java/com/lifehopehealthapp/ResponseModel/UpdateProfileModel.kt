package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep

@Keep
class UpdateProfileModel {
    constructor(
        firstName: String,
        lastName: String,
        dob: String,
        mobileNo: String,
        address: String,
        height: String,
        weight: String,
        bloodGroup: String,
        gender: String,
        isProfileUpdate: Boolean,
        isVerified: Int,
        email: String
    ) {
        this.firstName = firstName
        this.lastName = lastName
        this.dob = dob
        this.mobileNo = mobileNo
        this.address = address
        this.height = height
        this.weight = weight
        this.bloodGroup = bloodGroup
        this.gender = gender
        this.isProfileUpdate = isProfileUpdate
        this.isVerified = isVerified
        this.email = email
    }

    private var firstName: String? = null
    private var lastName: String? = null
    private var dob: String? = null
    private var mobileNo: String? = null
    private var address: String? = null
    private var height: String? = null
    private var weight: String? = null
    private var bloodGroup: String? = null
    private var gender: String? = null
    private var isProfileUpdate: Boolean? = null
    private var isVerified: Int? = null
    private var email: String? = ""
}