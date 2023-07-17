package com.lifehopehealthapp.login

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.Observer
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.LogInDataModel
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.ResponseModel.SettingsResponse
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.dashBoard.DashBoardActivity
import com.lifehopehealthapp.databinding.ActivityLoginBinding
import com.lifehopehealthapp.myProfile.MyProfileActivity
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.splash.SplashActivity
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.accessToken
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.checkProfileStatus
import com.lifehopehealthapp.utils.PreferenceHelper.checkuserstatus
import com.lifehopehealthapp.utils.PreferenceHelper.email
import com.lifehopehealthapp.utils.PreferenceHelper.firstname
import com.lifehopehealthapp.utils.PreferenceHelper.isVerify
import com.lifehopehealthapp.utils.PreferenceHelper.lastname
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.PreferenceHelper.s3BuckeoldprofileImg
import com.lifehopehealthapp.utils.PreferenceHelper.saveFCMToken
import com.lifehopehealthapp.utils.PreferenceHelper.saveMobileNo
import com.lifehopehealthapp.utils.PreferenceHelper.saveSettingsAPI
import com.lifehopehealthapp.utils.PreferenceHelper.userID
import com.lifehopehealthapp.utils.Utils
import org.json.JSONObject


class LogInActivity : BaseActivity<LogInViewModel, LogInModel>() {

    private var mGmailSign: GoogleSignInClient? = null
    private var fcmToken: String? = ""
    private var firstName: String? = ""
    private var lastName: String? = ""
    private var mobilenumber: String? = ""
    private var email: String? = ""
    private var deviceToken: String? = ""
    private var deviceVersion: String? = ""
    private var manifacture: String? = ""
    private var apiResponse: String? = ""
    private var countryCode: String? = ""

    private var progressDoalog: Dialog? = null
    private var mPrefs: SharedPreferences? = null

    private lateinit var binding: ActivityLoginBinding

    override fun getViewModel() = LogInViewModel::class.java

    override fun getActivityRepository() =
        LogInModel(remoteDataSource.buildApi(APIManager::class.java), PreferenceHelper)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        email = mPrefs!!.email.toString()
        firstName = mPrefs!!.firstname.toString()
        lastName = mPrefs!!.lastname.toString()
        fcmToken = mPrefs!!.saveFCMToken
        apiResponse = mPrefs!!.saveSettingsAPI

        val aGson = Gson()
        val aSettingConfig: SettingsResponse =
            aGson.fromJson(apiResponse!!, SettingsResponse::class.java)
        //countryCode = aSettingConfig.getValue()!!.data!!.countryCode
        val obj = JSONObject(apiResponse.toString())
        countryCode = obj.get("countryCode").toString()

        binding.textviewCountryCode.text = countryCode

        binding.ediitextMobileNumber.addTextChangedListener(object : TextWatcher {
            var lastChar: String? = null

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val text: String = binding.ediitextMobileNumber.getText().toString()
                val textLength: Int = binding.ediitextMobileNumber.getText()!!.length
                if (text.endsWith("-") || text.endsWith(" ") || text.endsWith(" ")) return
                if (textLength == 1) {
                    if (!text.contains("(")) {
                        binding.ediitextMobileNumber.setText(
                            StringBuilder(text).insert(text.length - 1, "(").toString()
                        )
                        binding.ediitextMobileNumber.setSelection(binding.ediitextMobileNumber.getText()!!.length)
                    }
                } else if (textLength == 5) {
                    if (!text.contains(")")) {
                        binding.ediitextMobileNumber.setText(
                            StringBuilder(text).insert(text.length - 1, ")").toString()
                        )
                        binding.ediitextMobileNumber.setSelection(binding.ediitextMobileNumber.getText()!!.length)
                    }
                } else if (textLength == 6) {
                    binding.ediitextMobileNumber.setText(
                        StringBuilder(text).insert(text.length - 1, " ").toString()
                    )
                    binding.ediitextMobileNumber.setSelection(binding.ediitextMobileNumber.getText()!!.length)
                } else if (textLength == 10) {
                    if (!text.contains("-")) {
                        binding.ediitextMobileNumber.setText(
                            StringBuilder(text).insert(text.length - 1, "-").toString()
                        )
                        binding.ediitextMobileNumber.setSelection(binding.ediitextMobileNumber.getText()!!.length)
                    }
                } else if (textLength == 15) {
                    if (text.contains("-")) {
                        binding.ediitextMobileNumber.setText(
                            StringBuilder(text).insert(text.length - 1, "-").toString()
                        )
                        binding.ediitextMobileNumber.setSelection(binding.ediitextMobileNumber.getText()!!.length)
                    }
                } else if (textLength == 18) {
                    if (text.contains("-")) {
                        binding.ediitextMobileNumber.setText(
                            StringBuilder(text).insert(text.length - 1, "-").toString()
                        )
                        binding.ediitextMobileNumber.setSelection(binding.ediitextMobileNumber.getText()!!.length)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.buttonNext.setOnClickListener {
            mobilenumber = binding.ediitextMobileNumber.text.toString().trim()
            var mMobile = mobilenumber
            mMobile = mMobile!!.replace("[^a-zA-Z0-9]".toRegex(), "")
            if (!mMobile.equals("")) {
                if (Utils.isValidPhoneNumber(mMobile)) {
                    if (Utils.isNetworkAvailable(this)) {
                        if (progressDoalog == null) {
                            progressDoalog = Utils.getDialog(this)
                        }
                        progressDoalog!!.show()
                        loginAPI()
                    } else {
                        Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
                    }
                } else {
                    binding.ediitextMobileNumber.setError("Enter valid mobile number")
                }
            } else {
                binding.ediitextMobileNumber.setError("Enter the mobile number")
            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val aGso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getResources().getString(R.string.web_client_id))
            .requestEmail()
            .build()

        mGmailSign = GoogleSignIn.getClient(this, aGso)

        FacebookSdk.sdkInitialize(this)
        //FB Logout
        LoginManager.getInstance().logOut()
        //Gmail Logout
        if (mGmailSign != null) {
            mGmailSign!!.signOut()
        }

        Utils.clearSharedPreferences(this)
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val extras = Bundle()
        extras.putString("LogOut", "LogOut")
        intent.putExtras(extras)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun loginAPI() {
        var str = mobilenumber
        str = str!!.replace("[^a-zA-Z0-9]".toRegex(), "")
        mPrefs!!.saveMobileNo = str
        if (Utils.isNetworkAvailable(this)) {
            deviceToken = Utils.getDeviceToken(this)
            deviceVersion = Utils.getDeviceOS()
            manifacture = Utils.getManufacture()

            val request = LogInDataModel(
                firstName.toString(),
                lastName.toString(),
                email.toString(),
                "",
                str.toString(),
                deviceToken!!,
                "",
                deviceVersion.toString(),
                0,
                manifacture.toString(), 0, fcmToken.toString()
            )
            viewModel.getLogInDetails(request)
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }

        viewModel.detaResponse.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (progressDoalog != null && progressDoalog!!.isShowing) {
                        progressDoalog!!.dismiss()
                    }

                    if (it.value.getStatusCode() == 200) {
                        if (it.value.getValue()!!.data!!.isRegistered == true && it.value.getValue()!!.data!!
                                .getIsProfileUpdate() == true
                        ) {
                            mPrefs!!.authToken =
                                "Bearer " + it.value.getValue()!!.data!!.accessToken!!
                            mPrefs!!.accessToken = it.value.getValue()!!.data!!.accessToken!!
                            mPrefs!!.refreshToken = it.value.getValue()!!.data!!.refreshToken
                            mPrefs!!.firstname = it.value.getValue()!!.data!!.fname.toString()
                            mPrefs!!.lastname = it.value.getValue()!!.data!!.lname.toString()
                            mPrefs!!.userID = it.value.getValue()!!.data!!.getUserId().toString()

                            //newly added
                            mPrefs!!.s3BuckeoldprofileImg =
                                it.value.getValue()!!.data!!.getProfilePicture().toString()

                            mPrefs!!.checkuserstatus =
                                it.value.getValue()!!.data!!.isRegistered.toString()
                            mPrefs!!.checkProfileStatus =
                                it.value.getValue()!!.data!!.getIsProfileUpdate().toString()
                            mPrefs!!.isVerify = it.value.getValue()!!.data!!.isVerified!!
                            startActivity(Intent(this, DashBoardActivity::class.java))
                            finish()
                        } else {
                            mPrefs!!.authToken =
                                "Bearer " + it.value.getValue()!!.data!!.accessToken!!
                            mPrefs!!.accessToken = it.value.getValue()!!.data!!.accessToken!!
                            mPrefs!!.refreshToken = it.value.getValue()!!.data!!.refreshToken
                            mPrefs!!.firstname = it.value.getValue()!!.data!!.fname.toString()
                            mPrefs!!.lastname = it.value.getValue()!!.data!!.lname.toString()
                            mPrefs!!.userID = it.value.getValue()!!.data!!.getUserId().toString()
                            mPrefs!!.checkuserstatus =
                                it.value.getValue()!!.data!!.isRegistered.toString()
                            mPrefs!!.checkProfileStatus =
                                it.value.getValue()!!.data!!.getIsProfileUpdate().toString()
                            mPrefs!!.isVerify = 0
                            startActivity(Intent(this, MyProfileActivity::class.java))
                            finish()
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
}