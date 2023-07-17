package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class DiagnosisHistoryDetailResponse {
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
        var data: Datam? = null

        @SerializedName("status")
        @Expose
        var status: Int? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

    }
}

class RecommendedTest {
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

class Question {
    @SerializedName("question")
    @Expose
    internal var question: String? = null

    @SerializedName("answerChoosed")
    @Expose
    internal var answerChoosed: String? = null

    @SerializedName("answer")
    @Expose
    private var answer: String? = null

    @SerializedName("isDefault")
    @Expose
    private var isDefault: Int? = null

    @SerializedName("orderBy")
    @Expose
    private var orderBy: Int? = null

    fun getQuestion(): String? {
        return question
    }

    fun setQuestion(question: String?) {
        this.question = question
    }

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

class PrimaryDiagnosis {
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

 class Datam {
    @SerializedName("question")
    @Expose
    internal var question: List<Question?>? =
        null

    @SerializedName("primaryDiagnoses")
    @Expose
    private var primaryDiagnoses: List<PrimaryDiagnosis?>? =
        null

    @SerializedName("recommendedTests")
    @Expose
    internal var recommendedTests: List<RecommendedTest?>? =
        null

    @SerializedName("diagnosesMeasure")
    @Expose
    internal var diagnosesMeasure: Double = 0.0

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
    private var testDate: Double? = null

    @SerializedName("shareInfo")
    @Expose
    internal var shareInfo: String? = null

    @SerializedName("location")
    @Expose
    internal var location: String? = null

    @SerializedName("concern")
    @Expose
    internal var concern: String? = null

    @SerializedName("quote")
    @Expose
    private var quote: Any? = null

    fun getQuestion(): List<Question?>? {
        return question
    }

    fun setQuestion(question: List<Question?>?) {
        this.question = question
    }

    fun getPrimaryDiagnoses(): List<PrimaryDiagnosis?>? {
        return primaryDiagnoses
    }

    fun setPrimaryDiagnoses(primaryDiagnoses: List<PrimaryDiagnosis?>?) {
        this.primaryDiagnoses = primaryDiagnoses
    }

    fun getRecommendedTests(): List<RecommendedTest?>? {
        return recommendedTests
    }

    fun setRecommendedTests(recommendedTests: List<RecommendedTest?>?) {
        this.recommendedTests = recommendedTests
    }

    fun getDiagnosesMeasure(): Double {
        return diagnosesMeasure
    }

    fun setDiagnosesMeasure(diagnosesMeasure: Double) {
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

    fun getTestDate(): Double? {
        return testDate
    }

    fun setTestDate(testDate: Double?) {
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

    fun getConcern(): String? {
        return concern
    }

    fun setConcern(concern: String?) {
        this.concern = concern
    }

    fun getQuote(): Any? {
        return quote
    }

    fun setQuote(quote: Any?) {
        this.quote = quote
    }

}

class ProfileInfo {
    @SerializedName("firstName")
    @Expose
    internal var firstName: String? = null

    @SerializedName("lastName")
    @Expose
    internal var lastName: String? = null

    @SerializedName("gender")
    @Expose
    internal var gender: String? = null

    @SerializedName("dob")
    @Expose
    internal var dob: Int? = null

    @SerializedName("profilePicture")
    @Expose
    internal var profilePicture: String? = null

    @SerializedName("height")
    @Expose
    private var height: Any? = null

    @SerializedName("weight")
    @Expose
    private var weight: Any? = null

    @SerializedName("bloodGroup")
    @Expose
    private var bloodGroup: Any? = null

    fun getFirstName(): String? {
        return firstName
    }

    fun setFirstName(firstName: String?) {
        this.firstName = firstName
    }

    fun getLastName(): String? {
        return lastName
    }

    fun setLastName(lastName: String?) {
        this.lastName = lastName
    }

    fun getGender(): String? {
        return gender
    }

    fun setGender(gender: String?) {
        this.gender = gender
    }

    fun getDob(): Int? {
        return dob
    }

    fun setDob(dob: Int?) {
        this.dob = dob
    }

    fun getProfilePicture(): String? {
        return profilePicture
    }

    fun setProfilePicture(profilePicture: String?) {
        this.profilePicture = profilePicture
    }

    fun getHeight(): Any? {
        return height
    }

    fun setHeight(height: Any?) {
        this.height = height
    }

    fun getWeight(): Any? {
        return weight
    }

    fun setWeight(weight: Any?) {
        this.weight = weight
    }

    fun getBloodGroup(): Any? {
        return bloodGroup
    }

    fun setBloodGroup(bloodGroup: Any?) {
        this.bloodGroup = bloodGroup
    }
}