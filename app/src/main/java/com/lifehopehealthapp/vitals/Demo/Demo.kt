package com.lifehopehealthapp.vitals.Demo

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.ResponseModel.OrderListData
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityVitalsHistorylistBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PaginationScrollListeners
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.Utils


class Demo : BaseActivity<DemoViewModel, DemoModel>() {

    private var mPrefs: SharedPreferences? = null
    private var token: String? = ""
    private var adapter: PaginationAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var progressBar: ProgressBar? = null
    private val PAGE_START = 1
    private var isLoading = false
    private var isLastPage = false
    var totalPageCount = 0
    private var currentPage = PAGE_START

    //private var mData: ArrayList<VitalsDatum> = ArrayList<VitalsDatum>()
    private var mData: ArrayList<OrderListData> = ArrayList<OrderListData>()

    override fun getViewModel() = DemoViewModel::class.java

    override fun getActivityRepository() =
        DemoModel(
            remoteDataSource.buildApi(
                APIManager::class.java
            ), PreferenceHelper
        )

    lateinit var binding: ActivityVitalsHistorylistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVitalsHistorylistBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        adapter = PaginationAdapter(this)
//        binding.recyclerviewVitalHistoryList.layoutManager =
//            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerviewVitalHistoryList.setLayoutManager(linearLayoutManager)

        binding.recyclerviewVitalHistoryList.setItemAnimator(DefaultItemAnimator())

        binding.recyclerviewVitalHistoryList.setAdapter(adapter)


        binding.recyclerviewVitalHistoryList.addOnScrollListener(object :
            PaginationScrollListeners(linearLayoutManager!!) {
            protected override fun loadMoreItems() {
                isLoading = true
                currentPage += 1

                // mocking network delay for API call
                mData.clear()
                binding.mProgressBar.isVisible = true
                Handler().postDelayed(Runnable { loadFirstPage() }, 1000)
            }

            fun getTotalPageCount(): Int {
                return totalPageCount
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })

        // mocking network delay for API call

        // mocking network delay for API call
        mData.clear()
        loadFirstPage()
        binding.recyclerviewVitalHistoryList.setHasFixedSize(true)
    }

    private fun loadFirstPage() {
        //viewModel.getVitalsList(token!!, 6, currentPage, 10)
        viewModel.getTestList(token!!, currentPage, 10)
        viewModel.Response.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    binding.mProgressBar.isVisible = false
                    val data1 = it.value.getValue()!!.data
                    mData = it.value.getValue()!!.data as ArrayList<OrderListData>
                    Log.e("data1", data1!!.size.toString())
                    totalPageCount = it.value.getValue()!!.totalPages!!
                    adapter!!.addAll(mData)
                    //it.value.getValue()!!.data.clear()
                    ///if (currentPage <= totalPageCount) adapter!!.addLoadingFooter() else isLastPage = true

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
        //val movies: List<Movie> = Movie.createMovies(adapter.getItemCount())

    }

    /*private fun loadNextPage() {
        viewModel.getVitalsList(token!!, 6, currentPage, 10)

        viewModel.Response.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    val data1 = it.value.data
                    Log.e("data1", data1!!.size.toString())
                    totalPageCount = it.value.totalPages!!
                    //adapter!!.removeLoadingFooter()
                    isLoading = false
                    adapter!!.addAll(data1)
                    //if (currentPage != totalPageCount) adapter!!.addLoadingFooter() else isLastPage = true
                    it.value.data!!.clear()
                }
                is Resource.Failure -> {

                }
            }
        })
    }*/
}