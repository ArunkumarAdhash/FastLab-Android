package com.lifehopehealthapp.symptomSearch.DiagnosisHistoryDetail

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.DiagnosisHistoryDetailRequest
import com.lifehopehealthapp.ResponseModel.DownloadImageViewModel
import com.lifehopehealthapp.ResponseModel.Question
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityDiagnosishistorydetailBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketName
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketRegion
import com.lifehopehealthapp.utils.PreferenceHelper.userID
import com.lifehopehealthapp.utils.PreferenceHelper.w3AccessKey
import com.lifehopehealthapp.utils.PreferenceHelper.w3SecretKey
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.widgets.TransitionHelper
import nl.dionsegijn.konfetti.models.Shape
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DiagnosisHistoryDetailActivity :
    BaseActivity<DiagnosisHistoryDetailViewModel, DiagnosisHistoryDetailModel>() {
    private var token: String? = ""
    private var progressDoalog: Dialog? = null
    private var diagnosesId: String? = ""
    private var testName: String? = ""
    private var datetime: String? = ""
    private var adapter: DiagnosisHistoryDetailAdapter? = null
    private var mPrefs: SharedPreferences? = null
    private var ShareInfo: String? = ""
    private lateinit var binding: ActivityDiagnosishistorydetailBinding
    private var adapter1: DiagnosisHistoryAdapter? = null

    override fun getViewModel() = DiagnosisHistoryDetailViewModel::class.java

    override fun getActivityRepository() =
        DiagnosisHistoryDetailModel(
            remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDiagnosishistorydetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        datetime = intent.getStringExtra("testDate")
        diagnosesId = intent.getStringExtra("diagnosesId")
        testName = intent.getStringExtra("testName")
        binding.textviewTitle.text = "Details - " + testName
        binding.recyclerViewRecommendedTest.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recyclerviewDetailview.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        Log.e("testDate->", datetime + "   " + diagnosesId)
        getAPICall()
        binding.imageviewClose.setOnClickListener {
            finish()
        }
        binding.textviewShare.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, ShareInfo)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    private fun getAPICall() {
        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            val data = DiagnosisHistoryDetailRequest()
            data.datetime = datetime!!.toDouble()
            data.resultId = diagnosesId!!

            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(data)
            Log.e("Model---->", json.toString())

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(json) as JsonObject
            Log.e("aJsonObject", aJsonObject.toString())

            viewModel.getDiagnosisDetailHistory(
                token!!, aJsonObject
            )

            viewModel.Response.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }

                        if (it.value.getStatusCode() == 200) {
                            Log.e("location", it.value.getValue()!!.data!!.location.toString())
                            ShareInfo = it.value.getValue()!!.data!!.shareInfo


                            Utils.Presigned(
                                it.value.getValue()!!.data!!.getProfileInfo()!!.profilePicture!!,
                                mPrefs!!.s3BucketName,
                                mPrefs!!.userID,
                                this,
                                Utils.decryption(mPrefs!!.w3SecretKey),
                                Utils.decryption(mPrefs!!.w3AccessKey),
                                mPrefs!!.s3BucketRegion,
                                ""
                            )
                            binding.textViewUserName.text = it.value.getValue()!!.data!!
                                .getProfileInfo()!!.firstName + " " + it.value.getValue()!!.data!!
                                .getProfileInfo()!!.lastName
                            if (it.value.getValue()!!.data!!.location.toString().equals("")) {
                                binding.textViewUserAge.text = it.value.getValue()!!.data!!
                                    .getProfileInfo()!!.dob.toString() + " years, " + it.value.getValue()!!.data!!
                                    .getProfileInfo()!!.gender
                            } else {
                                binding.textViewUserAge.text = it.value.getValue()!!.data!!
                                    .getProfileInfo()!!.dob.toString() + " years, " + it.value.getValue()!!.data!!
                                    .getProfileInfo()!!.gender + ", " + it.value.getValue()!!.data!!.location
                            }

                            binding.imageViewUserPhoto.setColorFilter(
                                ContextCompat.getColor(
                                    this,
                                    android.R.color.transparent
                                )
                            )
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
                            if (it.value.getValue()!!.data!!.recommendedTests!!.size == 0) {
                                binding.cardTestRecommended.isVisible = false
                            } else {
                                adapter1 =
                                    DiagnosisHistoryAdapter(it.value.getValue()!!.data!!.recommendedTests!!)
                                binding.recyclerViewRecommendedTest.adapter = adapter1
                            }
                            adapter =
                                DiagnosisHistoryDetailAdapter(
                                    (it.value.getValue()!!.data!!.question as List<Question>?)!!,
                                    this
                                )
                            binding.recyclerviewDetailview.adapter = adapter

                            if (it.value.getValue()!!.data!!.concern!!.equals("") || it.value.getValue()!!.data!!.concern!!.equals(
                                    "null"
                                )
                            ) {
                                //binding.textViewConcernsLabel.isVisible = false
                                binding.cardViewUserCommants.isVisible = false
                                //binding.textViewConcerns.isVisible = false
                            } else {
                                binding.cardViewUserCommants.isVisible = true
                                binding.textViewConcerns.text =
                                    it.value.getValue()!!.data!!.concern!!
                            }
                            /* binding.customProgress.incrementProgressBy(
                                 it.value.getData()!!.diagnosesMeasure!!
                             )*/


                            binding.textviewPercentage.text = (it.value.getValue()!!.data!!.diagnosesMeasure!!.toInt()).toString() + " %"
                           



                            binding.customProgress.progress =
                                it.value.getValue()!!.data!!.getDiagnosesMeasure()!!.toFloat()

                            /*binding.customProgress.postOnAnimationDelayed(100L) {
                                startAnimation()
                                binding.textviewPercentage.isVisible = true
                            }*/
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

    private fun startAnimation() {


        val drawable = ContextCompat.getDrawable(
            applicationContext, R.drawable.ic_heart
        )
        val drawableShape = Shape.DrawableShape(drawable!!, true)


        /*binding.viewKonfetti.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(drawableShape)
            .addSizes(nl.dionsegijn.konfetti.models.Size(12, 5f))
            .setPosition(-50f, binding.viewKonfetti.width + 50f, -50f, -50f)
            .streamFor(200, 5000L)*/


    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TransitionHelper.removeActivityFromTransitionManager(this)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: DownloadImageViewModel) {

        Glide.with(this).load(event.mUrl)
            //.diskCacheStrategy(DiskCacheStrategy.NONE)
            //.skipMemoryCache(true)
            .placeholder(R.drawable.ic_profile_no_image)
            .error(R.drawable.ic_profile_no_image)
            .into(binding.imageViewUserPhoto)
    }
}