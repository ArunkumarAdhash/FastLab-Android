package com.lifehopehealthapp.editProfile

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.DownloadImageViewModel
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.dashBoard.DashBoardActivity
import com.lifehopehealthapp.databinding.ActivityEditProfileBinding
import com.lifehopehealthapp.myProfile.MyProfileActivity
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.splash.SplashActivity
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.checkProfileStatus
import com.lifehopehealthapp.utils.PreferenceHelper.isVerify
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketName
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketRegion
import com.lifehopehealthapp.utils.PreferenceHelper.saveSettingsAPI
import com.lifehopehealthapp.utils.PreferenceHelper.userAge
import com.lifehopehealthapp.utils.PreferenceHelper.userGender
import com.lifehopehealthapp.utils.PreferenceHelper.userID
import com.lifehopehealthapp.utils.PreferenceHelper.w3AccessKey
import com.lifehopehealthapp.utils.PreferenceHelper.w3SecretKey
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.widgets.TransitionHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.io.File


class EditProfileActivity : BaseActivity<EditProfileViewModel, EditProfileModel>() {
    private lateinit var binding: ActivityEditProfileBinding

    private var token: String? = ""
    private var apiResponse: String? = ""
    private var countryCode: String? = ""
    private var isShow: Boolean = true
    private var scrollRange: Int = -1
    private var userStatus = ""
    private var progressDialog: Dialog? = null
    private var menuItem: MenuItem? = null
    private var mPrefs: SharedPreferences? = null

    override fun getViewModel() = EditProfileViewModel::class.java

    override fun getActivityRepository() =
        EditProfileModel(
            remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
        )

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        apiResponse = mPrefs!!.saveSettingsAPI

        setSupportActionBar(binding.toolbar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)

        /*Glide.with(this).load(mPrefs!!.profileOldImg)
            //.diskCacheStrategy(DiskCacheStrategy.NONE)
            //.skipMemoryCache(true)
            .placeholder(R.drawable.ic_profile_no_image)
            .error(R.drawable.ic_profile_no_image)
            .into(binding.profileImage)*/

        binding.appbarLay.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->

            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset == 0) {
                binding.collapseLay.title = resources.getString(R.string.text_profile_information)
                isShow = true
            } else if (isShow) {
                binding.collapseLay.title =
                    " "
                isShow = false
            }
        })
        /*val aGson = Gson()
        val aSettingConfig: SettingsResponse =
            aGson.fromJson(apiResponse!!, SettingsResponse::class.java)
        countryCode = aSettingConfig.getValue()!!.data!!.countryCode*/
        val obj = JSONObject(apiResponse.toString())
        countryCode = obj.get("countryCode").toString()
        if (Utils.isNetworkAvailable(this)) {
            if (progressDialog == null) {
                progressDialog = Utils.getDialog(this)
            }
            progressDialog!!.show()
            viewModel.getUserDetails(token!!)

        } else {

        }

        viewModel.Response.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    if (progressDialog != null && progressDialog!!.isShowing()) {
                        progressDialog!!.dismiss()
                    }
                    if (it.value.getStatusCode() == 200) {
                        binding.textviewUserName.setText(it.value.getValue()!!.data!!.firstName + " " + it.value.getValue()!!.data!!.lastName)
                        //binding.collapseLay.setTitleEnabled(true)

                        binding.layoutView.textviewMobileInfo.setText(
                            ": " + countryCode + " " + Utils.USFormatMobile(
                                it.value.getValue()!!.data!!.mobileNo!!.toLong()
                            )
                        )
                        binding.layoutView.textviewEmailInfo.setText(it.value.getValue()!!.data!!.email)
                        var date = it.value.getValue()!!.data!!.dob
                        if (date.equals("") || date.equals("null") || date == null) {

                        } else {
                            val a: String = Utils.getDateCurrentTimeZone(
                                it.value.getValue()!!.data!!.dob!!.toLong(),
                                Constants.TIMESTAMP_DOB.toString()
                            )!!
                            Log.e("age->", a)
                            val parts: List<String> = a.split("/")
                            val part1 = parts[0]
                            val part2 = parts[1]
                            val part3 = parts[2]
                            Log.e(
                                "agenew",
                                Utils.getAge(part3.toInt(), part1.toInt(), part2.toInt()).toString()
                            )
                            binding.textviewUserAge.setText(
                                Utils.getAge(
                                    part3.toInt(),
                                    part1.toInt(),
                                    part2.toInt()
                                ).toString() + " Years, " + it.value.getValue()!!.data!!.getGender()
                            )
                            val AGE: Int =
                                Utils.getAge(part3.toInt(), part1.toInt(), part2.toInt())!!.toInt()
                            mPrefs!!.userAge = AGE
                            mPrefs!!.userGender =
                                it.value.getValue()!!.data!!.getGender().toString()
                            /* val calendar = Calendar.getInstance()
                             val thisYear = calendar[Calendar.YEAR]
                             val oldYear: Int = a.toInt()
                             Log.e("age->", a)
                             Log.e("oldYear", oldYear.toString())
                             val USERAGE = thisYear - oldYear
                             val AGE: Int = USERAGE
                             Log.e("AGE", USERAGE.toString())
                             //getAge()
                            */
                        }
                        if (it.value.getValue()!!.data!!.address.equals("")) {
                            binding.layoutView.textviewAddressInfo.isVisible = false
                            binding.layoutView.textviewColanLabel.isVisible = false
                            binding.layoutView.textviewAddressInfoLabel.isVisible = false
                        } else {
                            binding.layoutView.textviewAddressInfo.setText(it.value.getValue()!!.data!!.address)
                        }
                        binding.layoutView.textviewHeightInfo.setText(": " + it.value.getValue()!!.data!!.height)
                        /* if (it.value.getData()!!.height!!.length == 1) {
                             binding.layoutView.textviewHeightInfo.setText(": " + it.value.getData()!!.height + " ' ")
                         } else {
                             val parts: List<String> = it.value.getData()!!.height!!.split(".")
                             if (parts.size == 2) {
                                 val part1 = parts[0]
                                 val part2 = parts[1]
                                 binding.layoutView.textviewHeightInfo.setText(": " + part1 + " ' " + part2 + " ''")
                             }
                         }*/

                        binding.layoutView.textviewWeightInfo.setText(": " + it.value.getValue()!!.data!!.weight + " lbs")
                        if (it.value.getValue()!!.data!!.bloodGroup.equals("Unknown Blood Type") || it.value.getValue()!!.data!!.bloodGroup.equals(
                                "Select Blood Type"
                            )
                        ) {
                            binding.layoutView.textviewBloodInfo.isVisible = false
                            binding.layoutView.textviewBloodInfoLabel.isVisible = false
                            binding.layoutView.textviewBloodInfo.setText(": " + it.value.getValue()!!.data!!.bloodGroup)
                        } else {
                            binding.layoutView.textviewBloodInfo.setText(": " + it.value.getValue()!!.data!!.bloodGroup + " ve")
                        }

                        mPrefs!!.checkProfileStatus =
                            it.value.getValue()!!.data!!.getIsProfileUpdate().toString()
                        mPrefs!!.isVerify = it.value.getValue()!!.data!!.isVerified!!
                        var mImage: String = it.value.getValue()!!.data!!.imageID.toString()
                        Log.e("imageID", it.value.getValue()!!.data!!.imageID!!)
                        if (mImage.equals("") || mImage.equals("null") || mImage == null) {

                        } else {

                            Log.e("s3BucketName", mPrefs!!.s3BucketName.toString())
                            val file =
                                File("dev-lifepri/ProfilePic/" + mPrefs!!.userID + "/" + it.value.getValue()!!.data!!.imageID!!)
                            /*Download(
                                file,
                                mPrefs!!.s3BucketName.toString(),
                                  mPrefs!!.userID + "/" + it.value.getValue()!!.data!!.imageID!!
                            )*/
                            /*Utils.downloadAWSimage(
                                it.value.getValue()!!.data!!.imageID!!,
                                mPrefs!!.s3BucketName,
                                mPrefs!!.userID,
                                this,
                                Utils.decryption(mPrefs!!.w3SecretKey),
                                Utils.decryption(mPrefs!!.w3AccessKey)
                            )*/
                            Utils.Presigned(
                                it.value.getValue()!!.data!!.imageID!!,
                                mPrefs!!.s3BucketName,
                                mPrefs!!.userID,
                                this,
                                Utils.decryption(mPrefs!!.w3SecretKey),
                                Utils.decryption(mPrefs!!.w3AccessKey),
                                mPrefs!!.s3BucketRegion,
                                ""
                            )

                        }

                        if (it.value.getValue()!!.data!!.email.equals("")) {
                            binding.layoutView.layoutEmail.isVisible = false
                            binding.layoutView.textviewEmailInfo.isVisible = false
                            binding.layoutView.textviewEmailInfoLabel.isVisible = false
                        }


                        if (it.value.getValue()!!.data!!.isVerified == 1) {
                            Log.e("edit1", it.value.getValue()!!.data!!.isVerified.toString())
                            binding.layoutView.imageviewMobileOtpVerify.setImageDrawable(
                                resources.getDrawable(
                                    R.drawable.ic_validated
                                )
                            )
                        } else {
                            Log.e("edit2", it.value.getValue()!!.data!!.isVerified.toString())
                            binding.layoutView.imageviewMobileOtpVerify.setImageDrawable(
                                resources.getDrawable(
                                    R.drawable.ic_invalidated
                                )
                            )
                        }
                    } else if (it.value.getStatusCode() == 401) {

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
                        Utils.showToast(this, it.value.getValue()!!.message.toString(), true)
                    }
                }
                is Resource.GenericError -> {
                    Utils.showToast(this, it.error!!.message.toString(), true)
                    if (it.code == 400) {
                        Utils.clearSharedPreferences(this)
                        val intent = Intent(this, SplashActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val extras = Bundle()
                        extras.putString("LogOut", "LogOut")
                        intent.putExtras(extras)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        Utils.showToast(this, "User doesn't exist", true)
                    } else if (it.code == 401) {
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

        binding.imageviewBackArrow.setOnClickListener {
            /*userStatus = mPrefs!!.checkuserstatus.toString()
            if (userStatus.equals("true")) {

            }*/
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        menuItem = menu?.findItem(R.id.viewProfile)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                //onBackPressed()
                startActivity(Intent(this@EditProfileActivity, DashBoardActivity::class.java))
                finish()
                return true
            }
            R.id.viewProfile -> {
                intent = Intent(this, MyProfileActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
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
            .into(binding.profileImage)

    }
}