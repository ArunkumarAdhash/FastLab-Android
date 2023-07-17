package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class UploadVaccinationDocumentRequest {
    @SerializedName("name")
    @Expose
    private var name: String? = null

    @SerializedName("fileUrl")
    @Expose
    private var fileUrl: String? = null

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getFileUrl(): String? {
        return fileUrl
    }

    fun setFileUrl(fileUrl: String?) {
        this.fileUrl = fileUrl
    }
}