package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class BulkOrderNewRequest {


    @SerializedName("address")
    @Expose
    private var address: String? = null

    @SerializedName("bookingType")
    @Expose
    private var bookingType: Int? = null

    @SerializedName("paymentType")
    @Expose
    private var paymentType: Int? = null

    @SerializedName("contractMonth")
    @Expose
    private var contractMonth: Int? = null

    @SerializedName("contactname")
    @Expose
    private var contactname: String? = null

    @SerializedName("contractList")
    @Expose
    private var contractList:  ContractListNew? = null

    @SerializedName("Currentlatlong")
    @Expose
    private var currentlatlong: String? = null

    @SerializedName("email")
    @Expose
    private var email: String? = null

    @SerializedName("imagePath")
    @Expose
    private var imagePath: String? = null

    @SerializedName("iosimagepath")
    @Expose
    private var iosimagepath: String? = null

    @SerializedName("latLong")
    @Expose
    private var latLong: String? = null

    @SerializedName("locality")
    @Expose
    private var locality: String? = null

    @SerializedName("location")
    @Expose
    private var location: String? = null

    @SerializedName("members")
    @Expose
    private var members: List<MemberNew?>? = null

    @SerializedName("orderDate")
    @Expose
    private var orderDate: Int? = null

    @SerializedName("peopleList")
    @Expose
    private var peopleList: List<PersonList?>? = null

    @SerializedName("surfaceList")
    @Expose
    private var surfaceList: List<Surface?>? = null

    @SerializedName("orderSummary")
    @Expose
    private var orderSummary: List<OrderSummaryNew?>? = null

    @SerializedName("organizationId")
    @Expose
    private var organizationId: Int? = null

    @SerializedName("organizationType")
    @Expose
    private var organizationType: String? = null

    @SerializedName("organizationTitle")
    @Expose
    private var organizationTitle: String? = null

    @SerializedName("payment")
    @Expose
    private var payment: List<PaymentNew?>? = null

    @SerializedName("phoneNo")
    @Expose
    private var phoneNo: String? = null

    @SerializedName("primaryName")
    @Expose
    private var primaryName: String? = null

    @SerializedName("scheduleDate")
    @Expose
    private var scheduleDate: Int? = null

    @SerializedName("state")
    @Expose
    private var state: String? = null

    @SerializedName("testCategoryId")
    @Expose
    private var testCategoryId: String? = null

    @SerializedName("testTypeID")
    @Expose
    private var testTypeID: String? = null

    @SerializedName("title")
    @Expose
    private var title: String? = null

    @SerializedName("userFCMToken")
    @Expose
    private var userFCMToken: String? = null


    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String?) {
        this.address = address
    }

    fun getBookingType(): Int? {
        return bookingType
    }

    fun setBookingType(bookingType: Int?) {
        this.bookingType = bookingType
    }

    fun getPaymentType(): Int? {
        return paymentType
    }

    fun setPaymentType(paymentType: Int?) {
        this.paymentType = paymentType
    }

    fun getContractMonth(): Int? {
        return contractMonth
    }

    fun setContractMonth(contractMonth: Int?) {
        this.contractMonth = contractMonth
    }

    fun getContactname(): String? {
        return contactname
    }

    fun setContactname(contactname: String?) {
        this.contactname = contactname
    }

    fun getContractList():  ContractListNew?{
        return contractList
    }

    fun setContractList(contractList: ContractListNew?) {
        this.contractList = contractList
    }

    fun getCurrentlatlong(): String? {
        return currentlatlong
    }

    fun setCurrentlatlong(currentlatlong: String?) {
        this.currentlatlong = currentlatlong
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getImagePath(): String? {
        return imagePath
    }

    fun setImagePath(imagePath: String?) {
        this.imagePath = imagePath
    }

    fun getIosimagepath(): String? {
        return iosimagepath
    }

    fun setIosimagepath(iosimagepath: String?) {
        this.iosimagepath = iosimagepath
    }

    fun getLatLong(): String? {
        return latLong
    }

    fun setLatLong(latLong: String?) {
        this.latLong = latLong
    }

    fun getLocality(): String? {
        return locality
    }

    fun setLocality(locality: String?) {
        this.locality = locality
    }

    fun getLocation(): String? {
        return location
    }

    fun setLocation(location: String?) {
        this.location = location
    }

    fun getMembers(): List<MemberNew?>? {
        return members
    }

    fun setMembers(members: List<MemberNew?>?) {
        this.members = members
    }

    fun getOrderDate(): Int? {
        return orderDate
    }

    fun setOrderDate(orderDate: Int?) {
        this.orderDate = orderDate
    }

    fun getPeopleList(): List<PersonList?>? {
        return peopleList
    }

    fun setPeopleList(peopleList: List<PersonList?>?) {
        this.peopleList = peopleList
    }

    fun getSurfaceList(): List<Surface?>? {
        return surfaceList
    }

    fun setSurfaceList(surfaceList: List<Surface?>?) {
        this.surfaceList = surfaceList
    }

    fun getOrderSummary(): List<OrderSummaryNew?>? {
        return orderSummary
    }

    fun setOrderSummary(orderSummary: List<OrderSummaryNew?>?) {
        this.orderSummary = orderSummary
    }

    fun getOrganizationId(): Int? {
        return organizationId
    }

    fun setOrganizationId(organizationId: Int?) {
        this.organizationId = organizationId
    }

    fun getOrganizationType(): String? {
        return organizationType
    }

    fun setOrganizationType(organizationType: String?) {
        this.organizationType = organizationType
    }

    fun getOrganizationTitle(): String? {
        return organizationTitle
    }

    fun setOrganizationTitle(organizationTitle: String?) {
        this.organizationTitle = organizationTitle
    }

    fun getPayment(): List<PaymentNew?>? {
        return payment
    }

    fun setPayment(payment: List<PaymentNew?>?) {
        this.payment = payment
    }

    fun getPhoneNo(): String? {
        return phoneNo
    }

    fun setPhoneNo(phoneNo: String?) {
        this.phoneNo = phoneNo
    }

    fun getPrimaryName(): String? {
        return primaryName
    }

    fun setPrimaryName(primaryName: String?) {
        this.primaryName = primaryName
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

    fun getTestCategoryId(): String? {
        return testCategoryId
    }

    fun setTestCategoryId(testCategoryId: String?) {
        this.testCategoryId = testCategoryId
    }

    fun getTestTypeID(): String? {
        return testTypeID
    }

    fun setTestTypeID(testTypeID: String?) {
        this.testTypeID = testTypeID
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getUserFCMToken(): String? {
        return userFCMToken
    }

    fun setUserFCMToken(userFCMToken: String?) {
        this.userFCMToken = userFCMToken
    }

}

class OrderSummaryNew {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("key")
    @Expose
    var key: String? = null

    @SerializedName("value")
    @Expose
    var value: Any?=null
}

class MemberNew {


}

class PaymentNew {
    @SerializedName("amount")
    @Expose
    var amount: Float? = null

    @SerializedName("paymentName")
    @Expose
    var paymentName: String? = null

    @SerializedName("testCount")
    @Expose
    var testCount: Int? = null
}

class ContractListNew {
    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("discount")
    @Expose
    var discount: String? = null

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("titleName")
    @Expose
    var titleName: String? = null
}

class Surface {
    @SerializedName("Id")
    @Expose
    var id: String? = null

    @SerializedName("TitleName")
    @Expose
    var titleName: String? = null

    @SerializedName("Value")
    @Expose
    var value: Int? = null

    @SerializedName("OrderBy")
    @Expose
    var orderBy: Int? = null
}

class PersonList {
    @SerializedName("Id")
    @Expose
    var id: String? = null

    @SerializedName("TitleName")
    @Expose
    var titleName: String? = null

    @SerializedName("Value")
    @Expose
    var value: Int? = null

    @SerializedName("OrderBy")
    @Expose
    var orderBy: Int? = null
}







