package com.lifehopehealthapp.myTestOrderList

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
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
import com.lifehopehealthapp.databinding.ActivityMyorderListBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.*
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.widgets.TransitionHelper

class MyTestOrderListActivity : BaseActivity<MyTestOrderListViewModel, MyTestOrderListModel>() {
    private var token: String? = ""
    private var progressDoalog: Dialog? = null
    private var data: ArrayList<OrderListData>? = ArrayList()

    private var adapter: MyTestOrderTestListAdapter? = null

    private var mPrefs: SharedPreferences? = null
    private var aDummyList: ArrayList<OrderListData>? =
        ArrayList()

    private var results: ArrayList<OrderListData>? = null

    private lateinit var binding: ActivityMyorderListBinding


    var nextPageNumber: Int? = 1
    var PageNumber: Int? = 1
    var TotalPageNumber: Int? = 1
    var previousPage: Int = 1

    override fun getViewModel() = MyTestOrderListViewModel::class.java

    override fun getActivityRepository() = MyTestOrderListModel(
        remoteDataSource.buildApi(APIManager::class.java),
        preferences = PreferenceHelper
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyorderListBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken

        binding.recyclerviewTestList.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)


        binding.recyclerviewTestList.setHasFixedSize(true)
        adapter = MyTestOrderTestListAdapter(aDummyList, this)
        binding.recyclerviewTestList.adapter = adapter


        getAPICall(1)

        binding.recyclerviewTestList.setPullRefreshEnabled(false)
        binding.recyclerviewTestList.setLoadingListener(object :
            XRecyclerView.LoadingListener {
            override fun onRefresh() {
            }


            override fun onLoadMore() {
                if (nextPageNumber != 0) {
                    getAPICall(nextPageNumber!!)
                } else {
                    binding.recyclerviewTestList.setNoMore(false)
                    binding.recyclerviewTestList.refreshComplete()
                }
            }
        })

        binding.imageviewBackArrow.setOnClickListener {
            finish()
        }
    }

    private fun getAPICall(pageno: Int) {
        if (Utils.isNetworkAvailable(this)) {

            if (pageno == 1) {
                if (progressDoalog == null) {
                    progressDoalog = Utils.getDialog(this)
                }
                progressDoalog!!.show()
            }


            val datum = MyOrderListAPIRequest()
            datum.pageNumber = pageno
            datum.pageSize = 10

            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(datum)
            Log.e("Model---->", json.toString())

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(json) as JsonObject

            viewModel.getTestList(token!!, aJsonObject)


            viewModel.Response.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }


                        if (it.value.getStatusCode() == 200) {
                            try {
                                if (it.value.getValue()!!.data!!.size != 0) {
                                    binding.textviewNoData.isVisible = false
                                    binding.recyclerviewTestList.isVisible = true

                                    val updateddata = it.value.getValue()!!.data
                                    data!!.addAll(updateddata!!)

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


                                    if (nextPageNumber == 1) {
                                        binding.textviewNoData.isVisible = true
                                        binding.recyclerviewTestList.isVisible = false
                                    } else {

                                    }
                                }


                                results =
                                    it.value.getValue()!!.data as ArrayList<OrderListData>?

                                if (results!!.size > 0) {
                                    adapter!!.addAllSearch(results!!)
                                    adapter!!.notifyDataSetChanged()
                                }


                                if (it.value.getValue()!!.nextPage == null) {
                                    binding.recyclerviewTestList!!.setNoMore(true)
                                } else {
                                    binding.recyclerviewTestList!!.setNoMore(false)
                                }

                                binding.recyclerviewTestList!!.refreshComplete()
                            } catch (e: Exception) {
                                Log.e("Exception", e.toString())
                            }
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
            })
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        TransitionHelper.removeActivityFromTransitionManager(this)
    }
}
