package com.lifehopehealthapp.ResponseModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class PeopleApiRequest {


        @SerializedName("Id")
        @Expose
        var id: String? = null

        @SerializedName("OrderBy")
        @Expose
        var orderBy: Int? = null

        @SerializedName("TitleName")
        @Expose
        var titleName: String? = null

        @SerializedName("Value")
        @Expose
        var value: Int? = null



    }


