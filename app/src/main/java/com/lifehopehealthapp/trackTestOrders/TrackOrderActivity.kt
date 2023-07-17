package com.lifehopehealthapp.trackTestOrders

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.DataEvent
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.ResponseModel.TrackOrderAPIRequest
import com.lifehopehealthapp.ResponseModel.TrackOrderResponse
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.dashBoard.DashBoardActivity
import com.lifehopehealthapp.databinding.ActivityTrackOrderBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.*
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.firstname
import com.lifehopehealthapp.utils.PreferenceHelper.lastname
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class TrackOrderActivity : BaseActivity<TrackOrderViewModel, TrackOrderModel>() {

    private lateinit var mAdapter: TrackOrderTimeLineAdapter
    private var trackDataList = ArrayList<TrackOrderResponse.TrackList>()
    private var memberList = ArrayList<TrackOrderResponse.MemberList>()
    private var paymentList = ArrayList<TrackOrderResponse.PaymentList>()
    private var orderID: String? = ""
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var binding: ActivityTrackOrderBinding
    private var progressDoalog: Dialog? = null
    private var orderId: String? = ""
    private var token: String? = ""
    private var fullName: String? = ""
    private var firstName: String? = ""
    private var lastName: String? = ""
    private var mPrefs: SharedPreferences? = null
    var decimalWithTwoDigit: DecimalFormat = DecimalFormat("#,###")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackOrderBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recyclerView.layoutManager = mLayoutManager

        val extras = intent.extras
        orderId = extras!!.getString("orderId").toString()
        Log.e("orderId", orderId.toString())
        firstName = mPrefs!!.firstname
        lastName = mPrefs!!.lastname
        fullName = firstName + " " + lastName
        binding.imageviewInfo.setOnClickListener {
            OrderDialogFragment.newInstance(memberList, paymentList, this)
                .show(supportFragmentManager, OrderDialogFragment.TAG)
        }

        binding.backImg.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        getOrderList()
    }

    private fun getOrderList() {
        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            val data = TrackOrderAPIRequest()
            data.orderId = orderId

            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(data)
            Log.e("Model---->", json.toString())

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(json) as JsonObject

            viewModel.getOrderTrackListData(token!!, aJsonObject)
            viewModel.responseData.observe(this) {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            try {
                                orderID = it.value.getValue()!!.data!!.orderId
                                binding.cardViewTestStatus.isVisible = true
                                binding.imageviewTestImage.loadSvg(it.value.getValue()!!.data!!.testImage)
                                if (it.value.getValue()!!.data!!.addonCount == 0) {
                                    binding.layoutTestcount.isVisible = false
                                } else {
                                    binding.layoutTestcount.isVisible = true
                                    binding.textviewCount.text =
                                        "+" + it.value.getValue()!!.data!!.addonCount.toString()
                                }
                                if (it.value.getValue()!!.data!!.testType == 1) {
                                    binding.textviewTestType.text = "S"
                                } else {
                                    binding.textviewTestType.text = "H"
                                }
                              /*  binding.textviewPrice.text =
                                    resources.getString(R.string.text_price) + decimalWithTwoDigit.format(it.value.getValue()!!.data!!.amount)*/

                                binding.textviewPrice.text =
                                    resources.getString(R.string.text_price_new) +" "+ NumberFormat.getCurrencyInstance(
                                    Locale("en", "US")
                                ).format(it.value.getValue()!!.data!!.amount)




                                binding.textviewTestOrderid.text =
                                    resources.getString(R.string.text_order_id) + " "+it.value.getValue()!!.data!!.orderNo
                                binding.textviewTestHeading.text = it.value.getValue()!!.data!!.testName
                                if (it.value.getValue()!!.data!!.scheduleDate!!.toInt() == 0) {
                                    binding.textviewScheduleTime.isVisible = false
                                } else {
                                    binding.textviewScheduleTime.text =
                                        resources.getString(R.string.text_schedule_txt) + Utils.getDateCurrentTimeZone(
                                            it.value.getValue()!!.data!!.scheduleDate!!.toLong()!!,
                                            Constants.TIMESTAMPTODATE_TWO!!
                                        )
                                }

                                binding.textviewTestDate.text = Utils.getDateCurrentTimeZone(
                                    it.value.getValue()!!.data!!.orderDate!!.toLong()!!,
                                    Constants.TIMESTAMPTODATE_TWO!!
                                )
                                val STATUS: Int = it.value.getValue()!!.data!!.status!!
                                selectStatus(STATUS)

                                memberList =
                                    (it.value.getValue()!!.data!!
                                        ?.getMembers() as ArrayList<TrackOrderResponse.MemberList>?)!!
                                paymentList =
                                    (it.value.getValue()!!.data!!
                                        ?.getPayments() as ArrayList<TrackOrderResponse.PaymentList>?)!!
                                OrderDialogFragment.newInstance(memberList, paymentList, this)



                                trackDataList =
                                    (it.value.getValue()!!.data!!.list as ArrayList<TrackOrderResponse.TrackList>?)!!

                                mAdapter = TrackOrderTimeLineAdapter(trackDataList, this)
                                binding.recyclerView.adapter = mAdapter
                                mAdapter.notifyDataSetChanged();
                                binding.recyclerView.scheduleLayoutAnimation();
                            } catch (e: Exception) {
                                Log.e("", e.toString())
                            }
                        } else if (it.value.getStatusCode() == 401) {
                            var data = RefreshAPIRequest()
                            data.token = mPrefs!!.authToken
                            data.refreshToken = mPrefs!!.refreshToken

                            val gson = Gson()
                            var json: String? = ""
                            json = gson.toJson(data)
                            Log.e("Model---->", json.toString())

                            val aJsonParser = JsonParser()
                            val aJsonObject = aJsonParser.parse(json) as JsonObject

                            val refresh = ErrorHandling(this)
                            refresh.onErrorHandling(mPrefs!!.authToken!!, aJsonObject)
                        } else {
                            Utils.showToast(this, it.value.getValue()!!.message.toString(), true)
                        }

                    }
                    is Resource.GenericError -> {
                        Utils.showToast(this, it.error!!.message.toString(),true)
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (it.code == 401) {
                            val data = RefreshAPIRequest()
                            val removeChar = mPrefs!!.authToken!!.replace("Bearer ", "")
                            data.token = removeChar
                            data.refreshToken = mPrefs!!.refreshToken

                            val gson = Gson()
                            var json: String? = ""
                            json = gson.toJson(data)
                            Log.e("Model---->", json.toString())

                            val aJsonParser = JsonParser()
                            val aJsonObject = aJsonParser.parse(json) as JsonObject

                            val refresh = ErrorHandling(this)
                            refresh.onErrorHandling(mPrefs!!.authToken!!, aJsonObject)
                        } else {

                        }
                    }
                }
            }
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
    }

    private fun selectStatus(status: Int) {
        when (status) {
            1 -> {
                binding.textviewTestStatus.text = "Pending"
                binding.layoutStatus.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_pending
                )
            }
            2 -> {
                binding.textviewTestStatus.text = "Approved"
                binding.layoutStatus.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_approved
                )
            }
            3 -> {
                binding.textviewTestStatus.text = "Rejected"
                binding.layoutStatus.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_rejected
                )
            }
            4 -> {
                binding.textviewTestStatus.text = "In-progress"
                binding.layoutStatus.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_inprogress
                )
            }
            5 -> {
                binding.textviewTestStatus.text = "Completed"
                binding.layoutStatus.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_completed
                )
            }
            6 -> {
                binding.textviewTestStatus.text =
                    resources.getString(R.string.text_declined)
                binding.layoutStatus.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_rejected
                )
            }
            7 -> {
                binding.textviewTestStatus.text =
                    resources.getString(R.string.text_refunded)
                binding.layoutStatus.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_pending
                )
            }
        }
    }

    fun ImageView.loadSvg(url: String?) {
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }

    override fun getViewModel() = TrackOrderViewModel::class.java


    override fun getActivityRepository() =
        TrackOrderModel(remoteDataSource.buildApi(APIManager::class.java), PreferenceHelper)

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: DataEvent?) {

        if (Utils.isNetworkAvailable(this)) {
            callShipmentAPI(event!!)
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
    }

    private fun callShipmentAPI(event: DataEvent) {
        val studentsObj = JSONObject()
      //  studentsObj.put("id",/* event.id*/1)
        studentsObj.put("orderId", orderID)
        studentsObj.put("shipmentName", event.shippingName)
        studentsObj.put("shipmentId", event.shippingID)
        studentsObj.put("primaryName", fullName)
        val jsonStr = studentsObj.toString()
        val aJsonParser = JsonParser()
        val aJsonObject = aJsonParser.parse(jsonStr) as JsonObject
        if (progressDoalog == null) {
            progressDoalog = Utils.getDialog(this)
        }
        progressDoalog!!.show()
        viewModel.getUpdateShippment(aJsonObject, token!!)

        viewModel.response.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    if (progressDoalog != null && progressDoalog!!.isShowing) {
                        progressDoalog!!.dismiss()
                    }

                    if (it.value.getValue()!!.getStatus() == 1) {
                        getOrderList()
                    }
                }
                is Resource.GenericError -> {
                    Utils.showToast(this, it.error!!.message.toString(),true)
                    if (progressDoalog != null && progressDoalog!!.isShowing) {
                        progressDoalog!!.dismiss()
                    }
                    if (it.code == 401) {
                        val data = RefreshAPIRequest()
                        val removeChar = mPrefs!!.authToken!!.replace("Bearer ", "")
                        data.token = removeChar
                        data.refreshToken = mPrefs!!.refreshToken

                        val gson = Gson()
                        var json: String? = ""
                        json = gson.toJson(data)
                        Log.e("Model---->", json.toString())

                        val aJsonParser = JsonParser()
                        val aJsonObject = aJsonParser.parse(json) as JsonObject

                        val refresh = ErrorHandling(this)
                        refresh.onErrorHandling(mPrefs!!.authToken!!, aJsonObject)
                    } else {

                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        if (isTaskRoot) {
            startActivity(
                Intent(
                    this,
                    DashBoardActivity::class.java
                )
            )
            finish()
        } else {
            super.onBackPressed()
        }
    }
}
