package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

@Keep
class SymptomSearchQuestionResponse {
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
        var data: Dataum? = null

        @SerializedName("status")
        @Expose
        var status: Int? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

    }
}

class Dataum {
    @SerializedName("header")
    @Expose
    private var header: String? = null

    @SerializedName("description")
    @Expose
    private var description: String? = null

    @SerializedName("imagePath")
    @Expose
    private var imagePath: String? = null

    @SerializedName("iOSImagePath")
    @Expose
    private var iOSImagePath: String? = null

    @SerializedName("question")
    @Expose
    internal var question: List<Questionses?>? = null

    fun getHeader(): String? {
        return header
    }

    fun setHeader(header: String?) {
        this.header = header
    }

    fun getDescription(): String? {
        return description
    }

    fun setDescription(description: String?) {
        this.description = description
    }

    fun getImagePath(): String? {
        return imagePath
    }

    fun setImagePath(imagePath: String?) {
        this.imagePath = imagePath
    }

    fun getIOSImagePath(): String? {
        return iOSImagePath
    }

    fun setIOSImagePath(iOSImagePath: String?) {
        this.iOSImagePath = iOSImagePath
    }

    fun getQuestion(): List<Questionses?>? {
        return question
    }

    fun setQuestion(question: List<Questionses?>?) {
        this.question = question
    }
}

class Option {
    @SerializedName("option")
    @Expose
    internal var option: String? = null

    @SerializedName("direction")
    @Expose
    internal var direction: Int? = null

    fun getOption(): String? {
        return option
    }

    fun setOption(option: String?) {
        this.option = option
    }

    fun getDirection(): Int? {
        return direction
    }

    fun setDirection(direction: Int?) {
        this.direction = direction
    }
}

class Questionses {
    @SerializedName("question")
    @Expose
    internal var question: String? = null

    @SerializedName("direction")
    @Expose
    internal var direction: Int? = null

    @SerializedName("options")
    @Expose
    internal var options: List<Option?>? =
        null

    @SerializedName("answer")
    @Expose
    internal var answer: String? = null

    @SerializedName("isDefault")
    @Expose
    internal var isDefault: Int? = null

    @SerializedName("orderBy")
    @Expose
    internal var orderBy: Int? = null

    @SerializedName("imagePath")
    @Expose
    private var imagePath: String? = null

    @SerializedName("iOSImagePath")
    @Expose
    private var iOSImagePath: String? = null

    fun getQuestion(): String? {
        return question
    }

    fun setQuestion(question: String?) {
        this.question = question
    }

    fun getDirection(): Int? {
        return direction
    }

    fun setDirection(direction: Int?) {
        this.direction = direction
    }

    fun getOptions(): List<Option?>? {
        return options
    }

    fun setOptions(options: List<Option?>?) {
        this.options = options
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

    fun getImagePath(): String? {
        return imagePath
    }

    fun setImagePath(imagePath: String?) {
        this.imagePath = imagePath
    }

    fun getIOSImagePath(): String? {
        return iOSImagePath
    }

    fun setIOSImagePath(iOSImagePath: String?) {
        this.iOSImagePath = iOSImagePath
    }
}

