package com.lifehopehealthapp.bulkBooking.organizationDetails

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kofigyan.stateprogressbar.StateProgressBar
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.bulkBooking.orderList.BulkBookingOrderListActivity
import com.lifehopehealthapp.databinding.ActivityBulkbookingBinding
import com.lifehopehealthapp.myProfile.MapsActivity
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.fcmToken
import com.lifehopehealthapp.utils.PreferenceHelper.latlng
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.TextChangedListener
import com.lifehopehealthapp.utils.Utils
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class BulkBookingActivity : BaseActivity<BulkBookingViewModel, BulkBookingModel>(),
    AdapterView.OnItemSelectedListener {

    private var mSelectedOriginazationID: Int = 0
    private var email: String? = ""
    private var contactName: String? = ""
    private var mPrefs: SharedPreferences? = null
    private var token: String? = ""
    private var progressDoalog: Dialog? = null
    private var organizationType: ArrayList<String> = ArrayList()
    private var stageOneAns: ArrayList<StageOne> = ArrayList()
    private var stageTwoAns: ArrayList<StageTwo> = ArrayList()
    private var stageThreeAns: ArrayList<StageThree> = ArrayList()
    private var stageFourAns: ArrayList<StageFour> = ArrayList()
    private var stageFiveAns: ArrayList<StageFive> = ArrayList()
    private var mListData: ArrayList<Organization> = ArrayList()
    private var organizationTypeID: ArrayList<Int> = ArrayList()

    private var mTestType: ArrayList<String> = ArrayList()
    private var mTitle: ArrayList<String> = ArrayList()
    private var mList: ArrayList<OrganizationTitleResponse.OrganizationList> = ArrayList()
    private var mOrganizationID: Int? = 0

    private var TestCategoryId: String? = ""
    private var TestTypeId: String? = ""
    private var locality: String? = ""
    private var mState: String? = ""
    private var latlng: String? = null

    private var mSchoolNameBoolean = false
    private var mSchoolAddressBoolean = false
    private var mContactNameBoolean = false
    private var mMobileBoolean = false
    private var mEmailBoolean = false

    private var mStageOne: Int? = 0
    private var mStageTwo: Int? = 0
    private var mStageThree: Int? = 0
    private var mStageFour: Int? = 0
    private var mStageFive: Int? = 0

    private var spinnerSelectedValue1: Int? = 0
    private var spinnerSelectedValue2: Int? = 0
    private var spinnerSelectedValue3: Int? = 0
    private var spinnerSelectedValue4: Int? = 0

    private var editTextStageTwoFirst: Int? = 0
    private var editTextStageTwoSecond: Int? = 0
    private var editTextStageTwoThird: Int? = 0
    private var spinnerStageTwo: Int? = 0
    private var editTextStageSecondTotalValue: Long? = 0

    private var editTextStageThreeFirst: Int? = 0
    private var editTextStageThreeSecond: Int? = 0
    private var editTextStageThreeThird: Int? = 0
    private var spinnerStageThree: Int? = 0
    private var editTextStageThirdTotalValue: Long? = 0

    private var editTextStageFourFirst: Int? = 0
    private var editTextStageFourSecond: Int? = 0
    private var editTextStageFourThird: Int? = 0
    private var spinnerStageFour: Int? = 0
    private var editTextStageFourthTotalValue: Long? = 0

    private var editTextStageFiveFirst: Int? = 0
    private var editTextStageFiveSecond: Int? = 0
    private var editTextStageFiveThird: Int? = 0
    private var spinnerStageFive: Int? = 0
    private var editTextStageFifthTotalValue: Long? = 0

    private var schoolName: String? = ""
    private var schoolAddress: String? = ""
    private var mobileNumber: String? = ""
    private var NoOfStudent: String? = ""
    private var NoOfTeacher: String? = ""
    private var NoOfCoaches: String? = ""
    private var NoOfDrivers: String? = ""

    private var contractListsString: String? = ""
    private var OrganizationTypeName: String? = ""
    private var tooltipSurface: String? = ""
    private var tooltipTotalAmount: String? = ""
    private lateinit var binding: ActivityBulkbookingBinding
    private var stateNumber: Int? = 1
    private var Payment: ArrayList<Payments> = ArrayList()
    private var mobileResponse: String? = ""
    private var emailResponse: String? = ""

    private var balloon: Balloon? = null
    private var mBalloon: Balloon? = null

    override fun getViewModel() = BulkBookingViewModel::class.java

    override fun getActivityRepository() =
        BulkBookingModel(
            remoteDataSource.buildApi(
                APIManager::class.java
            ), PreferenceHelper
        )

    var descriptionData =
        arrayOf("1", "2", "3", "4")

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBulkbookingBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)

        token = mPrefs!!.authToken
        //binding.stateProgress.setStateDescriptionData(descriptionData)
        binding.layoutBulkBookingFirstPage.isVisible = true

        OrganizationTypeName = intent.getStringExtra("SelectItemName")
        val data = intent.getStringExtra("SelectedID")
        mSelectedOriginazationID = data!!.toInt()

        getAPICall(data.toInt())
        emailAlertPopup(data)
        /*val extras = intent.extras
        if (extras != null) {
            val data = extras.getInt("SelectItem")
            OrganizationTypeName = extras.getString("SelectItemName")

        }*/
        editTextData()
        binding.editTextEmail.setOnClickListener {
            emailAlertPopup(data)
        }
        binding.spinnerPerMonth.setOnItemSelectedListener(this)
        binding.spinnerPerMonth1.setOnItemSelectedListener(this)
        binding.spinnerPerMonth2.setOnItemSelectedListener(this)
        binding.spinnerPerMonth3.setOnItemSelectedListener(this)
        getToolTip()
        binding.imageViewBackArrow.setOnClickListener {
            finish()
        }
        binding.edittextAddress.setOnClickListener {
            googleMap()

        }
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
                        organizationType.add("Select Organization Type")
                        organizationTypeID.add(0)
                        if (it.value.getStatusCode() == 200) {
                            binding.buttonNextPage.isVisible = true
                            binding.buttonPrevious.isVisible = false
                            binding.buttonNext.isVisible = false
                            TestCategoryId =
                                it.value.getValue()!!.data!!.testType!![0].testCategoryId
                            TestTypeId = it.value.getValue()!!.data!!.testType!![0].testTypeId

                            mTestType.add("Select Test Type")
                            for (i in it.value.getValue()!!.data!!.testType!!.indices) {
                                val type: String? =
                                    it.value.getValue()!!.data!!.testType!![i].categoryName
                                mTestType.add(type!!)
                            }

                            val organizationTypeIDadapter = ArrayAdapter(
                                this,
                                android.R.layout.simple_spinner_item,
                                mTestType
                            )
                            organizationTypeIDadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.spinnerChooseTest.setAdapter(organizationTypeIDadapter)
                            binding.spinnerChooseTest.setSelection(1)
                            binding.spinnerChooseTest.isEnabled = false
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

        binding.editTextMobileNo.addTextChangedListener(object : TextWatcher {
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
                val text: String = binding.editTextMobileNo.getText().toString()
                val textLength: Int = binding.editTextMobileNo.getText()!!.length
                if (text.endsWith("-") || text.endsWith(" ") || text.endsWith(" ")) return
                if (textLength == 1) {
                    if (!text.contains("(")) {
                        binding.editTextMobileNo.setText(
                            StringBuilder(text).insert(text.length - 1, "(").toString()
                        )
                        binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                    }
                } else if (textLength == 5) {
                    if (!text.contains(")")) {
                        binding.editTextMobileNo.setText(
                            StringBuilder(text).insert(text.length - 1, ")").toString()
                        )
                        binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                    }
                } else if (textLength == 6) {
                    binding.editTextMobileNo.setText(
                        StringBuilder(text).insert(text.length - 1, " ").toString()
                    )
                    binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                } else if (textLength == 10) {
                    if (!text.contains("-")) {
                        binding.editTextMobileNo.setText(
                            StringBuilder(text).insert(text.length - 1, "-").toString()
                        )
                        binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                    }
                } else if (textLength == 15) {
                    if (text.contains("-")) {
                        binding.editTextMobileNo.setText(
                            StringBuilder(text).insert(text.length - 1, "-").toString()
                        )
                        binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                    }
                } else if (textLength == 18) {
                    if (text.contains("-")) {
                        binding.editTextMobileNo.setText(
                            StringBuilder(text).insert(text.length - 1, "-").toString()
                        )
                        binding.editTextMobileNo.setSelection(binding.editTextMobileNo.getText()!!.length)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })


        binding.buttonNextPage.setOnClickListener {
            var localStageOne: Int? = 0
            if (stateNumber == 1) {
                if (mList.size == 0) {
                    Utils.showToast(this, "Coming Soon", true)
                } else {
                    schoolName = binding.editTextSchoolName.text!!.toString()
                    schoolName = schoolName!!.trimStart()
                    schoolAddress = binding.edittextAddress.text!!.toString()
                    contactName = binding.edittextname.text!!.toString()
                    contactName = contactName!!.trimStart()
                    mobileNumber = binding.editTextMobileNo.text!!.toString()
                    email = binding.editTextEmail.text!!.trim().toString()
                    val title = binding.spinnerTitle.selectedItem.toString()


                    binding.layoutBulkBookingFirstPage.isVisible = true
                    binding.layoutBulkBookingFivethPage.isVisible = false
                    binding.layoutBulkBookingSecondPage.isVisible = false
                    binding.layoutBulkBookingThirdPage.isVisible = false
                    binding.layoutBulkBookingFourthPage.isVisible = false
                    binding.textViewPageCount.text = "1/5"
                    binding.textViewContent.text = mList[0].titleName
                    binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.ONE)
                    binding.stateProgress.enableAnimationToCurrentState(true)


                    stageOneAns.clear()
                    if (!binding.spinnerChooseTest.selectedItem.equals("Select Test Type")) {
                        if (mSchoolNameBoolean && (!schoolName.equals(""))) {
                            localStageOne = 1 + localStageOne!!
                            val req = StageOne()
                            req.id = mList[0].placeHolderList!![0]!!.id.toString()
                            req.value = schoolName
                            req.header = mList[0].placeHolderList!![0]!!.name.toString()
                            stageOneAns.add(req)

                            if (mSchoolAddressBoolean && (!schoolAddress.equals(""))) {
                                localStageOne = 1 + localStageOne!!
                                val req = StageOne()
                                req.id = mList[0].placeHolderList!![1]!!.id.toString()
                                req.value = schoolAddress
                                req.header = mList[0].placeHolderList!![1]!!.name.toString()
                                stageOneAns.add(req)

                                if (mContactNameBoolean && (!contactName.equals(""))) {
                                    localStageOne = 1 + localStageOne!!

                                    val req = StageOne()
                                    req.id = mList[0].placeHolderList!![2]!!.id.toString()
                                    req.value = contactName
                                    req.header = mList[0].placeHolderList!![2]!!.name.toString()
                                    stageOneAns.add(req)

                                    if ((!title.equals("")) && (!title.equals("Select Title"))) {
                                        localStageOne = 1 + localStageOne!!
                                        val req = StageOne()
                                        req.id = mList[0].placeHolderList!![3]!!.id.toString()
                                        req.value = title.toString()
                                        req.header = mList[0].placeHolderList!![3]!!.name.toString()
                                        stageOneAns.add(req)

                                        var mMobile = mobileNumber
                                        mMobile = mMobile!!.replace("[^a-zA-Z0-9]".toRegex(), "")

                                        if (mMobileBoolean && (!mobileNumber.equals("")) && Utils.isValidPhoneNumber(
                                                mMobile!!
                                            )
                                        ) {
                                            localStageOne = 1 + localStageOne!!
                                            val req = StageOne()
                                            req.id = mList[0].placeHolderList!![4]!!.id.toString()
                                            req.value = mobileNumber
                                            req.header =
                                                mList[0].placeHolderList!![4]!!.name.toString()
                                            stageOneAns.add(req)

                                            if (mEmailBoolean && (!email.equals(""))) {
                                                if (Utils.isValidEmail(email!!)) {
                                                    localStageOne = 1 + localStageOne!!
                                                    val req = StageOne()
                                                    req.id =
                                                        mList[0].placeHolderList!![5]!!.id.toString()
                                                    req.value = email
                                                    req.header =
                                                        mList[0].placeHolderList!![5]!!.name.toString()
                                                    stageOneAns.add(req)

                                                    if (mStageOne == localStageOne) {
                                                        if (stateNumber == 5) {
                                                            val intent =
                                                                Intent(
                                                                    this,
                                                                    BulkBookingOrderListActivity::class.java
                                                                )
                                                            startActivity(intent)
                                                            Log.e(
                                                                "stateNumber",
                                                                stateNumber.toString()
                                                            )
                                                        } else {
                                                            binding.buttonNext.isVisible = true
                                                            binding.buttonPrevious.isVisible = true
                                                            binding.buttonNextPage.isVisible = false
                                                            stateNumber = 1 + stateNumber!!
                                                            binding.textViewPageCount.text = "2/5"
                                                            binding.stateProgress.setCurrentStateNumber(
                                                                StateProgressBar.StateNumber.TWO
                                                            )
                                                            binding.stateProgress.enableAnimationToCurrentState(
                                                                true
                                                            )
                                                            binding.layoutBulkBookingFirstPage.isVisible =
                                                                false
                                                            binding.layoutBulkBookingSecondPage.isVisible =
                                                                true
                                                            binding.layoutBulkBookingThirdPage.isVisible =
                                                                false
                                                            binding.layoutBulkBookingFourthPage.isVisible =
                                                                false
                                                            binding.layoutBulkBookingFivethPage.isVisible =
                                                                false
                                                            for (i in mList.indices) {
                                                                mStageTwo =
                                                                    mList[1].placeHolderList!!.size
                                                                binding.textViewContent.text =
                                                                    mList[1].titleName
                                                                if (!mList[1].placeHolderList!![0]!!.name.equals(
                                                                        ""
                                                                    )
                                                                ) {
                                                                    binding.layoutBulkBookingSecondPage
                                                                    binding.layoutNoOfStudent.hint =
                                                                        mList[1].placeHolderList!![0]!!.name
                                                                } else {

                                                                }
                                                                if (!mList[1].placeHolderList!![1]!!.name.equals(
                                                                        ""
                                                                    )
                                                                ) {
                                                                    binding.layoutNoOfClassRoom.hint =
                                                                        mList[1].placeHolderList!![1]!!.name
                                                                } else {

                                                                }
                                                                if (!mList[1].placeHolderList!![2]!!.name.equals(
                                                                        ""
                                                                    )
                                                                ) {
                                                                    binding.layoutSurface.hint =
                                                                        mList[1].placeHolderList!![2]!!.name
                                                                } else {

                                                                }


                                                                if (!mList[1].placeHolderList!![3]!!.name.equals(
                                                                        ""
                                                                    )
                                                                ) {
                                                                    binding.textViewPerMonthLabel.text =
                                                                        mList[1].placeHolderList!![3]!!.name
                                                                    if (spinnerSelectedValue1 == 0) {
                                                                        val adapter1 = ArrayAdapter(
                                                                            this@BulkBookingActivity,
                                                                            android.R.layout.simple_spinner_item,
                                                                            descriptionData
                                                                        )
                                                                        adapter1.setDropDownViewResource(
                                                                            android.R.layout.simple_spinner_dropdown_item
                                                                        )
                                                                        binding.spinnerPerMonth.setAdapter(
                                                                            adapter1
                                                                        )
                                                                    } else {
                                                                        binding.spinnerPerMonth.setSelection(
                                                                            spinnerSelectedValue1!! - 1
                                                                        )
                                                                    }

                                                                } else {

                                                                }
                                                                if (!mList[1].placeHolderList!![4]!!.name.equals(
                                                                        ""
                                                                    )
                                                                ) {
                                                                    binding.layoutMonthlyTestCount.hint =
                                                                        mList[1].placeHolderList!![4]!!.name
                                                                } else {

                                                                }

                                                                if (mList[1].placeHolderList!![2]!!.tooltip.equals(
                                                                        ""
                                                                    )
                                                                ) {
                                                                    binding.imageViewSurfaceValueIcon.isVisible =
                                                                        false
                                                                } else {
                                                                    tooltipSurface =
                                                                        mList[1].placeHolderList!![2]!!.tooltip
                                                                    balloon =
                                                                        tooltipSurface?.let {
                                                                            Balloon.Builder(
                                                                                baseContext
                                                                            )
                                                                                .setArrowSize(10)
                                                                                .setArrowOrientation(
                                                                                    ArrowOrientation.BOTTOM
                                                                                )
                                                                                .setArrowPosition(
                                                                                    0.5f
                                                                                )
                                                                                .setWidthRatio(0.70f)
                                                                                .setMarginLeft(48)
                                                                                .setPadding(10)
                                                                                .setText(it)
                                                                                .setCornerRadius(4f)
                                                                                .setTextColorResource(
                                                                                    R.color.white
                                                                                )
                                                                                .setArrowColorResource(
                                                                                    R.color.purple_500
                                                                                )
                                                                                .setBackgroundDrawableResource(
                                                                                    R.drawable.button_theme_bg
                                                                                )
                                                                                .setBalloonAnimation(
                                                                                    BalloonAnimation.FADE
                                                                                )
                                                                                .build()
                                                                        }
                                                                }

                                                                if (mList[1].placeHolderList!![4]!!.tooltip.equals(
                                                                        ""
                                                                    )
                                                                ) {
                                                                    binding.imageViewTotalValueIcon.isVisible =
                                                                        false
                                                                } else {
                                                                    tooltipTotalAmount =
                                                                        mList[1].placeHolderList!![4]!!.tooltip
                                                                    mBalloon =
                                                                        tooltipTotalAmount?.let {
                                                                            Balloon.Builder(
                                                                                baseContext
                                                                            )
                                                                                .setArrowSize(10)
                                                                                .setArrowOrientation(
                                                                                    ArrowOrientation.TOP
                                                                                )
                                                                                .setArrowPosition(
                                                                                    0.5f
                                                                                )
                                                                                .setWidthRatio(0.70f)
                                                                                .setMarginLeft(48)
                                                                                .setPadding(10)
                                                                                .setText(it)
                                                                                .setCornerRadius(4f)
                                                                                .setTextColorResource(
                                                                                    R.color.white
                                                                                )
                                                                                .setArrowColorResource(
                                                                                    R.color.purple_500
                                                                                )
                                                                                .setBackgroundDrawableResource(
                                                                                    R.drawable.button_theme_bg
                                                                                )
                                                                                .setBalloonAnimation(
                                                                                    BalloonAnimation.FADE
                                                                                )
                                                                                .build()
                                                                        }
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        Utils.showToast(
                                                            this,
                                                            resources.getString(R.string.toast_empty_data),
                                                            true
                                                        )
                                                    }
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
                                                    getString(R.string.text_enter) + mList[0].placeHolderList!![5]!!.name.toString()
                                                        .dropLast(1),
                                                    true
                                                )
                                            }
                                        } else {
                                            Utils.showToast(
                                                this,
                                                getString(R.string.text_enter) + mList[0].placeHolderList!![4]!!.name.toString()
                                                    .dropLast(1),
                                                true
                                            )
                                        }

                                    } else {
                                        Utils.showToast(this, "Select Your Title", true)
                                    }
                                } else {
                                    Utils.showToast(
                                        this,
                                        getString(R.string.text_enter) + mList[0].placeHolderList!![2]!!.name.toString()
                                            .dropLast(1),
                                        true
                                    )
                                }
                            } else {
                                Utils.showToast(
                                    this,
                                    getString(R.string.text_enter) + mList[0].placeHolderList!![1]!!.name.toString()
                                        .dropLast(1),
                                    true
                                )
                            }
                        } else {
                            Utils.showToast(
                                this,
                                getString(R.string.text_enter) + mList[0].placeHolderList!![0]!!.name.toString()
                                    .dropLast(1),
                                true
                            )
                        }
                    } else {
                        Utils.showToast(this, "Select Your Test", true)
                    }

                }

            }
        }

        binding.buttonNext.setOnClickListener {
            val currentTimestamp = System.currentTimeMillis() / 1000
            var localStageOne: Int? = 0
            var localStageTwo: Int? = 0
            var localStageThree: Int? = 0
            var localStageFour: Int? = 0
            var localStageFive: Int? = 0
            val data = BulkBookingOrderAPIRequest()
            if (stateNumber == 1) {
                if (mList.size == 0) {
                    Utils.showToast(this, "Coming Soon", true)
                } else {
                    schoolName = binding.editTextSchoolName.text!!.toString()
                    schoolAddress = binding.edittextAddress.text!!.toString()
                    contactName = binding.edittextname.text!!.toString()
                    mobileNumber = binding.editTextMobileNo.text!!.toString()
                    email = binding.editTextEmail.text!!.trim().toString()
                    val title = binding.spinnerTitle.selectedItem.toString()

                    binding.textViewPageCount.text = "1/5"
                    binding.textViewContent.text = mList[0].titleName
                    binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.ONE)
                    binding.stateProgress.enableAnimationToCurrentState(true)


                    stageOneAns.clear()
                    if (!binding.spinnerChooseTest.selectedItem.equals("Select Test Type")) {
                        if (!title.equals("Select Title")) {
                            if (mSchoolNameBoolean && (!schoolName.equals(""))) {
                                localStageOne = 1 + localStageOne!!
                                val req = StageOne()
                                req.id = mList[0].placeHolderList!![0]!!.id.toString()
                                req.value = schoolName
                                req.header = mList[0].placeHolderList!![0]!!.name.toString()
                                stageOneAns.add(req)

                            } else {

                            }

                            if (mSchoolAddressBoolean && (!schoolAddress.equals(""))) {
                                localStageOne = 1 + localStageOne!!
                                val req = StageOne()
                                req.id = mList[0].placeHolderList!![1]!!.id.toString()
                                req.value = schoolAddress
                                req.header = mList[0].placeHolderList!![1]!!.name.toString()
                                stageOneAns.add(req)
                            } else {

                            }

                            if (mContactNameBoolean && (!contactName.equals(""))) {
                                localStageOne = 1 + localStageOne!!
                                val req = StageOne()
                                req.id = mList[0].placeHolderList!![2]!!.id.toString()
                                req.value = contactName
                                req.header = mList[0].placeHolderList!![2]!!.name.toString()
                                stageOneAns.add(req)
                            } else {

                            }

                            if (!title.equals("")) {
                                localStageOne = 1 + localStageOne!!
                                val req = StageOne()
                                req.id = mList[0].placeHolderList!![3]!!.id.toString()
                                req.value = title.toString()
                                req.header = mList[0].placeHolderList!![3]!!.name.toString()
                                stageOneAns.add(req)
                            } else {

                            }

                            if (mMobileBoolean && (!mobileNumber.equals("")) && Utils.isValidPhoneNumber(
                                    mobileNumber!!
                                )
                            ) {
                                localStageOne = 1 + localStageOne!!
                                val req = StageOne()
                                req.id = mList[0].placeHolderList!![4]!!.id.toString()
                                req.value = mobileNumber
                                req.header = mList[0].placeHolderList!![4]!!.name.toString()
                                stageOneAns.add(req)
                            } else {

                            }

                            if (mEmailBoolean && (!email.equals("")) && Utils.isValidEmail(email!!)) {
                                localStageOne = 1 + localStageOne!!
                                val req = StageOne()
                                req.id = mList[0].placeHolderList!![5]!!.id.toString()
                                req.value = email
                                req.header = mList[0].placeHolderList!![5]!!.name.toString()
                                stageOneAns.add(req)
                            } else {

                            }

                            if (mStageOne == localStageOne) {
                                if (stateNumber == 5) {
                                    val intent =
                                        Intent(this, BulkBookingOrderListActivity::class.java)
                                    startActivity(intent)
                                    Log.e("stateNumber", stateNumber.toString())
                                } else {
                                    stateNumber = 1 + stateNumber!!
                                    binding.textViewPageCount.text = "2/5"
                                    binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.TWO)
                                    binding.stateProgress.enableAnimationToCurrentState(true)
                                    binding.layoutBulkBookingSecondPage.isVisible = true
                                    binding.layoutBulkBookingFirstPage.isVisible = false
                                    binding.layoutBulkBookingThirdPage.isVisible = false
                                    binding.layoutBulkBookingFourthPage.isVisible = false
                                    binding.layoutBulkBookingFivethPage.isVisible = false
                                    for (i in mList.indices) {
                                        mStageTwo = mList[1].placeHolderList!!.size
                                        binding.textViewContent.text = mList[1].titleName
                                        if (!mList[1].placeHolderList!![0]!!.name.equals("")) {
                                            binding.layoutBulkBookingSecondPage
                                            binding.layoutNoOfStudent.hint =
                                                mList[1].placeHolderList!![0]!!.name
                                        } else {

                                        }
                                        if (!mList[1].placeHolderList!![1]!!.name.equals("")) {
                                            binding.layoutNoOfClassRoom.hint =
                                                mList[1].placeHolderList!![1]!!.name
                                        } else {

                                        }
                                        if (!mList[1].placeHolderList!![2]!!.name.equals("")) {
                                            binding.layoutSurface.hint =
                                                mList[1].placeHolderList!![2]!!.name
                                        } else {

                                        }


                                        if (!mList[1].placeHolderList!![3]!!.name.equals("")) {
                                            binding.textViewPerMonthLabel.text =
                                                mList[1].placeHolderList!![3]!!.name
                                            if (spinnerSelectedValue1 == 0) {
                                                val adapter1 = ArrayAdapter(
                                                    this@BulkBookingActivity,
                                                    android.R.layout.simple_spinner_item,
                                                    descriptionData
                                                )
                                                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                                binding.spinnerPerMonth.setAdapter(adapter1)
                                            } else {
                                                binding.spinnerPerMonth.setSelection(
                                                    spinnerSelectedValue1!! - 1
                                                )
                                            }


                                        } else {

                                        }
                                        if (!mList[1].placeHolderList!![4]!!.name.equals("")) {
                                            binding.layoutMonthlyTestCount.hint =
                                                mList[1].placeHolderList!![4]!!.name
                                        } else {

                                        }

                                        if (!mList[1].placeHolderList!![4]!!.tooltip.equals("")) {
                                            tooltipTotalAmount =
                                                mList[1].placeHolderList!![2]!!.tooltip
                                            balloon = tooltipTotalAmount?.let {
                                                Balloon.Builder(baseContext)
                                                    .setArrowSize(10)
                                                    .setArrowOrientation(ArrowOrientation.BOTTOM)
                                                    .setArrowPosition(0.5f)
                                                    .setWidthRatio(0.70f)
                                                    .setMarginLeft(48)
                                                    .setPadding(10)
                                                    .setText(it)
                                                    .setCornerRadius(4f)
                                                    .setTextColorResource(
                                                        R.color.white
                                                    )
                                                    .setArrowColorResource(R.color.purple_500)
                                                    .setBackgroundDrawableResource(
                                                        R.drawable.button_theme_bg
                                                    )
                                                    .setBalloonAnimation(BalloonAnimation.FADE)
                                                    .build()
                                            }
                                        }
                                        tooltipSurface = mList[1].placeHolderList!![2]!!.tooltip
                                        mBalloon =
                                            tooltipSurface?.let {
                                                Balloon.Builder(baseContext)
                                                    .setArrowSize(10)
                                                    .setArrowOrientation(ArrowOrientation.TOP)
                                                    .setArrowPosition(0.5f)
                                                    .setWidthRatio(0.70f)
                                                    .setMarginLeft(48)
                                                    .setPadding(10)
                                                    .setText(it)
                                                    .setArrowColorResource(R.color.purple_500)
                                                    .setCornerRadius(4f)
                                                    .setTextColorResource(R.color.white)
                                                    .setBackgroundDrawableResource(R.drawable.button_theme_bg)
                                                    .setBalloonAnimation(BalloonAnimation.FADE)
                                                    .build()
                                            }
                                    }
                                }
                            } else {
                                Utils.showToast(
                                    this,
                                    resources.getString(R.string.toast_empty_data),
                                    true
                                )
                            }


                        } else {
                            Utils.showToast(this, "Select Your Title", true)
                        }

                    } else {
                        Utils.showToast(this, "Select Your Test", true)
                    }

                }

            }

            if (stateNumber == 2) {

                NoOfStudent = binding.editTextNoOfStudent.text.toString()
                var classroomCount = binding.editTextNoOfClassRoom.text.toString()
                var surfaceperClassroom = binding.editTextSurface.text.toString()
                var monthlyTestCount = binding.editTextMonthlyTestCount.text.toString()
                var monthFrequency = binding.spinnerPerMonth.selectedItem.toString()
                spinnerSelectedValue1 = monthFrequency.toInt()

                stageTwoAns.clear()
                if (!NoOfStudent.equals("")) {
                    localStageTwo = 1 + localStageTwo!!
                    val req = StageTwo()
                    req.id = mList[1].placeHolderList!![0]!!.id.toString()
                    req.value = NoOfStudent
                    req.header = mList[1].placeHolderList!![0]!!.name.toString()
                    stageTwoAns.add(req)

                    if (!classroomCount.equals("")) {
                        localStageTwo = 1 + localStageTwo!!
                        val req = StageTwo()
                        req.id = mList[1].placeHolderList!![1]!!.id.toString()
                        req.value = classroomCount
                        req.header = mList[1].placeHolderList!![1]!!.name.toString()
                        stageTwoAns.add(req)

                        if (!surfaceperClassroom.equals("")) {
                            localStageTwo = 1 + localStageTwo!!
                            val req = StageTwo()
                            req.id = mList[1].placeHolderList!![2]!!.id.toString()
                            req.value = surfaceperClassroom
                            req.header = mList[1].placeHolderList!![2]!!.name.toString()
                            stageTwoAns.add(req)

                            if (!monthFrequency.equals("")) {
                                localStageTwo = 1 + localStageTwo!!
                                val req = StageTwo()
                                req.id = mList[1].placeHolderList!![3]!!.id.toString()
                                req.value = monthFrequency
                                req.header = mList[1].placeHolderList!![3]!!.name.toString()
                                stageTwoAns.add(req)

                                if (!monthlyTestCount.equals("")) {
                                    localStageTwo = 1 + localStageTwo!!
                                    val req = StageTwo()
                                    req.id = mList[1].placeHolderList!![4]!!.id.toString()
                                    req.value = monthlyTestCount
                                    req.header = mList[1].placeHolderList!![4]!!.name.toString()
                                    stageTwoAns.add(req)

                                    if (stateNumber == 5) {
                                        val intent =
                                            Intent(this, BulkBookingOrderListActivity::class.java)
                                        startActivity(intent)
                                        Log.e("stateNumber", stateNumber.toString())
                                    } else {
                                        binding.textViewPageCount.text = "3/5"
                                        mStageThree = mList[2].placeHolderList!!.size
                                        for (i in mList.indices) {
                                            binding.textViewContent.text = mList[2].titleName
                                            if (!mList[2].placeHolderList!![0]!!.name.equals("")) {
                                                binding.layoutNoOfTeacher.hint =
                                                    mList[2].placeHolderList!![0]!!.name
                                            } else {

                                            }
                                            if (!mList[2].placeHolderList!![1]!!.name.equals("")) {
                                                binding.layoutNoOfOffice.hint =
                                                    mList[2].placeHolderList!![1]!!.name
                                            } else {

                                            }
                                            if (!mList[2].placeHolderList!![2]!!.name.equals("")) {
                                                binding.layoutSurfaceRoomTest.hint =
                                                    mList[2].placeHolderList!![2]!!.name
                                            } else {

                                            }
                                            if (!mList[2].placeHolderList!![3]!!.name.equals("")) {
                                                binding.textViewPerMonthLabel1.text =
                                                    mList[2].placeHolderList!![3]!!.name

                                                if (spinnerSelectedValue2 == 0) {
                                                    val adapter1 = ArrayAdapter(
                                                        this@BulkBookingActivity,
                                                        android.R.layout.simple_spinner_item,
                                                        descriptionData
                                                    )
                                                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                                    binding.spinnerPerMonth1.setAdapter(adapter1)
                                                } else {
                                                    binding.spinnerPerMonth1.setSelection(
                                                        spinnerSelectedValue2!! - 1
                                                    )
                                                }
                                            } else {

                                            }
                                            if (!mList[2].placeHolderList!![4]!!.name.equals("")) {
                                                binding.layoutTotalNoOfTest.hint =
                                                    mList[2].placeHolderList!![4]!!.name
                                            } else {

                                            }

                                            if (mList[2].placeHolderList!![2]!!.tooltip.equals(
                                                    ""
                                                )
                                            ) {
                                                binding.imageViewSurfaceValueIcon1.isVisible =
                                                    false
                                            } else {
                                                tooltipSurface =
                                                    mList[2].placeHolderList!![2]!!.tooltip
                                                balloon =
                                                    tooltipSurface?.let {
                                                        Balloon.Builder(
                                                            baseContext
                                                        )
                                                            .setArrowSize(10)
                                                            .setArrowOrientation(
                                                                ArrowOrientation.BOTTOM
                                                            )
                                                            .setArrowPosition(
                                                                0.5f
                                                            )
                                                            .setWidthRatio(0.70f)
                                                            .setMarginLeft(48)
                                                            .setPadding(10)
                                                            .setText(it)
                                                            .setCornerRadius(4f)
                                                            .setArrowColorResource(R.color.purple_500)
                                                            .setTextColorResource(R.color.white)
                                                            .setBackgroundDrawableResource(R.drawable.button_theme_bg)
                                                            .setBalloonAnimation(
                                                                BalloonAnimation.FADE
                                                            )
                                                            .build()
                                                    }
                                            }

                                            if (mList[2].placeHolderList!![4]!!.tooltip.equals(
                                                    ""
                                                )
                                            ) {
                                                binding.imageViewTotalValueIcon1.isVisible =
                                                    false
                                            } else {
                                                tooltipTotalAmount =
                                                    mList[2].placeHolderList!![4]!!.tooltip
                                                mBalloon =
                                                    tooltipTotalAmount?.let {
                                                        Balloon.Builder(
                                                            baseContext
                                                        )
                                                            .setArrowSize(10)
                                                            .setArrowOrientation(
                                                                ArrowOrientation.TOP
                                                            )
                                                            .setArrowPosition(
                                                                0.5f
                                                            )
                                                            .setWidthRatio(0.70f)
                                                            .setMarginLeft(48)
                                                            .setPadding(10)
                                                            .setText(it)
                                                            .setCornerRadius(4f)
                                                            .setArrowColorResource(R.color.purple_500)
                                                            .setTextColorResource(R.color.white)
                                                            .setBackgroundDrawableResource(R.drawable.button_theme_bg)
                                                            .setBalloonAnimation(
                                                                BalloonAnimation.FADE
                                                            )
                                                            .build()
                                                    }
                                            }

                                        }
                                        binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.THREE)
                                        binding.stateProgress.enableAnimationToCurrentState(true)
                                        binding.layoutBulkBookingFirstPage.isVisible = false
                                        binding.layoutBulkBookingSecondPage.isVisible = false
                                        binding.layoutBulkBookingThirdPage.isVisible = true
                                        binding.layoutBulkBookingFourthPage.isVisible = false
                                        binding.layoutBulkBookingFivethPage.isVisible = false
                                        binding.buttonNext.isVisible = true
                                        binding.buttonPrevious.isVisible = true
                                        binding.buttonNextPage.isVisible = false
                                        stateNumber = 1 + stateNumber!!
                                    }
                                } else {
                                    Utils.showToast(
                                        this,
                                        getString(R.string.text_enter) + mList[1].placeHolderList!![4]!!.name.toString()
                                            .dropLast(1),
                                        true
                                    )
                                }
                            } else {
                                Utils.showToast(
                                    this,
                                    getString(R.string.text_enter) + mList[1].placeHolderList!![3]!!.name.toString()
                                        .dropLast(1),
                                    true
                                )
                            }
                        } else {
                            Utils.showToast(
                                this,
                                getString(R.string.text_enter) + mList[1].placeHolderList!![2]!!.name.toString()
                                    .dropLast(1),
                                true
                            )
                        }
                    } else {
                        Utils.showToast(
                            this,
                            getString(R.string.text_enter) + mList[1].placeHolderList!![1]!!.name.toString()
                                .dropLast(1),
                            true
                        )
                    }
                } else {
                    Utils.showToast(
                        this,
                        getString(R.string.text_enter) + mList[1].placeHolderList!![0]!!.name.toString()
                            .dropLast(1),
                        true
                    )
                }

            } else if (stateNumber == 3) {


                NoOfTeacher = binding.editTextNoOfTeacher.text.toString()
                var officeCount = binding.editTextNoOfOffice.text.toString()
                var surface = binding.editTextSurfaceRoomTest.text.toString()
                var monthFrequency = binding.spinnerPerMonth1.selectedItem.toString()
                var monthlyTestCount = binding.editTextTotalNoOfTest.text.toString()
                spinnerSelectedValue2 = monthFrequency.toInt()

                stageThreeAns.clear()
                if (!NoOfTeacher.equals("")) {
                    localStageThree = 1 + localStageThree!!
                    val req = StageThree()
                    req.id = mList[2].placeHolderList!![0]!!.id.toString()
                    req.value = NoOfTeacher
                    req.header = mList[2].placeHolderList!![0]!!.name.toString()
                    stageThreeAns.add(req)

                    if (!officeCount.equals("")) {
                        localStageThree = 1 + localStageThree!!
                        val req = StageThree()
                        req.id = mList[2].placeHolderList!![1]!!.id.toString()
                        req.value = officeCount
                        req.header = mList[2].placeHolderList!![1]!!.name.toString()
                        stageThreeAns.add(req)

                        if (!surface.equals("")) {
                            localStageThree = 1 + localStageThree!!
                            val req = StageThree()
                            req.id = mList[2].placeHolderList!![2]!!.id.toString()
                            req.value = surface
                            req.header = mList[2].placeHolderList!![2]!!.name.toString()
                            stageThreeAns.add(req)

                            if (!monthFrequency.equals("")) {
                                localStageThree = 1 + localStageThree!!
                                val req = StageThree()
                                req.id = mList[2].placeHolderList!![3]!!.id.toString()
                                req.value = monthFrequency
                                req.header = mList[2].placeHolderList!![3]!!.name.toString()
                                stageThreeAns.add(req)

                                if (!monthlyTestCount.equals("")) {
                                    localStageThree = 1 + localStageThree!!
                                    val req = StageThree()
                                    req.id = mList[2].placeHolderList!![4]!!.id.toString()
                                    req.value = monthlyTestCount
                                    req.header = mList[2].placeHolderList!![4]!!.name.toString()
                                    stageThreeAns.add(req)

                                    if (stateNumber == 5) {
                                        val intent =
                                            Intent(this, BulkBookingOrderListActivity::class.java)
                                        startActivity(intent)
                                        Log.e("stateNumber", stateNumber.toString())
                                    } else {
                                        binding.textViewPageCount.text = "4/5"
                                        mStageFour = mList[3].placeHolderList!!.size
                                        for (i in mList.indices) {
                                            binding.textViewContent.text = mList[3].titleName
                                            if (!mList[3].placeHolderList!![0]!!.name.equals("")) {
                                                binding.layoutNoOfCoaches.hint =
                                                    mList[3].placeHolderList!![0]!!.name
                                            } else {

                                            }
                                            if (!mList[3].placeHolderList!![1]!!.name.equals("")) {
                                                binding.layoutNoOfOffices.hint =
                                                    mList[3].placeHolderList!![1]!!.name
                                            } else {

                                            }
                                            if (!mList[3].placeHolderList!![2]!!.name.equals("")) {
                                                binding.layoutSurfaceRoomTest1.hint =
                                                    mList[3].placeHolderList!![2]!!.name
                                            } else {

                                            }
                                            if (!mList[3].placeHolderList!![3]!!.name.equals("")) {
                                                binding.textViewPerMonthLabel2.text =
                                                    mList[3].placeHolderList!![3]!!.name

                                                if (spinnerSelectedValue3 == 0) {
                                                    val adapter1 = ArrayAdapter(
                                                        this@BulkBookingActivity,
                                                        android.R.layout.simple_spinner_item,
                                                        descriptionData
                                                    )
                                                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                                    binding.spinnerPerMonth2.setAdapter(adapter1)

                                                } else {
                                                    binding.spinnerPerMonth2.setSelection(
                                                        spinnerSelectedValue3!! - 1
                                                    )
                                                }
                                            } else {

                                            }
                                            if (!mList[3].placeHolderList!![4]!!.name.equals("")) {
                                                binding.layoutTotalNoOfTest1.hint =
                                                    mList[3].placeHolderList!![4]!!.name
                                            } else {

                                            }

                                            if (mList[3].placeHolderList!![2]!!.tooltip.equals(
                                                    ""
                                                )
                                            ) {
                                                binding.imageViewSurfaceValueIcon2.isVisible =
                                                    false
                                            } else {
                                                tooltipSurface =
                                                    mList[3].placeHolderList!![2]!!.tooltip
                                                balloon =
                                                    tooltipSurface?.let {
                                                        Balloon.Builder(
                                                            baseContext
                                                        )
                                                            .setArrowSize(10)
                                                            .setArrowOrientation(
                                                                ArrowOrientation.BOTTOM
                                                            )
                                                            .setArrowPosition(
                                                                0.5f
                                                            )
                                                            .setWidthRatio(0.70f)
                                                            .setMarginLeft(48)
                                                            .setPadding(10)
                                                            .setText(it)
                                                            .setCornerRadius(4f)
                                                            .setArrowColorResource(R.color.purple_500)
                                                            .setTextColorResource(R.color.white)
                                                            .setBackgroundDrawableResource(R.drawable.button_theme_bg)
                                                            .setBalloonAnimation(
                                                                BalloonAnimation.FADE
                                                            )
                                                            .build()
                                                    }
                                            }

                                            if (mList[3].placeHolderList!![4]!!.tooltip.equals(
                                                    ""
                                                )
                                            ) {
                                                binding.imageViewTotalValueIcon2.isVisible =
                                                    false
                                            } else {
                                                tooltipTotalAmount =
                                                    mList[3].placeHolderList!![4]!!.tooltip
                                                mBalloon =
                                                    tooltipTotalAmount?.let {
                                                        Balloon.Builder(
                                                            baseContext
                                                        )
                                                            .setArrowSize(10)
                                                            .setArrowOrientation(
                                                                ArrowOrientation.TOP
                                                            )
                                                            .setArrowPosition(
                                                                0.5f
                                                            )
                                                            .setWidthRatio(0.70f)
                                                            .setMarginLeft(48)
                                                            .setPadding(10)
                                                            .setText(it)
                                                            .setCornerRadius(4f)
                                                            .setTextColorResource(R.color.white)
                                                            .setArrowColorResource(R.color.purple_500)
                                                            .setBackgroundDrawableResource(R.drawable.button_theme_bg)
                                                            .setBalloonAnimation(
                                                                BalloonAnimation.FADE
                                                            )
                                                            .build()
                                                    }
                                            }
                                        }
                                        binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR)
                                        binding.stateProgress.enableAnimationToCurrentState(true)
                                        binding.layoutBulkBookingFirstPage.isVisible = false
                                        binding.layoutBulkBookingSecondPage.isVisible = false
                                        binding.layoutBulkBookingThirdPage.isVisible = false
                                        binding.layoutBulkBookingFourthPage.isVisible = true
                                        binding.layoutBulkBookingFivethPage.isVisible = false
                                        binding.buttonNext.isVisible = true
                                        binding.buttonPrevious.isVisible = true
                                        binding.buttonNextPage.isVisible = false
                                        stateNumber = 1 + stateNumber!!
                                    }
                                } else {
                                    Utils.showToast(
                                        this,
                                        getString(R.string.text_enter) + mList[2].placeHolderList!![4]!!.name.toString()
                                            .dropLast(1),
                                        true
                                    )
                                }
                            } else {
                                Utils.showToast(
                                    this,
                                    getString(R.string.text_enter) + mList[2].placeHolderList!![3]!!.name.toString()
                                        .dropLast(1),
                                    true
                                )
                            }
                        } else {
                            Utils.showToast(
                                this,
                                getString(R.string.text_enter) + mList[2].placeHolderList!![2]!!.name.toString()
                                    .dropLast(1),
                                true
                            )
                        }
                    } else {
                        Utils.showToast(
                            this,
                            getString(R.string.text_enter) + mList[2].placeHolderList!![1]!!.name.toString()
                                .dropLast(1),
                            true
                        )
                    }
                } else {
                    Utils.showToast(
                        this,
                        getString(R.string.text_enter) + mList[2].placeHolderList!![0]!!.name.toString()
                            .dropLast(1),
                        true
                    )
                }

            } else if (stateNumber == 4) {

                NoOfCoaches = binding.editTextNoOfCoaches.text.toString()
                var busCount = binding.editTextNoOfOffices.text.toString()
                var surface = binding.editTextSurfaceRoomTest1.text.toString()
                var monthFrequency = binding.spinnerPerMonth2.selectedItem.toString()
                var monthlyTest = binding.editTextTotalNoOfTest1.text.toString()

                spinnerSelectedValue3 = monthFrequency.toInt()

                stageFourAns.clear()
                if (!NoOfCoaches.equals("")) {
                    localStageFour = 1 + localStageFour!!
                    val req = StageFour()
                    req.id = mList[3].placeHolderList!![0]!!.id.toString()
                    req.value = NoOfCoaches
                    req.header = mList[3].placeHolderList!![0]!!.name.toString()
                    stageFourAns.add(req)

                    if (!busCount.equals("")) {
                        localStageFour = 1 + localStageFour!!
                        val req = StageFour()
                        req.id = mList[3].placeHolderList!![1]!!.id.toString()
                        req.value = busCount
                        req.header = mList[3].placeHolderList!![1]!!.name.toString()
                        stageFourAns.add(req)

                        if (!surface.equals("")) {
                            localStageFour = 1 + localStageFour!!
                            val req = StageFour()
                            req.id = mList[3].placeHolderList!![2]!!.id.toString()
                            req.value = surface
                            req.header = mList[3].placeHolderList!![2]!!.name.toString()
                            stageFourAns.add(req)

                            if (!monthFrequency.equals("")) {
                                localStageFour = 1 + localStageFour!!
                                val req = StageFour()
                                req.id = mList[3].placeHolderList!![3]!!.id.toString()
                                req.value = monthFrequency
                                req.header = mList[3].placeHolderList!![3]!!.name.toString()
                                stageFourAns.add(req)

                                if (!monthlyTest.equals("")) {
                                    localStageFour = 1 + localStageFour!!
                                    val req = StageFour()
                                    req.id = mList[3].placeHolderList!![4]!!.id.toString()
                                    req.value = monthlyTest
                                    req.header = mList[3].placeHolderList!![4]!!.name.toString()
                                    stageFourAns.add(req)

                                    if (stateNumber == 5) {
                                        val intent =
                                            Intent(this, BulkBookingOrderListActivity::class.java)
                                        startActivity(intent)
                                        Log.e("stateNumber", stateNumber.toString())
                                    } else {
                                        binding.textViewPageCount.text = "5/5"
                                        mStageFive = mList[4].placeHolderList!!.size
                                        for (i in mList.indices) {
                                            binding.textViewContent.text = mList[4].titleName
                                            if (!mList[4].placeHolderList!![0]!!.name.equals("")) {
                                                binding.layoutOne.hint =
                                                    mList[4].placeHolderList!![0]!!.name
                                            } else {

                                            }
                                            if (!mList[4].placeHolderList!![1]!!.name.equals("")) {
                                                binding.layoutTwo.hint =
                                                    mList[4].placeHolderList!![1]!!.name
                                            } else {

                                            }
                                            if (!mList[4].placeHolderList!![2]!!.name.equals("")) {
                                                binding.layoutThree.hint =
                                                    mList[4].placeHolderList!![2]!!.name
                                            } else {

                                            }
                                            if (!mList[4].placeHolderList!![3]!!.name.equals("")) {
                                                binding.textViewPerMonthLabel3.text =
                                                    mList[4].placeHolderList!![3]!!.name
                                                binding.textViewPerMonthLabel2.text =
                                                    mList[3].placeHolderList!![3]!!.name

                                                if (spinnerSelectedValue4 == 0) {
                                                    val adapter1 = ArrayAdapter(
                                                        this@BulkBookingActivity,
                                                        android.R.layout.simple_spinner_item,
                                                        descriptionData
                                                    )
                                                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                                    binding.spinnerPerMonth3.setAdapter(adapter1)
                                                } else {
                                                    binding.spinnerPerMonth3.setSelection(
                                                        spinnerSelectedValue4!! - 1
                                                    )
                                                }

                                            } else {

                                            }
                                            if (!mList[4].placeHolderList!![4]!!.name.equals("")) {
                                                binding.layoutFourth.hint =
                                                    mList[4].placeHolderList!![4]!!.name
                                            } else {

                                            }

                                            if (mList[4].placeHolderList!![2]!!.tooltip.equals(
                                                    ""
                                                )
                                            ) {
                                                binding.imageViewSurfaceValueIcon3.isVisible =
                                                    false
                                            } else {
                                                tooltipSurface =
                                                    mList[4].placeHolderList!![2]!!.tooltip
                                                balloon =
                                                    tooltipSurface?.let {
                                                        Balloon.Builder(
                                                            baseContext
                                                        )
                                                            .setArrowSize(10)
                                                            .setArrowOrientation(
                                                                ArrowOrientation.BOTTOM
                                                            )
                                                            .setArrowPosition(
                                                                0.5f
                                                            )
                                                            .setWidthRatio(0.70f)
                                                            .setMarginLeft(48)
                                                            .setPadding(10)
                                                            .setText(it)
                                                            .setCornerRadius(4f)
                                                            .setTextColorResource(R.color.white)
                                                            .setArrowColorResource(R.color.purple_500)
                                                            .setBackgroundDrawableResource(R.drawable.button_theme_bg)
                                                            .setBalloonAnimation(
                                                                BalloonAnimation.FADE
                                                            )
                                                            .build()
                                                    }
                                            }

                                            if (mList[4].placeHolderList!![4]!!.tooltip.equals(
                                                    ""
                                                )
                                            ) {
                                                binding.imageViewTotalValueIcon3.isVisible =
                                                    false
                                            } else {
                                                tooltipTotalAmount =
                                                    mList[4].placeHolderList!![4]!!.tooltip
                                                mBalloon =
                                                    tooltipTotalAmount?.let {
                                                        Balloon.Builder(
                                                            baseContext
                                                        )
                                                            .setArrowSize(10)
                                                            .setArrowOrientation(
                                                                ArrowOrientation.TOP
                                                            )
                                                            .setArrowPosition(
                                                                0.5f
                                                            )
                                                            .setWidthRatio(0.70f)
                                                            .setMarginLeft(48)
                                                            .setPadding(10)
                                                            .setText(it)
                                                            .setCornerRadius(4f)
                                                            .setTextColorResource(R.color.white)
                                                            .setArrowColorResource(R.color.purple_500)
                                                            .setBackgroundDrawableResource(R.drawable.button_theme_bg)
                                                            .setBalloonAnimation(
                                                                BalloonAnimation.FADE
                                                            )
                                                            .build()
                                                    }
                                            }
                                        }
                                        binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE)
                                        binding.stateProgress.enableAnimationToCurrentState(true)
                                        binding.layoutBulkBookingFirstPage.isVisible = false
                                        binding.layoutBulkBookingSecondPage.isVisible = false
                                        binding.layoutBulkBookingThirdPage.isVisible = false
                                        binding.layoutBulkBookingFourthPage.isVisible = false
                                        binding.layoutBulkBookingFivethPage.isVisible = true
                                        binding.buttonNext.isVisible = true
                                        binding.buttonPrevious.isVisible = true
                                        binding.buttonNextPage.isVisible = false
                                        stateNumber = 1 + stateNumber!!
                                    }
                                } else {
                                    Utils.showToast(
                                        this,
                                        getString(R.string.text_enter) + mList[3].placeHolderList!![4]!!.name.toString()
                                            .dropLast(1),
                                        true
                                    )
                                }
                            } else {
                                Utils.showToast(
                                    this,
                                    getString(R.string.text_enter) + mList[3].placeHolderList!![3]!!.name.toString()
                                        .dropLast(1),
                                    true
                                )
                            }
                        } else {
                            Utils.showToast(
                                this,
                                getString(R.string.text_enter) + mList[3].placeHolderList!![2]!!.name.toString()
                                    .dropLast(1),
                                true
                            )
                        }
                    } else {
                        Utils.showToast(
                            this,
                            getString(R.string.text_enter) + mList[3].placeHolderList!![1]!!.name.toString()
                                .dropLast(1),
                            true
                        )
                    }
                } else {
                    Utils.showToast(
                        this,
                        getString(R.string.text_enter) + mList[3].placeHolderList!![0]!!.name.toString()
                            .dropLast(1),
                        true
                    )
                }

            } else if (stateNumber == 5) {
                NoOfDrivers = binding.editTextOne.text.toString()
                var gymCount = binding.editTextTwo.text.toString()
                var surface = binding.editTextThree.text.toString()
                var monthlyFrequency = binding.spinnerPerMonth3.selectedItem.toString()
                var monthlyCount = binding.editTextFourth.text.toString()

                spinnerSelectedValue4 = monthlyFrequency.toInt()

                stageFiveAns.clear()
                if (!NoOfDrivers.equals("")) {
                    localStageFive = 1 + localStageFive!!
                    val req = StageFive()
                    req.id = mList[4].placeHolderList!![0]!!.id.toString()
                    req.value = NoOfDrivers
                    req.header = mList[4].placeHolderList!![0]!!.name.toString()
                    stageFiveAns.add(req)

                    if (!gymCount.equals("")) {
                        localStageFive = 1 + localStageFive!!
                        val req = StageFive()
                        req.id = mList[4].placeHolderList!![1]!!.id.toString()
                        req.value = gymCount
                        req.header = mList[4].placeHolderList!![1]!!.name.toString()
                        stageFiveAns.add(req)

                        if (!surface.equals("")) {
                            localStageFive = 1 + localStageFive!!
                            val req = StageFive()
                            req.id = mList[4].placeHolderList!![2]!!.id.toString()
                            req.value = surface
                            req.header = mList[4].placeHolderList!![2]!!.name.toString()
                            stageFiveAns.add(req)

                            if (!monthlyFrequency.equals("")) {
                                localStageFive = 1 + localStageFive!!
                                val req = StageFive()
                                req.id = mList[4].placeHolderList!![3]!!.id.toString()
                                req.value = monthlyFrequency
                                req.header = mList[4].placeHolderList!![3]!!.name.toString()
                                stageFiveAns.add(req)

                                if (!monthlyCount.equals("")) {
                                    localStageFive = 1 + localStageFive!!
                                    val req = StageFive()
                                    req.id = mList[4].placeHolderList!![4]!!.id.toString()
                                    req.value = monthlyCount
                                    req.header = mList[4].placeHolderList!![4]!!.name.toString()
                                    stageFiveAns.add(req)

                                    if (stateNumber == 5) {
                                        val req = BulkBookingOrderAPIRequest()
                                        req.setUserFCMToken(mPrefs!!.fcmToken)
                                        req.setTestCategoryId(TestCategoryId)
                                        req.setLocality(locality)
                                        req.setOrderDate(currentTimestamp.toInt())

                                        val payment = Payments()
                                        payment.amount = 28325
                                        payment.paymentName = "Spit Test"
                                        payment.testCount = 1
                                        Payment.add(payment)
                                        req.setPayment(Payment)

                                        req.setPrimaryName(schoolName)
                                        req.setTitle(binding.spinnerChooseTest.selectedItem.toString())
                                        req.setEmail(email)
                                        req.setContactname(contactName)
                                        req.setAddress(schoolAddress)
                                        req.setScheduleDate(0)
                                        req.setState(mState)
                                        req.setTestTypeID(TestTypeId)
                                        req.setPhoneNo(mobileNumber)
                                        req.setImagePath("")
                                        req.setLatLong(latlng)
                                        req.setIosimagepath("")
                                        req.setLocation(locality)
                                        req.setCurrentlatlong(mPrefs!!.latlng)
                                        req.setBookingType(1)

                                        val listData = Organization()
                                        listData.stageOne = stageOneAns
                                        listData.stageTwo = stageTwoAns
                                        listData.stageThree = stageThreeAns
                                        listData.stageFour = stageFourAns
                                        listData.stageFive = stageFiveAns
                                        mListData.add(listData)
                                        req.setOrganizationList(mListData)

                                        val member = Memberes()
                                        req.setMembers(listOf(member))
                                        req.setOrganizationType(OrganizationTypeName)


                                        val gson = Gson()
                                        var json: String? = ""
                                        json = gson.toJson(req)
                                        Log.e("Model---->", json.toString())

                                        val intent =
                                            Intent(this, BulkBookingOrderListActivity::class.java)
                                        val extras = Bundle()

                                        extras.putInt("organizationID", mSelectedOriginazationID)
                                        extras.putString("NoOfStudent", NoOfStudent)
                                        extras.putString("NoOfTeacher", NoOfTeacher)
                                        extras.putString("NoOfCoaches", NoOfCoaches)
                                        extras.putString("NoOfDrivers", NoOfDrivers)
                                        extras.putString("json", json)
                                        extras.putInt("spinnerStageTwo", spinnerStageTwo!!)
                                        extras.putInt("spinnerStageThree", spinnerStageThree!!)
                                        extras.putInt("spinnerStageFour", spinnerStageFour!!)
                                        extras.putInt("spinnerStageFive", spinnerStageFive!!)
                                        extras.putLong(
                                            "editTextStageSecondTotalValue",
                                            editTextStageSecondTotalValue!!
                                        )
                                        extras.putLong(
                                            "editTextStageThirdTotalValue",
                                            editTextStageThirdTotalValue!!
                                        )
                                        extras.putLong(
                                            "editTextStageFourthTotalValue",
                                            editTextStageFourthTotalValue!!
                                        )
                                        extras.putLong(
                                            "editTextStageFifthTotalValue",
                                            editTextStageFifthTotalValue!!
                                        )
                                        extras.putString("contractListsString", contractListsString)
                                        intent.putExtras(extras)
                                        startActivity(intent)
                                        Log.e("stateNumber", stateNumber.toString())
                                    } else {
                                        stateNumber = 1 + stateNumber!!
                                    }

                                } else {
                                    Utils.showToast(
                                        this,
                                        getString(R.string.text_enter) + mList[4].placeHolderList!![4]!!.name.toString()
                                            .dropLast(1),
                                        true
                                    )
                                }
                            } else {
                                Utils.showToast(
                                    this,
                                    getString(R.string.text_enter) + mList[4].placeHolderList!![3]!!.name.toString()
                                        .dropLast(1),
                                    true
                                )
                            }

                        } else {
                            Utils.showToast(
                                this,
                                getString(R.string.text_enter) + mList[4].placeHolderList!![2]!!.name.toString()
                                    .dropLast(1),
                                true
                            )
                        }
                    } else {
                        Utils.showToast(
                            this,
                            getString(R.string.text_enter) + mList[4].placeHolderList!![1]!!.name.toString()
                                .dropLast(1),
                            true
                        )
                    }
                } else {
                    Utils.showToast(
                        this,
                        getString(R.string.text_enter) + mList[4].placeHolderList!![0]!!.name.toString()
                            .dropLast(1),
                        true
                    )
                }
            }

        }
        binding.buttonPrevious.setOnClickListener {

            stateNumber = stateNumber!! - 1
            Log.e("stateNumber", stateNumber.toString())
            if (stateNumber == 2) {
                binding.textViewPageCount.text = "2/5"
                binding.textViewContent.text =
                    mList[1].titleName
                binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.TWO)
                binding.stateProgress.enableAnimationToCurrentState(true)
                binding.layoutBulkBookingFirstPage.isVisible = false
                binding.layoutBulkBookingSecondPage.isVisible = true
                binding.layoutBulkBookingThirdPage.isVisible = false
                binding.layoutBulkBookingFourthPage.isVisible = false
                binding.layoutBulkBookingFivethPage.isVisible = false
                tooltipTotalAmount =
                    mList[1].placeHolderList!![4]!!.tooltip
                tooltipSurface =
                    mList[1].placeHolderList!![2]!!.tooltip
                balloon =
                    tooltipSurface?.let {
                        Balloon.Builder(
                            baseContext
                        )
                            .setArrowSize(10)
                            .setArrowOrientation(
                                ArrowOrientation.BOTTOM
                            )
                            .setArrowPosition(
                                0.5f
                            )
                            .setWidthRatio(0.70f)
                            .setMarginLeft(48)
                            .setPadding(10)
                            .setText(it)
                            .setCornerRadius(4f)
                            .setTextColorResource(
                                R.color.white
                            )
                            .setArrowColorResource(
                                R.color.purple_500
                            )
                            .setBackgroundDrawableResource(
                                R.drawable.button_theme_bg
                            )
                            .setBalloonAnimation(
                                BalloonAnimation.FADE
                            )
                            .build()
                    }
                mBalloon =
                    tooltipTotalAmount?.let {
                        Balloon.Builder(
                            baseContext
                        )
                            .setArrowSize(10)
                            .setArrowOrientation(
                                ArrowOrientation.TOP
                            )
                            .setArrowPosition(
                                0.5f
                            )
                            .setWidthRatio(0.70f)
                            .setMarginLeft(48)
                            .setPadding(10)
                            .setText(it)
                            .setCornerRadius(4f)
                            .setTextColorResource(
                                R.color.white
                            )
                            .setArrowColorResource(
                                R.color.purple_500
                            )
                            .setBackgroundDrawableResource(
                                R.drawable.button_theme_bg
                            )
                            .setBalloonAnimation(
                                BalloonAnimation.FADE
                            )
                            .build()
                    }
            } else if (stateNumber == 3) {
                binding.textViewPageCount.text = "3/5"
                binding.textViewContent.text =
                    mList[2].titleName
                binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.THREE)
                binding.stateProgress.enableAnimationToCurrentState(true)
                binding.layoutBulkBookingFirstPage.isVisible = false
                binding.layoutBulkBookingSecondPage.isVisible = false
                binding.layoutBulkBookingThirdPage.isVisible = true
                binding.layoutBulkBookingFourthPage.isVisible = false
                binding.layoutBulkBookingFivethPage.isVisible = false
                if (spinnerSelectedValue2 == 0) {
                    val adapter1 = ArrayAdapter(
                        this@BulkBookingActivity,
                        android.R.layout.simple_spinner_item,
                        descriptionData
                    )
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerPerMonth1.setAdapter(adapter1)
                } else {
                    binding.spinnerPerMonth1.setSelection(spinnerSelectedValue2!! - 1)
                }
                tooltipTotalAmount =
                    mList[2].placeHolderList!![4]!!.tooltip
                tooltipSurface =
                    mList[2].placeHolderList!![2]!!.tooltip
                balloon =
                    tooltipSurface?.let {
                        Balloon.Builder(
                            baseContext
                        )
                            .setArrowSize(10)
                            .setArrowOrientation(
                                ArrowOrientation.BOTTOM
                            )
                            .setArrowPosition(
                                0.5f
                            )
                            .setWidthRatio(0.70f)
                            .setMarginLeft(48)
                            .setPadding(10)
                            .setText(it)
                            .setCornerRadius(4f)
                            .setTextColorResource(
                                R.color.white
                            )
                            .setArrowColorResource(
                                R.color.purple_500
                            )
                            .setBackgroundDrawableResource(
                                R.drawable.button_theme_bg
                            )
                            .setBalloonAnimation(
                                BalloonAnimation.FADE
                            )
                            .build()
                    }
                mBalloon =
                    tooltipTotalAmount?.let {
                        Balloon.Builder(
                            baseContext
                        )
                            .setArrowSize(10)
                            .setArrowOrientation(
                                ArrowOrientation.TOP
                            )
                            .setArrowPosition(
                                0.5f
                            )
                            .setWidthRatio(0.70f)
                            .setMarginLeft(48)
                            .setPadding(10)
                            .setText(it)
                            .setCornerRadius(4f)
                            .setTextColorResource(
                                R.color.white
                            )
                            .setArrowColorResource(
                                R.color.purple_500
                            )
                            .setBackgroundDrawableResource(
                                R.drawable.button_theme_bg
                            )
                            .setBalloonAnimation(
                                BalloonAnimation.FADE
                            )
                            .build()
                    }
            } else if (stateNumber == 4) {
                binding.textViewPageCount.text = "4/5"
                binding.textViewContent.text =
                    mList[3].titleName
                binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR)
                binding.stateProgress.enableAnimationToCurrentState(true)
                binding.layoutBulkBookingFirstPage.isVisible = false
                binding.layoutBulkBookingSecondPage.isVisible = false
                binding.layoutBulkBookingThirdPage.isVisible = false
                binding.layoutBulkBookingFourthPage.isVisible = true
                binding.layoutBulkBookingFivethPage.isVisible = false
                if (spinnerSelectedValue3 == 0) {
                    val adapter1 = ArrayAdapter(
                        this@BulkBookingActivity,
                        android.R.layout.simple_spinner_item,
                        descriptionData
                    )
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerPerMonth2.setAdapter(adapter1)

                } else {
                    binding.spinnerPerMonth2.setSelection(spinnerSelectedValue3!! - 1)
                }
                tooltipTotalAmount =
                    mList[3].placeHolderList!![4]!!.tooltip
                tooltipSurface =
                    mList[3].placeHolderList!![2]!!.tooltip
                balloon =
                    tooltipSurface?.let {
                        Balloon.Builder(
                            baseContext
                        )
                            .setArrowSize(10)
                            .setArrowOrientation(
                                ArrowOrientation.BOTTOM
                            )
                            .setArrowPosition(
                                0.5f
                            )
                            .setWidthRatio(0.70f)
                            .setMarginLeft(48)
                            .setPadding(10)
                            .setText(it)
                            .setCornerRadius(4f)
                            .setTextColorResource(
                                R.color.white
                            )
                            .setArrowColorResource(
                                R.color.purple_500
                            )
                            .setBackgroundDrawableResource(
                                R.drawable.button_theme_bg
                            )
                            .setBalloonAnimation(
                                BalloonAnimation.FADE
                            )
                            .build()
                    }
                mBalloon =
                    tooltipTotalAmount?.let {
                        Balloon.Builder(
                            baseContext
                        )
                            .setArrowSize(10)
                            .setArrowOrientation(
                                ArrowOrientation.TOP
                            )
                            .setArrowPosition(
                                0.5f
                            )
                            .setWidthRatio(0.70f)
                            .setMarginLeft(48)
                            .setPadding(10)
                            .setText(it)
                            .setCornerRadius(4f)
                            .setTextColorResource(
                                R.color.white
                            )
                            .setArrowColorResource(
                                R.color.purple_500
                            )
                            .setBackgroundDrawableResource(
                                R.drawable.button_theme_bg
                            )
                            .setBalloonAnimation(
                                BalloonAnimation.FADE
                            )
                            .build()
                    }
            } else if (stateNumber == 5) {
                binding.textViewPageCount.text = "5/5"
                binding.textViewContent.text =
                    mList[4].titleName
                binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE)
                binding.stateProgress.enableAnimationToCurrentState(true)
                binding.layoutBulkBookingFirstPage.isVisible = false
                binding.layoutBulkBookingSecondPage.isVisible = false
                binding.layoutBulkBookingFivethPage.isVisible = true
                binding.layoutBulkBookingThirdPage.isVisible = false
                binding.layoutBulkBookingFourthPage.isVisible = false
                if (spinnerSelectedValue4 == 0) {
                    val adapter1 = ArrayAdapter(
                        this@BulkBookingActivity,
                        android.R.layout.simple_spinner_item,
                        descriptionData
                    )
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerPerMonth3.setAdapter(adapter1)
                } else {
                    binding.spinnerPerMonth3.setSelection(spinnerSelectedValue4!! - 1)
                }

                tooltipTotalAmount =
                    mList[4].placeHolderList!![4]!!.tooltip
                tooltipSurface =
                    mList[4].placeHolderList!![2]!!.tooltip
                balloon =
                    tooltipSurface?.let {
                        Balloon.Builder(
                            baseContext
                        )
                            .setArrowSize(10)
                            .setArrowOrientation(
                                ArrowOrientation.BOTTOM
                            )
                            .setArrowPosition(
                                0.5f
                            )
                            .setWidthRatio(0.70f)
                            .setMarginLeft(48)
                            .setPadding(10)
                            .setText(it)
                            .setCornerRadius(4f)
                            .setTextColorResource(
                                R.color.white
                            )
                            .setArrowColorResource(
                                R.color.purple_500
                            )
                            .setBackgroundDrawableResource(
                                R.drawable.button_theme_bg
                            )
                            .setBalloonAnimation(
                                BalloonAnimation.FADE
                            )
                            .build()
                    }
                mBalloon =
                    tooltipTotalAmount?.let {
                        Balloon.Builder(
                            baseContext
                        )
                            .setArrowSize(10)
                            .setArrowOrientation(
                                ArrowOrientation.TOP
                            )
                            .setArrowPosition(
                                0.5f
                            )
                            .setWidthRatio(0.70f)
                            .setMarginLeft(48)
                            .setPadding(10)
                            .setText(it)
                            .setCornerRadius(4f)
                            .setTextColorResource(
                                R.color.white
                            )
                            .setArrowColorResource(
                                R.color.purple_500
                            )
                            .setBackgroundDrawableResource(
                                R.drawable.button_theme_bg
                            )
                            .setBalloonAnimation(
                                BalloonAnimation.FADE
                            )
                            .build()
                    }
            } else if (stateNumber == 1) {
                binding.buttonNext.isVisible = false
                binding.buttonPrevious.isVisible = false
                binding.buttonNextPage.isVisible = true
                binding.layoutBulkBookingFirstPage.isVisible = true
                binding.layoutBulkBookingFivethPage.isVisible = false
                binding.layoutBulkBookingSecondPage.isVisible = false
                binding.layoutBulkBookingThirdPage.isVisible = false
                binding.layoutBulkBookingFourthPage.isVisible = false
                binding.textViewPageCount.text = "1/5"
                binding.textViewContent.text =
                    mList[0].titleName
                binding.stateProgress.setCurrentStateNumber(StateProgressBar.StateNumber.ONE)
                binding.stateProgress.enableAnimationToCurrentState(true)
            }
        }
    }

    private fun emailAlertPopup(mID: String) {
        val alertDialog = Dialog(this)
        val lp = WindowManager.LayoutParams()
        alertDialog.setCancelable(false)
        val window = alertDialog.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.alert_dialog_email_validation)
        val posTv: AppCompatButton = alertDialog.findViewById(R.id.button_proceed)
        val editTextEmail: EditText = alertDialog.findViewById(R.id.editTextEmail)
        val imgClearEmail: ImageView = alertDialog.findViewById(R.id.email_clear)
        if (emailResponse!!.isNotEmpty()) {
            imgClearEmail.visibility = View.VISIBLE
        } else {
            imgClearEmail.visibility = View.GONE
        }
        imgClearEmail.setOnClickListener {
            editTextEmail.setText("")
            editTextEmail.requestFocus()
            imgClearEmail.visibility = View.GONE
        }
        editTextEmail.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    imgClearEmail.visibility = View.GONE
                }
                return false
            }
        })



        editTextEmail.setText(emailResponse)
        posTv.setOnClickListener({
            val mEmail = editTextEmail.text.trim().toString()
            if (!mEmail.equals("")) {
                if (Utils.isValidEmail(mEmail)) {
                    callAPI(alertDialog, mEmail, mID)
                    alertDialog.dismiss()
                } else {
                    editTextEmail.setError(resources.getString(R.string.text_enter_email_validation))
                }
            } else {
                editTextEmail.setError(resources.getString(R.string.text_enter_email))
            }
        })
        val negTv: AppCompatButton = alertDialog.findViewById(R.id.button_cancel)
        negTv.text = resources.getString(R.string.button_cancel)
        negTv.setOnClickListener {
            if (emailResponse!!.isNotEmpty()) {
                alertDialog.dismiss()
            } else {
                alertDialog.dismiss()
                finish()
            }

        }
        alertDialog.show()
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

    private fun callAPI(alertDialog: Dialog, email: String, mID: String) {
        val data = VerifyEmailRequest()
        data.setEmail(email)
        data.setOrganizationId(mID.toInt())
        viewModel.getUserDetails(token!!, data)

        viewModel.Response2!!.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    if (progressDoalog != null && progressDoalog!!.isShowing) {
                        progressDoalog!!.dismiss()
                    }
                    if (it.value.getStatusCode() == 200) {
                        alertDialog.dismiss()
                        mobileResponse = it.value.getValue()!!.data!!.getMobileNumber().toString()
                            .replace("(-+.^:,)", "")
                        emailResponse = it.value.getValue()!!.data!!.getEmail()
                        binding.editTextEmail.setText(emailResponse)
                        binding.editTextSchoolName.setText("")
                        binding.edittextAddress.setText("")
                        binding.edittextname.setText("")
                        binding.editTextMobileNo.setText("")
                        if (it.value.getValue()!!.data!!.getMobileNumber()!!.equals("")) {
                            binding.editTextSchoolName.isEnabled = true
                            binding.edittextAddress.isEnabled = true
                            binding.edittextname.isEnabled = true
                            binding.editTextMobileNo.isEnabled = true
                        } else {

                            binding.edittextname.setText(it.value.getValue()!!.data!!.getFirstName())
                            binding.edittextAddress.setText(
                                it.value.getValue()!!.data!!.getAddress().toString()
                            )
                            getLocationDetails(it.value.getValue()!!.data!!.getAddress().toString())
                            binding.editTextMobileNo.setText(
                                Utils.USFormatMobile(
                                    mobileResponse!!.replace(
                                        Regex("\\D"),
                                        ""
                                    ).toLong()
                                )
                            )
                            binding.editTextSchoolName.setText(it.value.getValue()!!.data!!.getOrganizationName())

                            binding.editTextSchoolName.isEnabled = false
                            binding.edittextAddress.isEnabled = false
                            binding.edittextname.isEnabled = false
                            binding.editTextMobileNo.isEnabled = false
                        }
                    }
                }

                is Resource.GenericError -> {
                    alertDialog.show()
                    Utils.showToast(this, it.error!!.message!!, true)
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

    private fun getToolTip() {
        /*tooltipSurface = "1 per desk, 1 for chair arms and 1 for door handles"
        tooltipTotalAmount =
            "Total Number of Monthly Test is calculated automatically based on the above inputs "
        val fourthScreen =
            "Gymnasium- Sports and Equipment on the top 1 per locker, 1 per basketball, 1 per sport equipment and 1 per bench"
        val fiveth = "1 per handle, 1 per door, 1 per steering wheel "
*/
        /*val mballoon2 = fiveth.let {
            Balloon.Builder(baseContext)
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowPosition(0.5f)
                .setWidthRatio(0.70f)
                .setMarginLeft(48)
                .setPadding(10)
                .setText(it)
                .setCornerRadius(4f)
                .setTextColorResource(R.color.black)
                .setBackgroundDrawableResource(R.drawable.alert_bg)
                .setBalloonAnimation(BalloonAnimation.FADE)
                .build()
        }*/
        /*val mballoon1 = fourthScreen.let {
            Balloon.Builder(baseContext)
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowPosition(0.5f)
                .setWidthRatio(0.70f)
                .setMarginLeft(48)
                .setPadding(10)
                .setText(it)
                .setCornerRadius(4f)
                .setTextColorResource(R.color.black)
                .setBackgroundDrawableResource(R.drawable.alert_bg)
                .setBalloonAnimation(BalloonAnimation.FADE)
                .build()
        }*/
        /*balloon = tooltipSurface?.let {
            Balloon.Builder(baseContext)
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowPosition(0.5f)
                .setWidthRatio(0.70f)
                .setMarginLeft(48)
                .setPadding(10)
                .setText(it)
                .setCornerRadius(4f)
                .setTextColorResource(R.color.black)
                .setBackgroundDrawableResource(R.drawable.alert_bg)
                .setBalloonAnimation(BalloonAnimation.FADE)
                .build()
        }*/
        /*val mBalloon = tooltipTotalAmount?.let {
            Balloon.Builder(baseContext)
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.TOP)
                .setArrowPosition(0.5f)
                .setWidthRatio(0.70f)
                .setMarginLeft(48)
                .setPadding(10)
                .setText(it)
                .setCornerRadius(4f)
                .setArrowColorResource(R.color.purple_500)
                .setTextColorResource(R.color.white)
                .setBackgroundDrawableResource(R.drawable.button_theme_bg)
                .setBalloonAnimation(BalloonAnimation.FADE)
                .build()
        }*/

        binding.imageViewTotalValueIcon.setOnClickListener {

            if (mBalloon!!.isShowing) {
                mBalloon!!.dismiss()
            } else {
                mBalloon!!.showAlignBottom(binding.imageViewTotalValueIcon)
            }
        }
        binding.imageViewSurfaceValueIcon.setOnClickListener {

            if (balloon!!.isShowing) {
                balloon!!.dismiss()
            } else {
                balloon!!.showAlignTop(binding.imageViewSurfaceValueIcon)
            }
        }

        binding.imageViewSurfaceValueIcon1.setOnClickListener {

            if (balloon!!.isShowing) {
                balloon!!.dismiss()
            } else {
                balloon!!.showAlignBottom(binding.imageViewSurfaceValueIcon1)
            }
        }

        binding.imageViewTotalValueIcon1.setOnClickListener {

            if (mBalloon!!.isShowing) {
                mBalloon!!.dismiss()
            } else {
                mBalloon!!.showAlignBottom(binding.imageViewTotalValueIcon1)
            }
        }

        binding.imageViewTotalValueIcon2.setOnClickListener {

            if (mBalloon!!.isShowing) {
                mBalloon!!.dismiss()
            } else {
                mBalloon!!.showAlignBottom(binding.imageViewTotalValueIcon2)
            }
        }

        binding.imageViewSurfaceValueIcon2.setOnClickListener {

            if (balloon!!.isShowing) {
                balloon!!.dismiss()
            } else {
                balloon!!.showAlignTop(binding.imageViewSurfaceValueIcon2)
            }
        }

        binding.imageViewTotalValueIcon3.setOnClickListener {

            if (mBalloon!!.isShowing) {
                mBalloon!!.dismiss()
            } else {
                mBalloon!!.showAlignBottom(binding.imageViewTotalValueIcon3)
            }
        }

        binding.imageViewSurfaceValueIcon3.setOnClickListener {

            if (balloon!!.isShowing) {
                balloon!!.dismiss()
            } else {
                balloon!!.showAlignTop(binding.imageViewSurfaceValueIcon3)
            }
        }
    }

    private fun getAPICall(data: Int?) {
        val request = OrganizationTitleRequest()
        request.organizationId = data

        val gson = Gson()
        var json: String? = ""
        json = gson.toJson(request)
        Log.e("Model---->", json.toString())

        val aJsonParser = JsonParser()
        val aJsonObject = aJsonParser.parse(json) as JsonObject
        Log.e("aJsonObject", aJsonObject.toString())
        viewModel.getTitle(token!!, aJsonObject)

        viewModel.Response1.observe(
            this@BulkBookingActivity,
            androidx.lifecycle.Observer {
                when (it) {
                    is Resource.Success -> {
                        if (it.value.getStatusCode() == 200) {
                            mTitle.clear()
                            mList =
                                it.value.getValue()!!.data!!.organizationLists as ArrayList<OrganizationTitleResponse.OrganizationList>

                            if (mList.size != 0) {
                                binding.textViewPageCount.text = "1/5"
                                binding.textViewContent.text = mList[0].titleName
                            }

                            if (mList.size != 0) {
                                for (i in 0 until it.value.getValue()!!.data!!.titleList!!.size) {
                                    val title =
                                        it.value.getValue()!!.data!!.titleList!![i].toString()
                                    mTitle.add(title)
                                }
                                mTitle.add(0, "Select Title")
                                val adapter = ArrayAdapter(
                                    this@BulkBookingActivity,
                                    android.R.layout.simple_spinner_item,
                                    mTitle
                                )
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                binding.spinnerTitle.setAdapter(adapter)
                                val gson = Gson()
                                val json =
                                    gson.toJson(it.value.getValue()!!.data!!.getContractLists())
                                Log.e("contractListsString", json.toString())
                                contractListsString = json


                                for (i in it.value.getValue()!!.data!!.organizationLists!!.indices) {
                                    mStageOne =
                                        it.value.getValue()!!.data!!.organizationLists!![0]!!.placeHolderList!!.size
                                    Log.e("mStageOne", mStageOne.toString())
                                    if (!it.value.getValue()!!.data!!.organizationLists!![0]!!.placeHolderList!![0]!!.name.equals(
                                            ""
                                        )
                                    ) {
                                        mSchoolNameBoolean = true
                                        binding.layoutSchoolName.isVisible = true
                                        binding.layoutSchoolName.hint =
                                            it.value.getValue()!!.data!!.organizationLists!![0]!!.placeHolderList!![0]!!.name
                                    } else {

                                    }

                                    if (!it.value.getValue()!!.data!!.organizationLists!![0]!!.placeHolderList!![1]!!.name.equals(
                                            ""
                                        )
                                    ) {
                                        mSchoolAddressBoolean = true
                                        binding.layoutAddress.isVisible = true
                                        binding.layoutAddress.hint =
                                            it.value.getValue()!!.data!!.organizationLists!![0]!!.placeHolderList!![1]!!.name
                                        binding.edittextAddress.setText("")
                                    } else {

                                    }

                                    if (!it.value.getValue()!!.data!!.organizationLists!![0]!!.placeHolderList!![2]!!.name.equals(
                                            ""
                                        )
                                    ) {
                                        mContactNameBoolean = true
                                        binding.layoutName.isVisible = true
                                        binding.layoutName.hint =
                                            it.value.getValue()!!.data!!.organizationLists!![0]!!.placeHolderList!![2]!!.name
                                    } else {

                                    }

                                    if (!it.value.getValue()!!.data!!.organizationLists!![0]!!.placeHolderList!![3]!!.name.equals(
                                            ""
                                        )
                                    ) {
                                        binding.textViewOrganizationAddressLabel.isVisible =
                                            true
                                        binding.spinnerTitle.isVisible = true
                                        binding.viewOrganizationAddress.isVisible = true
                                        binding.textViewOrganizationAddressLabel.text =
                                            it.value.getValue()!!.data!!.organizationLists!![0]!!.placeHolderList!![3]!!.name

                                    } else {

                                    }

                                    if (!it.value.getValue()!!.data!!.organizationLists!![0]!!.placeHolderList!![4]!!.name.equals(
                                            ""
                                        )
                                    ) {
                                        mMobileBoolean = true
                                        binding.layoutMobileNo.isVisible = true
                                        binding.layoutMobileNo.hint =
                                            it.value.getValue()!!.data!!.organizationLists!![0]!!.placeHolderList!![4]!!.name
                                    } else {

                                    }

                                    if (!it.value.getValue()!!.data!!.organizationLists!![0]!!.placeHolderList!![5]!!.name.equals(
                                            ""
                                        )
                                    ) {
                                        binding.layoutEmail.isVisible = true
                                        mEmailBoolean = true
                                        binding.layoutEmail.hint =
                                            it.value.getValue()!!.data!!.organizationLists!![0]!!.placeHolderList!![5]!!.name
                                    }
                                }

                            } else {

                            }
                        }
                    }
                    is Resource.GenericError -> {
                        Utils.showToast(this, it.error!!.message.toString(), true)

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


    private fun editTextData() {

        //TODO Stage 2th
        binding.editTextNoOfStudent.addTextChangedListener(object :
            TextChangedListener<EditText?>(binding.editTextNoOfStudent) {
            override fun onTextChanged(target: EditText?, s: Editable?) {
                if (!target!!.text.toString().equals("")) {
                    if (target.text.toString().toInt() != 0) {
                        editTextStageTwoFirst = target.text.toString().toInt()
                        if (editTextStageTwoFirst != 0 && editTextStageTwoSecond != 0 && editTextStageTwoThird != 0) {
                            val mSumValue =
                                editTextStageTwoFirst!!.toLong() + editTextStageTwoSecond!!.toLong() + editTextStageTwoThird!!.toLong()
                            val mMul = (mSumValue * spinnerStageTwo!!.toLong())
                            editTextStageSecondTotalValue = mMul
                            Log.e("total", mSumValue.toString())
                            binding.editTextMonthlyTestCount.setText(editTextStageSecondTotalValue.toString())
                        }
                    } else {
                        binding.editTextNoOfStudent.setText("")
                    }
                }
            }
        })

        binding.editTextNoOfClassRoom.addTextChangedListener(
            object :
                TextChangedListener<EditText?>(binding.editTextNoOfClassRoom) {
                override fun onTextChanged(target1: EditText?, s: Editable?) {
                    if (!target1!!.text.toString().equals("")) {
                        if (target1.text.toString().toInt() != 0) {
                            editTextStageTwoSecond = target1.text.toString().toInt()
                            if (editTextStageTwoFirst != 0 && editTextStageTwoSecond != 0 && editTextStageTwoThird != 0) {
                                val mSumValue =
                                    editTextStageTwoFirst!!.toLong() + editTextStageTwoSecond!!.toLong() + editTextStageTwoThird!!.toLong()
                                val mMul = (mSumValue * spinnerStageTwo!!.toLong())
                                editTextStageSecondTotalValue = mMul
                                Log.e("total", mSumValue.toString())
                                binding.editTextMonthlyTestCount.setText(
                                    editTextStageSecondTotalValue.toString()
                                )

                            }
                        } else {
                            binding.editTextNoOfClassRoom.setText("")
                        }
                    }
                }
            })

        binding.editTextSurface.addTextChangedListener(
            object :
                TextChangedListener<EditText?>(binding.editTextSurface) {
                override fun onTextChanged(target2: EditText?, s: Editable?) {
                    if (!target2!!.text.toString().equals("")) {
                        if (target2.text.toString().toInt() != 0) {
                            editTextStageTwoThird = target2.text.toString().toInt()
                            if (editTextStageTwoFirst != 0 && editTextStageTwoSecond != 0 && editTextStageTwoThird != 0) {
                                val mSumValue =
                                    editTextStageTwoFirst!!.toLong() + editTextStageTwoSecond!!.toLong() + editTextStageTwoThird!!.toLong()
                                val mMul = (mSumValue * spinnerStageTwo!!.toLong())
                                editTextStageSecondTotalValue = mMul
                                Log.e("total", mSumValue.toString())
                                binding.editTextMonthlyTestCount.setText(
                                    editTextStageSecondTotalValue.toString()
                                )
                            }
                        } else {
                            binding.editTextSurface.setText("")
                        }
                    }

                }
            })

        //TODO Stage 3th
        binding.editTextNoOfTeacher.addTextChangedListener(
            object :
                TextChangedListener<EditText?>(binding.editTextNoOfTeacher) {
                override fun onTextChanged(target: EditText?, s: Editable?) {
                    if (!target!!.text.toString().equals("")) {
                        if (target.text.toString().toInt() != 0) {
                            editTextStageThreeFirst = target.text.toString().toInt()
                            if (editTextStageThreeFirst != 0 && editTextStageThreeSecond != 0 && editTextStageThreeThird != 0) {
                                val mSumValue =
                                    editTextStageThreeFirst!!.toLong() + editTextStageThreeSecond!!.toLong() + editTextStageThreeThird!!.toLong()
                                editTextStageThirdTotalValue =
                                    spinnerStageThree!!.toLong() * mSumValue
                                Log.e("total", editTextStageSecondTotalValue.toString())
                                binding.editTextTotalNoOfTest.setText(editTextStageThirdTotalValue.toString())
                            }

                        } else {
                            binding.editTextNoOfTeacher.setText("")
                        }
                    }

                }
            })

        binding.editTextNoOfOffice.addTextChangedListener(
            object :
                TextChangedListener<EditText?>(binding.editTextNoOfOffice) {
                override fun onTextChanged(target: EditText?, s: Editable?) {
                    if (!target!!.text.toString().equals("")) {
                        if (target.text.toString().toInt() != 0) {
                            editTextStageThreeSecond = target.text.toString().toInt()
                            if (editTextStageThreeFirst != 0 && editTextStageThreeSecond != 0 && editTextStageThreeThird != 0) {
                                val mSumValue =
                                    editTextStageThreeFirst!!.toLong() + editTextStageThreeSecond!!.toLong() + editTextStageThreeThird!!.toLong()
                                editTextStageThirdTotalValue =
                                    spinnerStageThree!!.toLong() * mSumValue
                                Log.e("total", editTextStageSecondTotalValue.toString())
                                binding.editTextTotalNoOfTest.setText(editTextStageThirdTotalValue.toString())
                            }

                        } else {
                            binding.editTextNoOfOffice.setText("")
                        }
                    }


                }
            })

        binding.editTextSurfaceRoomTest.addTextChangedListener(
            object :
                TextChangedListener<EditText?>(binding.editTextSurfaceRoomTest) {
                override fun onTextChanged(target: EditText?, s: Editable?) {
                    if (!target!!.text.toString().equals("")) {
                        if (target.text.toString().toInt() != 0) {
                            editTextStageThreeThird = target.text.toString().toInt()
                            if (editTextStageThreeFirst != 0 && editTextStageThreeSecond != 0 && editTextStageThreeThird != 0) {
                                val mSumValue =
                                    editTextStageThreeFirst!!.toLong() + editTextStageThreeSecond!!.toLong() + editTextStageThreeThird!!.toLong()
                                editTextStageThirdTotalValue =
                                    spinnerStageThree!!.toLong() * mSumValue
                                Log.e("total", editTextStageSecondTotalValue.toString())
                                binding.editTextTotalNoOfTest.setText(editTextStageThirdTotalValue.toString())
                            }

                        } else {
                            binding.editTextSurfaceRoomTest.setText("")
                        }
                    }


                }
            })

        //TODO Stage 4th
        binding.editTextNoOfCoaches.addTextChangedListener(
            object :
                TextChangedListener<EditText?>(binding.editTextNoOfCoaches) {
                override fun onTextChanged(target: EditText?, s: Editable?) {
                    if (!target!!.text.toString().equals("")) {
                        if (target.text.toString().toInt() != 0) {
                            editTextStageFourFirst = target.text.toString().toInt()
                            if (editTextStageFourFirst != 0 && editTextStageFourSecond != 0 && editTextStageFourThird != 0) {
                                val mSumValue =
                                    editTextStageFourFirst!!.toLong() + editTextStageFourSecond!!.toLong() + editTextStageFourThird!!.toLong()
                                editTextStageFourthTotalValue =
                                    mSumValue * spinnerStageFour!!.toLong()
                                binding.editTextTotalNoOfTest1.setText(editTextStageFourthTotalValue.toString())
                            }
                        } else {
                            binding.editTextNoOfCoaches.setText("")
                        }
                    }
                }
            })

        binding.editTextNoOfOffices.addTextChangedListener(
            object :
                TextChangedListener<EditText?>(binding.editTextNoOfOffices) {
                override fun onTextChanged(target: EditText?, s: Editable?) {
                    if (!target!!.text.toString().equals("")) {
                        if (target.text.toString().toInt() != 0) {
                            editTextStageFourSecond = target.text.toString().toInt()
                            if (editTextStageFourFirst != 0 && editTextStageFourSecond != 0 && editTextStageFourThird != 0) {
                                val mSumValue =
                                    editTextStageFourFirst!!.toLong() + editTextStageFourSecond!!.toLong() + editTextStageFourThird!!.toLong()
                                editTextStageFourthTotalValue =
                                    mSumValue * spinnerStageFour!!.toLong()
                                Log.e(
                                    "total",
                                    (editTextStageFourFirst!! + (editTextStageFourSecond!! * editTextStageFourThird!!) * spinnerStageFour!!).toString()
                                )
                                binding.editTextTotalNoOfTest1.setText(editTextStageFourthTotalValue.toString())
                            }
                        } else {
                            binding.editTextNoOfOffices.setText("")
                        }
                    }

                }
            })

        binding.editTextSurfaceRoomTest1.addTextChangedListener(
            object :
                TextChangedListener<EditText?>(binding.editTextSurfaceRoomTest1) {
                override fun onTextChanged(target: EditText?, s: Editable?) {
                    if (!target!!.text.toString().equals("")) {
                        if (target.text.toString().toInt() != 0) {
                            editTextStageFourThird = target.text.toString().toInt()
                            if (editTextStageFourFirst != 0 && editTextStageFourSecond != 0 && editTextStageFourThird != 0) {
                                val mSumValue =
                                    editTextStageFourFirst!!.toLong() + editTextStageFourSecond!!.toLong() + editTextStageFourThird!!.toLong()
                                editTextStageFourthTotalValue =
                                    mSumValue * spinnerStageFour!!.toLong()
                                Log.e(
                                    "total",
                                    (editTextStageFourFirst!! + (editTextStageFourSecond!! * editTextStageFourThird!!) * spinnerStageFour!!).toString()
                                )
                                binding.editTextTotalNoOfTest1.setText(editTextStageFourthTotalValue.toString())
                            }
                        } else {
                            binding.editTextSurfaceRoomTest1.setText("")
                        }
                    }

                }
            })

        //TODO Stage 5th
        binding.editTextOne.addTextChangedListener(
            object :
                TextChangedListener<EditText?>(binding.editTextOne) {
                override fun onTextChanged(target: EditText?, s: Editable?) {
                    if (!target!!.text.toString().equals("")) {
                        if (target.text.toString().toInt() != 0) {
                            editTextStageFiveFirst = target.text.toString().toInt()
                            if (editTextStageFiveFirst != 0 && editTextStageFiveSecond != 0 && editTextStageFiveThird != 0) {
                                val mSumValue =
                                    editTextStageFiveFirst!!.toLong() + editTextStageFiveSecond!!.toLong() + editTextStageFiveThird!!.toLong()
                                editTextStageFifthTotalValue =
                                    spinnerStageFive!!.toLong() * mSumValue
                                Log.e("total", editTextStageSecondTotalValue.toString())
                                binding.editTextFourth.setText(editTextStageFifthTotalValue.toString())
                            }
                        } else {
                            binding.editTextOne.setText("")
                        }
                    }

                }
            })

        binding.editTextTwo.addTextChangedListener(
            object :
                TextChangedListener<EditText?>(binding.editTextTwo) {
                override fun onTextChanged(target: EditText?, s: Editable?) {
                    if (!target!!.text.toString().equals("")) {
                        if (target.text.toString().toInt() != 0) {
                            editTextStageFiveSecond = target.text.toString().toInt()
                            if (editTextStageFiveFirst != 0 && editTextStageFiveSecond != 0 && editTextStageFiveThird != 0) {
                                val mSumValue =
                                    editTextStageFiveFirst!!.toLong() + editTextStageFiveSecond!!.toLong() + editTextStageFiveThird!!.toLong()
                                editTextStageFifthTotalValue =
                                    spinnerStageFive!!.toLong() * mSumValue
                                Log.e("total", editTextStageSecondTotalValue.toString())
                                binding.editTextFourth.setText(editTextStageFifthTotalValue.toString())
                            }
                        } else {
                            binding.editTextTwo.setText("")
                        }
                    }
                }
            })

        binding.editTextThree.addTextChangedListener(
            object :
                TextChangedListener<EditText?>(binding.editTextThree) {
                override fun onTextChanged(target: EditText?, s: Editable?) {
                    if (!target!!.text.toString().equals("")) {
                        if (target.text.toString().toInt() != 0) {
                            editTextStageFiveThird = target.text.toString().toInt()
                            if (editTextStageFiveFirst != 0 && editTextStageFiveSecond != 0 && editTextStageFiveThird != 0) {
                                val mSumValue =
                                    editTextStageFiveFirst!!.toLong() + editTextStageFiveSecond!!.toLong() + editTextStageFiveThird!!.toLong()
                                editTextStageFifthTotalValue =
                                    spinnerStageFive!!.toLong() * mSumValue
                                Log.e("total", editTextStageSecondTotalValue.toString())
                                binding.editTextFourth.setText(editTextStageFifthTotalValue.toString())
                            }
                        } else {
                            binding.editTextThree.setText("")
                        }
                    }
                }
            })
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun handleEvent(event: String) {
        binding.edittextAddress.setText(event)
        getLocationDetails(event)
    }

    private fun getLocationDetails(event: String) {
        val coder = Geocoder(this@BulkBookingActivity, Locale.ENGLISH)
        //val coder = Geocoder(context)
        var address: List<Address?>

        try {
            // May throw an IOException
            address = coder.getFromLocationName(event, 5)
            if (address == null) {
                return
            }
            val location = address[0]!!
            val latlngs = LatLng(location.latitude, location.longitude)
            latlng = location.latitude.toString() + "," + location.longitude

            val addresses =
                coder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses.size > 0) {
                val fetchedAddress = addresses[0]
                val strAddress = StringBuilder()
                locality = addresses[0].getLocality()
                mState = addresses[0].adminArea
                Log.e("stateName", mState!!)
                for (i in 0 until fetchedAddress.maxAddressLineIndex) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append(" ")
                }
            } else {
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onhandleEvent(event: BulkTestingEditModel) {

        editTextStageTwoFirst = event.NoOfStudent!!.toInt()
        editTextStageThreeFirst = event.NoOfTeacher!!.toInt()
        editTextStageFourFirst = event.NoOfCoaches!!.toInt()
        editTextStageFiveFirst = event.NoOfDrivers!!.toInt()


        spinnerStageTwo = event.spinnerStageTwo
        spinnerStageThree = event.spinnerStageThree
        spinnerStageFour = event.spinnerStageFour
        spinnerStageFive = event.spinnerStageFive
        spinnerSelectedValue1 = spinnerStageTwo
        spinnerSelectedValue2 = spinnerStageThree
        spinnerSelectedValue3 = spinnerStageFour
        spinnerSelectedValue4 = spinnerStageFive
        Log.e(
            "ooomg",
            spinnerStageTwo.toString() + "" + spinnerStageThree + "" + spinnerStageFour + "" + spinnerStageFive
        )

        binding.editTextNoOfStudent.setText(editTextStageTwoFirst.toString())
        binding.editTextNoOfTeacher.setText(editTextStageThreeFirst.toString())
        binding.editTextNoOfCoaches.setText(editTextStageFourFirst.toString())
        binding.editTextOne.setText(editTextStageFiveFirst.toString())

        Log.e(
            "spinnerValue",
            spinnerStageTwo.toString() + "  3->" + spinnerStageThree + "  4->" + spinnerStageFour + "  5->" + spinnerStageFive
        )
        if (spinnerStageTwo!! <= 4) {
            binding.spinnerPerMonth.setSelection(spinnerStageTwo!! - 1)
            spinnerStageTwo = spinnerStageTwo!!
        }
        if (spinnerStageThree!! <= 4) {
            binding.spinnerPerMonth1.setSelection(spinnerStageThree!! - 1)
            spinnerStageThree = spinnerStageThree!!
        }
        if (spinnerStageFour!! <= 4) {
            binding.spinnerPerMonth2.setSelection(spinnerStageFour!! - 1)
            spinnerStageFour = spinnerStageFour!!
        }
        if (spinnerStageFive!! <= 4) {
            binding.spinnerPerMonth3.setSelection(spinnerStageFive!! - 1)
            spinnerStageFive = spinnerStageFive!!
        }

        if (editTextStageFiveFirst != 0 && editTextStageFiveSecond != 0 && editTextStageFiveThird != 0) {
            editTextStageFifthTotalValue =
                (editTextStageFiveFirst!!.toLong() + editTextStageFiveSecond!!.toLong() + editTextStageFiveThird!!.toLong()) * spinnerStageFive!!.toLong()
            Log.e("total", editTextStageSecondTotalValue.toString())
            binding.editTextFourth.setText(editTextStageFifthTotalValue.toString())
        }

        if (editTextStageFourFirst != 0 && editTextStageFourSecond != 0 && editTextStageFourThird != 0) {
            editTextStageFourthTotalValue =
                (editTextStageFourFirst!!.toLong() + editTextStageFourSecond!!.toLong() + editTextStageFourThird!!.toLong()) * spinnerStageFour!!.toLong()
            Log.e("total", editTextStageSecondTotalValue.toString())
            binding.editTextTotalNoOfTest1.setText(editTextStageFourthTotalValue.toString())
        }

        if (editTextStageThreeFirst != 0 && editTextStageThreeSecond != 0 && editTextStageThreeThird != 0) {
            editTextStageThirdTotalValue =
                (editTextStageThreeFirst!!.toLong() + editTextStageThreeSecond!!.toLong() + editTextStageThreeThird!!.toLong()) * spinnerStageThree!!.toLong()
            Log.e("total", editTextStageSecondTotalValue.toString())
            binding.editTextTotalNoOfTest.setText(editTextStageThirdTotalValue.toString())
        }

        if (editTextStageTwoFirst != 0 && editTextStageTwoSecond != 0 && editTextStageTwoThird != 0) {
            editTextStageSecondTotalValue =
                (editTextStageTwoFirst!!.toLong() + editTextStageTwoSecond!!.toLong() + editTextStageTwoThird!!.toLong()) * spinnerStageTwo!!.toLong()
            Log.e("total", editTextStageSecondTotalValue.toString())
            binding.editTextMonthlyTestCount.setText(editTextStageSecondTotalValue.toString())
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

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val spinner: Spinner = parent as Spinner

        if (spinner.id == R.id.spinnerPerMonth3) {
            spinnerStageFive = binding.spinnerPerMonth3.selectedItem.toString().toInt()
            if (editTextStageFiveFirst != 0 && editTextStageFiveSecond != 0 && editTextStageFiveThird != 0) {
                val mSumValue =
                    editTextStageFiveFirst!!.toLong() + editTextStageFiveSecond!!.toLong() + editTextStageFiveThird!!.toLong()
                editTextStageFifthTotalValue =
                    spinnerStageFive!!.toLong() * mSumValue
                Log.e("total", editTextStageSecondTotalValue.toString())
                binding.editTextFourth.setText(editTextStageFifthTotalValue.toString())
            }
            spinnerSelectedValue4 = spinnerStageFive
        } else if (spinner.id == R.id.spinnerPerMonth2) {
            spinnerStageFour = binding.spinnerPerMonth2.selectedItem.toString().toInt()
            if (editTextStageFourFirst != 0 && editTextStageFourSecond != 0 && editTextStageFourThird != 0) {
                val mSumValue =
                    editTextStageFourFirst!!.toLong() + editTextStageFourSecond!!.toLong() + editTextStageFourThird!!.toLong()
                editTextStageFourthTotalValue =
                    mSumValue * spinnerStageFour!!.toLong()
                Log.e("total", editTextStageFourthTotalValue.toString())
                binding.editTextTotalNoOfTest1.setText(editTextStageFourthTotalValue.toString())
            }
            spinnerSelectedValue3 = spinnerStageFour
        } else if (spinner.id == R.id.spinnerPerMonth1) {
            spinnerStageThree = binding.spinnerPerMonth1.selectedItem.toString().toInt()
            if (editTextStageThreeFirst != 0 && editTextStageThreeSecond != 0 && editTextStageThreeThird != 0) {
                val mSumValue =
                    editTextStageThreeFirst!!.toLong() + editTextStageThreeSecond!!.toLong() + editTextStageThreeThird!!.toLong()
                editTextStageThirdTotalValue =
                    spinnerStageThree!!.toLong() * mSumValue
                binding.editTextTotalNoOfTest.setText(editTextStageThirdTotalValue.toString())
                Log.e("total->", editTextStageThirdTotalValue.toString())
            }
            spinnerSelectedValue2 = spinnerStageThree
        } else if (spinner.id == R.id.spinnerPerMonth) {
            spinnerStageTwo = binding.spinnerPerMonth.selectedItem.toString().toInt()
            if (editTextStageTwoFirst != 0 && editTextStageTwoSecond != 0 && editTextStageTwoThird != 0) {
                val mSumValue =
                    editTextStageTwoFirst!!.toLong() + editTextStageTwoSecond!!.toLong() + editTextStageTwoThird!!.toLong()
                val mMul = (mSumValue * spinnerStageTwo!!.toLong())
                editTextStageSecondTotalValue = mMul
                Log.e("total", mSumValue.toString())
                binding.editTextMonthlyTestCount.setText(editTextStageSecondTotalValue.toString())
            }
            spinnerSelectedValue1 = spinnerStageTwo
        }
    }
}