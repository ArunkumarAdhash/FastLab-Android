package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class VerifyEmailResponse {
    @SerializedName("Value")
    @Expose
    private var value: VerifyValue? = null

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

    fun getValue(): VerifyValue? {
        return value
    }

    fun setValue(value: VerifyValue?) {
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
}

class VerifyValue {
    @SerializedName("data")
    @Expose
    var data: VerifyData? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null
}

class VerifyData {
    @SerializedName("UserId")
    @Expose
    private var userId: String? = ""

    @SerializedName("Email")
    @Expose
    private var email: String? = ""

    @SerializedName("FirstName")
    @Expose
    private var firstName: String? = ""

    @SerializedName("LastName")
    @Expose
    private var lastName: Any? = ""

    @SerializedName("RoleName")
    @Expose
    private var roleName: Any? = ""

    @SerializedName("RoleId")
    @Expose
    private var roleId: String? = ""

    @SerializedName("OrganizationName")
    @Expose
    private var organizationName: String? = ""

    @SerializedName("OrganizationType")
    @Expose
    private var organizationType: Any? = ""

    @SerializedName("Address")
    @Expose
    private var address: Any? = ""

    @SerializedName("Title")
    @Expose
    private var title: Any? = ""

    @SerializedName("MobileNumber")
    @Expose
    private var mobileNumber: Any? = ""


    fun getUserId(): String? {
        return userId
    }

    fun setUserId(userId: String?) {
        this.userId = userId
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getFirstName(): String? {
        return firstName
    }

    fun setFirstName(firstName: String?) {
        this.firstName = firstName
    }

    fun getLastName(): Any? {
        return lastName
    }

    fun setLastName(lastName: Any?) {
        this.lastName = lastName
    }

    fun getRoleName(): Any? {
        return roleName
    }

    fun setRoleName(roleName: Any?) {
        this.roleName = roleName
    }

    fun getRoleId(): String? {
        return roleId
    }

    fun setRoleId(roleId: String?) {
        this.roleId = roleId
    }

    fun getOrganizationName(): String? {
        return organizationName
    }

    fun setOrganizationName(organizationName: String?) {
        this.organizationName = organizationName
    }

    fun getOrganizationType(): Any? {
        return organizationType
    }

    fun setOrganizationType(organizationType: Any?) {
        this.organizationType = organizationType
    }

    fun getAddress(): Any? {
        return address
    }

    fun setAddress(address: Any?) {
        this.address = address
    }

    fun getTitle(): Any? {
        return title
    }

    fun setTitle(title: Any?) {
        this.title = title
    }

    fun getMobileNumber(): Any? {
        return mobileNumber
    }

    fun setMobileNumber(mobileNumber: Any?) {
        this.mobileNumber = mobileNumber
    }
}