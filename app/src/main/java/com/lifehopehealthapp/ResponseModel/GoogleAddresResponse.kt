package com.lifehopehealthapp.ResponseModel

import com.google.android.libraries.places.api.internal.impl.net.pablo.PlaceResult
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GoogleAddresResponse {
    @SerializedName("plus_code")
    @Expose
    private var plusCode: PlusCode? = null

    @SerializedName("results")
    @Expose
    private var results: List<Result?>? = null

    @SerializedName("status")
    @Expose
    private var status: String? = null

    fun getPlusCode(): PlusCode? {
        return plusCode
    }

    fun setPlusCode(plusCode: PlusCode?) {
        this.plusCode = plusCode
    }

    fun getResults(): List<Result?>? {
        return results
    }

    fun setResults(results: List<Result?>?) {
        this.results = results
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }
}
class AddressComponent {
    @SerializedName("long_name")
    @Expose
    var longName: String? = null

    @SerializedName("short_name")
    @Expose
    var shortName: String? = null

    @SerializedName("types")
    @Expose
    var types: List<String>? = null
}
class Bounds {
    @SerializedName("northeast")
    @Expose
    var northeast: Northeast__1? = null

    @SerializedName("southwest")
    @Expose
    var southwest: Southwest__1? = null
}
class Geometry {
    @SerializedName("location")
    @Expose
    var location: Location? = null

    @SerializedName("location_type")
    @Expose
    var locationType: String? = null

    @SerializedName("viewport")
    @Expose
    var viewport: PlaceResult.Geometry.Viewport? = null

    @SerializedName("bounds")
    @Expose
    var bounds: Bounds? = null
}
class Location {
    @SerializedName("lat")
    @Expose
    var lat: Double? = null

    @SerializedName("lng")
    @Expose
    var lng: Double? = null
}
class Northeast {
    @SerializedName("lat")
    @Expose
    var lat: Double? = null

    @SerializedName("lng")
    @Expose
    var lng: Double? = null
}
class Northeast__1 {
    @SerializedName("lat")
    @Expose
    var lat: Double? = null

    @SerializedName("lng")
    @Expose
    var lng: Double? = null
}
class PlusCode {
    @SerializedName("compound_code")
    @Expose
    var compoundCode: String? = null

    @SerializedName("global_code")
    @Expose
    var globalCode: String? = null
}
class PlusCode__1 {
    @SerializedName("compound_code")
    @Expose
    var compoundCode: String? = null

    @SerializedName("global_code")
    @Expose
    var globalCode: String? = null
}
class Result {
    @SerializedName("address_components")
    @Expose
    var addressComponents: List<AddressComponent>? = null

    @SerializedName("formatted_address")
    @Expose
    var formattedAddress: String? = null

    @SerializedName("geometry")
    @Expose
    var geometry: Geometry? = null

    @SerializedName("place_id")
    @Expose
    var placeId: String? = null

    @SerializedName("plus_code")
    @Expose
    var plusCode: PlusCode__1? = null

    @SerializedName("types")
    @Expose
    var types: List<String>? = null
}
class Southwest {
    @SerializedName("lat")
    @Expose
    var lat: Double? = null

    @SerializedName("lng")
    @Expose
    var lng: Double? = null
}
class Southwest__1 {
    @SerializedName("lat")
    @Expose
    var lat: Double? = null

    @SerializedName("lng")
    @Expose
    var lng: Double? = null
}
class Viewport {
    @SerializedName("northeast")
    @Expose
    var northeast: Northeast? = null

    @SerializedName("southwest")
    @Expose
    var southwest: Southwest? = null
}