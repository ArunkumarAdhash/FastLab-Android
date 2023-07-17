package com.lifehopehealthapp.bulkbookingNewOrderList

import com.lifehopehealthapp.base.BaseRepository
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper

class NewBulkBookingOrderListModel(private val api: APIManager, val preferences: PreferenceHelper) :
    BaseRepository() {

}