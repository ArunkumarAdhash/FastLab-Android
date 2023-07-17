package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class BulkBookingOrderAPIRequest {

    @SerializedName("userFCMToken")
    @Expose
    private var userFCMToken: String? = null

    @SerializedName("testCategoryId")
    @Expose
    private var testCategoryId: String? = null

    @SerializedName("locality")
    @Expose
    private var locality: String? = null

    @SerializedName("orderDate")
    @Expose
    private var orderDate: Int? = null

    @SerializedName("payment")
    @Expose
    private var payment: List<Payments?>? = null

    @SerializedName("primaryName")
    @Expose
    private var primaryName: String? = null

    @SerializedName("members")
    @Expose
    private var members: List<Memberes?>? = null

    @SerializedName("address")
    @Expose
    private var address: String? = null

    @SerializedName("scheduleDate")
    @Expose
    private var scheduleDate: Int? = null

    @SerializedName("state")
    @Expose
    private var state: String? = null

    @SerializedName("testTypeID")
    @Expose
    private var testTypeID: String? = null

    @SerializedName("phoneNo")
    @Expose
    private var phoneNo: String? = null

    @SerializedName("imagePath")
    @Expose
    private var imagePath: String? = null

    @SerializedName("latLong")
    @Expose
    private var latLong: String? = null

    @SerializedName("iosimagepath")
    @Expose
    private var iosimagepath: String? = null

    @SerializedName("location")
    @Expose
    private var location: String? = null

    @SerializedName("Currentlatlong")
    @Expose
    private var currentlatlong: String? = null

    @SerializedName("bookingType")
    @Expose
    private var bookingType: Int? = null

    @SerializedName("organizationList")
    @Expose
    private var organizationList: List<Organization?>? =
        null

    @SerializedName("organizationType")
    @Expose
    private var organizationType: String? = null

    @SerializedName("organizationId")
    @Expose
    private var organizationId: Int? = 0


    @SerializedName("orderSummary")
    @Expose
    private var orderSummary: List<OrderSummary?>? = null

    @SerializedName("title")
    @Expose
    private var title: String? = null

    @SerializedName("email")
    @Expose
    private var email: String? = null

    @SerializedName("contactname")
    @Expose
    private var contactname: String? = null

    @SerializedName("contractList")
    @Expose
    private var contractList: ContractList? = null

    fun getUserFCMToken(): String? {
        return userFCMToken
    }

    fun setOrganizationID(organizationId: Int?) {
        this.organizationId = organizationId
    }

    fun getOrganizationID(): Int? {
        return organizationId
    }

    fun setUserFCMToken(userFCMToken: String?) {
        this.userFCMToken = userFCMToken
    }

    fun getTestCategoryId(): String? {
        return testCategoryId
    }

    fun setTestCategoryId(testCategoryId: String?) {
        this.testCategoryId = testCategoryId
    }

    fun getLocality(): String? {
        return locality
    }

    fun setLocality(locality: String?) {
        this.locality = locality
    }

    fun getOrderDate(): Int? {
        return orderDate
    }

    fun setOrderDate(orderDate: Int?) {
        this.orderDate = orderDate
    }

    fun getPayment(): List<Payments?>? {
        return payment
    }

    fun setPayment(payment: List<Payments?>?) {
        this.payment = payment
    }

    fun getPrimaryName(): String? {
        return primaryName
    }

    fun setPrimaryName(primaryName: String?) {
        this.primaryName = primaryName
    }

    fun getMembers(): List<Memberes?>? {
        return members
    }

    fun setMembers(members: List<Memberes?>?) {
        this.members = members
    }

    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String?) {
        this.address = address
    }

    fun getScheduleDate(): Int? {
        return scheduleDate
    }

    fun setScheduleDate(scheduleDate: Int?) {
        this.scheduleDate = scheduleDate
    }

    fun getState(): String? {
        return state
    }

    fun setState(state: String?) {
        this.state = state
    }

    fun getTestTypeID(): String? {
        return testTypeID
    }

    fun setTestTypeID(testTypeID: String?) {
        this.testTypeID = testTypeID
    }

    fun getPhoneNo(): String? {
        return phoneNo
    }

    fun setPhoneNo(phoneNo: String?) {
        this.phoneNo = phoneNo
    }

    fun getImagePath(): String? {
        return imagePath
    }

    fun setImagePath(imagePath: String?) {
        this.imagePath = imagePath
    }

    fun getLatLong(): String? {
        return latLong
    }

    fun setLatLong(latLong: String?) {
        this.latLong = latLong
    }

    fun getIosimagepath(): String? {
        return iosimagepath
    }

    fun setIosimagepath(iosimagepath: String?) {
        this.iosimagepath = iosimagepath
    }

    fun getLocation(): String? {
        return location
    }

    fun setLocation(location: String?) {
        this.location = location
    }

    fun getCurrentlatlong(): String? {
        return currentlatlong
    }

    fun setCurrentlatlong(currentlatlong: String?) {
        this.currentlatlong = currentlatlong
    }

    fun getBookingType(): Int? {
        return bookingType
    }

    fun setBookingType(bookingType: Int?) {
        this.bookingType = bookingType
    }

    fun getOrganizationList(): List<Organization?>? {
        return organizationList
    }

    fun setOrganizationList(organizationList: List<Organization?>?) {
        this.organizationList = organizationList
    }

    fun getOrganizationType(): String? {
        return organizationType
    }

    fun setOrganizationType(organizationType: String?) {
        this.organizationType = organizationType
    }

    fun getOrderSummary(): List<OrderSummary?>? {
        return orderSummary
    }

    fun setOrderSummary(orderSummary: List<OrderSummary?>?) {
        this.orderSummary = orderSummary
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getContactname(): String? {
        return contactname
    }

    fun setContactname(contactname: String?) {
        this.contactname = contactname
    }

    fun getContractList(): ContractList? {
        return contractList
    }

    fun setContractList(contractList: ContractList?) {
        this.contractList = contractList
    }
}

class OrderSummary {
    @SerializedName("key")
    @Expose
    var key: String? = null

    @SerializedName("value")
    @Expose
    var value: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

}

class Organization {
    @SerializedName("StageFive")
    @Expose
    var stageFive: List<StageFive>? = null

    @SerializedName("StageFour")
    @Expose
    var stageFour: List<StageFour>? = null

    @SerializedName("StageOne")
    @Expose
    var stageOne: List<StageOne>? = null

    @SerializedName("StageThree")
    @Expose
    var stageThree: List<StageThree>? = null

    @SerializedName("StageTwo")
    @Expose
    var stageTwo: List<StageTwo>? = null

}

class Payments {
    @SerializedName("paymentName")
    @Expose
    var paymentName: String? = null

    @SerializedName("amount")
    @Expose
    var amount: Long? = null

    @SerializedName("testCount")
    @Expose
    var testCount: Int? = null

}

class StageFive {
    @SerializedName("header")
    @Expose
    var header: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("value")
    @Expose
    var value: String? = null

}

class StageFour {
    @SerializedName("header")
    @Expose
    var header: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("value")
    @Expose
    var value: String? = null

}

class StageOne {
    @SerializedName("header")
    @Expose
    var header: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("value")
    @Expose
    var value: String? = null

}

class StageThree {
    @SerializedName("header")
    @Expose
    var header: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("value")
    @Expose
    var value: String? = null

}

class StageTwo {
    @SerializedName("header")
    @Expose
    var header: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("value")
    @Expose
    var value: String? = null

}

class ContractList {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("titleName")
    @Expose
    var titleName: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("discount")
    @Expose
    var discount: String? = null

}

class Memberes {

}