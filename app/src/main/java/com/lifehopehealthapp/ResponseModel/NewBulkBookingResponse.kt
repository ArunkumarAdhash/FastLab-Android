package com.lifehopehealthapp.dashboard

class NewBulkBookingResponse
{
    private var text: String = " "
    private var isSelected : Boolean = false
    private var value: Int = 0


    fun getText(): String {
        return text
    }

    fun setText(text: String) {
        this.text = text
    }

    fun isSelected(): Boolean {
        return isSelected
    }

    fun setSelected(value: Boolean) {
        this.isSelected = value
    }

    fun getValue() : Int {
        return value
    }

    fun setValue(value: Int) {
        this.value = value
    }


}