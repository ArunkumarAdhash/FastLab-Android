package com.lifehopehealthapp.mobileOtpVerify

import `in`.aabhasjindal.otptextview.OTPListener
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.LogInDataModel
import com.lifehopehealthapp.ResponseModel.OTPModel
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.dashBoard.DashBoardActivity
import com.lifehopehealthapp.databinding.ActivityOtpVerifyScreenBinding

import com.lifehopehealthapp.myProfile.MyProfileActivity
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.splash.SplashActivity
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.accessToken
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.checkProfileStatus
import com.lifehopehealthapp.utils.PreferenceHelper.checkuserstatus
import com.lifehopehealthapp.utils.PreferenceHelper.dob
import com.lifehopehealthapp.utils.PreferenceHelper.email
import com.lifehopehealthapp.utils.PreferenceHelper.firstname
import com.lifehopehealthapp.utils.PreferenceHelper.isVerify
import com.lifehopehealthapp.utils.PreferenceHelper.lastname
import com.lifehopehealthapp.utils.PreferenceHelper.localOTPcheck
import com.lifehopehealthapp.utils.PreferenceHelper.profile
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.PreferenceHelper.s3BuckeoldprofileImg
import com.lifehopehealthapp.utils.PreferenceHelper.saveFCMToken
import com.lifehopehealthapp.utils.PreferenceHelper.saveMobileNo
import com.lifehopehealthapp.utils.PreferenceHelper.saveSettingsAPI
import com.lifehopehealthapp.utils.PreferenceHelper.userAge
import com.lifehopehealthapp.utils.PreferenceHelper.userGender
import com.lifehopehealthapp.utils.PreferenceHelper.userID
import com.lifehopehealthapp.utils.Utils
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class MobileOTPVerifyActivity :
    BaseActivity<MobileOTPVerifyViewModel, MobileOTPVerifyModel>() {
    private lateinit var binding: ActivityOtpVerifyScreenBinding

    private var storedVerificationId: String? = ""
    private var verificationCode: String = ""
    private var countryCode: String = ""
    private var mobileNumber: String = ""
    private var deviceToken: String? = ""
    private var deviceVersion: String? = ""
    private var manifacture: String? = ""
    private var name: String? = ""
    private var email: String? = ""
    private var profilePicture: String? = ""
    private var mobile: String? = ""
    private var fcmToken: String? = ""
    private var firstName: String? = ""
    private var lastName: String? = ""
    private var ProfileImage: String? = ""
    private var apiResponse: String? = ""

    private var auth: FirebaseAuth? = null
    private var progressDoalog: Dialog? = null
    private var mPrefs: SharedPreferences? = null

    val timer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding.textViewTimeCounter.setText("0" + millisecondsToTime(millisUntilFinished))
        }

        override fun onFinish() {
            binding.textViewTimeCounter.text = ""
            binding.textViewResendCode.isVisible = true
        }
    }


    override fun getViewModel() = MobileOTPVerifyViewModel::class.java

    override fun getActivityRepository() =
        MobileOTPVerifyModel(
            remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityOtpVerifyScreenBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        timer.start()
        mPrefs = PreferenceHelper.defaultPreference(this)
        val intent: Intent = intent
        mobile = intent.getStringExtra("mobilenumber").toString()
        apiResponse = mPrefs!!.saveSettingsAPI
        firstName = mPrefs!!.firstname.toString()
        lastName = mPrefs!!.lastname.toString()
        ProfileImage = mPrefs!!.profile.toString()
        email = mPrefs!!.email
        fcmToken = mPrefs!!.saveFCMToken
        Log.e("Social", firstName + "" + lastName + "" + profilePicture + "" + email)

        /*val aGson = Gson()
        val aSettingConfig: SettingsResponse =
            aGson.fromJson(apiResponse!!, SettingsResponse::class.java)
*/
        val gson = Gson()
        val Response = gson.toJson(apiResponse)
        Log.e("countryCode", apiResponse.toString())
        val obj = JSONObject(apiResponse.toString())
        val code: String = obj.get("countryCode").toString()
        countryCode = code
        binding.textViewHeading.setText(
            resources.getString(R.string.text_detect_mobile_number) + countryCode + " " + Utils.USFormatMobile(
                mobile!!.toLong()
            )
        )
        var str = mobile
        mobile = str!!.replace("[^a-zA-Z0-9-]".toRegex(), "")
        Log.e("str", str)
        mobileNumber = "$countryCode ${mobile!!.trim()}"

        Log.e("mobileNumber", mobileNumber)
        val wordtoSpan: Spannable = SpannableString(resources.getString(R.string.text_resend_code))
        wordtoSpan.setSpan(
            ForegroundColorSpan(getColor(R.color.timer_color)),
            24,
            36,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.textViewResendCode.setText(wordtoSpan)
        FirebaseOTP()

        binding.buttonVerify.setOnClickListener {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            var otpvalue: String = binding.otpView.otp.toString()
            if (otpvalue != null && otpvalue.length == 6) {
                verify(otpvalue)
            } else {

                if (progressDoalog != null && progressDoalog!!.isShowing) {
                    progressDoalog!!.dismiss()
                }


                if(otpvalue.isEmpty()) {
                    Utils.showToast(this, getString(R.string.enter_the_otp), true)
                }
                else
                {
                    Utils.showToast(this, resources.getString(R.string.toast_otp_empty), true)
                }


            }
        }


        binding.imageviewBackArrow.setOnClickListener {
            finish()
        }


        binding.textViewResendCode.setOnClickListener {
            FirebaseOTP()
            binding.textViewTimeCounter.isVisible = true
            binding.textViewResendCode.isVisible = false
            timer.start()
        }


        binding.otpView.setOtpListener(object : OTPListener {
            override fun onInteractionListener() {
            }

            override fun onOTPComplete(otp: String) {
                if (otp.length == 6) {
                    if (progressDoalog == null) {
                        progressDoalog = Utils.getDialog(this@MobileOTPVerifyActivity)
                    }
                    progressDoalog!!.show()
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(
                        binding.otpView,
                        InputMethodManager.SHOW_IMPLICIT
                    )
                    Log.e("otp", otp)
                    verify(otp)
                }
            }
        })
    }

    private fun FirebaseOTP() {

        val options = PhoneAuthOptions.newBuilder(auth!!)
            .setPhoneNumber(mobileNumber)
            .setTimeout(30L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    verificationCode = verificationId
                }

                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    Log.d("", "onVerificationCompleted:$phoneAuthCredential")
                    storedVerificationId = phoneAuthCredential.smsCode
                    //val credential = PhoneAuthProvider.getCredential(storedVerificationId.toString(), resendToken.toString())
                    signInWithPhoneAuthCredential(phoneAuthCredential)
                    if (storedVerificationId != null) {
                        Log.e("otp1", storedVerificationId.toString())
                        binding.otpView.setOTP(storedVerificationId)
                    } else {
                        Log.e("otp2", storedVerificationId.toString())
                    }

                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // ...
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verify(otpvalue: String) {
        try {
            val credential = PhoneAuthProvider.getCredential(verificationCode, otpvalue)
            signInWithPhoneAuthCredential(credential)
        } catch (e: Exception) {
            if (progressDoalog != null && progressDoalog!!.isShowing) {
                progressDoalog!!.dismiss()
            }
            Utils.showToast(this, "Enter valid mobile number", true)
            Log.i("exception", e.toString())
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithCredential:success")
                    Utils.showToast(this, getString(R.string.otp_verify_success), true)
                    var local = mPrefs!!.localOTPcheck
                    if (local.equals("local")) {
                        mPrefs!!.isVerify = 1
                        val data = OTPModel()
                        data.checkOTP = 1
                        EventBus.getDefault().post(data)
                        finish()
                    } else {
                        if (Utils.isNetworkAvailable(this)) {
                            loginAPI()
                        } else {
                            Utils.noInternetAlert(
                                resources.getString(R.string.no_internet_msg),
                                this
                            )
                        }

                    }

                    val user = task.result?.user
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        Utils.showToast(this, resources.getString(R.string.toast_otp_empty), true)
                    }
                }
            }
    }

    private fun loginAPI() {

        if (Utils.isNetworkAvailable(this)) {
            deviceToken = Utils.getDeviceToken(this)
            deviceVersion = Utils.getDeviceOS()
            manifacture = Utils.getManufacture()


            val request = LogInDataModel(
                firstName.toString(),
                lastName.toString(),
                email.toString(),
                "",
                mobile.toString(),
                deviceToken!!,
                "",
                deviceVersion.toString(),
                0,
                manifacture.toString(),
                1, fcmToken!!
            )
            val gson = Gson()
            val json = gson.toJson(request)
            Log.e("Model---->", json.toString())
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
                        binding.textViewTimeCounter.text = ""
                        binding.buttonVerify.isEnabled = false
                        val gson = Gson()
                        val json = gson.toJson(it.value.getValue()!!.data)
                        Log.e("loginResponse", it.value.getValue()!!.data.toString())
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
                            mPrefs!!.checkuserstatus =
                                it.value.getValue()!!.data!!.isRegistered.toString()
                            mPrefs!!.checkProfileStatus =
                                it.value.getValue()!!.data!!.getIsProfileUpdate().toString()
                            mPrefs!!.isVerify = 1
                            mPrefs!!.profile = it.value.getValue()!!.data!!.getProfilePicture()!!
                            mPrefs!!.userGender = it.value.getValue()!!.data!!.gender
                            mPrefs!!.dob = it.value.getValue()!!.data!!.dob!!.toInt()
                            mPrefs!!.saveMobileNo = mobile

                            //newly added
                            mPrefs!!.s3BuckeoldprofileImg =
                                it.value.getValue()!!.data!!.getProfilePicture().toString()

                            val a: String = Utils.getDateCurrentTimeZone(
                                it.value.getValue()!!.data!!.dob!!.toLong(),
                                Constants.TIMESTAMP_DOB.toString()
                            )!!
                            val parts: List<String> = a!!.split("/")
                            val part1 = parts[0]
                            val part2 = parts[1]
                            val part3 = parts[2]
                            val AGE: Int =
                                Utils.getAge(part3.toInt(), part1.toInt(), part2.toInt())!!.toInt()
                            mPrefs!!.userAge = AGE

                            Handler(Looper.getMainLooper()).postDelayed({

                                val dashboardActivity =Intent(this, DashBoardActivity::class.java)
                                dashboardActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                dashboardActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(dashboardActivity)
                                finish()

                            }, 1000)

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
                            mPrefs!!.isVerify = 1
                            mPrefs!!.profile = it.value.getValue()!!.data!!.getProfilePicture()!!
                            mPrefs!!.saveMobileNo = mobile

                            val myProfileActivity =Intent(this, MyProfileActivity::class.java)
                            myProfileActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            myProfileActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(myProfileActivity)
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


    override fun onResume() {
        super.onResume()
        binding.textViewTimeCounter.isVisible = true
    }

    override fun onPause() {
        super.onPause()
        binding.textViewTimeCounter.isVisible = true
    }

    private fun millisecondsToTime(milliseconds: Long): String? {
        return String.format(
            "%d : %d ",
            TimeUnit.MILLISECONDS.toMinutes(milliseconds),
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            milliseconds
                        )
                    )
        )
    }
}