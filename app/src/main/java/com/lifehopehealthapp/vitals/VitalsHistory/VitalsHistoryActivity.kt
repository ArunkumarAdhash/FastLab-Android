package com.lifehopehealthapp.vitals.VitalsHistory

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.VitalsDatum
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityVitalsHistorylistBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.PaginationScrollListener
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.Utils

class VitalsHistoryActivity : BaseActivity<VitalsHistoryViewModel, VitalsHistoryModel>() {

    private var progressDoalog: Dialog? = null
    private var mPrefs: SharedPreferences? = null
    private var adapter: VitalsHistoryAdapter? = null
    private var token: String? = ""
    private var aDummyList: ArrayList<VitalsDatum>? = ArrayList<VitalsDatum>()
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private var PAGENO: Int = 1
    private var totalItemCount: Int = 0
    private var updted: Boolean = false
    private var datavalue: Boolean = false

    override fun getViewModel() = VitalsHistoryViewModel::class.java

    override fun getActivityRepository() =
        VitalsHistoryModel(
            remoteDataSource.buildApi(
                APIManager::class.java
            ), PreferenceHelper
        )

    private lateinit var binding: ActivityVitalsHistorylistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVitalsHistorylistBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        binding.recyclerviewVitalHistoryList.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val vitalID: String? = intent.getStringExtra(Constants.VITALS_ID)
        val name: String? = intent.getStringExtra(Constants.VITALS_NAME)
        binding.textViewLabel.text = name
        binding.imageViewBackArrow.setOnClickListener {
            finish()
        }

        if (PAGENO == 1) {
            aDummyList!!.clear()
            getAPICall(vitalID, PAGENO)
        }

        binding.recyclerviewVitalHistoryList.addOnScrollListener(object :
            PaginationScrollListener(binding.recyclerviewVitalHistoryList.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                PAGENO += 1
                if (totalItemCount >= PAGENO) {
                    updted = false
                    datavalue = true
                    aDummyList!!.clear()
                    getAPICall(vitalID, PAGENO)
                } else {

                }
            }
        })

        binding.recyclerviewVitalHistoryList.setHasFixedSize(true)
        adapter = VitalsHistoryAdapter()
        binding.recyclerviewVitalHistoryList.adapter = adapter


    }

    private fun getAPICall1(vitalID: String?, pageno: Int) {
        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            viewModel.getVitalsList(token!!, vitalID!!.toInt(), pageno, 10)

            viewModel.Response.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }

                        val updateddata = it.value.data
                        Log.e("data1", updateddata!!.size.toString())
                        totalItemCount = it.value.totalPages!!

                        if (updted) {
                            if (updateddata.size <= 10) {
                                if (datavalue) {
                                    datavalue = false
                                    adapter!!.removeLoadingFooter()
                                    isLoading = false
                                    adapter!!.addAll(updateddata)
                                    adapter!!.notifyDataSetChanged()
                                    //viewModel.Response.postValue(null)
                                    Log.e("data1", updateddata.size.toString())
                                    viewModel.Response.postValue(null)
                                    it.value.data!!.clear()
                                    if (totalItemCount == PAGENO) {

                                    } else {
                                        if (PAGENO != PAGENO + 1) {
                                            adapter!!.addLoadingFooter()
                                        } else {
                                            isLastPage = true
                                        }
                                    }

                                }

                            } else {
                                Log.e("", "")
                            }

                        } else {
                            updted = true
                        }
                    }
                    is Resource.GenericError -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                    }
                }
            })
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
    }

    private fun getAPICall(vitalID: String?, pageno: Int) {
        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            viewModel.getVitalsList(token!!, vitalID!!.toInt(), pageno, 10)

            viewModel.Response.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        aDummyList = it.value.data
                        Log.e("data1", aDummyList!!.size.toString())
                        totalItemCount = it.value.totalPages!!
                        if (aDummyList!!.size == 0) {
                            binding.recyclerviewVitalHistoryList.isVisible = false
                            binding.textviewNoData.isVisible = true
                            binding.textviewNoData.text =
                                resources.getString(R.string.text_no_test_data)
                        } else {
                            adapter!!.addAll(aDummyList!!)
                            Log.e("historySize", aDummyList!!.size.toString())
                            viewModel.Response.postValue(null)
                            it.value.data!!.clear()
                            Log.e("it", it.value.data!!.size.toString())
                            if (aDummyList!!.size <= 10) {

                            } else {
                                if (PAGENO <= PAGENO + 1) adapter!!.addLoadingFooter() else isLastPage =
                                    true
                            }
                        }
                    }
                    is Resource.GenericError -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                    }
                }
            })
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
    }
}