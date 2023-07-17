package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class APIRequest {
    @SerializedName("type")
    @Expose
    private var type: String? = null

    fun getType(): String? {
        return type
    }

    fun setType(type: String?) {
        this.type = type
    }
}