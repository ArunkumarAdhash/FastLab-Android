package com.lifehopehealthapp.symptomSearch.DiagnosisList

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.Datums
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityDiagnosisListBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.symptomSearch.DiagnosisHistoryList.DiagnosisHistoryListActivity
import com.lifehopehealthapp.symptomSearch.UserDetails.UserDetailsActivity
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.RecyclerItemClickListenr
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.widgets.TransitionHelper
import java.util.*

class DiagnosisListActivity : BaseActivity<DiagnosisListViewModel, DiagnosisListModel>() {

    private lateinit var binding: ActivityDiagnosisListBinding
    public var mData: ArrayList<Datums>? = ArrayList<Datums>()
    private var token: String? = ""
    private var progressDoalog: Dialog? = null
    private var adapter: DiagnosisListAdapter? = null
    var mPrefs: SharedPreferences? = null

    override fun getViewModel() = DiagnosisListViewModel::class.java

    override fun getActivityRepository() =
        DiagnosisListModel(
            remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDiagnosisListBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        val labelName = intent.getStringExtra("TitleName")
        binding.textViewLabel.text = labelName
        token = mPrefs!!.authToken
        binding.recyclerviewDiagnosis.layoutManager = GridLayoutManager(this, 3)
        getAPICall()
        binding.imageviewBackArrow.setOnClickListener {
            finish()
        }
        getItemClick()
        binding.imageviewSymptom.setOnClickListener {
            intent = Intent(
                this,
                DiagnosisHistoryListActivity::class.java
            )
            if (Utils.isLollipopHigher() && it != null) {
                val pairs: Array<Pair<View, String>> =
                    TransitionHelper.createSafeTransitionParticipants(
                        this,
                        false,
                        Pair(it, getString(R.string.trans_tool_bar_title))
                    )
                val transitionActivityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this,
                        *pairs
                    )
                startActivity(intent, transitionActivityOptions.toBundle())
            } else {
                startActivity(intent)
            }
        }
    }

    private fun getItemClick() {
        binding.recyclerviewDiagnosis.addOnItemTouchListener(
            RecyclerItemClickListenr(
                this,
                binding.recyclerviewDiagnosis,
                object : RecyclerItemClickListenr.OnItemClickListener {

                    override fun onItemClick(view: View, position: Int) {
                        if (position == 0) {
                            Log.e("mData", mData!![position].id.toString())
                            intent = Intent(
                                this@DiagnosisListActivity,
                                UserDetailsActivity::class.java
                            )
                            intent.putExtra("symptomID", "1")
                            intent.putExtra("symptomName", mData!![position].name.toString())
                            if (Utils.isLollipopHigher() && view != null) {
                                val pairs: Array<Pair<View, String>> =
                                    TransitionHelper.createSafeTransitionParticipants(
                                        this@DiagnosisListActivity,
                                        false,
                                        Pair(view, getString(R.string.trans_tool_bar_title))
                                    )
                                val transitionActivityOptions =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@DiagnosisListActivity,
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

    private fun getAPICall() {
        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            viewModel.getDiagnosisList(token!!)

            viewModel.Response.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            mData = it.value.getValue()!!.data as ArrayList<Datums>?
                            adapter =
                                DiagnosisListAdapter(it.value.getValue()!!.data!!, this)
                            binding.recyclerviewDiagnosis.adapter = adapter
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