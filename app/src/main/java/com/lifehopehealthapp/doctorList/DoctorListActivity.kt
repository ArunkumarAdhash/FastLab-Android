package com.lifehopehealthapp.doctorList

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
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
import com.lifehopehealthapp.databinding.ActivityDoctorListBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.utils.Utils.isNetworkAvailable
import com.lifehopehealthapp.utils.Utils.noInternetAlert
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DoctorListActivity : BaseActivity<DoctorListViewModel, DoctorListModel>() {

    private lateinit var binding: ActivityDoctorListBinding
    private var mPrefs: SharedPreferences? = null
    private var token: String? = ""
    private var editTextSearchData: String? = ""
    private var adapter: DoctorListAdapter? = null
    private var progressDoalog: Dialog? = null

    var nextPageNumber: Int? = 1
    var PageNumber: Int? = 1
    var TotalPageNumber: Int? = 1
    var previousPage: Int = 1

    private var aDummyList: ArrayList<DoctorListResponse.Datum> =
        ArrayList<DoctorListResponse.Datum>()
    private var results: ArrayList<DoctorListResponse.Datum>? = null

    override fun getViewModel() = DoctorListViewModel::class.java

    override fun getActivityRepository() =
        DoctorListModel(
            remoteDataSource.buildApi(
                APIManager::class.java
            ), PreferenceHelper
        )


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDoctorListBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        binding.recyclerviewDoctorList.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding.imageviewBackArrow.setOnClickListener {
            finish()
        }

        adapter = DoctorListAdapter(
            this, aDummyList
        )
        binding.recyclerviewDoctorList.adapter = adapter

        binding.layoutSearch.setOnClickListener {
            binding.editTextSearch.setIconifiedByDefault(true)
            binding.editTextSearch.isFocusable = true
            binding.editTextSearch.isIconified = false
            binding.editTextSearch.requestFocusFromTouch()
        }

        binding.editTextSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                //adapter.filter.filter(p0)
                try {

                    if (p0 == "" && p0.isEmpty()) {
                        binding.editTextSearch.setIconifiedByDefault(false)
                        binding.editTextSearch.clearFocus()
                        editTextSearchData = p0
                        binding.recyclerviewDoctorList.smoothScrollToPosition(0)
                        loadNextPage(1, editTextSearchData!!)
                        adapter!!.notifyDataSetChanged()
                    }
                    editTextSearchData = p0
                    if (isNetworkAvailable(this@DoctorListActivity)) {
                        if (editTextSearchData!!.length >= 3) {
                            loadNextPage(1, editTextSearchData!!)
                        }
                    } else {
                        noInternetAlert(
                            resources.getString(R.string.no_internet_msg),
                            this@DoctorListActivity
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return false
            }
        })

        loadNextPage(1, editTextSearchData!!)
        binding.recyclerviewDoctorList.setPullRefreshEnabled(false)
        binding.recyclerviewDoctorList.setLoadingListener(object :
            XRecyclerView.LoadingListener {
            override fun onRefresh() {
            }


            override fun onLoadMore() {
                if (nextPageNumber != 0) {
                    loadNextPage(nextPageNumber!!, editTextSearchData!!)
                }
                else{
                    binding.recyclerviewDoctorList.setNoMore(false)
                    binding.recyclerviewDoctorList.refreshComplete()
                }
            }
        })
    }

    private fun loadNextPage(nextpageNumber: Int, SearchData: String) {
        if (isNetworkAvailable(this)) {
            /*lifecycleScope.launchWhenCreated {
                viewModel.getDoctorListData(
                    token!!, editTextSearchData!!
                ).collectLatest {
                    adapter!!.submitData(it)
                }
            }*/
            if (nextpageNumber != 0) {
                val data = DoctorListRequest()
                data.setText(SearchData)
                val paging = Pagination()
                paging.pageSize = 10
                paging.pageNumber = nextpageNumber
                data.setPaginationQuery(paging)
                viewModel.getDoctorList(token!!, data)

            }

            viewModel.Response!!.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            try {
                                if (it.value.getValue()!!.data!!.size != 0) {
                                    binding.layoutNoData.isVisible = false
                                    binding.recyclerviewDoctorList.isVisible = true

                                  //newly hided
                                 /*   if (!it.value.getValue()!!.nextPage.equals("")) {
                                        nextPageNumber = nextPageNumber!! + 1
                                    } else {
                                        nextPageNumber = 0
                                    }
                                    TotalPageNumber = it.value.getValue()!!.totalPages
                                    results =
                                        it.value.getValue()!!.data as ArrayList<DoctorListResponse.Datum>?
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
                                   /* if (nextPageNumber == 0) {

                                    } else {
                                        binding.layoutNoData.isVisible = true
                                        binding.recyclerviewDoctorList.isVisible = false
                                    }*/

                                     //newly added
                                    if (nextpageNumber == 1) {
                                        binding.layoutNoData.isVisible = true
                                        binding.recyclerviewDoctorList.isVisible = false
                                    } else {

                                    }
                                }

                                results =
                                    it.value.getValue()!!.data as ArrayList<DoctorListResponse.Datum>?

                                if (results!!.size > 0) {
                                    adapter!!.addAllSearch(results!!)
                                    adapter!!.notifyDataSetChanged()
                                }


                                if (it.value.getValue()!!.nextPage == null) {
                                    binding.recyclerviewDoctorList!!.setNoMore(true)
                                } else {
                                    binding.recyclerviewDoctorList!!.setNoMore(false)
                                }

                                binding.recyclerviewDoctorList!!.refreshComplete()
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
            binding.recyclerviewDoctorList.isVisible = false
        } else {
            binding.layoutNoData.isVisible = false
            binding.recyclerviewDoctorList.isVisible = true
        }
    }

}