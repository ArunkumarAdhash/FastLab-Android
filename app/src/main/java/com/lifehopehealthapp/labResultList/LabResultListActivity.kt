package com.lifehopehealthapp.labResultList

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.LabResultListResponse
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityLabResultListBinding
import com.lifehopehealthapp.labResult.LabResultActivity
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.RecyclerItemClickListenr
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.widgets.TransitionHelper

class LabResultListActivity : BaseActivity<LabResultListViewModel, LabResultListModel>() {

    private var token: String? = ""

    private var progressDoalog: Dialog? = null
    private var data: List<LabResultListResponse.Datum>? = null
    private var adapter: LabResultListAdapter? = null
    private var mPrefs: SharedPreferences? = null

    private lateinit var binding: ActivityLabResultListBinding

    override fun getViewModel() = LabResultListViewModel::class.java

    override fun getActivityRepository() =
        LabResultListModel(
            remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLabResultListBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        binding.recyclerviewLabResult.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        getAPICall()
        binding.imageviewBackArrow.setOnClickListener {
            finish()
        }
        getItemClick()
    }

    private fun getItemClick() {
        binding.recyclerviewLabResult.addOnItemTouchListener(
            RecyclerItemClickListenr(
                this,
                binding.recyclerviewLabResult,
                object : RecyclerItemClickListenr.OnItemClickListener {

                    override fun onItemClick(view: View, position: Int) {
                        var testId = data!!.get(position).getCategoryId()
                        intent = Intent(this@LabResultListActivity, LabResultActivity::class.java)
                        intent.putExtra("testId", testId)
                        intent.putExtra("categoryName", data!!.get(position).categoryName)
                        if (Utils.isLollipopHigher() && view != null) {
                            val pairs: Array<Pair<View, String>> =
                                TransitionHelper.createSafeTransitionParticipants(
                                    this@LabResultListActivity,
                                    false,
                                    Pair(view, getString(R.string.trans_tool_bar_title))
                                )
                            val transitionActivityOptions =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    this@LabResultListActivity,
                                    *pairs
                                )
                            startActivity(intent, transitionActivityOptions.toBundle())
                        } else {
                            startActivity(intent)
                        }
                    }
                })
        )
    }

    private fun getAPICall() {
        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            viewModel.getLabResultList(token!!)

            viewModel.Response.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }

                        if (it.value.getStatusCode() == 200) {
                            data = (it.value.getValue()!!.data as List<LabResultListResponse.Datum>?)!!

                            if (data!!.size == 0) {
                                binding.recyclerviewLabResult.isVisible = false
                                binding.textviewNoData.isVisible = true
                                binding.textviewNoData.text =
                                    resources.getString(R.string.text_no_test_data)
                            } else {
                                adapter = LabResultListAdapter(it.value.getValue()!!.data!!, this)
                                binding.recyclerviewLabResult.adapter = adapter
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
                            Utils.showToast(this, it.value.getValue()!!.message.toString(), true)
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