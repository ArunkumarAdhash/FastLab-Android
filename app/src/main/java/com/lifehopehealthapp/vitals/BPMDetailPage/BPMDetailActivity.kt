package com.lifehopehealthapp.vitals.BPMDetailPage

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
import androidx.core.view.isVisible
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.Fill
import com.github.mikephil.charting.utils.Transformer
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
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
import com.lifehopehealthapp.vitals.VitalsCategoryList.VitalsCategoryListActivity
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BPMDetailActivity : BaseActivity<BPMDetailViewModel, BPMDetailModel>() {

    private var lastDate: String? = ""
    private var mPrefs: SharedPreferences? = null
    private var vitalID: String? = ""
    private var name: String? = ""
    private var lastValue: String? = ""
    private var token: String? = ""
    private var image: String? = ""
    private var progressDoalog: Dialog? = null
    private var progressDialog: Dialog? = null
    private var graphMin: Int? = 0
    private var graphMax: Int? = 0
    private var shareBooleanData: Boolean = false
    private var mYear: Int? = 0
    private var mMonth: Int? = 0
    private var mDay: Int? = 0
    private var ExitingDate: String? = ""
    private var startDate: EditText? = null
    private var endDate: EditText? = null
    private var startTimeStamp: Int? = 0
    private var endTimeStamp: Int? = 0
    private var graphInitialization: Boolean = true
    private var chartMetrics: String = ""
    private var chartDateTimeFormat: String? = ""
    private var progressDialogFilter: Dialog? = null
    private var ExitingData: String? = ""
    private var mValue: ArrayList<BarDatum> = ArrayList<BarDatum>()
    private var mDate: ArrayList<BarTimestamp> = ArrayList<BarTimestamp>()
    private var mColor: ArrayList<BarColorScheme> = ArrayList<BarColorScheme>()
    private var shareStartDate: Int? = 0
    private var shareEndDate: Int? = 0

    private lateinit var binding: ActivityTemperatureDetailsPageBinding

    override fun getViewModel() = BPMDetailViewModel::class.java

    override fun getActivityRepository() =
        BPMDetailModel(
            remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
        )

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
        binding.imageViewVitalsImage.loadSvg(image)
        binding.layoutHeader.textViewLabel.text = name

        binding.layoutHeader.imageviewBackArrow.setOnClickListener {
            onBackPressed()
        }
        binding.layoutHeader.imageViewHistory.setOnClickListener {
            filterPopUp()
            /*intent = Intent(
                this@TemperatureDetailActivity,
                VitalsHistoryActivity::class.java
            )
            intent.putExtra(Constants.VITALS_ID, vitalID)
            intent.putExtra(Constants.VITALS_NAME, name)
            if (Utils.isLollipopHigher() && it != null) {
                val pairs: Array<Pair<View, String>> =
                    TransitionHelper.createSafeTransitionParticipants(
                        this@TemperatureDetailActivity,
                        false,
                        Pair(it, getString(R.string.trans_tool_bar_title))
                    )
                val transitionActivityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@TemperatureDetailActivity,
                        *pairs
                    )
                startActivity(intent, transitionActivityOptions.toBundle())
            } else {
                startActivity(intent)
            }*/
        }

        binding.buttonShare.setOnClickListener {
            shareBooleanData = true
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            ShareVitals()
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
                                this@BPMDetailActivity,
                                resources.getString(R.string.toast_enter_valid_date),
                                true
                            )
                            startDateValidation = false
                            //binding.edittextDob.setError("Invalid Date")
                        } else {
                            if (Utils.compareDates(current, s.toString()) < 0) {
                                //  binding.edittextDob.setError("Invalid Date")
                                Utils.showToast(
                                    this@BPMDetailActivity,
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
                                    shareStartDate = upToNCharacters.toInt()
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
                                this@BPMDetailActivity,
                                resources.getString(R.string.toast_enter_valid_date),
                                true
                            )
                            endDateValidation = false
                            //binding.edittextDob.setError("Invalid Date")
                        } else {
                            if (Utils.compareDates(current, s.toString()) < 0) {
                                //  binding.edittextDob.setError("Invalid Date")
                                Utils.showToast(
                                    this@BPMDetailActivity,
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
                                    shareEndDate = upToNCharacters.toInt()
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

    fun ImageView.loadSvg(url: String?) {
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }

    @SuppressLint("SetTextI18n")
    private fun apiCall() {
        if (Utils.isNetworkAvailable(this)) {
            if (progressDialogFilter == null) {
                progressDialogFilter = Utils.getDialog(this)
            }
            progressDialogFilter!!.show()
            mValue.clear()
            mDate.clear()
            mColor.clear()
            val data = TemperatureInputModel()
            data.setVitalId(vitalID!!.toInt())
            data.setStartdate(startTimeStamp)
            data.setEnddate(endTimeStamp)

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
                        if (progressDialogFilter != null && progressDialogFilter!!.isShowing) {
                            progressDialogFilter!!.dismiss()
                        }

                        if (it.value.getStatusCode()==200){
                            val tz = TimeZone.getDefault()
                            val now = Date()
                            val offsetFromUtc = tz.getOffset(now.time) / 1000
                            val timeStamp =
                                it.value.getValue()!!.data!!.getStartDate()!!.toInt() - offsetFromUtc
                            if (it.value.getValue()!!.data!!.getEndDate()!!.toInt() == 0) {
                                binding.textViewHeading.text = Utils.getDateCurrentTimeZone(
                                    timeStamp.toLong(),
                                    Constants.TIMESTAMP_VITALS_DETAILS!!
                                )
                            } else {
                                val timeStamp1 =
                                    it.value.getValue()!!.data!!.getEndDate()!!.toInt() - offsetFromUtc
                                binding.textViewHeading.text = Utils.getDateCurrentTimeZone(
                                    timeStamp.toLong(),
                                    Constants.TIMESTAMP_VITALS_DETAILS!!
                                ) + " ~ " + Utils.getDateCurrentTimeZone(
                                    timeStamp1.toLong(),
                                    Constants.TIMESTAMP_VITALS_DETAILS
                                )
                            }

                            chartDateTimeFormat = it.value.getValue()!!.data!!.getGraphDateFormat()
                            graphMin = it.value.getValue()!!.data!!.getGraphMinVal()
                            graphMax = it.value.getValue()!!.data!!.getGraphMaxVal()

                            mValue = it.value.getValue()!!.data!!.getBarData() as ArrayList<BarDatum>
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
                                binding.textViewContent.isVisible = false
                                binding.textViewHeading.isVisible = false
                                binding.buttonShare.isVisible = false
                                binding.tabLayout.isVisible=true
                            } else if (mValue.size == mDate.size) {
                                if (progressDialogFilter != null && progressDialogFilter!!.isShowing) {
                                    progressDialogFilter!!.dismiss()
                                }
                                binding.textViewContent.text = it.value.getValue()!!.data!!.getGraphDesc()
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
                            }
                        }else if (it.value.getStatusCode() == 401) {
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
            if (graphInitialization) {
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

            } else if (graphInitialization) {

                //viewModel.getTemperatureList(token!!, aJsonObject)

            }


            viewModel.Response.observe(this, androidx.lifecycle.Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDialog != null && progressDialog!!.isShowing) {
                            progressDialog!!.dismiss()
                        }

                        if (it.value.getStatusCode()==200){
                            val tz = TimeZone.getDefault()
                            val now = Date()
                            val offsetFromUtc = tz.getOffset(now.time) / 1000
                            val timeStamp = lastDate!!.toInt() - offsetFromUtc
                            val typeValue: String =
                                it.value.getValue()!!.data!!.getGraphType()!!.graphTypeData!!
                            binding.textViewHeading.text = Utils.getDateCurrentTimeZone(
                                lastDate!!.toLong(),
                                Constants.TIMESTAMP_VITALS_DETAILS!!
                            ) +
                                    " ~ " + typeValue
                            chartDateTimeFormat = it.value.getValue()!!.data!!.getGraphDateFormat()
                            chartMetrics = it.value.getValue()!!.data!!.getGraphMetrics()!!
                            graphMin = it.value.getValue()!!.data!!.getGraphMinVal()
                            graphMax = it.value.getValue()!!.data!!.getGraphMaxVal()
                            mValue = it.value.getValue()!!.data!!.getBarData() as ArrayList<BarDatum>
                            mDate =
                                it.value.getValue()!!.data!!.getBarTimestamp() as ArrayList<BarTimestamp>
                            mColor = it.value.getValue()!!.data!!
                                .getBarColorSchemes() as ArrayList<BarColorScheme>
                            Log.e("mValue", mValue.size.toString())
                            if (mValue.size == 0) {
                                binding.textViewContent.isVisible = false
                                binding.textViewHeading.isVisible = false
                                binding.buttonShare.isVisible = false
                            } else if (mValue.size == mDate.size) {
                                binding.textViewContent.text = it.value.getValue()!!.data!!.getGraphDesc()
                                initGraphValue(mValue, mDate, mColor)
                                ExitingDate = ""
                                binding.textViewContent.isVisible = true
                                binding.textViewHeading.isVisible = true
                                binding.buttonShare.isVisible = true
                            }
                        }else if (it.value.getStatusCode() == 401) {
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
                Log.e("Model1---->", json.toString())

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
                Log.e("Model1---->", json.toString())

                val aJsonParser = JsonParser()
                val aJsonObject = aJsonParser.parse(json) as JsonObject
                viewModel.shareVitalsData(token!!, aJsonObject)
            }

            viewModel.Response1.observe(this, androidx.lifecycle.Observer {
                when (it) {
                    is Resource.Success -> {
                        if (it.value.getStatusCode()==200){
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
                        }else if (it.value.getStatusCode() == 401) {
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
        binding.temperatureChart.setExtraBottomOffset(50f)

        val xAxis: XAxis = binding.temperatureChart.getXAxis()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
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
        mColor: ArrayList<BarColorScheme>
    ) {

        if (mDateValue.size != 0) {
            Log.e("chartDateTimeFormat", chartDateTimeFormat.toString())
            xAxis.valueFormatter = object : IAxisValueFormatter {
                private val mFormat =
                    //SimpleDateFormat(Constants.TIMESTAMP_VITAL_TIME, Locale.ENGLISH)
                    SimpleDateFormat(chartDateTimeFormat, Locale.ENGLISH)
                //SimpleDateFormat("MM/dd \nhh:mm a", Locale.ENGLISH)

                override fun getFormattedValue(value: Float, axis: AxisBase): String {
                    //return (mDate.get(value.toInt()).timestamp!!)
                    return mFormat.format(Date(mDateValue.get(value.toInt()).timestamp!!.toLong() * 1000))
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
            set1 = BarDataSet(values, "Heart Rate")
            set1.setDrawIcons(false)
            //set1.valueFormatter = NonZeroChartValueFormatter(0)
            val gradientFill: MutableList<Fill> = ArrayList()
            for (j in 0 until range.size) {

                //gradientFill.add(Fill(mColor[j].colorHexcode!!.toInt(), mColor[j].colorHexcode!!.toInt()))
                Log.e("startColor1", this.mColor[j].colorHexcode!!.toString())
            }


            /*val startColor1 = ContextCompat.getColor(
                this,
                android.R.color.holo_orange_light
            )
            val startColor2 =
                ContextCompat.getColor(this, android.R.color.holo_blue_light)
            val startColor3 = ContextCompat.getColor(
                this,
                android.R.color.holo_orange_light
            )
            val startColor4 =
                ContextCompat.getColor(this, android.R.color.holo_green_light)
            val startColor5 =
                ContextCompat.getColor(this, android.R.color.holo_red_light)
            val endColor1 =
                ContextCompat.getColor(this, android.R.color.holo_blue_dark)
            val endColor2 =
                ContextCompat.getColor(this, android.R.color.holo_purple)
            val endColor3 =
                ContextCompat.getColor(this, android.R.color.holo_green_dark)
            val endColor4 =
                ContextCompat.getColor(this, android.R.color.holo_red_dark)
            val endColor5 =
                ContextCompat.getColor(this, android.R.color.holo_orange_dark)
            val gradientFills: MutableList<Fill> = ArrayList()
            gradientFills.add(Fill(startColor1, endColor1))
            gradientFills.add(Fill(startColor2, endColor2))
            gradientFills.add(Fill(startColor3, endColor3))
            gradientFills.add(Fill(startColor4, endColor4))
            gradientFills.add(Fill(startColor5, endColor5))
            set1.fills = gradientFills*/
            val dataSets = ArrayList<IBarDataSet>()
            var ColorValue: String? = ""
            for (i in 0 until mColor.size) {
                ColorValue = "#" + mColor[i].colorHexcode
                set1.color = Color.parseColor(ColorValue)
            }
            dataSets.add(set1)
            val data = BarData(dataSets)
            data.setValueFormatter(LargeValueFormatter())
            data.setValueTextSize(10f)
            data.barWidth = 0.5f
            binding.temperatureChart.setData(data)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}