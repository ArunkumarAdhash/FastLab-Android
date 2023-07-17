package com.lifehopehealthapp.productcart

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.ProductListValue
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.databinding.ProductCartListActivityBinding
import com.lifehopehealthapp.myProfile.MapsActivity
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.productDeliveryAddress
import com.lifehopehealthapp.utils.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class ProductCartActivity : BaseActivity<ProductCartListViewModel,ProductCartListModel>(),
    ProductCartListAdapter.ProductListValueCleared, ProductCartListAdapter.ProductListUpdate
{
    private lateinit var binding: ProductCartListActivityBinding
    override fun getViewModel() = ProductCartListViewModel::class.java
    lateinit var productCartListAdapter: ProductCartListAdapter
    private var mProductCartListSelectedValue: ArrayList<ProductListValue> = ArrayList()
    private var mPrefs: SharedPreferences? = null


    override fun getActivityRepository() = ProductCartListModel(
        remoteDataSource.buildApi(APIManager::class.java),
        PreferenceHelper
    )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProductCartListActivityBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        setValues()

        binding.rvProductCartList.setPullRefreshEnabled(false)
        binding.rvProductCartList.setLoadingMoreEnabled(false)


        binding.imageViewBackArrow.setOnClickListener {
            onBackPressed()
        }


        if(mPrefs!!.productDeliveryAddress!!.isNotEmpty())
        {
            binding.address.text= mPrefs!!.productDeliveryAddress
        }
        else
        {
            binding.address.text= resources.getString(R.string.toast_enter_address)
        }


        binding.editAddress.setOnClickListener {
            googleMap()
        }




    }

    private fun setValues() {

      /*  productCartListAdapter = ProductCartListAdapter(
            this,mProductCartListSelectedValue,this,this)
        binding.rvProductCartList.adapter = productCartListAdapter*/
    }

    override fun productValueUpdate(value: ProductListValue, position: Int) {






    }

    override fun productValueCleared(value: ProductListValue, position: Int) {



    }


    private fun googleMap() {
        val str = binding.address.text
        if (str!!.contains("LandMark")) {
            val separated: List<String> = str.split(":")
            Log.e("LandMark", separated[1])
            intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("LandMark", separated[1])
            startActivity(intent)
        } else {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }



    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun handleEvent(event: String) {
        binding.address.text=event

    }







}