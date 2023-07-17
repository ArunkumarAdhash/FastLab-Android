package com.lifehopehealthapp.covidstatus

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityCovidStatusListBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketName
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketRegion
import com.lifehopehealthapp.utils.PreferenceHelper.w3AccessKey
import com.lifehopehealthapp.utils.PreferenceHelper.w3SecretKey
import com.lifehopehealthapp.utils.Utils
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CovidStatusActivity : BaseActivity<CovidStatusViewModel, CovidStatusModel>() {
    private lateinit var binding: ActivityCovidStatusListBinding
    private var progressDoalog: Dialog? = null
    private lateinit var mLayoutManager: LinearLayoutManager
    private var token: String? = ""
    private var mPrefs: SharedPreferences? = null
    private lateinit var mAdapter: CovidListPagingAdapter
    private var aDummyList: ArrayList<CovidStatusResponse.Data> = ArrayList<CovidStatusResponse.Data>()
    private var results: ArrayList<CovidStatusResponse.Data>? = null

    var nextPageNumber: Int? = 1
    var PageNumber: Int? = 1
    var TotalPageNumber: Int? = 1
    var previousPage: Int = 1
    var balloon: Balloon? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCovidStatusListBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvCovidStatus.layoutManager = mLayoutManager

        val secrtKey = Utils.decryption(mPrefs!!.w3SecretKey)
        val accessKey = Utils.decryption(mPrefs!!.w3AccessKey)


        mAdapter = CovidListPagingAdapter(
            aDummyList,
            this, mPrefs!!.s3BucketName, secrtKey, accessKey, mPrefs!!.s3BucketRegion
        )
        binding.rvCovidStatus.adapter = mAdapter

        binding.ivCloseIcon.setOnClickListener {
            finish()
        }

        val extras = intent.extras
        if (extras != null) {
            binding.toolbarTitle.text = extras.getString("toolBarTitle")
        }

        loadNextPage(1)

        binding.rvCovidStatus.setPullRefreshEnabled(false)
        binding.rvCovidStatus.setLoadingListener(object :
            XRecyclerView.LoadingListener {
            override fun onRefresh() {
            }


            override fun onLoadMore() {
                if (nextPageNumber != 0) {
                    loadNextPage(nextPageNumber!!)
                }
                else{
                    binding.rvCovidStatus.setNoMore(false)
                    binding.rvCovidStatus.refreshComplete()
                }
            }
        })

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


            if (balloon!!.isShowing) {
                balloon!!.dismiss()
            } else {
                balloon!!.showAlignBottom(binding.imageViewtooltip)
            }
        }
    }

    private fun loadNextPage(i: Int) {
        if (Utils.isNetworkAvailable(this)) {


            if (i == 1) {
                if (progressDoalog == null) {
                    progressDoalog = Utils.getDialog(this)
                }
                progressDoalog!!.show()
            }




                val data = CovidStatusAPIRequest()
                data.setPageSize(10)
                data.setPageNumber(i)

                val gson = Gson()
                var json: String? = ""
                json = gson.toJson(data)
                Log.e("Model---->", json.toString())

                viewModel.getCovidStatusList(token!!, data)

            viewModel.Response!!.observe(this) {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (it.value.StatusCode == 200) {
                            try {
                                if (it.value.value!!.data!!.size != 0) {
                                    binding.layoutNoData.isVisible = false
                                    binding.rvCovidStatus.isVisible = true

                                    binding.imageViewtooltip.isVisible = true

                                    TotalPageNumber = it.value.value!!.totalPages
                                    if (nextPageNumber!! <= TotalPageNumber!!) {
                                        if (!it.value.value!!.nextPage.equals("")) {
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


                                    if (nextPageNumber == 1) {
                                        binding.layoutNoData.isVisible = true
                                        binding.rvCovidStatus.isVisible = false
                                        binding.imageViewtooltip.isVisible = false
                                    } else {

                                    }
                                }

                                results =
                                    it.value.value!!.data

                                if (results!!.size > 0) {
                                    mAdapter.addAllSearch(results!!)
                                    mAdapter!!.notifyDataSetChanged()
                                }


                                if (it.value.value!!.nextPage== null) {
                                    binding.rvCovidStatus!!.setNoMore(true)
                                } else {
                                    binding.rvCovidStatus!!.setNoMore(false)
                                }

                                binding.rvCovidStatus!!.refreshComplete()
                            } catch (e: Exception) {
                                Log.e("Exception", e.toString())
                            }
                        }

                        else if (it.value.StatusCode == 401) {
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

    override fun getViewModel() = CovidStatusViewModel::class.java

    override fun getActivityRepository() = CovidStatusModel(
        remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
    )


    @SuppressLint("NotifyDataSetChanged")
    private fun getCovidStatusList() {
        if (Utils.isNetworkAvailable(this)) {
            /*if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()*/

            //val secrtKey = Utils.decryption(mPrefs!!.w3SecretKey)
            //val accessKey = Utils.decryption(mPrefs!!.w3AccessKey)

            val data = CovidStatusAPIRequest()
            data.setPageSize(10)
            data.setPageNumber(1)


            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(data)
            Log.e("Model---->", json.toString())

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(json) as JsonObject

            // viewModel.getCovidStatusList(token!!,aJsonObject)


            /*lifecycleScope.launchWhenCreated {
                viewModel.getCovidListData(token!!).collectLatest {
                    mAdapter.submitData(it)
                }
            }*/


            /* viewModel.responseData.observe(this) {
                 when (it) {
                     is Resource.Success -> {
                         if (progressDoalog != null && progressDoalog!!.isShowing) {
                             progressDoalog!!.dismiss()
                         }
                         if (it.value.StatusCode == 200) {

                             try {
                                 if (it.value.value!!.data.size>0) {
                                     binding.layoutNoData.isVisible = false
                                     binding.rvCovidStatus.isVisible = true
                                     mAdapter =
                                         CovidStatusListAdapter(
                                             it.value.value!!.data,
                                             this,
                                             mPrefs!!.s3BucketName,
                                             secrtKey,
                                             accessKey,mPrefs!!.s3BucketRegion!!)
                                     binding.rvCovidStatus.adapter = mAdapter
                                     mAdapter.notifyDataSetChanged()
                                 } else {
                                     binding.layoutNoData.isVisible = true
                                     binding.rvCovidStatus.isVisible = false
                                 }
                             } catch (e: Exception) {
                                 Log.e("", e.toString())
                             }



                         } else if (it.value.StatusCode == 401) {
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
                            // Utils.showToast(this, it.value.getValue()!!.message.toString(), true)
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
             }*/
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
    }


    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleEvent(event: NoDataFoundModel) {
        if (event.arraySize == 0) {
            binding.layoutNoData.isVisible = true
            binding.rvCovidStatus.isVisible = false
        } else {
            binding.layoutNoData.isVisible = false
            binding.rvCovidStatus.isVisible = true
        }
    }
}