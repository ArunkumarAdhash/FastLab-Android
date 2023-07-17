package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class VerifyEmailRequest {
    @SerializedName("email")
    @Expose
    private var email: String? = null

    @SerializedName("organizationId")
    @Expose
    private var organizationId: Int? = null

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getOrganizationId(): Int? {
        return organizationId
    }

    fun setOrganizationId(organizationId: Int?) {
        this.organizationId = organizationId
    }
}