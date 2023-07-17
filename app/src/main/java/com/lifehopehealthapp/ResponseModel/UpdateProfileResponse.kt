package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName



@Keep
class UpdateProfileResponse {

    @SerializedName("data")
    @Expose
    private var data: Boolean? = null

    @SerializedName("status")
    @Expose
    private var status: Int? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    fun getData(): Boolean? {
        return data
    }

    fun setData(data: Boolean?) {
        this.data = data
    }

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

}