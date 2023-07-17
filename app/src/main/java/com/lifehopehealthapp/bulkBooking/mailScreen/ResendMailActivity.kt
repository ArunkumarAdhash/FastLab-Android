package com.lifehopehealthapp.bulkBooking.mailScreen

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.MailSendModel
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.dashBoard.DashBoardActivity
import com.lifehopehealthapp.databinding.ActivityMailConfirmationBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.Utils

class ResendMailActivity : BaseActivity<ResendMailViewModel, ResendMailModel>() {

    private var mPrefs: SharedPreferences? = null
    private var token: String? = ""
    private var progressDialog: Dialog? = null

    private var bulkbooking : String?=""
    private var bookingType : String?=""
    private var paymentStatus : Int=0


    private lateinit var binding: ActivityMailConfirmationBinding

    override fun getViewModel() = ResendMailViewModel::class.java

    override fun getActivityRepository() =
        ResendMailModel(
            remoteDataSource.buildApi(APIManager::class.java),
            PreferenceHelper
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMailConfirmationBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)

        token = mPrefs!!.authToken
        val statusvalue: String? = intent.getStringExtra("emailContent")
        val orderID: String? = intent.getStringExtra("orderID")
        binding.textViewMailContent.setText(statusvalue)


        bulkbooking=intent.getStringExtra("bulkbooking")

        bookingType=intent.getStringExtra("bookingType")

        paymentStatus=intent.getIntExtra("paymentStatus",0)

       // binding.buttonResend.isVisible= bookingType.equals("1")


        binding.buttonOkay.setOnClickListener {
            val i = Intent(this, DashBoardActivity::class.java)
            i.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
            finish()
        }

        binding.buttonResend.setOnClickListener {

            if(bulkbooking.equals("1"))
            {
                if (Utils.isNetworkAvailable(this)) {

                    val data = MailSendModel()
                    data.bookingType = 1
                    data.orderID = orderID
                    data.paymentStatus = if(bookingType.equals("0")) paymentStatus else 1

                    val gson = Gson()
                    var json: String? = ""
                    json = gson.toJson(data)
                    Log.e("OMGGGG", json)

                    val aJsonParser = JsonParser()
                    val aJsonObject = aJsonParser.parse(json) as JsonObject

                    if (progressDialog == null) {
                        progressDialog = Utils.getDialog(this)
                    }
                    progressDialog!!.show()
                    viewModel.getBulkOrderConfirm(token!!, aJsonObject)

                    viewModel.Response2.observe(this, Observer {
                        when (it) {
                            is Resource.Success -> {
                                if (progressDialog != null && progressDialog!!.isShowing) {
                                    progressDialog!!.dismiss()
                                }
                                if (it.value.getStatusCode() == 200) {
                                    Utils.showToast(this,
                                        it.value.getValue()!!.data!!.getEmailContent()!!, true)
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
                                    Utils.showToast(
                                        this,
                                        it.value.getValue()!!.message.toString(),
                                        true
                                    )
                                }

                            }
                            is Resource.GenericError -> {
                                Utils.showToast(this, it.error!!.message.toString(),true)

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
                } else {
                    Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
                }
            }


            else
            {

                if (Utils.isNetworkAvailable(this)) {

                    val data = MailSendModel()
                    data.bookingType = 1
                    data.orderID = orderID
                    data.paymentStatus = 1

                    val gson = Gson()
                    var json: String? = ""
                    json = gson.toJson(data)
                    Log.e("OMGGGG",""+ json)

                    val aJsonParser = JsonParser()
                    val aJsonObject = aJsonParser.parse(json) as JsonObject

                    if (progressDialog == null) {
                        progressDialog = Utils.getDialog(this)
                    }
                    progressDialog!!.show()
                    viewModel.getOrderConfirm(token!!, aJsonObject)

                    viewModel.Response1.observe(this, Observer {
                        when (it) {
                            is Resource.Success -> {
                                if (progressDialog != null && progressDialog!!.isShowing) {
                                    progressDialog!!.dismiss()
                                }
                                if (it.value.getStatusCode() == 200) {
                                    Utils.showToast(this, "Can you please check your mail", true)
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
                                    Utils.showToast(
                                        this,
                                        it.value.getValue()!!.message.toString(),
                                        true
                                    )
                                }

                            }
                            is Resource.GenericError -> {
                                Utils.showToast(this, it.error!!.message.toString(),true)

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
                } else {
                    Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
                }
            }


        }
    }


    override fun onBackPressed() {
        val i = Intent(this, DashBoardActivity::class.java)
        i.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
        finish()
    }
}