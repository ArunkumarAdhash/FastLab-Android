package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


@Keep
class MyTestOrderResponse {
    @SerializedName("Value")
    @Expose
    var value: Values? = null

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

    @JvmName("getValue1")
    fun getValue(): Values? {
        return value
    }

    @JvmName("setValue1")
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

    fun getStatusCode(): Int? {
        return statusCode
    }

    fun setStatusCode(statusCode: Int?) {
        this.statusCode = statusCode
    }

    class Values {
        @SerializedName("data")
        @Expose
        var data: List<OrderListData>? = null

        @SerializedName("pageNumber")
        @Expose
        var pageNumber: Int? = 0

        @SerializedName("pageSize")
        @Expose
        var pageSize: Int? = 0

        @SerializedName("totalPages")
        @Expose
        var totalPages: Int? = 0

        @SerializedName("nextPage")
        @Expose
        var nextPage: String? = ""

        @SerializedName("previousPage")
        @Expose
        var previousPage: String? = ""

    }
}

class OrderListData {
    @SerializedName("orderId")
    @Expose
    internal var orderId: String? = null

    @SerializedName("testImage")
    @Expose
    internal var testImage: String? = null

    @SerializedName("iOSTestImage")
    @Expose
    private var iOSTestImage: String? = null

    @SerializedName("addonCount")
    @Expose
    internal var addonCount: Int? = null

    @SerializedName("testName")
    @Expose
    internal var testName: String? = null

    @SerializedName("orderDate")
    @Expose
    internal var orderDate: Double? = null

    @SerializedName("scheduleDate")
    @Expose
    internal var scheduleDate: Int? = null

    @SerializedName("amount")
    @Expose
    internal var amount: Double? = null

    @SerializedName("testType")
    @Expose
    internal var testType: Int? = null

    @SerializedName("patientName")
    @Expose
    private var patientName: Any? = null

    @SerializedName("status")
    @Expose
    internal var status: Int? = null

    @SerializedName("orderNo")
    @Expose
    internal var orderNo: Int? = null

    @SerializedName("notes")
    @Expose
    private var notes: Any? = null

    @SerializedName("list")
    @Expose
    private var list: List<Any?>? = null

    @SerializedName("members")
    @Expose
    private var members: Any? = null

    @SerializedName("payments")
    @Expose
    private var payments: Any? = null

    @SerializedName("currentPanel")
    @Expose
    private var currentPanel: Any? = null

    @SerializedName("labDetails")
    @Expose
    private var labDetails: Any? = null

    @SerializedName("profilePic")
    @Expose
    private var profilePic: Any? = null

    fun getOrderId(): String? {
        return orderId
    }

    fun setOrderId(orderId: String?) {
        this.orderId = orderId
    }

    fun getTestImage(): String? {
        return testImage
    }

    fun setTestImage(testImage: String?) {
        this.testImage = testImage
    }

    fun getIOSTestImage(): String? {
        return iOSTestImage
    }

    fun setIOSTestImage(iOSTestImage: String?) {
        this.iOSTestImage = iOSTestImage
    }

    fun getAddonCount(): Int? {
        return addonCount
    }

    fun setAddonCount(addonCount: Int?) {
        this.addonCount = addonCount
    }

    fun getTestName(): String? {
        return testName
    }

    fun setTestName(testName: String?) {
        this.testName = testName
    }

    fun getOrderDate(): Double? {
        return orderDate
    }

    fun setOrderDate(orderDate: Double?) {
        this.orderDate = orderDate
    }

    fun getScheduleDate(): Int? {
        return scheduleDate
    }

    fun setScheduleDate(scheduleDate: Int?) {
        this.scheduleDate = scheduleDate
    }

    fun getAmount(): Double? {
        return amount
    }

    fun setAmount(amount: Double?) {
        this.amount = amount
    }

    fun getTestType(): Int? {
        return testType
    }

    fun setTestType(testType: Int?) {
        this.testType = testType
    }

    fun getPatientName(): Any? {
        return patientName
    }

    fun setPatientName(patientName: Any?) {
        this.patientName = patientName
    }

    fun getStatus(): Int? {
        return status
    }

    fun setStatus(status: Int?) {
        this.status = status
    }

    fun getOrderNo(): Int? {
        return orderNo
    }

    fun setOrderNo(orderNo: Int?) {
        this.orderNo = orderNo
    }

    fun getNotes(): Any? {
        return notes
    }

    fun setNotes(notes: Any?) {
        this.notes = notes
    }

    fun getList(): List<Any?>? {
        return list
    }

    fun setList(list: List<Any?>?) {
        this.list = list
    }

    fun getMembers(): Any? {
        return members
    }

    fun setMembers(members: Any?) {
        this.members = members
    }

    fun getPayments(): Any? {
        return payments
    }

    fun setPayments(payments: Any?) {
        this.payments = payments
    }

    fun getCurrentPanel(): Any? {
        return currentPanel
    }

    fun setCurrentPanel(currentPanel: Any?) {
        this.currentPanel = currentPanel
    }

    fun getLabDetails(): Any? {
        return labDetails
    }

    fun setLabDetails(labDetails: Any?) {
        this.labDetails = labDetails
    }

    fun getProfilePic(): Any? {
        return profilePic
    }

    fun setProfilePic(profilePic: Any?) {
        this.profilePic = profilePic
    }
}


