package com.lifehopehealthapp.notificationList

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityNotificationListBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NotificationListActivity : BaseActivity<NotificationListViewModel, NotificationListModel>() {

    private var token: String? = ""
    internal var mPrefs: SharedPreferences? = null
    private var progressDialog: Dialog? = null
    private var adapter: NotificationAdapter? = null
    private var progressDoalog: Dialog? = null

    var nextPageNumber: Int? = 1
    var PageNumber: Int? = 1
    var TotalPageNumber: Int? = 1
    var previousPage: Int = 1

    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var currentPage: Int = 1
    private var totalPages: Int? = 0

    private var aDummyList: ArrayList<NotificationListResponse.Datum> = ArrayList()
    private var results: ArrayList<NotificationListResponse.Datum>? = null


    private lateinit var binding: ActivityNotificationListBinding

    override fun getViewModel() = NotificationListViewModel::class.java

    override fun getActivityRepository() =
        NotificationListModel(remoteDataSource.buildApi(APIManager::class.java), PreferenceHelper)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken

        /*binding.recyclerviewNotificationList.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
*/
//        binding.recyclerviewNotificationList.layoutManager =
//            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        adapter = NotificationAdapter(this, aDummyList)
        binding.recyclerviewNotificationList.adapter = adapter

        getNotifications(1)

        val mLayoutManager: LinearLayoutManager
        mLayoutManager = LinearLayoutManager(this)
        binding.recyclerviewNotificationList.setLayoutManager(mLayoutManager)

        binding.recyclerviewNotificationList.setPullRefreshEnabled(false)
        binding.recyclerviewNotificationList.setLoadingListener(object :
            XRecyclerView.LoadingListener {
            override fun onRefresh() {

            }

            override fun onLoadMore() {
                if (nextPageNumber != 0) {
                    getNotifications(nextPageNumber!!)
                } else {
                    binding.recyclerviewNotificationList.setNoMore(false)
                    binding.recyclerviewNotificationList.refreshComplete()
                }
            }
        })

        binding.recyclerviewNotificationList.setHasFixedSize(true)

        binding.imageviewBackArrow.setOnClickListener {
            finish()
        }
    }

    private fun getNotifications(currentPage: Int) {
        if (progressDialog == null) {
            progressDialog = Utils.getDialog(this)
        }
        progressDialog!!.show()
        val datum = MyOrderListAPIRequest()
        datum.pageNumber = currentPage
        datum.pageSize = 10

        val gson = Gson()
        var json: String? = ""
        json = gson.toJson(datum)
        Log.e("Model---->", json.toString())

        val aJsonParser = JsonParser()
        val aJsonObject = aJsonParser.parse(json) as JsonObject

        viewModel.getNotificationList(token!!, aJsonObject)

        viewModel.Response.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (progressDialog != null && progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }

                    if (it.value.getStatusCode() == 200) {
                        totalPages = it.value.getValue()!!.totalPages!!


                        results =
                            it.value.getValue()!!.data as ArrayList<NotificationListResponse.Datum>?

                        if (it.value.getValue()!!.data!!.size != 0) {
                            binding.textviewNoData.isVisible = false
                            binding.recyclerviewNotificationList.isVisible = true

                            //newly added
                            TotalPageNumber = it.value.getValue()!!.totalPages
                            if (nextPageNumber!! <= TotalPageNumber!!) {
                                if (it.value.getValue()!!.nextPage!= null) {
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
                            if (currentPage == 1) {
                                binding.textviewNoData.isVisible = true
                                binding.recyclerviewNotificationList.isVisible = false
                            }
                        }
                        if (results!!.size > 0) {
                            adapter!!.addAllSearch(results!!)
                            adapter!!.notifyDataSetChanged()
                        }

                        if (it.value.getValue()!!.nextPage == null) {
                            binding.recyclerviewNotificationList!!.setNoMore(true)
                        } else {
                            binding.recyclerviewNotificationList!!.setNoMore(false)
                        }

                        binding.recyclerviewNotificationList!!.refreshComplete()

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
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: NotificationUpdateModel) {
        getAPICalling(event.id, event.status)
    }

    private fun getAPICalling(id: String?, status: Int?) {

        val datum = NotificationUpdateRequest()
        datum.id = id
        datum.type = status

        val gson = Gson()
        var json: String? = ""
        json = gson.toJson(datum)
        Log.e("Model---->", json.toString())

        val aJsonParser = JsonParser()
        val aJsonObject = aJsonParser.parse(json) as JsonObject

        viewModel.getNotificationUpdated(token!!, aJsonObject)

        viewModel.Response1.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (progressDialog != null && progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }

                    if (it.value.getStatusCode() == 200) {
                        if (it.value.getValue()!!.data!!) {
                            finish();
                            startActivity(getIntent());
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
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}