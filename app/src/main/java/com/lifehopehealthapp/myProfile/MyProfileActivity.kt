package com.lifehopehealthapp.myProfile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.dashBoard.DashBoardActivity
import com.lifehopehealthapp.databinding.ActivityMyProfileBinding
import com.lifehopehealthapp.editProfile.EditProfileActivity
import com.lifehopehealthapp.mobileOtpVerify.MobileOTPVerifyActivity
import com.lifehopehealthapp.orderSummary.OrderSummaryActivity
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.scheduleDateTime.ScheduleDateTimeActivity
import com.lifehopehealthapp.splash.SplashActivity
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.address
import com.lifehopehealthapp.utils.PreferenceHelper.addresslatlng
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.checkProfileStatus
import com.lifehopehealthapp.utils.PreferenceHelper.checkuserstatus
import com.lifehopehealthapp.utils.PreferenceHelper.email
import com.lifehopehealthapp.utils.PreferenceHelper.firstname
import com.lifehopehealthapp.utils.PreferenceHelper.isVerify
import com.lifehopehealthapp.utils.PreferenceHelper.lastname
import com.lifehopehealthapp.utils.PreferenceHelper.localOTPcheck
import com.lifehopehealthapp.utils.PreferenceHelper.productDeliveryAddress
import com.lifehopehealthapp.utils.PreferenceHelper.profile
import com.lifehopehealthapp.utils.PreferenceHelper.profileOldImg
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.PreferenceHelper.s3BuckeoldprofileImg
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketName
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketRegion
import com.lifehopehealthapp.utils.PreferenceHelper.saveMobileNo
import com.lifehopehealthapp.utils.PreferenceHelper.saveSettingsAPI
import com.lifehopehealthapp.utils.PreferenceHelper.userAge
import com.lifehopehealthapp.utils.PreferenceHelper.userGender
import com.lifehopehealthapp.utils.PreferenceHelper.userID
import com.lifehopehealthapp.utils.PreferenceHelper.w3AccessKey
import com.lifehopehealthapp.utils.PreferenceHelper.w3SecretKey
import com.lifehopehealthapp.utils.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MyProfileActivity : BaseActivity<MyProfileViewModel, MyProfileModel>() {
    private lateinit var binding: ActivityMyProfileBinding

    private val CAMERA_REQUEST = 0
    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 4
    private val PICK_IMAGE_REQUEST = 1
    private var genderName: ArrayList<String>? = null
    private var mGmailSign: GoogleSignInClient? = null
    lateinit var retroService: APIManager
    private var mAge: Int? = 0

    //private var viewStub: ViewStub? = null
    private var mYear: Int? = 0
    private var mMonth: Int? = 0
    private var mDay: Int? = 0
    private var token: String? = ""
    private var profileStatus: String? = ""
    private var realPath: String = ""
    private var destination: File? = null
    private var BookTest: String? = ""
    private var TestType: String? = ""
    private var title: String? = ""
    private var description: String? = ""
    private var image: String? = ""
    private var price: String? = ""
    private var testTypeId: String? = ""
    private var testCategoryId: String? = ""
    private var tax: String? = ""
    private var shippingCharge: String? = ""
    private var profileImage: String? = ""
    private var TYPE: Int? = 0
    private var firstName: String? = ""
    private var lastName: String? = ""
    private var gender: String? = ""
    private var dob: String? = ""
    private var mobileNo: String? = ""
    private var mMobile: String? = ""
    private var address: String? = ""
    private var height: String? = ""
    private var weight: String? = ""
    private var extraInfo: String? = ""
    private var BookingType: Int = 0
    private var mobileOTPStatus: Int = 0
    private var runOTP: Boolean? = false
    private var bloodGroupData: String? = ""
    private var ProfileImage: String = ""
    private var email: String = ""
    private var progressDoalog: Dialog? = null
    private var progressDialog: Dialog? = null
    private var userStatus: String = ""
    private var localOTPCheck: String = ""
    private var apiResponse: String? = ""
    private var iosImage: String? = ""
    private var bloodGroup: ArrayList<String>? = ArrayList<String>()
    var mPrefs: SharedPreferences? = null
    var aSettingConfig: SettingsResponse? = null
    var userAgree: String? = ""
    private var infoText: String? = ""
    private var current = ""
    private val ddmmyyyy = "MMDDYYYY"
    private var ExitingDate: String? = ""
    private var DOBLength: Boolean = false
    private var DOBValidation: Boolean = false
    val MAX_FORMAT_LENGTH = 8
    val MIN_FORMAT_LENGTH = 3
    lateinit var credentialsProvider: CognitoCachingCredentialsProvider
    private var updatedText: String? = null
    private var editing = false
    private val cal = Calendar.getInstance()
    private var fileUri: Uri? = null
    override fun getViewModel() = MyProfileViewModel::class.java

    override fun getActivityRepository() =
        MyProfileModel(
            remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
        )

    private fun validateMonth(month: String): String {
        if (month.length == 1 && month.toInt() in 2..9) {
            return "0$month"
        }
        if (month.length == 2 && month.toInt() > 12) {
            return month.substring(0, 1)
        }
        return month
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        apiResponse = mPrefs!!.saveSettingsAPI
        profileStatus = mPrefs!!.checkProfileStatus
        Log.e("apiResponse", apiResponse.toString())

        retroService = remoteDataSource.BuildAPI(APIManager::class.java)

        val obj = JSONObject(apiResponse.toString())
        userAgree = obj.getString("userAgree")
        val array2: JSONArray = obj.getJSONArray("bloodgroups")
        bloodGroup!!.add("Select Blood Type")
        for (i in 0 until array2.length()) {
            val Year = array2[i].toString()
            Log.d("Year", Year)
            bloodGroup!!.add(Year)
        }
        Log.e("str", bloodGroup!!.size.toString())

        /*val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroup!!)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBlood.setAdapter(adapter)
        binding.spinnerBlood.prompt = "Select Blood Type"
*/
        val spinnerArrayAdapter =
            object : ArrayAdapter<String?>(
                this, android.R.layout.simple_spinner_item,
                bloodGroup as List<String?>
            ) {
                override fun isEnabled(position: Int): Boolean {
                    return if (position == 0) {
                        false
                    } else {
                        true

                    }
                }

                override fun getDropDownView(
                    position: Int, convertView: View?,
                    parent: ViewGroup?
                ): View? {
                    val view = super.getDropDownView(
                        position,
                        convertView,
                        parent
                    )
                    val tv = view as TextView
                    if (position == 0) {
                        tv.setTextColor(Color.GRAY)
                    } else {
                        tv.setTextColor(Color.BLACK)
                    }
                    return view
                }
            }
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBlood.setAdapter(spinnerArrayAdapter)

        binding.spinnerBlood.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItemText =
                    parent.getItemAtPosition(position) as String
                if (position > 0) {

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        val extras = intent.extras
        if (extras != null) {
            BookTest = extras.getString("BookTest").toString()
            TestType = extras.getString("TestType").toString()
            title = extras.getString("title").toString()
            iosImage = extras.getString("iosImage").toString()
            description = extras.getString("des").toString()
            image = extras.getString("pic").toString()
            price = extras.getString("price").toString()
            testTypeId = extras.getString("testTypeId").toString()
            testCategoryId = extras.getString("testCategoryId").toString()
            tax = extras.getString("tax").toString()
            shippingCharge = extras.getString("shippingCharge").toString()
            infoText = extras.getString("testNotes").toString()
            extraInfo = extras.getString("extraInfo")
            BookingType = extras.getInt("BookingType")
        }
        if (Utils.isNetworkAvailable(this)) {
            val request =
                SettingsRequest(type = "android")

            //viewModel.getsettingsDetails(request)
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }


        // binding.edittextWeight.setFilters(arrayOf<InputFilter>(DecimalDigitsInputFilter(5, 2)))

        binding.edittextWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                val str: String = binding.edittextWeight.text.toString()
                if (str.isEmpty()) return
                if (str.contentEquals("0")) {
                    Toast.makeText(
                        this@MyProfileActivity,
                        "0 must not be allowed",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    binding.edittextWeight.setText("")
                } else {
                    val str2: String? = PerfectDecimal(str, 3, 2)

                    if (str2 != str) {
                        binding.edittextWeight.setText(str2)
                        binding.edittextWeight.setSelection(str2!!.length)
                    }

                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        genderName = ArrayList<String>()
        genderName!!.add("Select Gender")
        genderName!!.add("Male")
        genderName!!.add("Female")

        /*val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderName!!)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.setAdapter(aa)
        binding.spinnerGender.setPrompt("Select Gender")*/


        val spinnerArray =
            object : ArrayAdapter<String?>(
                this, android.R.layout.simple_spinner_item,
                genderName as List<String?>
            ) {
                override fun isEnabled(position: Int): Boolean {
                    return if (position == 0) {
                        false
                    } else {
                        true

                    }
                }

                override fun getDropDownView(
                    position: Int, convertView: View?,
                    parent: ViewGroup?
                ): View? {
                    val view = super.getDropDownView(
                        position,
                        convertView,
                        parent
                    )
                    val tv = view as TextView
                    if (position == 0) {
                        tv.setTextColor(Color.GRAY)
                    } else {
                        tv.setTextColor(Color.BLACK)
                    }
                    return view
                }
            }
        spinnerArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.setAdapter(spinnerArray)


        binding.edittextHeightFeet.setOnClickListener {
            showHeightPopup()
        }
        binding.imageViewCalender.setOnClickListener {
            datePickerData()
        }
        binding.edittextDob.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                // TODO Auto-generated method stub
                //Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
                if (s.toString().contains("M") || s.toString().contains("D") || s.toString()
                        .contains("Y")
                ) {
                } else {
                    val length = s.toString().length
                    if (length == 10) {
                        if (Utils.compareWithCurrentDate(current) > 0) {
                            Utils.showToast(
                                this@MyProfileActivity,
                                resources.getString(R.string.toast_enter_valid_date),
                                true
                            )
                            DOBValidation = false
                            //binding.edittextDob.setError("Invalid Date")
                        } else {
                            if (Utils.compareDates(current, s.toString()) < 0) {
                                //  binding.edittextDob.setError("Invalid Date")
                                Utils.showToast(
                                    this@MyProfileActivity,
                                    resources.getString(R.string.toast_enter_valid_date),
                                    true
                                )
                                DOBValidation = false
                            } else {
                                DOBValidation = true
                            }
                        }
                    }
                }
            }

            override fun beforeTextChanged(cs: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s.toString() != current) {
                    var clean = s.toString().replace("[^\\d.]|\\.".toRegex(), "")
                    val cleanC = current.replace("[^\\d.]|\\.".toRegex(), "")
                    val cl = clean.length
                    var sel = cl
                    var i = 2
                    while (i <= cl && i < 6) {
                        sel++
                        i += 2
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean == cleanC) sel--

                    if (clean.length <= 2) {
                        if (clean.length == 2) {
                            clean = clean + ddmmyyyy.substring(clean.length)
                            var mon = clean.substring(0, 2).toInt()
                            mon = if (mon < 1) 1 else if (mon > 12) 12 else mon
                            cal[Calendar.MONTH] = mon - 1
                            clean = String.format("%02d", mon)
                        }
                    }
                    if (clean.length == 4) {
                        clean = clean + ddmmyyyy.substring(clean.length)
                        var mon = clean.substring(0, 2).toInt()
                        var day = clean.substring(2, 4).toInt()
                        mon = if (mon < 1) 1 else if (mon > 12) 12 else mon
                        cal[Calendar.MONTH] = mon - 1
                        day = if (day > cal.getActualMaximum(Calendar.DATE)) cal.getActualMaximum(
                            Calendar.DATE
                        ) else day
                        clean = String.format("%02d%02d", mon, day)
                    }

                    if (clean.length < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length)
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        var mon = clean.substring(0, 2).toInt()
                        var day = clean.substring(2, 4).toInt()
                        var year = clean.substring(4, 8).toInt()
                        mon = if (mon < 1) 1 else if (mon > 12) 12 else mon
                        cal[Calendar.MONTH] = mon - 1
                        year = if (year < 1900) 1900 else if (year > 2100) 2100 else year
                        cal[Calendar.YEAR] = year
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        mAge = Utils.getAge(year, mon, day)
                        if (mAge!! >= 15) {

                        } else {
                            Toast.makeText(
                                this@MyProfileActivity,
                                getString(R.string.text_age_must_be_greater),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        day = if (day > cal.getActualMaximum(Calendar.DATE)) cal.getActualMaximum(
                            Calendar.DATE
                        ) else day
                        clean = String.format("%02d%02d%02d", mon, day, year)
                    }
                    clean = String.format(
                        "%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8)
                    )
                    sel = if (sel < 0) 0 else sel
                    if (sel == 10 || sel == 11) {
                        DOBLength = true
                    } else {
                        DOBLength = false
                    }
                    current = clean
                    binding.edittextDob.setText(current)
                    binding.edittextDob.setSelection(if (sel < current.length) sel else current.length)
                }
            }
        })

        binding.layoutView.layoutSelectImage.setOnClickListener {
            checkPermissions()
        }
        binding.layoutView.userProfileImage.setOnClickListener {
            checkPermissions()
        }

        binding.edittextMobile.addTextChangedListener(object : TextWatcher {
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
                val text: String = binding.edittextMobile.getText().toString()
                val textLength: Int = binding.edittextMobile.getText()!!.length
                if (text.endsWith("-") || text.endsWith(" ") || text.endsWith(" ")) return
                if (textLength == 1) {
                    if (!text.contains("(")) {
                        binding.edittextMobile.setText(
                            StringBuilder(text).insert(text.length - 1, "(").toString()
                        )
                        binding.edittextMobile.setSelection(binding.edittextMobile.getText()!!.length)
                    }
                } else if (textLength == 5) {
                    if (!text.contains(")")) {
                        binding.edittextMobile.setText(
                            StringBuilder(text).insert(text.length - 1, ")").toString()
                        )
                        binding.edittextMobile.setSelection(binding.edittextMobile.getText()!!.length)
                    }
                } else if (textLength == 6) {
                    binding.edittextMobile.setText(
                        StringBuilder(text).insert(text.length - 1, " ").toString()
                    )
                    binding.edittextMobile.setSelection(binding.edittextMobile.getText()!!.length)
                } else if (textLength == 10) {
                    if (!text.contains("-")) {
                        binding.edittextMobile.setText(
                            StringBuilder(text).insert(text.length - 1, "-").toString()
                        )
                        binding.edittextMobile.setSelection(binding.edittextMobile.getText()!!.length)
                    }
                } else if (textLength == 15) {
                    if (text.contains("-")) {
                        binding.edittextMobile.setText(
                            StringBuilder(text).insert(text.length - 1, "-").toString()
                        )
                        binding.edittextMobile.setSelection(binding.edittextMobile.getText()!!.length)
                    }
                } else if (textLength == 18) {
                    if (text.contains("-")) {
                        binding.edittextMobile.setText(
                            StringBuilder(text).insert(text.length - 1, "-").toString()
                        )
                        binding.edittextMobile.setSelection(binding.edittextMobile.getText()!!.length)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        localOTPCheck = mPrefs!!.localOTPcheck.toString()
        mobileNo = mPrefs!!.saveMobileNo
        if (profileStatus.equals("false")) {
            binding.layoutView.textViewDone.text = resources.getString(R.string.button_save)
        } else {
            binding.layoutView.textViewDone.text = resources.getString(R.string.update)
        }
        if (userStatus.equals("true") || profileStatus.equals("true")) {
            if (Utils.isNetworkAvailable(this)) {
                if (progressDoalog == null) {
                    progressDoalog = Utils.getDialog(this)
                }
                progressDoalog!!.show()
                viewModel.getUserDetails(token!!)
            } else {
                Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
            }
        } else {
            Glide.with(this).load(mPrefs!!.profile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_profile_no_image)
                .error(R.drawable.ic_profile_no_image)
                .into(binding.layoutView.userProfileImage)

            /* Utils.uploadAWSimage(
                 mPrefs!!.profile!!,
                 mPrefs!!.s3BucketName,
                 mPrefs!!.userID,
                 this,
                 Utils.decryption(mPrefs!!.w3SecretKey),
                 Utils.decryption(mPrefs!!.w3AccessKey), ""
             )*/
            firstName = mPrefs!!.firstname
            lastName = mPrefs!!.lastname
            ProfileImage = mPrefs!!.profile.toString()
            email = mPrefs!!.email.toString()
            mobileNo = mPrefs!!.saveMobileNo
            localOTPCheck = mPrefs!!.localOTPcheck.toString()
            binding.edittextMobile.setText(
                Utils.USFormatMobile(
                    mobileNo!!.replace(
                        "[^a-zA-Z0-9]".toRegex(),
                        ""
                    ).toLong()
                )
            )

            if (firstName!!.contentEquals("null")) {
                binding.edittextName.setText("")
            } else {
                binding.edittextName.setText(firstName)
            }
            if (lastName!!.contentEquals("null")) {
                binding.edittextLastname.setText("")
            } else {
                binding.edittextLastname.setText(lastName)
            }
            if (email.contentEquals("null")) {
                binding.edittextEmail.setText("")
            } else {
                binding.edittextEmail.setText(email)
            }
        }

        viewModel.detaResponse.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    if (progressDoalog != null && progressDoalog!!.isShowing) {
                        progressDoalog!!.dismiss()
                    }
                    if (it.value.getStatusCode() == 200) {
                        if (localOTPCheck.equals("")) {
                            binding.edittextMobile.setText(
                                Utils.USFormatMobile(
                                    it.value.getValue()!!.data!!.mobileNo!!.replace(
                                        "[^a-zA-Z0-9]".toRegex(),
                                        ""
                                    )!!.toLong()
                                )
                            )
                            Log.e("Mobile", it.value.getValue()!!.data!!.mobileNo!!)
                        } else {
                            binding.edittextMobile.setText(
                                Utils.USFormatMobile(
                                    mobileNo!!.replace(
                                        "[^a-zA-Z0-9]".toRegex(),
                                        ""
                                    )!!.toLong()
                                )
                            )
                            Log.e("Mobile", mobileNo!!)
                        }
                        binding.edittextName.setText(it.value.getValue()!!.data!!.firstName)
                        binding.edittextLastname.setText(it.value.getValue()!!.data!!.lastName)
                        if (BookTest.equals("")) {
                            binding.edittextEmail.setText(it.value.getValue()!!.data!!.email)
                        } else {
                            if (it.value.getValue()!!.data!!.email.equals("")) {
                                binding.layoutEmail.isVisible = false
                            } else {
                                binding.edittextEmail.isEnabled = false
                                binding.edittextEmail.setText(it.value.getValue()!!.data!!.email)
                            }

                        }
                        binding.edittextAddress.setText(it.value.getValue()!!.data!!.address)

                        //newly added 06_02_22
                        mPrefs!!.productDeliveryAddress=it.value.getValue()!!.data!!.address




                        binding.edittextHeightFeet.setText(it.value.getValue()!!.data!!.height)
                        binding.edittextWeight.setText(it.value.getValue()!!.data!!.weight)
                        Log.e("RESPONSE", it.value.getValue()!!.data!!.toString())

                        mPrefs!!.isVerify = it.value.getValue()!!.data!!.isVerified!!
                        mPrefs!!.profile = it.value.getValue()!!.data!!.imageID!!
                        mPrefs!!.address = it.value.getValue()!!.data!!.address!!
                        mPrefs!!.checkProfileStatus =
                            it.value.getValue()!!.data!!.getIsProfileUpdate().toString()


                        var mDate: String = it.value.getValue()!!.data!!.dob!!
                        var mImage: String = it.value.getValue()!!.data!!.imageID.toString()
                        if (mImage.equals("") || mImage.equals("null") || mImage == null) {

                        } else {
                            profileImage = it.value.getValue()!!.data!!.imageID!!
                            /*Utils.Presigned(
                                it.value.getValue()!!.data!!.imageID!!,
                                mPrefs!!.s3BucketName,
                                mPrefs!!.userID,
                                this,
                                Utils.decryption(mPrefs!!.w3SecretKey),
                                Utils.decryption(mPrefs!!.w3AccessKey)
                            )*/
                            Glide.with(this).load(mPrefs!!.profileOldImg)
                                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                                //.skipMemoryCache(true)
                                .placeholder(R.drawable.ic_profile_no_image)
                                .error(R.drawable.ic_profile_no_image)
                                .into(binding.layoutView.userProfileImage)
                        }

                        if (mDate.equals("") || mDate.equals("null") || mDate == null) {
                            binding.edittextDob.setText("")
                        } else {
                            ExitingDate = Utils.getDateCurrentTimeZone(
                                it.value.getValue()!!.data!!.dob!!.toLong(),
                                Constants.TIMESTAMP_DOB.toString()
                            )!!
                            val calendar = Calendar.getInstance()
                            val thisYear = calendar[Calendar.YEAR]
                            val month = calendar[Calendar.DAY_OF_MONTH]
                            val day = calendar[Calendar.DAY_OF_WEEK]
                            Log.e("DOB", ExitingDate.toString())
                            Log.e("DOB", "year->" + thisYear + "month->" + month + "day->" + day)
                            binding.edittextDob.setText(getDateTime(it.value.getValue()!!.data!!.dob!!).toString())
                        }
                        Log.e("blood1", it.value.getValue()!!.data!!.bloodGroup!!)
                        for (position in 0 until bloodGroup!!.size) {
                            if (bloodGroup!![position] == it.value.getValue()!!.data!!.bloodGroup) {
                                binding.spinnerBlood.setSelection(position)
                                bloodGroupData = it.value.getValue()!!.data!!.bloodGroup
                                Log.e("blood1", it.value.getValue()!!.data!!.bloodGroup!!)
                            }
                        }
                        for (position in 0 until genderName!!.size) {
                            if (genderName!![position] == it.value.getValue()!!.data!!.gender) {
                                binding.spinnerGender.setSelection(position)
                                gender = it.value.getValue()!!.data!!.gender
                                Log.e("gender", it.value.getValue()!!.data!!.gender!!)
                            }
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
        mobileOTPStatus = mPrefs!!.isVerify
        if (mobileOTPStatus == 1) {
            binding.imageviewOtpVerification.setImageDrawable(resources.getDrawable(R.drawable.ic_validated))
        } else {
            mobileOTPStatus = 0
            binding.imageviewOtpVerification.setImageDrawable(resources.getDrawable(R.drawable.ic_invalidated))
        }
        mobileNo = mPrefs!!.saveMobileNo
        userStatus = mPrefs!!.checkuserstatus.toString()
        binding.edittextMobile.setText(
            Utils.USFormatMobile(
                mobileNo!!.replace(
                    "[^a-zA-Z0-9]".toRegex(),
                    ""
                )!!.toLong()
            )
        )

        var mAddress: String? = intent.getStringExtra("MapAddress")
        binding.edittextAddress.setText(mAddress)

        binding.layoutView.textviewSelectMedicalInfo.setOnClickListener {
            binding.layoutBasicInfo.isVisible = false
            binding.layoutMedicalInfo.isVisible = true
            binding.layoutView.viewMedicalInfo.isVisible = true
            binding.layoutView.textviewSelectBasicInfo.setTextColor(resources.getColor(R.color.info_color))
            binding.layoutView.textviewSelectMedicalInfo.setTextColor(resources.getColor(R.color.black))
            binding.layoutView.viewBasicInfo.isVisible = false
        }

        binding.layoutView.textviewSelectBasicInfo.setOnClickListener {
            binding.layoutBasicInfo.isVisible = true
            binding.layoutMedicalInfo.isVisible = false
            binding.layoutView.viewMedicalInfo.isVisible = false
            binding.layoutView.viewBasicInfo.isVisible = true
            binding.layoutView.textviewSelectBasicInfo.setTextColor(resources.getColor(R.color.black))
            binding.layoutView.textviewSelectMedicalInfo.setTextColor(resources.getColor(R.color.info_color))
        }

        binding.edittextDob.setOnClickListener {
            //datePickerData()
        }

        binding.edittextAddress.setOnClickListener {
            googleMap()

        }
        binding.imageviewOtpVerification.setOnClickListener {
            if (mobileOTPStatus == 1) {

            } else if (runOTP!!) {
                mMobile = binding.edittextMobile.text!!.trim().toString()
                mMobile = mMobile!!.replace("[^a-zA-Z0-9]".toRegex(), "")
                if (mMobile!!.equals("")) {
                    Utils.showToast(
                        this,
                        resources.getString(R.string.text_enter_mobile_number),
                        true
                    )
                } else {
                    if (Utils.isValidPhoneNumbers(mMobile!!.trim())) {
                        showPopup()
                    } else {
                        Utils.showToast(
                            this,
                            resources.getString(R.string.toast_enter_mobile),
                            true
                        )
                    }
                }

            } else {
                mMobile = binding.edittextMobile.text!!.trim().toString()
                mMobile = mMobile!!.replace("[^a-zA-Z0-9]".toRegex(), "")
                if (mMobile!!.equals("")) {
                    Utils.showToast(
                        this,
                        resources.getString(R.string.text_enter_mobile_number),
                        true
                    )
                } else {
                    if (Utils.isValidPhoneNumbers(mMobile!!.trim())) {
                        showPopup()
                    } else {
                        Utils.showToast(
                            this,
                            resources.getString(R.string.toast_enter_mobile),
                            true
                        )
                    }
                }

            }

        }

        binding.edittextMobile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 1) {
                    var str = s
                    str = str!!.replace("[^a-zA-Z0-9]".toRegex(), "")
                    if (mobileNo!!.contentEquals(str!!)) {
                        mobileOTPStatus = 1
                        binding.imageviewOtpVerification.setImageDrawable(resources.getDrawable(R.drawable.ic_validated))
                    } else {
                        runOTP = true
                        mobileOTPStatus = 0
                        binding.imageviewOtpVerification.setImageDrawable(resources.getDrawable(R.drawable.ic_invalidated))
                    }
                } else if (count == 0) {
                    mobileOTPStatus = 0
                    binding.imageviewOtpVerification.setImageDrawable(resources.getDrawable(R.drawable.ic_invalidated))
                }

            }
        })


        if (BookTest.equals("BookTest")) {
            binding.layoutAddress.hint = resources.getText(R.string.text_address_)
            binding.edittextAddress.hint = resources.getText(R.string.text_address_)
            binding.edittextName.isEnabled = false
            binding.edittextLastname.isEnabled = false
            binding.edittextDob.isEnabled = false
            binding.spinnerGender.isEnabled = false
            binding.layoutView.textViewDone.isVisible = false
            binding.imageViewCalender.isEnabled = false
            binding.textViewInfoText.isVisible = true
            binding.textViewInfoText.text = infoText
            binding.layoutView.textviewProfileSetting.setText(getString(R.string.contact_details))
            TYPE = 1
            binding.layoutView.textviewSelectBasicInfo.isVisible = false
            binding.layoutView.textviewSelectMedicalInfo.isVisible = false
            binding.layoutView.viewBasicInfo.isVisible = false
            binding.layoutView.layoutTabSelect.isVisible = true
            //binding.imageViewInfo.isVisible = true
            binding.layoutButtonView.isVisible = true
            val wordtoSpan: Spannable =
                SpannableString(getString(R.string.text_i_Agree))
            val teremsAndCondition: ClickableSpan =
                object : ClickableSpan() {
                    override fun onClick(textView: View) {
                        showCheckboxPopup()
                    }
                }
            wordtoSpan.setSpan(teremsAndCondition, 8, 26, 0)
            wordtoSpan.setSpan(
                ForegroundColorSpan(
                    resources
                        .getColor(R.color.link_color)
                ), 8, 26, 0
            )
            binding.textViewTerms.setMovementMethod(LinkMovementMethod.getInstance())
            binding.textViewTerms.setText(wordtoSpan, TextView.BufferType.SPANNABLE)

            if (TestType.equals("1")) {
                binding.buttonBookNow.isVisible = true
                binding.buttonScheduleTest.isVisible = false
                binding.textViewInfoText.text = "Self Test " + "(" + infoText + ")"
            } else if (TestType.equals("2")) {
                binding.buttonBookNow.isVisible = true
                binding.buttonScheduleTest.isVisible = true
                binding.textViewInfoText.text = "In-Home Test " + "(" + infoText + ")"
            }

            binding.buttonScheduleTest.setOnClickListener {
                if (mobileOTPStatus == 1) {
                    var addressData: String = binding.edittextAddress.text.toString()
                    var mobileData: String = binding.edittextMobile.text!!.toString()
                    intent = Intent(this, ScheduleDateTimeActivity::class.java)
                    val extra = Bundle()

                    if ((mobileData.equals("")) || (addressData.equals(""))) {
                        Utils.showToast(
                            this,
                            resources.getString(R.string.toast_enter_address),
                            true
                        )
                    } else if (binding.checkBoxCondition.isChecked) {
                        getLocationFromAddress(this, addressData)
                        extra.putString("TestType", TestType)
                        extra.putString("title", title)
                        extra.putString("des", description)
                        extra.putString("pic", image)
                        extra.putString("price", price)
                        extra.putString("testTypeId", testTypeId)
                        extra.putString("testCategoryId", testCategoryId)
                        extra.putString("tax", tax)
                        extra.putString("shippingCharge", shippingCharge)
                        extra.putString("iosImage", iosImage)
                        extra.putInt("BookingType", BookingType)
                        extra.putString(
                            "name",
                            binding.edittextName.text.toString() + " " + binding.edittextLastname.text.toString()
                        )
                        extra.putString("mobile", binding.edittextMobile.text.toString())
                        extra.putString("address", binding.edittextAddress.text.toString())
                        extra.putString("ProfilePic", profileImage)
                        extra.putString("extraInfo", extraInfo)
                        intent.putExtras(extra)
                        startActivity(intent)
                    } else {
                        Utils.showToast(this, getString(R.string.toast_booking_terms), true)
                    }

                } else {
                    mMobile = binding.edittextMobile.text!!.trim().toString()
                    mMobile = mMobile!!.replace("[^a-zA-Z0-9]".toRegex(), "")
                    if (mMobile!!.equals("")) {
                        Utils.showToast(
                            this,
                            resources.getString(R.string.text_enter_mobile_number),
                            true
                        )
                    } else {
                        if (Utils.isValidPhoneNumbers(mMobile!!.trim())) {
                            showPopup()
                        } else {
                            Utils.showToast(
                                this,
                                resources.getString(R.string.toast_enter_mobile),
                                true
                            )
                        }
                    }

                }

            }

            binding.buttonBookNow.setOnClickListener {
                if (mobileOTPStatus == 1) {
                    var addressData: String = binding.edittextAddress.text.toString()
                    var mobileData: String = binding.edittextMobile.text!!.toString()
                    if ((mobileData.equals("")) || (addressData.equals(""))) {
                        Utils.showToast(
                            this,
                            resources.getString(R.string.toast_enter_address),
                            true
                        )
                    } else if (binding.checkBoxCondition.isChecked) {
                        getLocationFromAddress(this, addressData)
                        Log.e("addressData", getLocationFromAddress(this, addressData).toString())
                        intent = Intent(this, OrderSummaryActivity::class.java)
                        val extra = Bundle()
                        extra.putString("ProfilePic", profileImage)
                        extra.putString("TestType", TestType)
                        extra.putString("title", title)
                        extra.putString("des", description)
                        extra.putString("pic", image)
                        extra.putString("price", price)
                        extra.putString("testTypeId", testTypeId)
                        extra.putString("testCategoryId", testCategoryId)
                        extra.putString("iosImage", iosImage)
                        extra.putString("tax", tax)
                        extra.putString("shippingCharge", shippingCharge)
                        extra.putString("extraInfo", extraInfo)
                        extra.putInt("BookingType", BookingType)
                        extra.putString(
                            "name",
                            binding.edittextName.text.toString() + " " + binding.edittextLastname.text.toString()
                        )
                        extra.putString("mobile", binding.edittextMobile.text.toString())
                        extra.putString("address", binding.edittextAddress.text.toString())
                        intent.putExtras(extra)
                        startActivity(intent)
                    } else {
                        Utils.showToast(this, getString(R.string.toast_booking_terms), true)
                    }

                } else {
                    mMobile = binding.edittextMobile.text!!.trim().toString()
                    mMobile = mMobile!!.replace("[^a-zA-Z0-9]".toRegex(), "")
                    if (mMobile!!.equals("")) {
                        Utils.showToast(
                            this,
                            resources.getString(R.string.text_enter_mobile_number),
                            true
                        )
                    } else {
                        Log.e("mMobile", mMobile.toString())
                        if (Utils.isValidPhoneNumbers(mMobile!!.trim())) {
                            showPopup()
                        } else {
                            Utils.showToast(
                                this,
                                resources.getString(R.string.toast_enter_mobile),
                                true
                            )
                        }
                    }
                }
            }
        } else {
            TYPE = 0
            //binding.imageViewInfo.isVisible = false
            binding.textViewInfoText.isVisible = false
            binding.buttonBookNow.isVisible = false
            binding.buttonScheduleTest.isVisible = false
        }
        binding.layoutView.imageviewBackArrow.setOnClickListener {
            onBackPressed()
        }

        binding.layoutView.textViewDone.setOnClickListener {
            validation()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (mPrefs!!.checkProfileStatus.equals("false")) {

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
        } else {
            finish()
        }
    }

    private fun showHeightPopup() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.heightpicker)

        val feetValue =
            dialog.findViewById<View>(R.id.feetValue) as NumberPicker
        val inchValue =
            dialog.findViewById<View>(R.id.inchValue) as NumberPicker

        feetValue.setMinValue(3)
        feetValue.setMaxValue(8)
        inchValue.setMinValue(0)
        inchValue.setMaxValue(11)


        val buttoncancel =
            dialog.findViewById<View>(R.id.buttonCancel) as TextView
        val buttonproceed =
            dialog.findViewById<View>(R.id.buttonProceed) as TextView

        buttoncancel.setOnClickListener { dialog.dismiss() }
        buttonproceed.setOnClickListener {
            Log.d(
                "DBG",
                "Price is: " + feetValue.value.toString() + "'" + inchValue.value + "''"
            )
            binding.edittextHeightFeet.setText(feetValue.value.toString() + "' " + inchValue.value + "''")
            dialog.dismiss()
        }

        dialog.show()

    }


    private fun showCheckboxPopup() {
        val alertDialog = Dialog(this)
        val lp = WindowManager.LayoutParams()
        val window = alertDialog.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.dialog_terms_condition)
        val heading: com.lifehopehealthapp.widgets.LifeHopenTextView =
            alertDialog.findViewById(R.id.textview_dialog_heading)
        val headingTxt: com.lifehopehealthapp.widgets.LifeHopenTextView =
            alertDialog.findViewById(R.id.textview_header)
        headingTxt.text = resources.getString(R.string.terms_conditions)
        heading.setText(userAgree)
        val posTv: AppCompatButton = alertDialog.findViewById(R.id.button_proceed)

        posTv.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
        lp.copyFrom(window.attributes)
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

    fun PerfectDecimal(
        str: String,
        MAX_BEFORE_POINT: Int,
        MAX_DECIMAL: Int
    ): String? {
        var str = str
        if (str[0] == '.') str = "0$str"
        val max = str.length
        var rFinal = ""
        var after = false
        var i = 0
        var up = 0
        var decimal = 0
        var t: Char
        while (i < max) {
            t = str[i]
            if (t != '.' && after == false) {
                up++
                if (up > MAX_BEFORE_POINT) return rFinal
            } else if (t == '.') {
                after = true
            } else {
                decimal++
                if (decimal > MAX_DECIMAL) return rFinal
            }
            rFinal = rFinal + t
            i++
        }
        return rFinal
    }

    private fun showPopup() {
        val alertDialog = Dialog(this)
        val lp = WindowManager.LayoutParams()
        val window = alertDialog.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.alert_dialog_view)
        val heading: com.lifehopehealthapp.widgets.LifeHopenTextView =
            alertDialog.findViewById(R.id.textview_dialog_heading)
        heading.setText(resources.getString(R.string.text_otp_popup))
        val posTv: AppCompatButton = alertDialog.findViewById(R.id.button_proceed)
        posTv.setText(resources.getString(R.string.button_proceed))

        posTv.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
            /*lifecycleScope.launch {
                viewModel.savelocalOTPcheck(token = "local")
            }*/
            mPrefs!!.localOTPcheck = "local"
            intent = Intent(this, MobileOTPVerifyActivity::class.java)
            intent.putExtra("mobilenumber", mMobile!!.trim())
            startActivity(intent)
        })
        val negTv: AppCompatButton = alertDialog.findViewById(R.id.button_cancel)
        negTv.setText(resources.getString(R.string.button_cancel))

        negTv.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
        })


        alertDialog.show()
        lp.copyFrom(window.attributes)
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

    private fun getDateTime(s: String): String? {
        try {
            val sdf = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SimpleDateFormat("MM/dd/yyyy")
            } else {
                TODO("VERSION.SDK_INT < N")
            }
            val netDate = Date(s.toLong() * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    private fun checkPermissions() {

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        SelectOption()
                    }
                    if (report.isAnyPermissionPermanentlyDenied()) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).withErrorListener(object : PermissionRequestErrorListener {
                override fun onError(error: DexterError?) {
                    Toast.makeText(
                        applicationContext,
                        "Error occurred! ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            .onSameThread()
            .check()
    }

    private fun showSettingsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton(
            "GOTO SETTINGS",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
                openSettings()
            })
        builder.setNegativeButton(
            "Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.show()
    }

    private fun openSettings() {
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    fun getFilePathFromContentUri(
        contentUri: Uri?,
        contentResolver: ContentResolver
    ): String? {
        val filePath: String
        val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor: Cursor =
            contentResolver.query(contentUri!!, filePathColumn, null, null, null)!!
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        filePath = cursor.getString(columnIndex)
        cursor.close()
        return filePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            fileUri = data?.data
            //binding..setImageURI(fileUri)
            Log.e("fileUri", fileUri.toString())
            //You can get File object from intent
            //val file: File = ImagePicker.getFile(data)!!

            //You can also get File Path from intent
            //val filePath: String = ImagePicker.getFilePath(data)!!
            val filePath = fileUri!!.path
            val filename = filePath!!.substring(filePath!!.lastIndexOf("/") + 1)
            //uploadFile(filePath)

            if (TYPE == 0) {
                if (progressDialog == null) {
                    progressDialog = Utils.getDialog(this)
                }
                progressDialog!!.show()
                val endPonit =
                    Utils.uploadAWSimage(
                        filePath,
                        mPrefs!!.s3BucketName,
                        mPrefs!!.userID,
                        this,
                        Utils.decryption(mPrefs!!.w3SecretKey),
                        Utils.decryption(mPrefs!!.w3AccessKey),
                        "",
                        mPrefs!!.s3BucketRegion
                    )
            } else {
                val endPonit =
                    Utils.uploadAWSimage(
                        filePath,
                        mPrefs!!.s3BucketName,
                        mPrefs!!.userID,
                        this,
                        Utils.decryption(mPrefs!!.w3SecretKey),
                        Utils.decryption(mPrefs!!.w3AccessKey),
                        "Book_",
                        mPrefs!!.s3BucketRegion
                    )
                binding.layoutView.userProfileImage.setImageURI(fileUri)
            }
            if (Utils.isNetworkAvailable(this)) {

            } else {
                Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        @Nullable permissions: Array<String?>,
        @Nullable grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> runOnUiThread {
                if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(
                        "Location::",
                        "Permission Granted, Now you can access data."
                    )
                } else {
                    Log.d(
                        "Location::",
                        "Permission Denied, You cannot access data."
                    )
                }
            }
        }
    }

    private fun SelectOption() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_upload_image)
        val Camera =
            dialog.findViewById<View>(R.id.camera_pic) as LinearLayout
        val Gallery =
            dialog.findViewById<View>(R.id.gallery_pic) as LinearLayout
        Camera.setOnClickListener {
            dialog.dismiss()
            TakePicture()
        }
        Gallery.setOnClickListener {
            dialog.dismiss()
            showFileChooser()
        }
        dialog.show()
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    private fun showFileChooser() {

        ImagePicker.with(this)
            .galleryOnly()
            .crop()                    //Crop image(Optional), Check Customization for more option
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .start()
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_PICK
//        startActivityForResult(
//            Intent.createChooser(intent, "Select Picture"),
//            PICK_IMAGE_REQUEST
//        )
    }

    private fun TakePicture() {
        ImagePicker.with(this)
            .cameraOnly()
            .crop()                    //Crop image(Optional), Check Customization for more option
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat")
    private fun validation() {
        var str_date: String? = binding.edittextDob.text.toString()
        if (str_date == null || str_date == "") {

        } else {
            if (DOBLength) {
                val parts: List<String> = str_date.split("/")
                val part1 = parts[0]
                val part2 = parts[1]
                val part3 = parts[2]
                val AGE: Int = Utils.getAge(part3.toInt(), part1.toInt(), part2.toInt())!!.toInt()
                mPrefs!!.userAge = AGE

                val date1: Date? = SimpleDateFormat("MM/dd/yyyy").parse(str_date)
                System.out.println("Today is " + date1!!.time)
                val dob1 = date1.time / 1000
                dob = dob1.toString()
                Log.e("timestam1", dob.toString())

            }
        }

        if (height!!.length == 1) {
            var addString = ".0"
            height = "$height $addString"
        } else {

        }

        firstName = binding.edittextName.text.toString().trim()
        lastName = binding.edittextLastname.text.toString().trim()
        email = binding.edittextEmail.text.toString().trim()
        mobileNo = binding.edittextMobile.text.toString().trim()
        //str = str.replace("[^a-zA-Z0-9]".toRegex(), "")
        //mobileNo = str
        address = binding.edittextAddress.text.toString()
        height = binding.edittextHeightFeet.text.toString().trim()
        weight = binding.edittextWeight.text.toString().trim()
        bloodGroupData = binding.spinnerBlood.selectedItem.toString()
        gender = binding.spinnerGender.selectedItem.toString()
        mPrefs!!.firstname = firstName.toString()
        mPrefs!!.lastname = lastName.toString()
        mPrefs!!.userGender = gender
        var mMobile = mobileNo
        mMobile = mMobile!!.replace("[^a-zA-Z0-9]".toRegex(), "")


        if (!firstName.equals("")) {
            if (!lastName.equals("")) {
                if (!gender!!.equals("Select Gender")) {
                    if ((!dob.equals("")) && DOBValidation && DOBLength) {
                        if (mAge!! >= 15) {
                            if (Utils.isValidPhoneNumber(mMobile.toString())) {
                                if (!height.equals("")) {
                                    if (!weight.equals("")) {
                                        if (weight!!.toDouble() <= 450.00 && weight!!.toDouble() != 0.0) {
                                            if (binding.edittextEmail.text.toString().equals("")) {
                                                apiCall(
                                                    firstName!!,
                                                    lastName!!,
                                                    dob,
                                                    mMobile,
                                                    address!!,
                                                    height!!,
                                                    weight!!,
                                                    bloodGroupData!!,
                                                    gender!!,
                                                    mobileOTPStatus,
                                                    email
                                                )
                                            } else if (Utils.isValidEmail(binding.edittextEmail.text.toString())) {
                                                apiCall(
                                                    firstName!!,
                                                    lastName!!,
                                                    dob,
                                                    mMobile,
                                                    address!!,
                                                    height!!,
                                                    weight!!,
                                                    bloodGroupData!!,
                                                    gender!!,
                                                    mobileOTPStatus,
                                                    email
                                                )
                                            } else {
                                                Utils.showToast(
                                                    this,
                                                    getString(R.string.text_enter_email_validation),
                                                    true
                                                )
                                            }
                                        } else {
                                            Utils.showToast(
                                                this,
                                                getString(R.string.text_weight_validation),
                                                true
                                            )
                                        }

                                    } else {
                                        Utils.showToast(
                                            this,
                                            getString(R.string.toast_enter_weight),
                                            true
                                        )
                                    }
                                } else {
                                    binding.layoutBasicInfo.isVisible = false
                                    binding.layoutMedicalInfo.isVisible = true
                                    binding.layoutView.viewMedicalInfo.isVisible = true
                                    binding.layoutView.textviewSelectBasicInfo.setTextColor(
                                        resources.getColor(R.color.info_color)
                                    )
                                    binding.layoutView.textviewSelectMedicalInfo.setTextColor(
                                        resources.getColor(R.color.black)
                                    )
                                    binding.layoutView.viewBasicInfo.isVisible = false
                                    Utils.showToast(
                                        this,
                                        getString(R.string.toast_enter_height),
                                        true
                                    )
                                }
                            } else {
                                Utils.showToast(
                                    this,
                                    getString(R.string.toast_valid_mobile),
                                    true
                                )
                            }
                        } else {
                            Utils.showToast(
                                this,
                                getString(R.string.toast_enter_valid_date),
                                true
                            )
                        }
                    } else {
                        Utils.showToast(
                            this,
                            getString(R.string.toast_valid_dob),
                            true
                        )
                    }
                } else {
                    Utils.showToast(
                        this,
                        getString(R.string.toast_select_gender),
                        true
                    )
                }
            } else {
                Utils.showToast(
                    this,
                    getString(R.string.toast_enter_lastname),
                    true
                )
            }
        } else {
            Utils.showToast(
                this,
                getString(R.string.toast_enter_firstname),
                true
            )
        }
    }

    private fun apiCall(
        firstName: String,
        lastName: String,
        dob: String?,
        mMobile: String,
        address: String,
        height: String,
        weight: String,
        bloodGroupData: String,
        gender: String,
        mobileOTPStatus: Int,
        email: String
    ) {
        val request =
            UpdateProfileModel(
                firstName = firstName,
                lastName = lastName,
                dob = dob!!,
                mobileNo = mMobile,
                address = address,
                height = height,
                weight = weight,
                bloodGroup = bloodGroupData,
                gender = gender, isProfileUpdate = true,
                isVerified = mobileOTPStatus, email = email
            )

        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            viewModel.updatedProfile(token!!, request)

            viewModel.updateprofileResponse.observe(this,
                androidx.lifecycle.Observer {
                    when (it) {
                        is Resource.Success -> {
                            if (progressDoalog != null && progressDoalog!!.isShowing) {
                                progressDoalog!!.dismiss()
                            }

                            it.value.getData().toString()
                            Utils.showToast(
                                this,
                                "Profile Updated Successfully",
                                true
                            )
                            mPrefs!!.isVerify = mobileOTPStatus
                            mPrefs!!.saveMobileNo = mMobile

                            if (mPrefs!!.checkProfileStatus.equals("false")) {
                                mPrefs!!.checkProfileStatus =
                                    "true"
                                startActivity(
                                    Intent(
                                        this,
                                        DashBoardActivity::class.java
                                    )
                                )
                                finish()
                            } else {
                                startActivity(
                                    Intent(
                                        this,
                                        EditProfileActivity::class.java
                                    )
                                )
                                finish()
                            }

                        }
                        is Resource.GenericError -> {
                            Utils.showToast(
                                this,
                                it.error!!.message.toString(),
                                true
                            )
                            if (progressDoalog != null && progressDoalog!!.isShowing) {
                                progressDoalog!!.dismiss()
                            }
                            if (it.code == 401) {
                                val data = RefreshAPIRequest()
                                val removeChar =
                                    mPrefs!!.authToken!!.replace(
                                        "Bearer ",
                                        ""
                                    )
                                data.token = removeChar
                                data.refreshToken =
                                    mPrefs!!.refreshToken

                                val gson = Gson()
                                var json: String? = ""
                                json = gson.toJson(data)
                                Log.e(
                                    "Model---->",
                                    json.toString()
                                )

                                val aJsonParser = JsonParser()
                                val aJsonObject =
                                    aJsonParser.parse(json) as JsonObject

                                val refresh =
                                    ErrorHandling(this)
                                refresh.onErrorHandling(
                                    mPrefs!!.authToken!!,
                                    aJsonObject
                                )
                            } else {

                            }
                        }
                    }
                })
        } else {
            Utils.noInternetAlert(
                resources.getString(R.string.no_internet_msg),
                this
            )
        }

    }

    private fun datePickerData() {
        if (ExitingDate.equals("")) {
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
        } else {
            val parts: List<String> = ExitingDate!!.split("/")
            val part1 = parts[0]
            val part2 = parts[1]
            val part3 = parts[2]
            mYear = part3.toInt()
            mMonth = part1.toInt() - 1
            mDay = part2.toInt()
        }


        val datePickerDialog = DatePickerDialog(
            this, android.R.style.Theme_Holo_Dialog, object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(
                    view: DatePicker?, year: Int,
                    monthOfYear: Int, dayOfMonth: Int
                ) {
                    var month = (monthOfYear + 1).toString()
                    var day = dayOfMonth.toString()
                    if (month.length == 1) {
                        month = "0" + month
                    }
                    if (day.length == 1) {
                        day = "0" + day
                    }
                    binding.edittextDob.setText(month + "/" + day + "/" + year)
                    Log.e(
                        "edittextDob",
                        (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
                    )
                }
            }, mYear!!, mMonth!!, mDay!!
        )

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000)
        datePickerDialog.getDatePicker().setLayoutMode(1)
        datePickerDialog.show()

    }

    private fun googleMap() {
        val str = binding.edittextAddress.text
        if (str!!.contains("LandMark")) {
            val separated: List<String> = str.split(":")
            Log.e("LandMark", separated[1])
            intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("LandMark", separated[1])
            startActivity(intent)
        } else {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun handleEvent(event: String) {
        binding.edittextAddress.setText(event)
        Log.e("fileUri", fileUri.toString())
        //binding.layoutView.userProfileImage.setImageURI(fileUri)
        Glide.with(this).load(File(fileUri!!.path))
            //.diskCacheStrategy(DiskCacheStrategy.NONE)
            //.skipMemoryCache(true)
            .placeholder(R.drawable.ic_profile_no_image)
            .error(R.drawable.ic_profile_no_image)
            .into(binding.layoutView.userProfileImage)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun handleEvent(event: UploadImageKey) {
        if (event.imageID!!.contains("Book_")) {
            Log.e("event->", event.imageID.toString())
            profileImage = event.imageID
        } else {
            profileImage = event.imageID
            mPrefs!!.profile = event.imageID
            uploadFileImage(event.imageID)
        }


        //newly hided 20-01-2022
        /* if (mPrefs!!.s3BuckeoldprofileImg.equals("")) {
             mPrefs!!.s3BuckeoldprofileImg = profileImage
         } else {
            // https://dev-lifehope.s3.us-west-1.amazonaws.com/ProfilePic/01027139-66f0-4da7-8d55-cca7be143def/01027139-66f0-4da7-8d55-cca7be143def_1639753351.jpg
           // val filePath="https://dev-lifehope.s3.us-west-1.amazonaws.com/ProfilePic/"+ mPrefs!!.userID + "/" + mPrefs!!.s3BuckeoldprofileImg

             val filePath="https://dev-lifehope.s3.us-west-1.amazonaws.com/ProfilePic/0425eb71-30ef-4520-b790-8d416e23f81f/0425eb71-30ef-4520-b790-8d416e23f81f_1641986834567.jpg"
             Utils.deleteAWSObject(
                 "ProfilePic/" + mPrefs!!.userID + "/" + mPrefs!!.s3BuckeoldprofileImg,
                 mPrefs!!.s3BucketName,
                 Utils.decryption(mPrefs!!.w3SecretKey),
                 Utils.decryption(mPrefs!!.w3AccessKey),
                 mPrefs!!.s3BucketRegion,this
             )



         }*/
    }

    private fun uploadFileImage(imageKey: String?) {
        if (Utils.isNetworkAvailable(this)) {
            val data = UploadImageKey()
            data.imageID = imageKey

            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(data)
            Log.e("Model---->", json.toString())

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(json) as JsonObject


            viewModel.uploadImage(token!!, aJsonObject)

            viewModel.updateImageResponse.observe(this,
                androidx.lifecycle.Observer {
                    when (it) {
                        is Resource.Success -> {
                            if (progressDialog != null && progressDialog!!.isShowing) {
                                progressDialog!!.dismiss()
                            }
                            Log.e("uploadAPI->>>", it.value.toString())
                            binding.layoutView.userProfileImage.setImageURI(fileUri)


                            //newly added 20-01-2022

                            Utils.deleteAWSObject(
                                "ProfilePic/" + mPrefs!!.userID + "/" + mPrefs!!.s3BuckeoldprofileImg,
                                mPrefs!!.s3BucketName,
                                Utils.decryption(mPrefs!!.w3SecretKey),
                                Utils.decryption(mPrefs!!.w3AccessKey),
                                mPrefs!!.s3BucketRegion, this, it.value.getValue()!!.data.toString()
                            )


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
            Utils.noInternetAlert(
                resources.getString(R.string.no_internet_msg),
                this
            )
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun handleEventStatus(event: OTPModel) {
        if (event.checkOTP == 1) {
            mobileOTPStatus = 1
            binding.imageviewOtpVerification.setImageDrawable(resources.getDrawable(R.drawable.ic_validated))
        } else {
            mobileOTPStatus = 0
            binding.imageviewOtpVerification.setImageDrawable(resources.getDrawable(R.drawable.ic_invalidated))
        }
    }

    fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: LatLng? = null
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location = address[0]
            p1 = LatLng(location.latitude, location.longitude)
            mPrefs!!.addresslatlng =
                location.latitude.toString() + "," + location.longitude.toString()
            Log.e("addresslatlng", mPrefs!!.addresslatlng.toString())
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return p1
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: DownloadImageViewModel) {
        Log.e("ProfilePic->", event.mUrl.toString())
        val names = event.mUrl
        val namesList = names!!.split("/").toTypedArray()
        val result = namesList[5]
        val namesLists = result.split(".jpg").toTypedArray()
        Log.e("omg-->", namesLists[0] + " " + namesLists[1])
        Log.e(
            "split-->",
            namesList[5]
        )
        Glide.with(this).load(event.mUrl)
            //.diskCacheStrategy(DiskCacheStrategy.NONE)
            //.skipMemoryCache(true)
            .placeholder(R.drawable.ic_profile_no_image)
            .error(R.drawable.ic_profile_no_image)
            .into(binding.layoutView.userProfileImage)
        //binding.layoutView.userProfileImage.setImageBitmap(event.bmp)
    }
}