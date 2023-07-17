package com.lifehopehealthapp.ResponseModel

import androidx.annotation.Keep

@Keep
class DataListModel {

    private var item: List<ListItem?>? = null

    fun getHistoryItem(): List<ListItem?>? {
        return item
    }

    fun setHistoryItem(members: List<ListItem?>?) {
        this.item = members
    }

}

class ListItem {
    var date: String? = ""
    var value: String? = ""
    var type: String? = ""
}