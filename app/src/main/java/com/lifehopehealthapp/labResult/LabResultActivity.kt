package com.lifehopehealthapp.labResult

import android.Manifest
import android.app.Dialog
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityLabResultBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.profile
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketName
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketRegion
import com.lifehopehealthapp.utils.PreferenceHelper.userID
import com.lifehopehealthapp.utils.PreferenceHelper.w3AccessKey
import com.lifehopehealthapp.utils.PreferenceHelper.w3SecretKey
import com.lifehopehealthapp.utils.Utils
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation

class LabResultActivity : BaseActivity<LabResultViewModel, LabResultModel>() {

    private var aDummyList: ArrayList<LabResultPDFResponse.Datum> =
        ArrayList<LabResultPDFResponse.Datum>()
    private var results: ArrayList<LabResultPDFResponse.Datum>? = null

    private var token: String? = ""
    private var categoryID: String? = ""
    private var categoryName: String? = ""
    private val PAGENO: Int = 1
    private var REQUEST_STORAGE_CODE = 99
    private var progressDoalog: Dialog? = null
    var balloon: Balloon? = null
    private var mPrefs: SharedPreferences? = null
    private var data: List<LabResultPDFResponse.Datum>? = null
    private var adapter: LabResultPDFViewAdapter? = null

    var nextPageNumber: Int? = 1
    var PageNumber: Int? = 1
    var TotalPageNumber: Int? = 1
    var previousPage: Int = 1
    private var NEXTPAGE: Int = 1


    private lateinit var binding: ActivityLabResultBinding

    override fun getViewModel() = LabResultViewModel::class.java

    override fun getActivityRepository() =
        LabResultModel(
            remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLabResultBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        categoryID = intent.getStringExtra("testId")
        categoryName = intent.getStringExtra("categoryName")
        binding.textviewTitle.text = categoryName
        binding.recyclerviewLabResult.layoutManager = GridLayoutManager(this, 3)
        val secrtKey = Utils.decryption(mPrefs!!.w3SecretKey)
        val accessKey = Utils.decryption(mPrefs!!.w3AccessKey)

        adapter = LabResultPDFViewAdapter(
            aDummyList,
            this,
            categoryName,
            mPrefs!!.s3BucketName,
            secrtKey,
            accessKey,
            mPrefs!!.userID,
            mPrefs!!.profile.toString(),
            mPrefs!!.s3BucketRegion!!
        )
        binding.recyclerviewLabResult.adapter = adapter

        getAPICall(categoryID)
        loadNextPage(1, categoryID)
        binding.recyclerviewLabResult.setPullRefreshEnabled(false)
        binding.recyclerviewLabResult.setLoadingListener(object :
            XRecyclerView.LoadingListener {
            override fun onRefresh() {
            }


            override fun onLoadMore() {
                if (nextPageNumber != 0) {
                    loadNextPage(nextPageNumber!!, categoryID)
                } else {
                    binding.recyclerviewLabResult.setNoMore(false)
                    binding.recyclerviewLabResult.refreshComplete()
                }
            }
        })
        binding.imageviewBackArrow.setOnClickListener {
            finish()
        }

        //newly hided - 28-03-2022

        balloon = resources.getString(R.string.tooltip_view_password).let {
            Balloon.Builder(baseContext)
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.TOP)
                .setArrowPosition(0.5f)
                .setWidthRatio(0.70f)
                .setMarginLeft(48)
                .setPadding(10)
                .setText(it)
                .setCornerRadius(4f)
                .setTextColorResource(R.color.black)
                .setBackgroundDrawableResource(R.drawable.alert_bg)
                .setBalloonAnimation(BalloonAnimation.FADE)
                .build()
        }


        binding.imageViewtooltip.setOnClickListener {

            //newly hided - 28-03-2022
            if (balloon!!.isShowing) {
                balloon!!.dismiss()
            } else {
                balloon!!.showAlignBottom(binding.imageViewtooltip)
            }
        }
    }

    private fun loadNextPage(i: Int, categoryID: String?) {
        if (Utils.isNetworkAvailable(this)) {

            //if condition-newly added
            if (i == 1) {
                if (progressDoalog == null) {
                    progressDoalog = Utils.getDialog(this)
                }
                progressDoalog!!.show()
            }

            if (i != 0) {
                val data = LabResultAPIRequest()
                data.setCategoryId(categoryID!!)
                val datam = LabResultAPIRequest.PaginationQuery()
                datam.pageNumber = i
                datam.pageSize = 15
                data.setPaginationQuery(datam)

                val gson = Gson()
                var json: String? = ""
                json = gson.toJson(data)
                Log.e("Model---->", json.toString())

                viewModel.getLabResult(token!!, data)
            }

            viewModel.Response!!.observe(this) {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            try {
                                if (it.value.getValue()!!.data!!.size != 0) {
                                    binding.textviewNoData.isVisible = false
                                    binding.recyclerviewLabResult.isVisible = true

                                  //newly added 28-03-2022
                                    binding.imageViewtooltip.isVisible = true

                                    //newly hided
                                    /* if (!it.value.getValue()!!.nextPage.equals("")) {
                                         nextPageNumber = nextPageNumber!! + 1
                                     } else {
                                         nextPageNumber = 0
                                     }
                                     TotalPageNumber = it.value.getValue()!!.totalPages
                                     results =
                                         it.value.getValue()!!.data as ArrayList<LabResultPDFResponse.Datum>?
                                     adapter!!.notifyDataSetChanged()*/


                                    //newly added
                                    TotalPageNumber = it.value.getValue()!!.totalPages
                                    if (nextPageNumber!! <= TotalPageNumber!!) {
                                        if (!it.value.getValue()!!.nextPage.equals("")) {
                                            if (nextPageNumber == TotalPageNumber) {
                                                nextPageNumber = 0
                                            } else {
                                                nextPageNumber = nextPageNumber!! + 1
                                            }

                                        } else {
                                            nextPageNumber = 0
                                        }
                                    }


                                } else {

                                    //newly hided
                                    /*if (nextPageNumber == 0) {

                                    } else {
                                        // binding.layoutNoData.isVisible = true
                                        //binding.imageViewUpload.isVisible = false
                                        binding.recyclerviewLabResult.isVisible = false
                                    }*/


                                    //newly added
                                    if (nextPageNumber == 1) {
                                        binding.textviewNoData.isVisible = true
                                        binding.recyclerviewLabResult.isVisible = false
                                        //newly added 28-03-22
                                        binding.imageViewtooltip.isVisible = false
                                    } else {
                                    }



                                }


                                results =
                                    it.value.getValue()!!.data as ArrayList<LabResultPDFResponse.Datum>?

                                if (results!!.size > 0) {
                                    adapter!!.addAllSearch(results!!)
                                    adapter!!.notifyDataSetChanged()
                                }


                                if (it.value.getValue()!!.nextPage == null) {
                                    binding.recyclerviewLabResult!!.setNoMore(true)
                                } else {
                                    binding.recyclerviewLabResult!!.setNoMore(false)
                                }

                                binding.recyclerviewLabResult!!.refreshComplete()
                            } catch (e: Exception) {
                                Log.e("Exception", e.toString())
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
                            //Utils.showToast(this, it.value.getValue()!!.message.toString(), true)
                        }

                    }
                    is Resource.GenericError -> {
                        Utils.showToast(this, it.error!!.message.toString(), true)
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

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkRunTimePermission() {
        val permissionArrays = arrayOf(
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, REQUEST_STORAGE_CODE)
        } else {
            // if already permition granted
            // PUT YOUR ACTION (Like Open cemara etc..)
        }
    }

    private fun getAPICall(categoryID: String?) {
        if (Utils.isNetworkAvailable(this)) {
            /*if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()*/

            /*lifecycleScope.launchWhenCreated {
                viewModel.getLabResultList(
                    token!!, categoryID!!
                ).collectLatest {
                    adapter!!.submitData(it)
                }
            }*/

            /*val data = LabResultAPIRequest()
            data.setCategoryId(categoryID!!)
            val datam = LabResultAPIRequest.PaginationQuery()
            datam.pageNumber = PAGENO
            datam.pageSize = 10
            data.setPaginationQuery(datam)

            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(data)
            Log.e("Model---->", json.toString())

            viewModel.getLabResult(token!!, data)

            viewModel.Response.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            val mData = it.value.getValue()!!.data

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
                            //Utils.showToast(this, it.value.getValue()!!.message.toString(), true)
                        }
                    }
                    is Resource.GenericError -> {
                        Utils.showToast(this, it.error!!.message.toString(), true)
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
            })*/
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_STORAGE_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.MANAGE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                    }
                } else {

                }
                return
            }
        }
    }

}