package com.lifehopehealthapp.bulkBooking.organizationType

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.OrganizationListResponse
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.bulkBookingNew.NewBulkBookingActivity
import com.lifehopehealthapp.databinding.ActivityOrganizationtypeBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.RecyclerItemClickListenr
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.widgets.TransitionHelper

class OrganizationTypeActivity : BaseActivity<OrganizationTypeViewModel, OrganizationTypeModel>() {

    private lateinit var binding: ActivityOrganizationtypeBinding
    private var token: String? = ""
    private var progressDoalog: Dialog? = null
    private var adapter: OrganizationTypeAdapter? = null
    private var mPrefs: SharedPreferences? = null
    private var organizationTypeID: ArrayList<Int> = ArrayList()
    private var mTestType: ArrayList<String> = ArrayList()
    private var organizationType: ArrayList<String> = ArrayList()
    private var mOrganization: ArrayList<OrganizationListResponse.OrganizationType>? = null
    private var clickEvent: Int? = 0

    private var mLastClickTime: Long = 0

    override fun getViewModel() = OrganizationTypeViewModel::class.java

    override fun getActivityRepository() = OrganizationTypeModel(
        remoteDataSource.buildApi(APIManager::class.java),
        PreferenceHelper
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrganizationtypeBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        clickEvent = 1
        binding.recyclerviewOrganizationType.layoutManager =
            GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)

        binding.imageviewBackArrow.setOnClickListener {
            finish()
        }

        binding.recyclerviewOrganizationType.addOnItemTouchListener(
            RecyclerItemClickListenr(
                this,
                binding.recyclerviewOrganizationType,
                object : RecyclerItemClickListenr.OnItemClickListener {


                    override fun onItemClick(view: View, position: Int) {


                        //newly added
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                            return
                        }
                        mLastClickTime = SystemClock.elapsedRealtime()


                        if (mOrganization!![position].isAvailable == 0) {
                            intent =
                                Intent(
                                    this@OrganizationTypeActivity,
                                    NewBulkBookingActivity::class.java
                                )
                            Log.e("organizationTypeID", organizationTypeID[position].toString())

                            intent.putExtra(
                                "SelectedID",
                                organizationTypeID[position].toString()
                            )
                            intent.putExtra("SelectItemName", organizationType[position])

                            intent.putExtra(
                                "isPayment",
                                mOrganization!![position].isPayment
                            )


                            if (Utils.isLollipopHigher() && view != null) {
                                val pairs: Array<Pair<View, String>> =
                                    TransitionHelper.createSafeTransitionParticipants(
                                        this@OrganizationTypeActivity,
                                        false,
                                        Pair(view, getString(R.string.trans_tool_bar_title))
                                    )
                                val transitionActivityOptions =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@OrganizationTypeActivity,
                                        *pairs
                                    )
                                startActivity(intent, transitionActivityOptions.toBundle())
                            } else {
                                startActivity(intent)
                            }
                        }
                    }
                })
        )

    }

    override fun onRestart() {
        super.onRestart()
        clickEvent = 1
    }

    override fun onPause() {
        super.onPause()
        clickEvent = 0
    }

    override fun onResume() {
        super.onResume()
        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()

            viewModel.getTestList(token!!)

            viewModel.Response.observe(this, androidx.lifecycle.Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }

                        if (it.value.getStatusCode() == 200) {
                            val gson = Gson()
                            val json = gson.toJson(it.value.getValue()!!.data.toString())
                            Log.e("OMG---->", json.toString())
                            mOrganization =
                                (it.value.getValue()!!.data!!.organizationType as ArrayList<OrganizationListResponse.OrganizationType>?)!!
                            adapter =
                                OrganizationTypeAdapter(
                                    it.value.getValue()!!.data!!.organizationType!!,
                                    this
                                )
                            binding.recyclerviewOrganizationType.adapter = adapter

                            for (i in it.value.getValue()!!.data!!.organizationType!!.indices) {
                                val type: String? =
                                    it.value.getValue()!!.data!!.organizationType!![i].getCategoryName()
                                val id: Int? =
                                    it.value.getValue()!!.data!!.organizationType!![i].id
                                organizationTypeID.add(id!!)
                                organizationType.add(type!!)
                            }

                            //newly hided 31-03-2022
                           /* for (i in it.value.getValue()!!.data!!.testType!!.indices) {
                                val type: String? =
                                    it.value.getValue()!!.data!!.testType!![i].categoryName
                                mTestType.add(type!!)
                            }*/
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
}