package com.lifehopehealthapp.symptomSearch.DiagnosisHistory

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityDiagnosisHistoryBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.*
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class DiagnosisHistoryActivity : BaseActivity<DiagnosisHistoryViewModel, DiagnosisHistoryModel>() {
    private var token: String? = ""
    private var DiagnosisID: Int? = 0
    private var progressDoalog: Dialog? = null
    var listAdapter: DiagnosisHistoryAdapter? = null
    var mPrefs: SharedPreferences? = null

    var mLayoutManager: LinearLayoutManager? = null
    private lateinit var binding: ActivityDiagnosisHistoryBinding


    private var aDummyList: ArrayList<DiagnosisHistoryResponse.Datum> =
        ArrayList<DiagnosisHistoryResponse.Datum>()
    private var results: ArrayList<DiagnosisHistoryResponse.Datum>? = null

    var nextPageNumber: Int? = 1
    var PageNumber: Int? = 1
    var TotalPageNumber: Int? = 1
    var previousPage: Int = 1
    private var NEXTPAGE: Int = 1


    override fun getViewModel() = DiagnosisHistoryViewModel::class.java

    override fun getActivityRepository() =
        DiagnosisHistoryModel(
            remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDiagnosisHistoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        val id = intent.getStringExtra("symptomID")
        val name = intent.getStringExtra("symptomName")
        binding.textviewTitle.text = name + " " + resources.getString(R.string.text_history)
        DiagnosisID = id!!.toInt()
        mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerviewDiagnosisHistory.setLayoutManager(mLayoutManager)
        binding.imageviewBackArrow.setOnClickListener {
            finish()
        }
        binding.recyclerviewDiagnosisHistory.setHasFixedSize(true)

        listAdapter = DiagnosisHistoryAdapter(this, aDummyList)
        binding.recyclerviewDiagnosisHistory.adapter = listAdapter

        getAPICall(DiagnosisID!!)
        loadNextPage(1, DiagnosisID!!)
        binding.recyclerviewDiagnosisHistory.setPullRefreshEnabled(false)
        binding.recyclerviewDiagnosisHistory.setLoadingListener(object :
            XRecyclerView.LoadingListener {
            override fun onRefresh() {
            }


            override fun onLoadMore() {
                if (nextPageNumber != 0) {
                    loadNextPage(nextPageNumber!!, DiagnosisID!!)
                } else {
                    binding.recyclerviewDiagnosisHistory.setNoMore(false)
                    binding.recyclerviewDiagnosisHistory.refreshComplete()
                }
            }
        })
    }

    private fun loadNextPage(i: Int, diagnosisID: Int) {
        if (Utils.isNetworkAvailable(this)) {


            if (i == 1) {

                if (progressDoalog == null) {
                    progressDoalog = Utils.getDialog(this)
                }
                progressDoalog!!.show()
            }



            if (i != 0) {
                val data = DiagnosisHistoryRequestModel()
                data.setDiagnosesId(diagnosisID)
                val data1 = PaginationQuery()
                data1.pageNumber = i
                data1.pageSize = 10
                data.setPaginationQuery(data1)

                val gson = Gson()
                var json: String? = ""
                json = gson.toJson(data)
                Log.e("Model---->", json.toString())

                viewModel.getDiagnosisHistory(token!!, data)
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
                                    binding.recyclerviewDiagnosisHistory.isVisible = true


                                    //newly hided
                                   /* if (!it.value.getValue()!!.nextPage.equals("")) {
                                        nextPageNumber = nextPageNumber!! + 1
                                    } else {
                                        nextPageNumber = 0
                                    }
                                    TotalPageNumber = it.value.getValue()!!.totalPages
                                    results =
                                        it.value.getValue()!!.data as ArrayList<DiagnosisHistoryResponse.Datum>?
                                    listAdapter!!.notifyDataSetChanged()*/


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
                                    /*if (nextPageNumber == 0) {

                                    } else {
//                                        binding.layoutNoData.isVisible = true
//                                        binding.imageViewUpload.isVisible = false
                                        binding.recyclerviewDiagnosisHistory.isVisible = false
                                    }*/


                                    if (nextPageNumber == 1) {
                                        binding.textviewNoData.isVisible = true
                                        binding.recyclerviewDiagnosisHistory.isVisible = false
                                    } else {

                                    }
                                }

                                results =
                                    it.value.getValue()!!.data as ArrayList<DiagnosisHistoryResponse.Datum>?


                                if (results!!.size > 0) {
                                    listAdapter!!.addAllSearch(results!!)
                                    listAdapter!!.notifyDataSetChanged()
                                }


                                if (it.value.getValue()!!.nextPage == null) {
                                    binding.recyclerviewDiagnosisHistory!!.setNoMore(true)
                                } else {
                                    binding.recyclerviewDiagnosisHistory!!.setNoMore(false)
                                }

                                binding.recyclerviewDiagnosisHistory!!.refreshComplete()
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

    private fun getAPICall(diagnosisID: Int) {
        if (Utils.isNetworkAvailable(this)) {
            /*if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()*/
            /*lifecycleScope.launchWhenCreated {
                viewModel.getDiagnosisHistoryList(
                    token!!, diagnosisID
                ).collectLatest {
                    listAdapter!!.submitData(it)
                }
            }*/
            /*viewModel.Response.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            val data1 = it.value.getValue()!!.data!!
                            totalItemCount = it.value.getValue()!!.totalPages!!
                            if (data1.size == 0) {
                                binding.textviewNoData.isVisible = true
                                binding.textviewNoData.text = resources.getString(R.string.text_no_data)
                            } else if (PAGENO == 1) {
                                listAdapter!!.addAll(data1)
                                datum.addAll(data1)
                                Log.e("data1", datum.size.toString())
                                if (data1.size <= 10) {

                                } else {
                                    if (PAGENO <= PAGENO + 1) listAdapter!!.addLoadingFooter() else isLastPage =
                                        true
                                }

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
            })*/
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleEvent(event: NoDataFoundModel) {
        if (event.arraySize == 0) {
            binding.textviewNoData.isVisible = true
            binding.recyclerviewDiagnosisHistory.isVisible = false
            binding.textviewNoData.text = resources.getString(R.string.text_no_data)
        } else {
            binding.textviewNoData.isVisible = false
            binding.recyclerviewDiagnosisHistory.isVisible = true
            listAdapter!!.notifyDataSetChanged()

        }
    }
}