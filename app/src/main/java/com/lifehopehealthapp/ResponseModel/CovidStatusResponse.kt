package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class CovidStatusResponse {

    @SerializedName("Value")
    var value: Value? = Value()

    @SerializedName("Formatters")
    var Formatters: ArrayList<String> = arrayListOf()

    @SerializedName("ContentTypes")
    var ContentTypes: ArrayList<String> = arrayListOf()

    @SerializedName("DeclaredType")
    var DeclaredType: String? = null

    @SerializedName("StatusCode")
    var StatusCode: Int? = null

    data class Data(

        @SerializedName("orderNo") var orderNo: Int? = null,
        @SerializedName("patientName") var patientName: String? = null,
        @SerializedName("orderDate") var orderDate: Int? = null,
        @SerializedName("imagePath") var imagePath: String? = null,
        @SerializedName("orderId") var orderId: String? = null,
        @SerializedName("bookingType") var bookingType: Int? = null,
        @SerializedName("isStatus") var isStatus: Int? = null

    )

    data class Value(

        @SerializedName("data") var data: ArrayList<Data> = arrayListOf(),
        @SerializedName("pageNumber") var pageNumber: Int? = null,
        @SerializedName("pageSize") var pageSize: Int? = null,
        @SerializedName("totalPages") var totalPages: Int? = null,
        @SerializedName("nextPage") var nextPage: String? = null,
        @SerializedName("previousPage") var previousPage: String? = null

    )

}

