package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class MyError {

    @SerializedName("status")
    @Expose
    internal var status: Int? = null

    @SerializedName("message")
    @Expose
    internal var message: String? = null

    @SerializedName("data")
    @Expose
    internal var data: String? = null

    fun getStatus(): Int? {
        return status
    }

    fun setStatus(status: Int?) {
        this.status = status
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getData(): String? {
        return data
    }

    fun setData(data: String?) {
        this.data = data
    }
}