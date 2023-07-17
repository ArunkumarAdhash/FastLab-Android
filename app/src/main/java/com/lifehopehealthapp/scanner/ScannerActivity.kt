package com.lifehopehealthapp.scanner

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.budiyev.android.codescanner.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.APIRequest
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityScannerBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.Utils

class ScannerActivity : BaseActivity<ScannerViewModel, ScannerModel>() {

    private lateinit var binding: ActivityScannerBinding
    private lateinit var codeScanner: CodeScanner
    private var mPrefs: SharedPreferences? = null
    private var token: String? = ""
    private var progressDialog: Dialog? = null

    override fun getViewModel() = ScannerViewModel::class.java

    override fun getActivityRepository() =
        ScannerModel(
            remoteDataSource.buildApiWOEncry(APIManager::class.java),
            PreferenceHelper
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        setContentView(binding.root)

        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken

        codeScanner = CodeScanner(this@ScannerActivity, binding.scannerView)

        // Parameters (default values)
        codeScanner.camera =
            CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                if (Utils.isNetworkAvailable(this@ScannerActivity)) {
                    callAPI(it.text)
                } else {
                    Utils.noInternetAlert(
                        resources.getString(R.string.no_internet_msg),
                        this@ScannerActivity
                    )
                }
                //Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(
                    this@ScannerActivity,
                    "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun callAPI(result: String) {
        if (progressDialog == null) {
            progressDialog = Utils.getDialog(this)
        }
        progressDialog!!.show()
        val data = APIRequest()
        data.setType(result)

        val gson = Gson()
        var json: String? = ""
        json = gson.toJson(data)
        Log.e("Model---->", json.toString())

        viewModel.checkQRStatus(token!!, data)

        viewModel.Response.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (progressDialog != null && progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                    if (it.value.getStatusCode() == 200) {
                        Utils.showToast(this, it.value.getValue()!!.data!!.description!!, true)
                        finish()
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
//                    Utils.showToast(this, it.error!!.message.toString(), true)
                    if (progressDialog != null && progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                    if (it.code == 500){
                        Utils.showToast(this, "QR code is not valid. Kindly scan with a LifeHope QR code", true)
                        finish()
                    }else{
                        Utils.showToast(this, it.error!!.message.toString(), true)
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
                            finish()
                        }
                    }

                }

                is Resource.GenericNewError ->{

                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}