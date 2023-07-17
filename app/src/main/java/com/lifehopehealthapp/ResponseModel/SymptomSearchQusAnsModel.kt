package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class SymptomSearchQusAnsModel {
    @SerializedName("question")
    @Expose
    internal var question: List<QuestionData?>? = null

    @SerializedName("diagnosesId")
    @Expose
    private var diagnosesId: Int? = null

    @SerializedName("diagnosesName")
    @Expose
    private var diagnosesName: String? = null

    @SerializedName("profileDetail")
    @Expose
    internal var profileDetail: ProfileDetails? = null

    @SerializedName("location")
    @Expose
    internal var location: String? = null

    @SerializedName("state")
    @Expose
    internal var state: String? = null

    @SerializedName("concern")
    @Expose
    private var concern: String? = null

    @SerializedName("testDate")
    @Expose
    internal var testDate: Int? = null

    fun getQuestion(): List<QuestionData?>? {
        return question
    }

    fun setQuestion(question: List<QuestionData?>?) {
        this.question = question
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

    fun getProfileDetail(): ProfileDetails? {
        return profileDetail
    }

    fun setProfileDetail(profileDetail: ProfileDetails?) {
        this.profileDetail = profileDetail
    }

    fun getLocation(): String? {
        return location
    }

    fun setLocation(location: String?) {
        this.location = location
    }

    fun getState(): String? {
        return state
    }

    fun setStatus(State: String?) {
        this.state = State
    }


    fun getConcern(): String? {
        return concern
    }

    fun setConcern(concern: String?) {
        this.concern = concern
    }

    fun getTestDate(): Int? {
        return testDate
    }

    fun setTestDate(testDate: Int?) {
        this.testDate = testDate
    }
}

class QuestionData {
    @SerializedName("question")
    @Expose
    var question: String? = ""

    @SerializedName("answerChoosed")
    @Expose
    var answerChoosed: String? = ""

    @SerializedName("answer")
    @Expose
    var answer: String? = ""

    @SerializedName("isDefault")
    @Expose
    var isDefault: Int? = 0

    @SerializedName("orderBy")
    @Expose
    var orderBy: Int? = 0

}

class ProfileDetails {
    @SerializedName("firstName")
    @Expose
    internal var firstName: String? = ""

    @SerializedName("lastName")
    @Expose
    internal var lastName: String? = ""

    @SerializedName("gender")
    @Expose
    internal var gender: String? = ""

    @SerializedName("dob")
    @Expose
    internal var dob: Int? = 0

    @SerializedName("profilePicture")
    @Expose
    internal var profilePicture: String? = ""

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
}


