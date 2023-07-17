package com.lifehopehealthapp.vitals.TemperatureDetailPage

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.Transformer
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityTemperatureDetailsPageBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.*
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.vitals.AddVitals.AddVitalsActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class TemperatureDetailActivity :
    BaseActivity<TemperatureDetailViewModel, TemperatureDetailModel>() {

    var datam: String? = null

    private var lastDate: String? = ""
    private var mPrefs: SharedPreferences? = null
    private var vitalID: String? = ""
    private var name: String? = ""
    private var lastValue: String? = ""
    private var token: String? = ""
    private var image: String? = ""
    private var progressDoalog: Dialog? = null
    private var progressDialog: Dialog? = null
    private var progressDialogFilter: Dialog? = null
    private var graphMin: Int? = 0
    private var graphMax: Int? = 0
    private var shareBooleanData: Boolean = false
    private var mYear: Int? = 0
    private var mMonth: Int? = 0
    private var mDay: Int? = 0
    private var ExitingDate: String? = ""
    private var ExitingData: String? = ""
    private var startDate: EditText? = null
    private var endDate: EditText? = null
    private var startTimeStamp: Int? = 0
    private var endTimeStamp: Int? = 0
    private var graphInitialization: Boolean = true
    private var chartMetrics: String = ""
    private var chartDateTimeFormat: String? = ""
    private var shareStartDate: Int? = 0
    private var shareEndDate: Int? = 0
    var currentDate: String? = ""

    // BottomSheetBehavior variable
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CoordinatorLayout>

    private var mValue: ArrayList<BarDatum> = ArrayList<BarDatum>()
    private var mDate: ArrayList<BarTimestamp> = ArrayList<BarTimestamp>()
    private var mColor: ArrayList<BarColorScheme> = ArrayList<BarColorScheme>()
    override fun getViewModel() = TemperatureDetailViewModel::class.java

    override fun getActivityRepository() =
        TemperatureDetailModel(
            remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
        )

    private lateinit var binding: ActivityTemperatureDetailsPageBinding

    fun ImageView.loadSvg(url: String?) {
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }

    @Subscribe
    fun onEvent(syncStatusMessage: VitalBandData) {
        val tsLong = System.currentTimeMillis() / 1000
        val tz = TimeZone.getDefault()
        val now = Date()
        val offsetFromUtc = tz.getOffset(now.time) / 1000
        val timeStamp = tsLong + offsetFromUtc

        var tempData = syncStatusMessage.mBandData
        val separated: List<String> = tempData!!.split(",")
        Log.e("separated", separated[0])

        val item = VitalsInputModel()
        item.setDate(timeStamp.toInt())
        if (syncStatusMessage.mVitalId == 3) {
            //val s = separated[0].substring(1, separated[0].length - 1)
            //item.setValue(s)
        } else {
            //item.setValue(separated[0])
        }
        //item.setValue("34.123")
        item.setVitalId(syncStatusMessage.mVitalId)
        item.setType(1)
        item.setCurrentDate(0)
        item.setGlucoseId(0)

        val gson = Gson()
        val json: String = gson.toJson(item)
        Log.e("json", json)
        val jsonFormattedString: String = json.replace("\"\":", "")
        val apiRequest =
            jsonFormattedString.substring(1, jsonFormattedString.length - 1)
        Log.e("json1", apiRequest)

        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(json) as JsonObject
            Log.e("aJsonObject", aJsonObject.toString())
            viewModel.saveVitals(token!!, aJsonObject)

            viewModel.Response2.observe(this, androidx.lifecycle.Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            binding.layoutChartView.isVisible = true
                            val intent = intent
                            finish()
                            startActivity(intent)
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
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityTemperatureDetailsPageBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        vitalID = intent.getStringExtra(Constants.VITALS_ID)
        name = intent.getStringExtra(Constants.VITALS_NAME)
        lastValue = intent.getStringExtra(Constants.VITALS_LAST_DATA)
        image = intent.getStringExtra(Constants.VITALS_IMAGE)
        lastDate = intent.getStringExtra(Constants.VITALS_DATE).toString()
        Utils.loadSvg(image, this, binding.imageViewVitalsImage)
        binding.layoutHeader.textViewLabel.text = name
        binding.layoutHeader.imageviewBackArrow.setOnClickListener {
            onBackPressed()
        }

        binding.layoutHeader.imageViewHistory.setOnClickListener {
            filterPopUp()
        }

        binding.buttonAddVitals.setOnClickListener {
            intent = Intent(this@TemperatureDetailActivity, AddVitalsActivity::class.java)
            intent.putExtra(Constants.VITALS_ID, vitalID)
            intent.putExtra(Constants.VITALS_NAME, name)
            intent.putExtra(Constants.VITALS_IMAGE, image)
            startActivityForResult(intent, 123)
        }

        binding.buttonAddVitals1.setOnClickListener {
            intent = Intent(this@TemperatureDetailActivity, AddVitalsActivity::class.java)
            intent.putExtra(Constants.VITALS_ID, vitalID)
            intent.putExtra(Constants.VITALS_NAME, name)
            intent.putExtra(Constants.VITALS_IMAGE, image)
            startActivityForResult(intent, 123)
        }
        binding.buttonShare.setOnClickListener {
            shareBooleanData = true
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            ShareVitals()
        }


       /* getGraphView()*/

        val sdf = SimpleDateFormat("MM/dd/yyyy")
        currentDate = sdf.format(Date())
        /* if (lastDate.equals("0")) {
             currentDate = sdf.format(Date())
         } else {
             currentDate = Utils.getDateCurrentTimeZone(
                 lastDate!!.toLong(),
                 Constants.TIMESTAMP_DOB!!
             )
             Log.e("currentDate", currentDate.toString())
         }*/
        getEndTimeStampData()
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("1 Day"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("7 Days"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("15 Days"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Custom"))
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL




        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 3) {
                    filterPopUp()
                    shareStartDate = 0
                } else if (tab.position == 2) {
                    shareStartDate = 0
                    getEndTimeStampData()
                    callEndDate(14)
                } else if (tab.position == 1) {
                    shareStartDate = 0
                    getEndTimeStampData()
                    callEndDate(6)
                } else if (tab.position == 0) {
                    shareStartDate = 0
                    shareEndDate = 0
                    getGraphView()
                }
                binding.tabLayout.setSelectedTabIndicatorHeight(10)
                (binding.tabLayout as TabLayout).setTabTextColors(
                    ContextCompat.getColor(this@TemperatureDetailActivity, R.color.white),
                    ContextCompat.getColor(this@TemperatureDetailActivity, R.color.purple_500)
                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                //Utils.showToast(this@BloodGlucoseActivity, tab.position.toString(), false)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                /* binding.tabLayout.setSelectedTabIndicatorHeight(10)
                 (binding.tabLayout as TabLayout).setTabTextColors(
                     ContextCompat.getColor(this@TemperatureDetailActivity, R.color.white),
                     ContextCompat.getColor(this@TemperatureDetailActivity, R.color.purple_500)
                 )
                 callEndDate(7)*/
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun getEndTimeStampData() {
        var localTime1: Date? = null
        var upToNCharacters1: String = ""
        try {
            localTime1 =
                SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(
                    currentDate
                )
            Log.e("currentDate", currentDate.toString())

            val formatter: DateFormat = SimpleDateFormat("MM/dd/yyyy")
            val date = formatter.parse(currentDate) as Date
            System.out.println("Today is " + date.time)
            Log.e("NEWWW", date.time.toString())
            upToNCharacters1 =
                localTime1.time.toString()
                    .substring(
                        0,
                        Math.min(localTime1.time.toString().length, 10)
                    )
            //shareEndDate = upToNCharacters1.toInt()
            val tz = TimeZone.getDefault()
            val now = Date()
            val offsetFromUtc = tz.getOffset(now.time) / 1000
            val tsLong = upToNCharacters1.toInt()
            val timeStamp = tsLong + offsetFromUtc
            endTimeStamp = timeStamp
            Log.e("timestamp1", upToNCharacters1)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun callEndDate(dateLimit: Int) {
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val currentDateandTime = sdf.format(Date())
        val cdate = sdf.parse(currentDateandTime)
        val now2 = Calendar.getInstance()
        now2.add(Calendar.DATE, -dateLimit)
        var dateValue = now2[Calendar.DATE].toString()
        var monthValue = (now2[Calendar.MONTH] + 1).toString()
        if (monthValue.length == 1) {
            monthValue = "0" + monthValue
        } else {
            dateValue = now2[Calendar.DATE].toString()
        }
        if (dateValue.length == 1) {
            dateValue = "0" + dateValue
        } else {
            monthValue = (now2[Calendar.MONTH] + 1).toString()
        }
        val beforedate = monthValue + "/" + dateValue + "/" + now2[Calendar.YEAR]
        Log.e("7daysbefore", beforedate.toString())
        val BeforeDate1 = sdf.parse(beforedate)
        cdate.compareTo(BeforeDate1)


        var localTime: Date? = null
        var upToNCharacters: String = ""
        try {
            localTime =
                SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(
                    beforedate
                )
            Log.e("timestamp", localTime!!.time.toString())

            val formatter: DateFormat = SimpleDateFormat("MM/dd/yyyy")
            val date = formatter.parse(beforedate) as Date
            System.out.println("Today is " + date.time)
            Log.e("NEWWW", date.time.toString())
            upToNCharacters =
                localTime.time.toString()
                    .substring(
                        0,
                        Math.min(localTime.time.toString().length, 10)
                    )
            //shareStartDate = upToNCharacters.toInt()
            val tz = TimeZone.getDefault()
            val now = Date()

            val offsetFromUtc = tz.getOffset(now.time) / 1000
            val tsLong = upToNCharacters.toInt()
            val timeStamp = tsLong + offsetFromUtc
            startTimeStamp = timeStamp
            Log.e("startTimeStamp", upToNCharacters)
            if (progressDialogFilter == null) {
                progressDialogFilter = Utils.getDialog(this)
            }
            progressDialogFilter!!.show()
            apiCall()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun filterPopUp() {
        var current = ""
        val ddmmyyyy = "MMDDYYYY"
        val cal = Calendar.getInstance()
        var startDateValidation: Boolean = false
        var endDateValidation: Boolean = false
        var DOBLength: Boolean = false

        val alertDialog = Dialog(this)
        val lp = WindowManager.LayoutParams()
        val window = alertDialog.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.dialog_filter)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)


        val closeButton: AppCompatButton = alertDialog.findViewById(R.id.buttonCancel)
        val applyFilter: AppCompatButton = alertDialog.findViewById(R.id.buttonFilter)
        startDate = alertDialog.findViewById(R.id.editTextStartDate)
        endDate = alertDialog.findViewById(R.id.editTextEndDate)
        val imageViewStartDateCalender: AppCompatImageView =
            alertDialog.findViewById(R.id.imageViewStartDateCalender)
        val imageViewEndDateCalender: AppCompatImageView =
            alertDialog.findViewById(R.id.imageViewEndDateCalender)

        startDate!!.addTextChangedListener(object : TextWatcher {

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
                                this@TemperatureDetailActivity,
                                resources.getString(R.string.toast_enter_valid_date),
                                true
                            )
                            startDateValidation = false
                            //binding.edittextDob.setError("Invalid Date")
                        } else {
                            if (Utils.compareDates(current, s.toString()) < 0) {
                                //  binding.edittextDob.setError("Invalid Date")
                                Utils.showToast(
                                    this@TemperatureDetailActivity,
                                    resources.getString(R.string.toast_enter_valid_date),
                                    true
                                )
                                startDateValidation = false
                            } else {
                                startDateValidation = true
                                ExitingDate = startDate!!.text.toString()
                                var localTime: Date? = null
                                var upToNCharacters: String = ""
                                try {
                                    localTime =
                                        SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(
                                            startDate!!.text.toString()
                                        )
                                    Log.e("timestamp", localTime!!.time.toString())

                                    val formatter: DateFormat = SimpleDateFormat("MM/dd/yyyy")
                                    val date = formatter.parse(startDate!!.text.toString()) as Date
                                    System.out.println("Today is " + date.time)
                                    Log.e("NEWWW", date.time.toString())
                                    upToNCharacters =
                                        localTime.time.toString()
                                            .substring(
                                                0,
                                                Math.min(localTime.time.toString().length, 10)
                                            )
                                    //shareStartDate = upToNCharacters.toInt()
                                    val tz = TimeZone.getDefault()
                                    val now = Date()
                                    val offsetFromUtc = tz.getOffset(now.time) / 1000
                                    val tsLong = upToNCharacters.toInt()
                                    val timeStamp = tsLong + offsetFromUtc
                                    startTimeStamp = timeStamp
                                    Log.e("timestamp1", upToNCharacters)
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }
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
                    startDate!!.setText(current)
                    startDate!!.setSelection(if (sel < current.length) sel else current.length)
                }
            }
        })

        endDate!!.addTextChangedListener(object : TextWatcher {

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
                                this@TemperatureDetailActivity,
                                resources.getString(R.string.toast_enter_valid_date),
                                true
                            )
                            endDateValidation = false
                            //binding.edittextDob.setError("Invalid Date")
                        } else {
                            if (Utils.compareDates(current, s.toString()) < 0) {
                                //  binding.edittextDob.setError("Invalid Date")
                                Utils.showToast(
                                    this@TemperatureDetailActivity,
                                    resources.getString(R.string.toast_enter_valid_date),
                                    true
                                )
                                endDateValidation = false
                            } else {
                                endDateValidation = true
                                var localTime: Date? = null
                                var upToNCharacters: String = ""
                                try {
                                    localTime =
                                        SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(
                                            endDate!!.text.toString()
                                        )
                                    Log.e("timestamp", localTime!!.time.toString())

                                    val formatter: DateFormat = SimpleDateFormat("MM/dd/yyyy")
                                    val date = formatter.parse(endDate!!.text.toString()) as Date
                                    System.out.println("Today is " + date.time)
                                    Log.e("NEWWW", date.time.toString())
                                    upToNCharacters =
                                        localTime!!.time.toString()
                                            .substring(
                                                0,
                                                Math.min(localTime!!.time.toString().length, 10)
                                            )
                                    //shareEndDate = upToNCharacters.toInt()
                                    val tz = TimeZone.getDefault()
                                    val now = Date()
                                    val offsetFromUtc = tz.getOffset(now.time) / 1000
                                    val tsLong = upToNCharacters.toInt()
                                    val timeStamp = tsLong + offsetFromUtc
                                    endTimeStamp = timeStamp
                                    Log.e("timestamp1", upToNCharacters)
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }
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
                    endDate!!.setText(current)
                    endDate!!.setSelection(if (sel < current.length) sel else current.length)
                }
            }
        })

        startDate!!.setOnClickListener {
            ExitingData = ExitingDate
            ExitingDate = ""
            datePickerData()
        }
        endDate!!.setOnClickListener {
            if (startDate!!.text.toString().equals("")) {
                Utils.showToast(this, getString(R.string.toast_select_start_date), true)
            } else {
                datePickerData()
            }
        }
        imageViewEndDateCalender.setOnClickListener {
            if (startDate!!.text.toString().equals("")) {
                Utils.showToast(this, getString(R.string.toast_select_start_date), true)
            } else {
                datePickerData()
            }
        }
        imageViewStartDateCalender.setOnClickListener {
            ExitingData = ExitingDate
            ExitingDate = ""
            datePickerData()
        }
        closeButton.setOnClickListener {
            alertDialog.dismiss()
            binding.tabLayout.getTabAt(0)!!.select()
            ExitingDate = ""
        }
        applyFilter.setOnClickListener {
            val start = startDate!!.text.toString()
            val end = endDate!!.text.toString()


            if ((!startDate!!.text.toString().equals("")) || (!endDate!!.text.toString()
                    .equals(""))
            ) {
                if (!startDate!!.text.toString().equals("")) {
                    if (!endDate!!.text.toString()
                            .equals("")
                    ) {
                        val daysCount = Utils.getDaysBetweenDates(start, end)
                        if (daysCount <= 30) {
                            graphInitialization = false
                            alertDialog.dismiss()
                            binding.mProgressBar.isVisible = true
                            if (progressDialogFilter == null) {
                                progressDialogFilter = Utils.getDialog(this)
                            }
                            progressDialogFilter!!.show()
                            apiCall()
                        } else {
                            Utils.showToast(this, getString(R.string.toast_date_validation), true)
                        }
                    } else {
                        Utils.showToast(this, getString(R.string.select_end_date), true)
                    }
                } else {
                    Utils.showToast(this, getString(R.string.select_start_date), true)
                }
            } else {
                Utils.showToast(this, getString(R.string.toast_filter_empty_data_validation), true)
            }

        }

        alertDialog.show()
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

    @SuppressLint("SetTextI18n")
    private fun apiCall() {
        if (Utils.isNetworkAvailable(this)) {
            mValue.clear()
            mDate.clear()
            mColor.clear()
            val data = TemperatureInputModel()
            data.setVitalId(vitalID!!.toInt())
            data.setStartdate(startTimeStamp)
            data.setEnddate(endTimeStamp)
            endTimeStamp = 0
            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(data)
            Log.e("Model---->", json.toString())
            if (binding.temperatureChart.data != null) {
                binding.temperatureChart.getData().clearValues()
                binding.temperatureChart.clear()
            }

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(json) as JsonObject

            viewModel.getTemperatureList(token!!, aJsonObject)

            viewModel.Response.observe(this, androidx.lifecycle.Observer {
                when (it) {
                    is Resource.Success -> {
                        if (it.value.getStatusCode() == 200) {
                            binding.layoutChartView.isVisible = true
                            shareStartDate = it.value.getValue()!!.data!!.getStartDate()!!
                            shareEndDate = it.value.getValue()!!.data!!.getEndDate()!!
                            Log.e(
                                "shareEndDate",
                                shareStartDate.toString() + " " + shareEndDate.toString()
                            )
                            val tz = TimeZone.getDefault()
                            val now = Date()
                            val offsetFromUtc = tz.getOffset(now.time) / 1000
                            val timeStamp =
                                it.value.getValue()!!.data!!.getStartDate()!!
                                    .toInt() - offsetFromUtc
                            if (it.value.getValue()!!.data!!.getEndDate()!!.toInt() == 0) {
                                binding.textViewHeading.text = Utils.getDateCurrentTimeZone(
                                    timeStamp.toLong(),
                                    Constants.TIMESTAMP_VITALS_DETAILS!!
                                )
                            } else {
                                val timeStamp1 =
                                    it.value.getValue()!!.data!!.getEndDate()!!
                                        .toInt() - offsetFromUtc
                                binding.textViewHeading.text = Utils.getDateCurrentTimeZone(
                                    timeStamp.toLong(),
                                    Constants.TIMESTAMP_VITALS_DETAILS!!
                                ) + " ~ " + Utils.getDateCurrentTimeZone(
                                    timeStamp1.toLong(),
                                    Constants.TIMESTAMP_VITALS_DETAILS
                                )
                            }

                            Log.e("EndDate", it.value.getValue()!!.data!!.getEndDate()!!.toString())
                            val typeValue: String =
                                it.value.getValue()!!.data!!.getGraphType()!!.graphTypeData!!

                            Log.e(
                                "Server",
                                it.value.getValue()!!.data!!.getStartDate()
                                    .toString() + " -- " + it.value.getValue()!!.data!!.getEndDate()
                            )
                            chartDateTimeFormat = it.value.getValue()!!.data!!.getGraphDateFormat()
                            graphMin = it.value.getValue()!!.data!!.getGraphMinVal()
                            graphMax = it.value.getValue()!!.data!!.getGraphMaxVal()
                            binding.textViewContentTitle.text = "About " + name
                            binding.textViewContent.text =
                                it.value.getValue()!!.data!!.getGraphDesc()
                            mValue =
                                it.value.getValue()!!.data!!.getBarData() as ArrayList<BarDatum>
                            mDate =
                                it.value.getValue()!!.data!!.getBarTimestamp() as ArrayList<BarTimestamp>
                            mColor = it.value.getValue()!!.data!!
                                .getBarColorSchemes() as ArrayList<BarColorScheme>
                            Log.e("mValue", mValue.size.toString())

                            if (mValue.size == 0) {
                                Handler().postDelayed({
                                    if (progressDialogFilter != null && progressDialogFilter!!.isShowing) {
                                        progressDialogFilter!!.dismiss()
                                    }
                                }, 1000)
                                binding.mProgressBar.isVisible = false
                                ExitingDate = ""
                                binding.textViewHeading.isVisible = false
                                binding.buttonShare.isVisible = false
                                binding.buttonAddVitals.isVisible = false
                                binding.buttonAddVitals1.isVisible = true
                            } else if (mValue.size == mDate.size) {
                                if (progressDialogFilter != null && progressDialogFilter!!.isShowing) {
                                    progressDialogFilter!!.dismiss()
                                }
                                if (binding.temperatureChart.data != null) {
                                    binding.temperatureChart.getData().clearValues()
                                    binding.temperatureChart.clear()
                                }
                                initGraphValue(mValue, mDate, mColor)
                                binding.mProgressBar.isVisible = false
                                ExitingDate = ""
                                binding.textViewContent.isVisible = true
                                binding.textViewHeading.isVisible = true
                                binding.buttonShare.isVisible = true
                                binding.buttonAddVitals.isVisible = true
                                binding.buttonAddVitals1.isVisible = false
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
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
    }

    private fun datePickerData() {
        val c1 = Calendar.getInstance()
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
            c1[Calendar.YEAR] = mYear!!
            c1[Calendar.MONTH] = mMonth!!
            c1[Calendar.DAY_OF_MONTH] = mDay!!
        }


        val datePickerDialog = DatePickerDialog(
            this,
            R.style.my_dialog_theme,
            object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(
                    view: DatePicker?,
                    year: Int,
                    monthOfYear: Int,
                    dayOfMonth: Int
                ) {
                    var month = (monthOfYear + 1).toString()
                    var day = dayOfMonth.toString()
                    if (month.length == 1) {
                        month = "0" + month
                    }
                    if (day.length == 1) {
                        day = "0" + day
                    }
                    if (ExitingDate.equals("")) {
                        startDate!!.setText(month + "/" + day + "/" + year)
                    } else {
                        endDate!!.setText(month + "/" + day + "/" + year)
                    }

                    Log.e(
                        "edittextDob",
                        (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
                    )
                }
            },
            mYear!!,
            mMonth!!,
            mDay!!
        )
        if (ExitingDate.equals("")) {
            //datePickerDialog.datePicker.minDate = c1.timeInMillis
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000)
            //datePickerDialog.getDatePicker().setLayoutMode(1)
            datePickerDialog.show()
        } else {
            datePickerDialog.datePicker.minDate = c1.timeInMillis
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000)
            //datePickerDialog.getDatePicker().setLayoutMode(1)
            datePickerDialog.show()
        }
        datePickerDialog.setButton(
            DialogInterface.BUTTON_NEGATIVE,
            getString(R.string.cancel),
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                        ExitingDate = ExitingData
                        startDate!!.setText(ExitingDate)
                    }
                }
            })
    }

    private fun getGraphView() {

        if (Utils.isNetworkAvailable(this)) {
            if (progressDialog == null) {
                progressDialog = Utils.getDialog(this)
            }

            progressDialog!!.show()
            if (lastDate.equals("0")) {
                lastDate = "1"
            }
            val data = BloodPressureInputModel()
            data.setVitalId(vitalID!!.toInt())
            data.setStartdate(lastDate!!.toInt())
            var json: String? = ""
            val gson = Gson()
            json = gson.toJson(data)
            Log.e("Model---->", json.toString())
            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(json) as JsonObject

            viewModel.getTemperatureList(token!!, aJsonObject)


            viewModel.Response.observe(this, androidx.lifecycle.Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDialog != null && progressDialog!!.isShowing) {
                            progressDialog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            binding.layoutChartView.isVisible = true
                            if (it.value.getValue()!!.status == 1) {
                                val tz = TimeZone.getDefault()
                                val now = Date()
                                val offsetFromUtc = tz.getOffset(now.time) / 1000
                                val timeStamp =
                                    it.value.getValue()!!.data!!.getStartDate()!!
                                        .toInt() - offsetFromUtc
                                binding.textViewContentTitle.text = "About " + name
                                binding.textViewContent.text =
                                    it.value.getValue()!!.data!!.getGraphDesc()
                                val typeValue: String =
                                    it.value.getValue()!!.data!!.getGraphType()!!.graphTypeData!!
                                binding.textViewHeading.text = Utils.getDateCurrentTimeZone(
                                    timeStamp!!.toLong(),
                                    Constants.TIMESTAMP_VITALS_DETAILS!!
                                ) +
                                        " ~ " + typeValue
                                chartDateTimeFormat =
                                    it.value.getValue()!!.data!!.getGraphDateFormat()
                                chartMetrics = it.value.getValue()!!.data!!.getGraphMetrics()!!
                                graphMin = it.value.getValue()!!.data!!.getGraphMinVal()
                                graphMax = it.value.getValue()!!.data!!.getGraphMaxVal()
                                mValue =
                                    it.value.getValue()!!.data!!.getBarData() as ArrayList<BarDatum>
                                mDate =
                                    it.value.getValue()!!.data!!.getBarTimestamp() as ArrayList<BarTimestamp>
                                mColor = it.value.getValue()!!.data!!
                                    .getBarColorSchemes() as ArrayList<BarColorScheme>
                                Log.e("mValue", mValue.size.toString())
                                if (mValue.size == 0) {
                                    binding.textViewHeading.isVisible = false
                                    binding.buttonShare.isVisible = false
                                    binding.buttonAddVitals.isVisible = false
                                    binding.buttonAddVitals1.isVisible = true
                                    binding.tabLayout.isVisible = true
                                } else if (mValue.size == mDate.size) {

                                    initGraphValue(mValue, mDate, mColor)
                                    binding.buttonAddVitals1.isVisible = false
                                    ExitingDate = ""
                                    binding.textViewContent.isVisible = true
                                    binding.textViewHeading.isVisible = true
                                    binding.buttonShare.isVisible = true
                                    binding.buttonAddVitals.isVisible = true
                                }

                            } else {
                                binding.textViewHeading.isVisible = false
                                binding.buttonShare.isVisible = false
                                binding.tabLayout.isVisible = true
                                binding.buttonAddVitals.isVisible = false
                                binding.buttonAddVitals1.isVisible = true
                            }
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
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
    }

    private fun ShareVitals() {
        if (Utils.isNetworkAvailable(this)) {
            if (shareStartDate == 0 && shareEndDate == 0) {
                val data = ShareVitalsInputModel()
                data.setVitalId(vitalID!!.toInt())
                data.setFromDate(lastDate!!.toInt())
                data.setToDate(lastDate!!.toInt())
                data.setType(0)
                val gson = Gson()
                var json: String? = ""
                json = gson.toJson(data)
                Log.e("Model---->", json.toString())

                val aJsonParser = JsonParser()
                val aJsonObject = aJsonParser.parse(json) as JsonObject
                viewModel.shareVitalsData(token!!, aJsonObject)
            } else {
                val data = ShareVitalsInputModel()
                data.setVitalId(vitalID!!.toInt())
                data.setFromDate(shareStartDate!!.toInt())
                data.setToDate(shareEndDate!!.toInt())
                data.setType(0)
                val gson = Gson()
                var json: String? = ""
                json = gson.toJson(data)
                Log.e("Model---->", json.toString())

                val aJsonParser = JsonParser()
                val aJsonObject = aJsonParser.parse(json) as JsonObject
                viewModel.shareVitalsData(token!!, aJsonObject)
            }



            viewModel.Response1.observe(this, androidx.lifecycle.Observer {
                when (it) {
                    is Resource.Success -> {
                        if (it.value.getStatusCode() == 200) {
                            if (shareBooleanData) {
                                shareBooleanData = false
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, it.value.getValue()!!.data!!)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                startActivity(shareIntent)
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

        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }

    }

    override fun onResume() {
        super.onResume()
        if (progressDoalog != null && progressDoalog!!.isShowing) {
            progressDoalog!!.dismiss()
        }
        getGraphView()
        //newly added
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
        binding.mNestedScrollView.fullScroll(ScrollView.FOCUS_UP)
    }


    @SuppressLint("SimpleDateFormat")
    private fun initGraphValue(
        mValue: ArrayList<BarDatum>,
        mDate: ArrayList<BarTimestamp>,
        mColor: ArrayList<BarColorScheme>
    ) {
        binding.temperatureChart.setDrawBarShadow(false)
        binding.temperatureChart.setDrawValueAboveBar(true)
        binding.temperatureChart.getDescription().setEnabled(false)
        binding.temperatureChart.setMaxVisibleValueCount(60)
        binding.temperatureChart.setPinchZoom(false)
        binding.temperatureChart.setDrawGridBackground(false)
        binding.temperatureChart.setFitBars(false)
        binding.temperatureChart.getViewPortHandler().setMaximumScaleX(2f)
        binding.temperatureChart.getViewPortHandler().setMaximumScaleY(1f)
        val trans: Transformer = binding.temperatureChart.getTransformer(YAxis.AxisDependency.LEFT)
        binding.temperatureChart.setXAxisRenderer(
            CustomXAxisRenderer(
                binding.temperatureChart.getViewPortHandler(),
                binding.temperatureChart.getXAxis(), trans
            )
        )
        binding.temperatureChart.setExtraBottomOffset(30f)


        val xAxis: XAxis = binding.temperatureChart.getXAxis()
        //val custom1: IAxisValueFormatter = MyAxisValueFormatter(chartDateTimeFormat)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        //xAxis.valueFormatter = custom1
        xAxis.setDrawGridLines(true)
        xAxis.granularity = 1f


        val custom: IAxisValueFormatter = MyAxisValueFormatter(chartMetrics)
        val leftAxis: YAxis = binding.temperatureChart.getAxisLeft()
        leftAxis.setLabelCount(10, false)
        leftAxis.valueFormatter = custom
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.spaceTop = 5f
        leftAxis.axisMinimum = graphMin!!.toFloat()
        leftAxis.axisMaximum = graphMax!!.toFloat()


        val rightAxis: YAxis = binding.temperatureChart.getAxisRight()
        rightAxis.isEnabled = false

        val l: Legend = binding.temperatureChart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.form = Legend.LegendForm.SQUARE
        l.formSize = 9f
        l.textSize = 11f
        l.xEntrySpace = 4f


        setData(mValue, xAxis, mDate, mColor)

        /*if (mDate.size != 0) {
            xAxis.valueFormatter = object : IAxisValueFormatter {
                private val mFormat =
                    SimpleDateFormat(Constants.TIMESTAMP_VITAL_TIME, Locale.ENGLISH)

                override fun getFormattedValue(value: Float, axis: AxisBase): String {
                    //return (mDate.get(value.toInt()).timestamp!!)
                    return mFormat.format(Date(mDate.get(value.toInt()).timestamp!!.toLong() * 1000))
                }
            }
        }*/

        // add a nice and smooth animation
        binding.temperatureChart.animateY(1000)
        binding.temperatureChart.invalidate()
    }

    //private fun setData(date: Int, range: Float) {
    private fun setData(
        range: ArrayList<BarDatum>,
        xAxis: XAxis,
        mDateValue: ArrayList<BarTimestamp>,
        mColorValue: ArrayList<BarColorScheme>
    ) {

        if (mDateValue.size != 0) {
            Log.e("chartDateTimeFormat", chartDateTimeFormat.toString())
            xAxis.valueFormatter = object : IAxisValueFormatter {
                private val mFormat =
                    //SimpleDateFormat(Constants.TIMESTAMP_VITAL_TIME, Locale.ENGLISH)
                    SimpleDateFormat("MM/dd \nhh:mm \na", Locale.ENGLISH)
                //SimpleDateFormat("MM/dd \nhh:mm a", Locale.ENGLISH)

                override fun getFormattedValue(value: Float, axis: AxisBase): String {
                    var timeStamp: Int? = 0
                    if (value.toInt() < mDateValue.size) {
                        val tz = TimeZone.getDefault()
                        val now = Date()
                        val offsetFromUtc = tz.getOffset(now.time) / 1000
                        timeStamp =
                            mDateValue.get(
                                value.toInt()
                            ).timestamp!!.toInt() - offsetFromUtc
                    } else {
                        timeStamp = 0
                    }

                    return if (value.toInt() < mDateValue.size) mFormat.format(
                        Date(
                            timeStamp.toLong() * 1000
                        )
                    ) else "0"
                    //return mFormat.format(Date(mDateValue.get(value.toInt()).timestamp!!.toLong() * 1000))
                }
            }
        }

        val values = ArrayList<BarEntry>()
        for (i in 0 until range.size) {
            values.add(BarEntry(i.toFloat(), range[i].barValue!!))
        }

        val set1: BarDataSet
        if (binding.temperatureChart.getData() != null &&
            binding.temperatureChart.getData().getDataSetCount() > 0
        ) {
            set1 = binding.temperatureChart.getData().getDataSetByIndex(0) as BarDataSet
            set1.values = values
            binding.temperatureChart.getData().notifyDataChanged()
            binding.temperatureChart.notifyDataSetChanged()
        } else {
            val dataSets = ArrayList<IBarDataSet>()
            if (vitalID.equals("2")) {
                set1 = BarDataSet(values, name)
                set1.setDrawIcons(false)
                set1.valueFormatter = NonZeroChartValueFormatter(0)
                var ColorValue: String? = ""
                for (i in 0 until mColorValue.size) {
                    ColorValue = "#" + mColorValue[i].colorHexcode
                    set1.color = Color.parseColor(ColorValue)
                }
                dataSets.add(set1)
            } else if (vitalID.equals("3")) {
                set1 = BarDataSet(values, name)
                set1.setDrawIcons(false)
                set1.valueFormatter = MyValueFormatter()
                var ColorValue: String? = ""
                for (i in 0 until mColorValue.size) {
                    ColorValue = "#" + mColorValue[i].colorHexcode
                    set1.color = Color.parseColor(ColorValue)
                }
                dataSets.add(set1)
            } else if (vitalID.equals("6")) {
                set1 = BarDataSet(values, name)
                set1.setDrawIcons(false)
                set1.valueFormatter = MyValueFormatter()
                var ColorValue: String? = ""
                for (i in 0 until mColorValue.size) {
                    ColorValue = "#" + mColorValue[i].colorHexcode
                    set1.color = Color.parseColor(ColorValue)
                }
                dataSets.add(set1)
            }
            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.barWidth = 0.5f
            binding.temperatureChart.setData(data)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data!=null){
            if (requestCode == 123) {
                lastDate = data!!.getStringExtra(Constants.VITALS_DATE).toString()
                Log.e("lastDate->", lastDate.toString())
            }
        }
    }
}
