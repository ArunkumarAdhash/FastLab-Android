package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class SettingsResponse {

    @SerializedName("Value")
    @Expose
    private var value: Value? = null

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

    fun getValue(): Value? {
        return value
    }

    fun setValue(value: Value?) {
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
class Value {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

}
class Data {
    @SerializedName("id")
    @Expose
    private var id: String? = null

    @SerializedName("appName")
    @Expose
    private var appName: String? = null

    @SerializedName("privacyPolicy")
    @Expose
    internal var privacyPolicy: String? = null

    @SerializedName("contactSupport")
    @Expose
    internal var contactSupport: String? = null

    @SerializedName("termsandCondition")
    @Expose
    internal var termsandCondition: String? = null

    @SerializedName("versionInfo")
    @Expose
    internal var versionInfo: VersionInfo? = null

    @SerializedName("addOnLimit")
    @Expose
    internal var addOnLimit: Int? = null

    @SerializedName("bloodgroups")
    @Expose
    private var bloodgroups: List<String?>? = null

    @SerializedName("googlePlayKey")
    @Expose
    private var googlePlayKey: String? = null

    @SerializedName("weatherApiKey")
    @Expose
    internal var weatherApiKey: String? = null

    @SerializedName("countryCode")
    @Expose
    internal var countryCode: String? = null

    @SerializedName("userAgree")
    @Expose
    private var userAgree: String? = null

    @SerializedName("accessKey")
    @Expose
    private var accessKey: String? = null

    @SerializedName("secretKey")
    @Expose
    private var secretKey: String? = null

    @SerializedName("bucketName")
    @Expose
    private var bucketName: String? = null

    @SerializedName("region")
    @Expose
    private var region: String? = null

    @SerializedName("s3URL")
    @Expose
    private var s3URL: String? = null

    @SerializedName("callToken")
    @Expose
    internal var callToken: String = ""

    fun getCallToken(): String {
        return callToken
    }

    fun setCallToken(id: String) {
        this.callToken = id
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getAppName(): String? {
        return appName
    }

    fun setAppName(appName: String?) {
        this.appName = appName
    }

    fun getPrivacyPolicy(): String? {
        return privacyPolicy
    }

    fun setPrivacyPolicy(privacyPolicy: String?) {
        this.privacyPolicy = privacyPolicy
    }

    fun getContactSupport(): String? {
        return contactSupport
    }

    fun setContactSupport(contactSupport: String?) {
        this.contactSupport = contactSupport
    }

    fun getTermsandCondition(): String? {
        return termsandCondition
    }

    fun setTermsandCondition(termsandCondition: String?) {
        this.termsandCondition = termsandCondition
    }

    fun getVersionInfo(): VersionInfo? {
        return versionInfo
    }

    fun setVersionInfo(versionInfo: VersionInfo?) {
        this.versionInfo = versionInfo
    }

    fun getAddOnLimit(): Int? {
        return addOnLimit
    }

    fun setAddOnLimit(addOnLimit: Int?) {
        this.addOnLimit = addOnLimit
    }

    fun getBloodgroups(): List<String?>? {
        return bloodgroups
    }

    fun setBloodgroups(bloodgroups: List<String?>?) {
        this.bloodgroups = bloodgroups
    }

    fun getGooglePlayKey(): String? {
        return googlePlayKey
    }

    fun setGooglePlayKey(googlePlayKey: String?) {
        this.googlePlayKey = googlePlayKey
    }

    fun getWeatherApiKey(): String? {
        return weatherApiKey
    }

    fun setWeatherApiKey(weatherApiKey: String?) {
        this.weatherApiKey = weatherApiKey
    }

    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?) {
        this.countryCode = countryCode
    }

    fun getUserAgree(): String? {
        return userAgree
    }

    fun setUserAgree(userAgree: String?) {
        this.userAgree = userAgree
    }

    fun getAccessKey(): String? {
        return accessKey
    }

    fun setAccessKey(accessKey: String?) {
        this.accessKey = accessKey
    }

    fun getSecretKey(): String? {
        return secretKey
    }

    fun setSecretKey(secretKey: String?) {
        this.secretKey = secretKey
    }

    fun getBucketName(): String? {
        return bucketName
    }

    fun setBucketName(bucketName: String?) {
        this.bucketName = bucketName
    }

    fun getRegion(): String? {
        return region
    }

    fun setRegion(region: String?) {
        this.region = region
    }

    fun getS3URL(): String? {
        return s3URL
    }

    fun setS3URL(s3URL: String?) {
        this.s3URL = s3URL
    }
}

class VersionInfo {
    @SerializedName("latestVersion")
    @Expose
    var latestVersion: Double? = null

    @SerializedName("forceUpdate")
    @Expose
    var forceUpdate: Boolean? = null

    @SerializedName("updateTitle")
    @Expose
    var updateTitle: String? = null

    @SerializedName("updateMsg")
    @Expose
    var updateMsg: String? = null

    @SerializedName("playStoreUrl")
    @Expose
    var playStoreUrl: String? = null

    @SerializedName("isUpdate")
    @Expose
    var isUpdate: Int? = null

}