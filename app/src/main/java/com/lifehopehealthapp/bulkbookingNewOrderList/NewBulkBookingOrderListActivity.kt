package com.lifehopehealthapp.bulkbookingNewOrderList

import android.os.Bundle
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.databinding.NewBulkBookingOrderListActivityBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.Utils

class NewBulkBookingOrderListActivity : BaseActivity<NewBulkBookingOrderListViewModel, NewBulkBookingOrderListModel>()
    {

    private lateinit var binding: NewBulkBookingOrderListActivityBinding
    override fun getViewModel() = NewBulkBookingOrderListViewModel::class.java


    override fun getActivityRepository() = NewBulkBookingOrderListModel(
        remoteDataSource.buildApi(APIManager::class.java),
        PreferenceHelper
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewBulkBookingOrderListActivityBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        setContentView(binding.root)





    }
}