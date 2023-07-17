package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


@Keep
class DiagnosisResultResponse {
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

    class Value {
        @SerializedName("data")
        @Expose
        var data: DataList? = null

        @SerializedName("status")
        @Expose
        var status: Int? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

    }
}

class DataList {
    @SerializedName("question")
    @Expose
    private var question: List<Questions?>? =
        null

    @SerializedName("primaryDiagnoses")
    @Expose
    private var primaryDiagnoses: List<PrimaryDiagnosiss?>? =
        null

    @SerializedName("recommendedTests")
    @Expose
    internal var recommendedTests: List<RecommendedTests?>? =
        null

    @SerializedName("diagnosesMeasure")
    @Expose
    private var diagnosesMeasure: Int? = null

    @SerializedName("diagnosesId")
    @Expose
    private var diagnosesId: Int? = null

    @SerializedName("diagnosesName")
    @Expose
    private var diagnosesName: String? = null

    @SerializedName("profileInfo")
    @Expose
    private var profileInfo: ProfileInfo? = null

    @SerializedName("testDate")
    @Expose
    private var testDate: Int? = null

    @SerializedName("shareInfo")
    @Expose
    private var shareInfo: String? = null

    @SerializedName("location")
    @Expose
    private var location: String? = null

    @SerializedName("concern")
    @Expose
    private var concern: Any? = null

    @SerializedName("quote")
    @Expose
    private var quote: String? = null

    fun getQuestion(): List<Questions?>? {
        return question
    }

    fun setQuestion(question: List<Questions?>?) {
        this.question = question
    }

    fun getPrimaryDiagnoses(): List<PrimaryDiagnosiss?>? {
        return primaryDiagnoses
    }

    fun setPrimaryDiagnoses(primaryDiagnoses: List<PrimaryDiagnosiss?>?) {
        this.primaryDiagnoses = primaryDiagnoses
    }

    fun getRecommendedTests(): List<RecommendedTests?>? {
        return recommendedTests
    }

    fun setRecommendedTests(recommendedTests: List<RecommendedTests?>?) {
        this.recommendedTests = recommendedTests
    }

    fun getDiagnosesMeasure(): Int? {
        return diagnosesMeasure
    }

    fun setDiagnosesMeasure(diagnosesMeasure: Int?) {
        this.diagnosesMeasure = diagnosesMeasure
    }

    fun getDiagnosesId(): Int? {
        return diagnosesId
    }

    fun setDiagnosesId(diagnosesId: Int?) {
        this.diagnosesId = diagnosesId
    }

    fun getDiagnosesName(): String? {
        return diagnosesName
    }

    fun setDiagnosesName(diagnosesName: String?) {
        this.diagnosesName = diagnosesName
    }

    fun getProfileInfo(): ProfileInfo? {
        return profileInfo
    }

    fun setProfileInfo(profileInfo: ProfileInfo?) {
        this.profileInfo = profileInfo
    }

    fun getTestDate(): Int? {
        return testDate
    }

    fun setTestDate(testDate: Int?) {
        this.testDate = testDate
    }

    fun getShareInfo(): String? {
        return shareInfo
    }

    fun setShareInfo(shareInfo: String?) {
        this.shareInfo = shareInfo
    }

    fun getLocation(): String? {
        return location
    }

    fun setLocation(location: String?) {
        this.location = location
    }

    fun getConcern(): Any? {
        return concern
    }

    fun setConcern(concern: Any?) {
        this.concern = concern
    }

    fun getQuote(): String? {
        return quote
    }

    fun setQuote(quote: String?) {
        this.quote = quote
    }
}

class PrimaryDiagnosiss {
    @SerializedName("result1")
    @Expose
    internal var result1: String? = null

    @SerializedName("result2")
    @Expose
    internal var result2: String? = null

    fun getResult1(): String? {
        return result1
    }

    fun setResult1(result1: String?) {
        this.result1 = result1
    }

    fun getResult2(): String? {
        return result2
    }

    fun setResult2(result2: String?) {
        this.result2 = result2
    }
}

class Questions {
    /*@SerializedName("question")
    @Expose
    var question: String? = null
*/
    @SerializedName("answerChoosed")
    @Expose
    private var answerChoosed: String? = null

    @SerializedName("answer")
    @Expose
    internal var answer: String? = null

    @SerializedName("isDefault")
    @Expose
    internal var isDefault: Int? = null

    @SerializedName("orderBy")
    @Expose
    internal var orderBy: Int? = null

    /*fun getQuestion(): String? {
        return question
    }

    fun setQuestion(question: String?) {
        this.question = question
    }
*/
    fun getAnswerChoosed(): String? {
        return answerChoosed
    }

    fun setAnswerChoosed(answerChoosed: String?) {
        this.answerChoosed = answerChoosed
    }

    fun getAnswer(): String? {
        return answer
    }

    fun setAnswer(answer: String?) {
        this.answer = answer
    }

    fun getIsDefault(): Int? {
        return isDefault
    }

    fun setIsDefault(isDefault: Int?) {
        this.isDefault = isDefault
    }

    fun getOrderBy(): Int? {
        return orderBy
    }

    fun setOrderBy(orderBy: Int?) {
        this.orderBy = orderBy
    }

}

class RecommendedTests {
    @SerializedName("name")
    @Expose
    internal var name: String? = null

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }
}