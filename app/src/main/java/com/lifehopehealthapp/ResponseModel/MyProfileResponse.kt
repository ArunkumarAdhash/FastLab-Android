package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class MyProfileResponse {

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

    class Data {
        @SerializedName("firstName")
        @Expose
        internal var firstName: String? = null

        @SerializedName("lastName")
        @Expose
        internal var lastName: String? = null

        @SerializedName("email")
        @Expose
        internal var email: String? = null

        @SerializedName("gender")
        @Expose
        internal var gender: String? = null

        @SerializedName("dob")
        @Expose
        internal var dob: String? = null

        @SerializedName("mobileNo")
        @Expose
        internal var mobileNo: String? = null

        @SerializedName("address")
        @Expose
        internal var address: String? = null

        @SerializedName("height")
        @Expose
        internal var height: String? = null

        @SerializedName("weight")
        @Expose
        internal var weight: String? = null

        @SerializedName("bloodGroup")
        @Expose
        internal var bloodGroup: String? = null

        @SerializedName("imageID")
        @Expose
        internal var imageID: String? = null

        @SerializedName("isVerified")
        @Expose
        internal var isVerified: Int? = null

        @SerializedName("isProfileUpdate")
        @Expose
        private var isProfileUpdate: Boolean? = null

        @SerializedName("isAgree")
        @Expose
        private var isAgree: Boolean? = null

        fun getFirstName(): String? {
            return firstName
        }

        fun setFirstName(firstName: String?) {
            this.firstName = firstName
        }

        fun getLastName(): String? {
            return lastName
        }

        fun setLastName(lastName: String?) {
            this.lastName = lastName
        }

        fun getEmail(): String? {
            return email
        }

        fun setEmail(email: String?) {
            this.email = email
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

        fun getMobileNo(): String? {
            return mobileNo
        }

        fun setMobileNo(mobileNo: String?) {
            this.mobileNo = mobileNo
        }

        fun getAddress(): String? {
            return address
        }

        fun setAddress(address: String?) {
            this.address = address
        }

        fun getHeight(): String? {
            return height
        }

        fun setHeight(height: String?) {
            this.height = height
        }

        fun getWeight(): String? {
            return weight
        }

        fun setWeight(weight: String?) {
            this.weight = weight
        }

        fun getBloodGroup(): String? {
            return bloodGroup
        }

        fun setBloodGroup(bloodGroup: String?) {
            this.bloodGroup = bloodGroup
        }

        fun getImageID(): String? {
            return imageID
        }

        fun setImageID(imageID: String?) {
            this.imageID = imageID
        }

        fun getIsVerified(): Int? {
            return isVerified
        }

        fun setIsVerified(isVerified: Int?) {
            this.isVerified = isVerified
        }

        fun getIsProfileUpdate(): Boolean? {
            return isProfileUpdate
        }

        fun setIsProfileUpdate(isProfileUpdate: Boolean?) {
            this.isProfileUpdate = isProfileUpdate
        }

        fun getIsAgree(): Boolean? {
            return isAgree
        }

        fun setIsAgree(isAgree: Boolean?) {
            this.isAgree = isAgree
        }
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
}