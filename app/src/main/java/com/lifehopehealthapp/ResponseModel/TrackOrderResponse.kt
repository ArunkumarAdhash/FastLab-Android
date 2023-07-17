package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class TrackOrderResponse {
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
        internal var orderDate: Int? = null

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
        private var patientName: String? = null

        @SerializedName("status")
        @Expose
        internal var status: Int? = null

        @SerializedName("orderNo")
        @Expose
        internal var orderNo: Int? = null

        @SerializedName("notes")
        @Expose
        private var notes: String? = null

        @SerializedName("list")
        @Expose
        internal var list: List<TrackList?>? = null

        @SerializedName("members")
        @Expose
        private var members: List<MemberList?>? = null

        @SerializedName("payments")
        @Expose
        private var payments: List<PaymentList?>? = null

        @SerializedName("currentPanel")
        @Expose
        private var currentPanel: String? = null

        @SerializedName("labDetails")
        @Expose
        private var labDetails: String? = null

        @SerializedName("profilePic")
        @Expose
        private var profilePic: String? = null

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

        fun getOrderDate(): Int? {
            return orderDate
        }

        fun setOrderDate(orderDate: Int?) {
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

        fun getPatientName(): String? {
            return patientName
        }

        fun setPatientName(patientName: String?) {
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

        fun getNotes(): String? {
            return notes
        }

        fun setNotes(notes: String?) {
            this.notes = notes
        }

        fun getList(): List<TrackList?>? {
            return list
        }

        fun setList(list: List<TrackList?>?) {
            this.list = list
        }

        fun getMembers(): List<MemberList?>? {
            return members
        }

        fun setMembers(members: List<MemberList?>?) {
            this.members = members
        }

        fun getPayments(): List<PaymentList?>? {
            return payments
        }

        fun setPayments(payments: List<PaymentList?>?) {
            this.payments = payments
        }

        fun getCurrentPanel(): String? {
            return currentPanel
        }

        fun setCurrentPanel(currentPanel: String?) {
            this.currentPanel = currentPanel
        }

        fun getLabDetails(): String? {
            return labDetails
        }

        fun setLabDetails(labDetails: String?) {
            this.labDetails = labDetails
        }

        fun getProfilePic(): String? {
            return profilePic
        }

        fun setProfilePic(profilePic: String?) {
            this.profilePic = profilePic
        }
    }


    class TrackList {

        @SerializedName("id")
        @Expose
        internal var id: String? = null

        @SerializedName("status")
        @Expose
        internal var status: Int? = null

        @SerializedName("statusName")
        @Expose
        internal var statusName: String? = null

        @SerializedName("statusDesc")
        @Expose
        internal var statusDesc: String? = null

        @SerializedName("contactPerson")
        @Expose
        internal var contactPerson: String? = null

        @SerializedName("date")
        @Expose
        internal var date: Int? = null

        @SerializedName("trackId")
        @Expose
        internal var trackId: String? = null

        @SerializedName("trackLink")
        @Expose
        internal var trackLink: String? = null

        @SerializedName("trackType")
        @Expose
        internal var trackType: Int? = null

        @SerializedName("mobileNumber")
        @Expose
        internal var mobileNumber: String? = null

        @SerializedName("shipmentId")
        @Expose
        internal var shipmentId: String? = null

        @SerializedName("arrivingTime")
        @Expose
        internal var arrivingTime: Double? = 0.0

        @SerializedName("labName")
        @Expose
        internal var labName: String? = ""

        @SerializedName("orderBy")
        @Expose
        private var orderBy: Int? = null

        fun getId(): String? {
            return id
        }

        fun setId(id: String?) {
            this.id = id
        }

        fun getStatus(): Int? {
            return status
        }

        fun setStatus(status: Int?) {
            this.status = status
        }

        fun getStatusName(): String? {
            return statusName
        }

        fun setStatusName(statusName: String?) {
            this.statusName = statusName
        }

        fun getStatusDesc(): String? {
            return statusDesc
        }

        fun setStatusDesc(statusDesc: String?) {
            this.statusDesc = statusDesc
        }

        fun getContactPerson(): String? {
            return contactPerson
        }

        fun setContactPerson(contactPerson: String?) {
            this.contactPerson = contactPerson
        }

        fun getDate(): Int? {
            return date
        }

        fun setDate(date: Int?) {
            this.date = date
        }

        fun getTrackId(): String? {
            return trackId
        }

        fun setTrackId(trackId: String?) {
            this.trackId = trackId
        }

        fun getTrackLink(): String? {
            return trackLink
        }

        fun setTrackLink(trackLink: String?) {
            this.trackLink = trackLink
        }

        fun getTrackType(): Int? {
            return trackType
        }

        fun setTrackType(trackType: Int?) {
            this.trackType = trackType
        }

        fun getMobileNumber(): String? {
            return mobileNumber
        }

        fun setMobileNumber(mobileNumber: String?) {
            this.mobileNumber = mobileNumber
        }

        fun getShipmentId(): String? {
            return shipmentId
        }

        fun setShipmentId(shipmentId: String?) {
            this.shipmentId = shipmentId
        }

        fun getArrivingTime(): Double? {
            return arrivingTime
        }

        fun setArrivingTime(arrivingTime: Double?) {
            this.arrivingTime = arrivingTime
        }

        fun getLabName(): String? {
            return labName
        }

        fun setLabName(labName: String?) {
            this.labName = labName
        }

        fun getOrderBy(): Int? {
            return orderBy
        }

        fun setOrderBy(orderBy: Int?) {
            this.orderBy = orderBy
        }
    }

    class MemberList {
        @SerializedName("patientName")
        @Expose
        internal var patientName: String? = ""

        @SerializedName("age")
        @Expose
        internal var age: Int? = null

        @SerializedName("gender")
        @Expose
        internal var gender: String? = null

        fun getPatientName(): String? {
            return patientName
        }

        fun setPatientName(patientName: String?) {
            this.patientName = patientName
        }

        fun getAge(): Int? {
            return age
        }

        fun setAge(age: Int?) {
            this.age = age
        }

        fun getGender(): String? {
            return gender
        }

        fun setGender(gender: String?) {
            this.gender = gender
        }

    }

    class PaymentList {
        @SerializedName("paymentName")
        @Expose
        internal var paymentName: String? = null

        @SerializedName("testCount")
        @Expose
        internal var testCount: Int? = null

        @SerializedName("amount")
        @Expose
        internal var amount: Double? = null

        fun getPaymentName(): String? {
            return paymentName
        }

        fun setPaymentName(paymentName: String?) {
            this.paymentName = paymentName
        }

        fun getTestCount(): Int? {
            return testCount
        }

        fun setTestCount(testCount: Int?) {
            this.testCount = testCount
        }

        fun getAmount(): Double? {
            return amount
        }

        fun setAmount(amount: Double?) {
            this.amount = amount
        }
    }

}