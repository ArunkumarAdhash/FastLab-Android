package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class VitalsInputModel {
    /* @SerializedName("vitalId")
     @Expose
     private var vitalId: Int? = null

     @SerializedName("date")
     @Expose
     private var date: Int? = null

     @SerializedName("value")
     @Expose
     private var value: String? = null

     @SerializedName("type")
     @Expose
     private var type: Int? = null

     @SerializedName("currentDate")
     @Expose
     private var currentDate: Int? = null

     @SerializedName("glucoseId")
     @Expose
     private var glucoseId: Int? = null

     fun getVitalId(): Int? {
         return vitalId
     }

     fun setVitalId(vitalId: Int?) {
         this.vitalId = vitalId
     }

     fun getDate(): Int? {
         return date
     }

     fun setDate(date: Int?) {
         this.date = date
     }

     fun getValue(): String? {
         return value
     }

     fun setValue(value: String?) {
         this.value = value
     }

     fun getType(): Int? {
         return type
     }

     fun setType(type: Int?) {
         this.type = type
     }

     fun getCurrentDate(): Int? {
         return currentDate
     }

     fun setCurrentDate(currentDate: Int?) {
         this.currentDate = currentDate
     }

     fun getGlucoseId(): Int? {
         return glucoseId
     }

     fun setGlucoseId(glucoseId: Int?) {
         this.glucoseId = glucoseId
     }
 */
    @SerializedName("vitalId")
    @Expose
    private var vitalId: Int? = null

    @SerializedName("timeOfCheck")
    @Expose
    private var timeOfCheck: Int? = null

    @SerializedName("type")
    @Expose
    private var type: Int? = null

    @SerializedName("value")
    @Expose
    private var value: List<String?>? = null

    @SerializedName("date")
    @Expose
    private var date: Int? = null

    @SerializedName("currentDate")
    @Expose
    private var currentDate: Int? = null

    @SerializedName("glucoseId")
    @Expose
    private var glucoseId: Int? = null

    fun getVitalId(): Int? {
        return vitalId
    }

    fun setVitalId(vitalId: Int?) {
        this.vitalId = vitalId
    }

    fun getTimeOfCheck(): Int? {
        return timeOfCheck
    }

    fun setTimeOfCheck(timeOfCheck: Int?) {
        this.timeOfCheck = timeOfCheck
    }

    fun getType(): Int? {
        return type
    }

    fun setType(type: Int?) {
        this.type = type
    }

    fun getValue(): List<String?>? {
        return value
    }

    fun setValue(value: List<String?>?) {
        this.value = value
    }

    fun getDate(): Int? {
        return date
    }

    fun setDate(date: Int?) {
        this.date = date
    }

    fun getCurrentDate(): Int? {
        return currentDate
    }

    fun setCurrentDate(currentDate: Int?) {
        this.currentDate = currentDate
    }

    fun getGlucoseId(): Int? {
        return glucoseId
    }

    fun setGlucoseId(glucoseId: Int?) {
        this.glucoseId = glucoseId
    }
}
