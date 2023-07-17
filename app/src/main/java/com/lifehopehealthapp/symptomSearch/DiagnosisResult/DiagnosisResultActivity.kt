package com.lifehopehealthapp.symptomSearch.DiagnosisResult

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.OvershootInterpolator
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
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.bookingList.TestBookingActivity
import com.lifehopehealthapp.databinding.ActivityDiagnosisResultBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.widgets.TransitionHelper
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView


class DiagnosisResultActivity : BaseActivity<DiagnosisResultViewModel, DiagnosisResultModel>() {

    private var mPrefs: SharedPreferences? = null
    private var token: String? = ""
    private var progressDoalog: Dialog? = null
    private var adapter: DiagnosisResultAdapter? = null
    private var mRequest: String? = ""
    private var mSaveRequest: String? = ""
    private var shareInfo: String? = ""
    private var delayTime: Long =0
    private var mQuestionData: SymptomSearchQusAnsModel? = null

    private lateinit var binding: ActivityDiagnosisResultBinding
    override fun getViewModel() = DiagnosisResultViewModel::class.java

    override fun getActivityRepository() =
        DiagnosisResultModel(
            remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDiagnosisResultBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken

        mRequest = intent.getStringExtra("json")
        /*  var qustion = intent.getStringExtra("Questions")
          mQuestionData = Gson().fromJson(qustion, SymptomSearchQusAnsModel::class.java)*/
        binding.recyclerViewRecommendedTest.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        getAPICall()
        binding.imageviewClose.setOnClickListener {
            finish()
        }

        binding.buttonShare.setOnClickListener {

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareInfo)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
            //saveResult()
        }
        binding.buttonBookTest.setOnClickListener {
            intent = Intent(this, TestBookingActivity::class.java)
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
            finish()
        }


//        binding.textviewDiagnosisLabel.setOnClickListener(View.OnClickListener {

//        })


    }
    private fun startAnimation(){

/*

        val drawable = ContextCompat.getDrawable(
            applicationContext, R.drawable.ic_heart
        )
        val drawableShape = DrawableShape(drawable!!, true)


        binding.viewKonfetti.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(drawableShape)
            .addSizes(nl.dionsegijn.konfetti.models.Size(12, 5f))
            .setPosition(-50f, binding.viewKonfetti.width + 50f, -50f, -50f)
            .streamFor(200, 5000L)
*/


    }

    private fun saveResult() {
        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            Log.e("request", mSaveRequest.toString())
            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(mSaveRequest) as JsonObject
            viewModel.saveDiagnosisResult(token!!, aJsonObject)

            viewModel.SaveResponse.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }

                        if (it.value.getStatus() == 1) {
                            Utils.showToast(
                                this,
                                resources.getString(R.string.toast_result_save),
                                true
                            )
                            //binding.buttonSave.setEnabled(false)
                        } else {
                            Utils.showToast(
                                this,
                                it.value.getMessage().toString(),
                                true
                            )
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

    private fun getAPICall() = if (Utils.isNetworkAvailable(this)) {
        if (progressDoalog == null) {
            progressDoalog = Utils.getDialog(this)
        }
        progressDoalog!!.show()
        Log.e("request", mRequest.toString())
        val aJsonParser = JsonParser()
        val aJsonObject = aJsonParser.parse(mRequest) as JsonObject
        viewModel.getDiagnosisResult(token!!, aJsonObject)
        viewModel.Response.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (progressDoalog != null && progressDoalog!!.isShowing) {
                        progressDoalog!!.dismiss()
                    }
                    if (it.value.getStatusCode() == 200) {
                        val dataList: DataList = it.value.getValue()!!.data!!
                        val tsLong = System.currentTimeMillis() / 1000
                        val timeStamp = tsLong.toString()
                        dataList.setTestDate(timeStamp.toInt())
                        val gson = Gson()
                        mSaveRequest = gson.toJson(dataList)
                        Log.e("DataList", mSaveRequest.toString())

                        binding.textviewResultMessage.text=it.value.getValue()!!.data!!.getQuote()

                        for (k in it.value.getValue()!!.data!!.getPrimaryDiagnoses()!!.indices) {

                            binding.textviewTestDescription.text =
                                it.value.getValue()!!.data!!.getPrimaryDiagnoses()!![k]!!.result1
                            if (it.value.getValue()!!.data!!
                                    .getPrimaryDiagnoses()!![k]!!.result2.equals("")
                            ) {
                                binding.textviewTestRecommended.isVisible = false
                            } else {
                                binding.textviewTestRecommended.text =
                                    it.value.getValue()!!.data!!.getPrimaryDiagnoses()!![k]!!.result2
                            }

                        }

                        binding.customProgress.progress = it.value.getValue()!!.data!!.getDiagnosesMeasure()!!.toFloat()


                        if(binding.customProgress.isProgressAnimating){

                            delayTime = when {
                                it.value.getValue()!!.data!!.getDiagnosesMeasure()==0 -> {
                                    1000
                                }
                                it.value.getValue()!!.data!!.getDiagnosesMeasure()==100 -> {
                                    4000
                                }
                                else -> {
                                    2000
                                }
                            }


                            Handler(Looper.getMainLooper()).postDelayed({

                                binding.textviewPercentage.setCharacterLists(TickerUtils.provideNumberList())
                                binding.textviewPercentage.animationDuration = 1500
                                binding.textviewPercentage.animationInterpolator = OvershootInterpolator()
                                binding.textviewPercentage.gravity = Gravity.START
                                binding.textviewPercentage.setPreferredScrollingDirection(TickerView.ScrollingDirection.ANY)

                                binding.textviewPercentage.text =
                                    it.value.getValue()!!.data!!.getDiagnosesMeasure()!!.toString() + " %"
                            }, delayTime)

                        }

                        binding.imageViewSuccess.isVisible = true
                        binding.textviewResultMessage.isVisible = true

                        if (it.value.getValue()!!.data!!.recommendedTests!!.size == 0) {
                            binding.cardTestRecommended.isVisible = false
                        } else {
                            adapter =
                                DiagnosisResultAdapter(
                                    (it.value.getValue()!!.data!!.recommendedTests as List<RecommendedTests>?)!!
                                )
                            binding.recyclerViewRecommendedTest.adapter = adapter
                        }
                        shareInfo = it.value.getValue()!!.data!!.getShareInfo()
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

    override fun onDestroy() {
        super.onDestroy()
        TransitionHelper.removeActivityFromTransitionManager(this)
    }
}