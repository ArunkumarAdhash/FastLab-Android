package com.lifehopehealthapp.doctorDetailPage

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.lifehopehealthapp.Calls.Video.VideoChatViewActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.DoctorDetailRequest
import com.lifehopehealthapp.ResponseModel.DownloadImageViewModel
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityDoctorDetailBinding
import com.lifehopehealthapp.doctorAppointmentDates.SetAppointmentDateActivity
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketName
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketRegion
import com.lifehopehealthapp.utils.PreferenceHelper.w3AccessKey
import com.lifehopehealthapp.utils.PreferenceHelper.w3SecretKey
import com.lifehopehealthapp.utils.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DoctorDetailActivity : BaseActivity<DoctorDetailViewModel, DoctorDetailModel>() {

    private var isShow: Boolean = true
    private var scrollRange: Int = -1
    private var mPrefs: SharedPreferences? = null
    private var token: String? = ""
    private var mDoctorID: String? = ""
    private var progressDoalog: Dialog? = null

    private lateinit var binding: ActivityDoctorDetailBinding


    override fun getViewModel() = DoctorDetailViewModel::class.java

    override fun getActivityRepository() =
        DoctorDetailModel(
            remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDoctorDetailBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)

        binding.appbarLay.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->

            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset == 0) {
                binding.collapseLay.title = resources.getString(R.string.text_doctor_detail)
                isShow = true
            } else if (isShow) {
                binding.collapseLay.title =
                    " "
                isShow = false
            }
        })

        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        mDoctorID = intent.getStringExtra(Constants.DOCTOR_ID)
        binding.buttonBookNow.setOnClickListener {
            val intent = Intent(this, SetAppointmentDateActivity::class.java)
            intent.putExtra(Constants.DOCTOR_ID, mDoctorID)
            startActivity(intent)
        }
        getAPICall()
        binding.layoutDetail.imageViewVideoCall.setOnClickListener {
            val intent = Intent(this, VideoChatViewActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getAPICall() {
        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            val data = DoctorDetailRequest()
            data.setDoctorId(mDoctorID)
            viewModel.getDoctorDetail(token!!, data)

            viewModel.Response.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        Utils.Presigned(
                            it.value.getValue()!!.profilePic!!,
                            mPrefs!!.s3BucketName,
                            it.value.getValue()!!.id,
                            this,
                            Utils.decryption(mPrefs!!.w3SecretKey),
                            Utils.decryption(mPrefs!!.w3AccessKey),
                            mPrefs!!.s3BucketRegion, "doctor"
                        )
                        binding.layoutDetail.textViewDoctorName.setText(it.value.getValue()!!.doctorName)
                        binding.layoutDetail.textViewDoctorDepartment.setText(it.value.getValue()!!.education)
                        binding.layoutDetail.textViewDoctorLocation.setText(it.value.getValue()!!.categoryName!!)
                        if (it.value.getValue()!!.yearOfExperience.toInt() == 1) {
//                            binding.layoutDetail.textViewDoctorExpInYrs.setText(it.value.getValue()!!.yearOfExperience+ " Year")
                            binding.layoutDetail.textViewDoctorExpInYrs.setText(it.value.getValue()!!.yearOfExperience + if (it.value.getValue()!!.experienceMonth.toInt() > 0) " +Yrs" else " Year")
                        } else {
                            binding.layoutDetail.textViewDoctorExpInYrs.setText(it.value.getValue()!!.yearOfExperience + if (it.value.getValue()!!.experienceMonth.toInt() > 0) " +Yrs" else " Yrs")
                        }
                        binding.layoutDetail.textviewPatientCount.setText(it.value.getValue()!!.patientsCount.toString())
                        binding.layoutDetail.textviewAboutMe.setText(it.value.getValue()!!.aboutUs)

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

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.doctor_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                //onBackPressed()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: DownloadImageViewModel) {
        Glide.with(this).load(event.mUrl)
            .placeholder(R.drawable.ic_profile_no_image)
            .error(R.drawable.ic_profile_no_image)
            .into(binding.imageViewDoctorImage)
    }
}