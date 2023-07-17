package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

@Keep
class OrderSummaryInputModel {
    @SerializedName("address")
    @Expose
    private var address: String? = null

    @SerializedName("latLong")
    @Expose
    private var latLong: String? = null

    @SerializedName("currentlatLong")
    @Expose
    private var currentlatLong: String? = null

    @SerializedName("primaryName")
    @Expose
    private var primaryName: String? = null

    @SerializedName("orderDate")
    @Expose
    private var orderDate: Int? = null

    @SerializedName("imagePath")
    @Expose
    private var imagePath: String? = null

    @SerializedName("iOSImagePath")
    @Expose
    private var iOSImagePath: String? = null

    @SerializedName("phoneNo")
    @Expose
    private var phoneNo: String? = null

    @SerializedName("members")
    @Expose
    private var members: List<Member?>? = null

    @SerializedName("payment")
    @Expose
    private var payment: List<Payment?>? = null

    @SerializedName("testTypeID")
    @Expose
    private var testTypeID: String? = null

    @SerializedName("scheduleDate")
    @Expose
    private var scheduleDate: Int? = null

    @SerializedName("testCategoryId")
    @Expose
    private var testCategoryId: String? = null

    @SerializedName("location")
    @Expose
    private var location: String? = null

    @SerializedName("state")
    @Expose
    private var state: String? = null

    @SerializedName("userFCMToken")
    @Expose
    private var userFCMToken: String? = null

    @SerializedName("BookingType")
    @Expose
    internal var BookingType: Int = 0

    @SerializedName("bookingType")
    @Expose
    internal var bookingtype: Int = 0

    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String?) {
        this.address = address
    }

    fun getLatLong(): String? {
        return latLong
    }

    fun setLatLong(latLong: String?) {
        this.latLong = latLong
    }

    fun getCurrentlatLong(): String? {
        return currentlatLong
    }

    fun setCurrentlatLong(currentlatLong: String?) {
        this.currentlatLong = currentlatLong
    }

    fun getPrimaryName(): String? {
        return primaryName
    }

    fun setPrimaryName(primaryName: String?) {
        this.primaryName = primaryName
    }

    fun getOrderDate(): Int? {
        return orderDate
    }

    fun setOrderDate(orderDate: Int?) {
        this.orderDate = orderDate
    }

    fun getImagePath(): String? {
        return imagePath
    }

    fun setImagePath(imagePath: String?) {
        this.imagePath = imagePath
    }

    fun getIOSImagePath(): String? {
        return iOSImagePath
    }

    fun setIOSImagePath(iOSImagePath: String?) {
        this.iOSImagePath = iOSImagePath
    }

    fun getPhoneNo(): String? {
        return phoneNo
    }

    fun setPhoneNo(phoneNo: String?) {
        this.phoneNo = phoneNo
    }

    fun getMembers(): List<Member?>? {
        return members
    }

    fun setMembers(members: List<Member?>?) {
        this.members = members
    }

    fun getPayment(): List<Payment?>? {
        return payment
    }

    fun setPayment(payment: List<Payment?>?) {
        this.payment = payment
    }

    fun getTestTypeID(): String? {
        return testTypeID
    }

    fun setTestTypeID(testTypeID: String?) {
        this.testTypeID = testTypeID
    }

    fun getScheduleDate(): Int? {
        return scheduleDate
    }

    fun setScheduleDate(scheduleDate: Int?) {
        this.scheduleDate = scheduleDate
    }

    fun getTestCategoryId(): String? {
        return testCategoryId
    }

    fun setTestCategoryId(testCategoryId: String?) {
        this.testCategoryId = testCategoryId
    }

    fun getLocation(): String? {
        return location
    }

    fun setLocation(location: String?) {
        this.location = location
    }

    fun getState(): String? {
        return state
    }

    fun setState(state: String?) {
        this.state = state
    }

    fun getUserFCMToken(): String? {
        return userFCMToken
    }

    fun setUserFCMToken(userFCMToken: String?) {
        this.userFCMToken = userFCMToken
    }

}

class Member {
    @SerializedName("patientName")
    @Expose
    var patientName: String? = ""

    @SerializedName("age")
    @Expose
    var age: Int? = 0

    @SerializedName("gender")
    @Expose
    var gender: String? = ""

}

class Payment {
    @SerializedName("paymentName")
    @Expose
    var paymentName: String? = ""

    @SerializedName("testCount")
    @Expose
    var testCount: Int? = 0

    @SerializedName("amount")
    @Expose
    var amount: Double? = null

}

class ProfileDetail {
    @SerializedName("firstName")
    @Expose
    internal var firstName: String? = ""

    @SerializedName("lastName")
    @Expose
    internal var lastName: String? = ""

    @SerializedName("gender")
    @Expose
    internal var gender: String? = ""

    @SerializedName("dob")
    @Expose
    internal var dob: Int? = 0

    @SerializedName("profilePicture")
    @Expose
    internal var profilePicture: String? = ""

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

    fun getGender(): String? {
        return gender
    }

    fun setGender(gender: String?) {
        this.gender = gender
    }

    fun getDob(): Int? {
        return dob
    }

    fun setDob(dob: Int?) {
        this.dob = dob
    }

    fun getProfilePicture(): String? {
        return profilePicture
    }

    fun setProfilePicture(profilePicture: String?) {
        this.profilePicture = profilePicture
    }
}