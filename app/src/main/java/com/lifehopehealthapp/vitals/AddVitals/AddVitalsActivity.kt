package com.lifehopehealthapp.vitals.AddVitals

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.ResponseModel.TemperatureInputModel
import com.lifehopehealthapp.ResponseModel.VitalsInputModel
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityAddVitalsBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.utils.Utils.PerfectDecimal
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AddVitalsActivity : BaseActivity<AddVitalsViewModel, AddVitalsModel>() {

    private var mPrefs: SharedPreferences? = null
    private var vitalID: String? = ""
    private var vitalName: String? = ""
    private var glucoseList: String? = ""
    private var image: String? = ""
    private var selectDate: String? = ""
    private var selectTime: String? = ""
    private var mCurrentDate: String? = ""
    private var mGlucoseDate: String? = ""

    private var token: String? = ""
    private var mYear: Int? = 0
    private var mMonth: Int? = 0
    private var mDay: Int? = 0
    private var mHours: Int? = 0
    private var mMins: Int? = 0
    private var mSpinnerPosition: Int? = 0
    private var progressDoalog: Dialog? = null
    private var mHeartRateBoolean: Boolean = false
    private var mBodyTemperatureBoolean: Boolean = false
    private var mOxygenLevelBoolean: Boolean = false
    private var mSystolicBoolean: Boolean = false
    private var mDiastolicBoolean: Boolean = false
    private var mWeightBoolean: Boolean = false
    private var mGlucoseBoolean: Boolean = false
    private var mGlucoseValue: Int? = 0
    private var mSelectDate: String? = ""
    private var progressDialog: Dialog? = null
    private var mGlucoseListName: ArrayList<String> = ArrayList<String>()

    override fun getViewModel() = AddVitalsViewModel::class.java

    override fun getActivityRepository() =
        AddVitalsModel(
            remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
        )

    private lateinit var binding: ActivityAddVitalsBinding

    private fun showKeyboard(temperature: AppCompatEditText) {
        val imm =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(temperature, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddVitalsBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        vitalName = intent.getStringExtra(Constants.VITALS_NAME)
        vitalID = intent.getStringExtra(Constants.VITALS_ID)
        glucoseList = intent.getStringExtra(Constants.VITALS_GLUCOSE_LIST)
        image = intent.getStringExtra(Constants.VITALS_IMAGE)
        binding.textViewHeading.text = vitalName
        Log.e("glucoseList", glucoseList.toString())
        val mGlucoseID: ArrayList<Int> = ArrayList()
        val mGlucoseType: ArrayList<String> = ArrayList()
        val mGlucoseAvaible: ArrayList<Int> = ArrayList()
        val tsLong = System.currentTimeMillis() / 1000


        val sdf = SimpleDateFormat(Constants.TIMESTAMP_VITALS_DETAILS)
        var currentDate = sdf.format(Date())
        val sdf1 = SimpleDateFormat(Constants.TIMESTAMP_DOB)
        val currentDate1 = sdf1.format(Date())
        binding.textViewDate.text = currentDate
        mCurrentDate = currentDate1
        val sdf2 = SimpleDateFormat(Constants.TIMESTAMP_VITAL_TIME)
        val currentTime = sdf2.format(Date())
        if (currentTime.contains("am")) {
            val newString: String = currentTime.replace("am", "AM")
            binding.textViewTime.text = newString
        } else {
            val newString: String = currentTime.replace("pm", "PM")
            binding.textViewTime.text = newString
        }
        selectTime = currentTime

        binding.textViewDate.setOnClickListener {
            datePickerData()
        }

        binding.imageviewClose.setOnClickListener {
            finish()
        }
        val mTimePicker: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        mTimePicker = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                var am_pm = ""
                var hourOfDay = hourOfDay
                when {
                    hourOfDay == 0 -> {
                        hourOfDay += 12
                        am_pm = "AM"
                    }
                    hourOfDay == 12 -> am_pm = "PM"
                    hourOfDay > 12 -> {
                        hourOfDay -= 12
                        am_pm = "PM"
                    }
                    else -> am_pm = "AM"
                }
                selectTime = hourOfDay.toString() + ":" + minute + " " + am_pm
                binding.textViewTime.text =
                    String.format(hourOfDay.toString() + " : " + minute + " " + am_pm)
            }
        }, hour, minute, false)

        binding.textViewTime.setOnClickListener {
            //mTimePicker.show()
            val timePickerListener =
                TimePickerDialog.OnTimeSetListener { view, hour, minute ->


                 /*   val datetime = Calendar.getInstance()
                    val c = Calendar.getInstance()
                    datetime[Calendar.HOUR_OF_DAY] = hour
                    datetime[Calendar.MINUTE] = minute
                    if (datetime.timeInMillis > c.timeInMillis) {
                        Log.e("date_check::","true")

                        //            it's after current
                    } else {
                        //            it's before current'
                        Log.e("date_check::","false")
                    }*/




                    var am_pm = ""
                    var hourOfDay = hour
                    when {
                        hourOfDay == 0 -> {
                            hourOfDay += 12
                            am_pm = "AM"
                        }
                        hourOfDay == 12 -> am_pm = "PM"
                        hourOfDay > 12 -> {
                            hourOfDay -= 12
                            am_pm = "PM"
                        }
                        else -> am_pm = "AM"
                    }
                    var mMinute: String? = ""
                    if (minute.toString().length == 1) {
                        mMinute = "0" + minute
                    }
                    selectTime = hourOfDay.toString() + ":" + minute + " " + am_pm

                    val txtDate: String = binding.textViewDate.text as String

                    if (selectDate.equals("")) {

                        val datetime = Calendar.getInstance()
                        val c = Calendar.getInstance()

                        datetime[Calendar.HOUR_OF_DAY] = hourOfDay
                        datetime[Calendar.MINUTE] = minute

                        Log.e(
                            "DateTime->",
                            datetime.timeInMillis.toString() + "  " + c.timeInMillis
                        )

                        val format = SimpleDateFormat("dd/MM/yyyy hh:mm aa")
                        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm aa")

                        val currentDate = SimpleDateFormat("dd/MM/yyyy")

                        val currentDateTime = sdf.format(Date())
                        val mCurrentDateTime: Date = format.parse(currentDateTime)
                        val mCurrentDate = currentDate.format(Date())

                        val selectDate =
                            mCurrentDate.toString() + " " + hourOfDay + ":" + minute + " " + am_pm
                        val mSelectDateTime: Date = format.parse(selectDate)
                        try {
                            if (mSelectDateTime.compareTo(mCurrentDateTime) > 0) {
                               /* Toast.makeText(
                                    applicationContext,
                                    "Future time not allowed",
                                    Toast.LENGTH_LONG
                                )
                                    .show()*/

                                Utils.showToast(this, resources.getString(R.string.future_date_not_allowed), true)


                            } else {
                                val hour = hourOfDay % 12
                                binding.textViewTime.text =
                                    java.lang.String.format(
                                        "%02d:%02d %s", if (hour == 0) 12 else hour,
                                        minute, am_pm
                                    )
                            }
                        } catch (e: Exception) {
                            // TODO: handle exception
                        }
                    } else if (selectDate.equals(mCurrentDate)) {


                        val datetime = Calendar.getInstance()
                        val c = Calendar.getInstance()
                        datetime[Calendar.HOUR_OF_DAY] = hourOfDay
                        datetime[Calendar.MINUTE] = minute
                        if (datetime.timeInMillis >= c.timeInMillis) {
                            //it's after current
                            val hour = hourOfDay % 12
                            binding.textViewTime.text =
                                java.lang.String.format(
                                    "%02d:%02d %s", if (hour == 0) 12 else hour,
                                    minute, if (hourOfDay < 12) "AM" else "PM"
                                )
                        } else {
                            //it's before current'
                           /* Toast.makeText(
                                applicationContext,
                                "Future time not allowed",
                                Toast.LENGTH_LONG
                            )
                                .show()*/

                            Utils.showToast(this, resources.getString(R.string.future_date_not_allowed), true)
                        }
                    } else {
                        binding.textViewTime.text =
                            String.format("%02d:%02d", hourOfDay, minute) + " " + am_pm
                    }

                }





            val c = Calendar.getInstance()
            val year: Int = c[Calendar.YEAR]
            val month: Int = c[Calendar.MONTH]
            val day: Int = c[Calendar.DAY_OF_MONTH]
            Log.e("selectTimeDate", year.toString() + " " + month.toString() + " " + day.toString())




            val timePickerDialog = TimePickerDialog(
                this@AddVitalsActivity, timePickerListener,
                c[Calendar.HOUR_OF_DAY], c[Calendar.MINUTE], false
            )


            timePickerDialog.show()
        }

        when (vitalID!!.toInt()) {
            2 -> {
                binding.layoutHeartRate.isVisible = true
                binding.editTextHeartRate.isVisible = true
                binding.editTextHeartRate.requestFocus()
            }
            3 -> {
                binding.layoutBodyTemperature.isVisible = true
                binding.editTextBodyTemperature.isVisible = true
                binding.editTextBodyTemperature.requestFocus()
            }
            4 -> {
                binding.layoutOxygenLevel.isVisible = true
                binding.editTextOxygenLevel.isVisible = true
                binding.editTextOxygenLevel.requestFocus()
            }
            5 -> {
                binding.layoutBloodPressure.isVisible = true
                binding.editTextSystolic.isVisible = true
                binding.editTextSystolic.requestFocus()
            }
            6 -> {
                binding.layoutWeight.isVisible = true
                binding.editTextWeight.isVisible = true
                binding.editTextWeight.requestFocus()
            }
            7 -> {
                binding.layoutBloodGlucose.isVisible = true
                binding.editTextGlucose.isVisible = true
                binding.editTextGlucose.requestFocus()
                var localTime: Date? = null
                var upToNCharacters: String = ""
                val selectDateTimeStamp: Int? = 0
                try {
                    localTime =
                        SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(
                            mCurrentDate
                        )

                    val formatter: DateFormat = SimpleDateFormat("MM/dd/yyyy")
                    val date = formatter.parse(mCurrentDate) as Date
                    System.out.println("Today is " + date.time)
                    Log.e("NEWWW", date.time.toString())
                    upToNCharacters =
                        localTime.time.toString()
                            .substring(
                                0,
                                Math.min(localTime.time.toString().length, 10)
                            )
                    val tz = TimeZone.getDefault()
                    val now = Date()
                    val offsetFromUtc = tz.getOffset(now.time) / 1000
                    val tsLong = upToNCharacters.toInt()
                    val timeStamp = tsLong + offsetFromUtc
                    Log.e("SelectTime", selectDateTimeStamp.toString())

                    if (progressDialog == null) {
                        progressDialog = Utils.getDialog(this)
                    }
                    progressDialog!!.show()
                    val data = TemperatureInputModel()
                    data.setVitalId(vitalID!!.toInt())
                    data.setStartdate(timeStamp.toInt())
                    data.setEnddate(timeStamp.toInt())
                    val gson = Gson()
                    var json: String? = ""
                    json = gson.toJson(data)
                    Log.e("Model---->", json.toString())
                    val aJsonParser = JsonParser()
                    val aJsonObject = aJsonParser.parse(json) as JsonObject

                    viewModel.getGlucoseList(token!!, aJsonObject)

                    viewModel.Response!!.observe(this, androidx.lifecycle.Observer {
                        when (it) {
                            is Resource.Success -> {
                                if (it.value.getStatusCode() == 200) {
                                    mGlucoseType.add("Select Type")
                                    mGlucoseID.add(0)
                                    for (i in it.value.getValue()!!.data!!.indices) {
                                        val id = it.value.getValue()!!.data!![i].glucoseId
                                        val isAvailable =
                                            it.value.getValue()!!.data!![i].isAvailable
                                        val type = it.value.getValue()!!.data!![i].glucoseType
                                        if (isAvailable == 0) {
                                            mGlucoseType.add(type!!)
                                            mGlucoseAvaible.add(isAvailable)
                                            mGlucoseID.add(id!!)
                                        }
                                        /*val aa =
                                            ArrayAdapter(
                                                this,
                                                android.R.layout.simple_spinner_item,
                                                mGlucoseType
                                            )
                                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        binding.spinnerMealType.setAdapter(aa)
                                        binding.spinnerMealType.prompt="Select Type"
*/
                                        val spinnerArrayAdapter =
                                            object : ArrayAdapter<String?>(
                                                this, android.R.layout.simple_spinner_item,
                                                mGlucoseType as List<String?>
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
                                        binding.spinnerMealType.setAdapter(spinnerArrayAdapter)

                                        binding.spinnerMealType.setOnItemSelectedListener(object :
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
                                                    if (mGlucoseID[position] == 0) {
                                                        mSpinnerPosition = 0
                                                        binding.buttonAddVitals.isEnabled = false
                                                    } else {
                                                        mSpinnerPosition = mGlucoseID[position]
                                                        if (mGlucoseBoolean) {
                                                            binding.buttonAddVitals.isEnabled = true
                                                        }
                                                    }
                                                }
                                            }

                                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                                        })


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
                                    Utils.showToast(
                                        this,
                                        it.value.getValue()!!.message.toString(),
                                        true
                                    )
                                }
                                if (progressDialog != null && progressDialog!!.isShowing) {
                                    progressDialog!!.dismiss()
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


                } catch (e: ParseException) {
                    e.printStackTrace()
                }

            }
        }

        binding.editTextHeartRate.addTextChangedListener(object : TextWatcher {
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

            }

            @SuppressLint("ResourceAsColor")
            override fun afterTextChanged(s: Editable) {
                val str: String = binding.editTextHeartRate.text.toString()
                if (str.isEmpty()) return

                if (str.equals("")) {
                    mHeartRateBoolean = false

                    binding.buttonAddVitals.isEnabled = false
                } else if (str.length == 1 && str.startsWith("0")) {
                    s.clear()
                } else if (str.length == 1) {
                    mHeartRateBoolean = false
                    binding.buttonAddVitals.isEnabled = false
                } else {
                    val mHeartRate: Int = str.toInt()

                    if (mHeartRate >= 60) {
                        mHeartRateBoolean = true
                        binding.buttonAddVitals.isEnabled = true
                        if (mHeartRate <= 170) {
                            mHeartRateBoolean = true
                            binding.buttonAddVitals.isEnabled = true
                        } else {
                            mHeartRateBoolean = false
                            binding.buttonAddVitals.isEnabled = false
                        }
                    } else {
                        mHeartRateBoolean = false
                        binding.buttonAddVitals.isEnabled = false
                    }
                }
            }
        })

        binding.editTextBodyTemperature.addTextChangedListener(object : TextWatcher {
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

            }

            override fun afterTextChanged(s: Editable) {
                val str: String = binding.editTextBodyTemperature.text.toString()
                if (str.isEmpty()) return
                if (str.contentEquals("0")) {
                    binding.editTextBodyTemperature.setText("")
                } else {
                    val str2: String? = PerfectDecimal(str, 3, 1)
                    if (str2 != str) {
                        binding.editTextBodyTemperature.setText(str2)
                        binding.editTextBodyTemperature.setSelection(str2!!.length)
                    }
                    val TempData: Double = str2.toDouble()
                    if (str.equals("")) {
                        mBodyTemperatureBoolean = false
                        binding.buttonAddVitals.isEnabled = false
                    } else if (str.length == 1) {
                        binding.buttonAddVitals.isEnabled = false
                        mBodyTemperatureBoolean = false
                    } else {

                        val mTemperature: Double = str2.toDouble()

                        if (mTemperature >= 94.0) {
                            mBodyTemperatureBoolean = true
                            binding.buttonAddVitals.isEnabled = true
                            if (mTemperature <= 115.7) {
                                mBodyTemperatureBoolean = true
                                binding.buttonAddVitals.isEnabled = true
                            } else {
                                binding.buttonAddVitals.isEnabled = false
                                mBodyTemperatureBoolean = false
                            }
                        } else {
                            mBodyTemperatureBoolean = false
                            binding.buttonAddVitals.isEnabled = false
                        }
                        if (str.equals("")) {
                            mBodyTemperatureBoolean = false
                            binding.buttonAddVitals.isEnabled = false
                        }
                    }
                }
            }
        })

        binding.editTextWeight.addTextChangedListener(object : TextWatcher {

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
                val str: String = binding.editTextWeight.text.toString()
                if (str.isEmpty()) return
                if (str.equals("")) {
                    mWeightBoolean = false
                    binding.buttonAddVitals.isEnabled = false
                }
                if (str.contentEquals("0")) {
                    binding.editTextWeight.setText("")
                } else {
                    val str2: String? = PerfectDecimal(str, 3, 2)

                    if (str2 != str) {
                        binding.editTextWeight.setText(str2)
                        binding.editTextWeight.setSelection(str2!!.length)
                    }
                    val weightdata: Double = str2.toDouble()

                    if (weightdata >= 66.0) {
                        mWeightBoolean = true
                        binding.buttonAddVitals.isEnabled = true
                        if (weightdata <= 500.0) {
                            mWeightBoolean = true
                            binding.buttonAddVitals.isEnabled = true
                        } else {
                            mWeightBoolean = false
                            binding.buttonAddVitals.isEnabled = false
                        }
                    } else {
                        mWeightBoolean = false
                        binding.buttonAddVitals.isEnabled = false
                    }
                    if (str.equals("")) {
                        mWeightBoolean = false
                        binding.buttonAddVitals.isEnabled = false
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.editTextOxygenLevel.addTextChangedListener(object : TextWatcher {
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

            }

            override fun afterTextChanged(s: Editable) {
                val str: String = binding.editTextOxygenLevel.text.toString()
                if (str.isEmpty()) return

                if (str.length == 1 && str.startsWith("0")) {
                    s.clear()
                }
                if (str.length == 1) {
                    mOxygenLevelBoolean = false
                    binding.buttonAddVitals.isEnabled = false
                } else {
                    val mHeartRate: Int = str.toInt()

                    if (mHeartRate >= 90) {
                        mOxygenLevelBoolean = true
                        binding.buttonAddVitals.isEnabled = true
                        if (mHeartRate <= 100) {
                            mOxygenLevelBoolean = true
                            binding.buttonAddVitals.isEnabled = true
                        } else {
                            mOxygenLevelBoolean = false
                            binding.buttonAddVitals.isEnabled = false
                        }
                    } else {
                        mOxygenLevelBoolean = false
                        binding.buttonAddVitals.isEnabled = false
                    }
                }
            }
        })

        binding.editTextSystolic.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val str: String = binding.editTextSystolic.text.toString()
                if (str.equals("")) {
                    mSystolicBoolean = false
                    binding.buttonAddVitals.isEnabled = false
                } else if (str.length == 1 && str.equals("0")) {
                    s!!.clear()
                } else {
                    val mSystolic: Int = str.toInt()

                    if (mSystolic >= 100) {
                        mSystolicBoolean = true
                        binding.buttonAddVitals.isEnabled = true
                        if (mSystolic <= 180) {
                            mSystolicBoolean = true
                            binding.buttonAddVitals.isEnabled = true
                        } else {
                            mSystolicBoolean = false
                            binding.buttonAddVitals.isEnabled = false
                        }
                    } else {
                        mSystolicBoolean = false
                        binding.buttonAddVitals.isEnabled = false
                    }
                }
                if (mSystolicBoolean && mDiastolicBoolean) {
                    binding.buttonAddVitals.isEnabled = true
                } else {
                    binding.buttonAddVitals.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        binding.editTextDiastolic.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val str: String = binding.editTextDiastolic.text.toString()
                if (str.equals("")) {
                    mDiastolicBoolean = false
                    binding.buttonAddVitals.isEnabled = false
                } else if (str.length == 1 && str.equals("0")) {
                    s!!.clear()
                } else {
                    val mSystolic: Int = str.toInt()

                    if (mSystolic >= 60) {
                        mDiastolicBoolean = true
                        binding.buttonAddVitals.isEnabled = true
                        if (mSystolic <= 110) {
                            mDiastolicBoolean = true
                            binding.buttonAddVitals.isEnabled = true
                        } else {
                            mDiastolicBoolean = false
                            binding.buttonAddVitals.isEnabled = false
                        }
                    } else {
                        mDiastolicBoolean = false
                        binding.buttonAddVitals.isEnabled = false
                    }
                }

                if (mSystolicBoolean && mDiastolicBoolean) {
                    binding.buttonAddVitals.isEnabled = true
                } else {
                    binding.buttonAddVitals.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        binding.editTextGlucose.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val str: String = binding.editTextGlucose.text.toString()
                if (str.equals("")) {
                    mGlucoseBoolean = false
                    binding.buttonAddVitals.isEnabled = false
                } else if (str.length == 1 && str.equals("0")) {
                    s!!.clear()
                } else {
                    mGlucoseValue = str.toInt()

                    if (mGlucoseValue!! > 0) {
                        mGlucoseBoolean = true
                        //binding.buttonAddVitals.isEnabled = true
                        if (mGlucoseValue!! <= 900) {
                            mGlucoseBoolean = true
                            //binding.buttonAddVitals.isEnabled = true
                        } else {
                            mGlucoseBoolean = false
                            //binding.buttonAddVitals.isEnabled = false
                        }
                    } else {
                        mGlucoseBoolean = false
                        //binding.buttonAddVitals.isEnabled = false
                    }

                    if (mGlucoseValue!! >= 990) {
                        mGlucoseBoolean = false
                    }
                }

                if (mGlucoseBoolean && mSpinnerPosition != 0) {
                    binding.buttonAddVitals.isEnabled = true
                } else {
                    if (binding.editTextGlucose.text!!.length >= 2) {
                        Utils.showToast(this@AddVitalsActivity, "Kindly select the meal type", true)
                    }
                    binding.buttonAddVitals.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        binding.buttonAddVitals.setOnClickListener {
            if (vitalID!!.toInt() == 7) {
                if (mGlucoseValue.toString().length == 2 || mGlucoseValue.toString().length == 1) {
                    if (mGlucoseValue!! < 50) {
                        alertPopup(mGlucoseValue!!)
                    } else {
                        mGlucoseBoolean = true
                        AddVitalsAPI()
                    }
                } else {
                    if (mGlucoseValue!! > 500) {
                        if (mGlucoseValue!! >= 901) {
                            binding.buttonAddVitals.isEnabled = false
                        } else {
                            alertPopup(mGlucoseValue!!)
                        }

                    } else {
                        mGlucoseBoolean = true
                        AddVitalsAPI()
                    }
                }
            } else {
                AddVitalsAPI()
            }
        }
    }

    private fun AddVitalsAPI() {
        //val selectItems: ArrayList<VitalsInput>? = ArrayList()
        if (binding.editTextBodyTemperature.text.toString()
                .equals("") || binding.editTextDiastolic.text.toString()
                .equals("") || binding.editTextHeartRate.text.toString().equals(
                ""
            ) || binding.editTextOxygenLevel.text.toString()
                .equals("") || binding.editTextSystolic.text.toString()
                .equals("") || binding.editTextWeight.text.toString().equals(
                ""
            ) || binding.editTextGlucose.text.toString().equals("")
        ) {
            if (mDiastolicBoolean || mSystolicBoolean || mBodyTemperatureBoolean || mHeartRateBoolean || mOxygenLevelBoolean || mWeightBoolean || mGlucoseBoolean) {
                val tsLong = System.currentTimeMillis() / 1000
                val currentTimeStamp =
                    Utils.getDateCurrentTimeZone(tsLong, Constants.TIMESTAMP_DOB.toString())
                val item = VitalsInputModel()
                var localTime: Date? = null
                var selectValue: String? = ""
                var upToNCharacters: String = ""
                var selectDateTimeStamp: Int? = 0
                if (selectDate.equals("")) {
                    selectValue = mCurrentDate + " " + selectTime
                    mGlucoseDate = mCurrentDate
                } else {
                    selectValue = selectDate + " " + selectTime
                    mGlucoseDate = selectDate
                }

                try {
                    localTime =
                        SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault()).parse(
                            selectValue
                        )

                    val formatter: DateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a")
                    val date = formatter.parse(selectValue) as Date
                    System.out.println("Today is " + date.time)
                    Log.e("NEWWW", date.time.toString())
                    upToNCharacters =
                        localTime.time.toString()
                            .substring(
                                0,
                                Math.min(localTime.time.toString().length, 10)
                            )
                    val tz = TimeZone.getDefault()
                    val now = Date()
                    val offsetFromUtc = tz.getOffset(now.time) / 1000
                    val tsLong = upToNCharacters.toInt()
                    val timeStamp = tsLong + offsetFromUtc
                    selectDateTimeStamp = timeStamp
                    Log.e("SelectTime", selectDateTimeStamp.toString())
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                var GlucoseDate: Int? = 0
                try {
                    localTime =
                        SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(
                            mGlucoseDate
                        )

                    val formatter: DateFormat = SimpleDateFormat("MM/dd/yyyy")
                    val date = formatter.parse(mGlucoseDate) as Date
                    System.out.println("Today is " + date.time)
                    Log.e("NEWWW", date.time.toString())
                    upToNCharacters =
                        localTime.time.toString()
                            .substring(
                                0,
                                Math.min(localTime.time.toString().length, 10)
                            )
                    val tz = TimeZone.getDefault()
                    val now = Date()
                    val offsetFromUtc = tz.getOffset(now.time) / 1000
                    val tsLong = upToNCharacters.toInt()
                    val timeStamp = tsLong + offsetFromUtc
                    GlucoseDate = timeStamp
                    Log.e("SelectTime", selectDateTimeStamp.toString())
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                Log.e("currentTimeStamp", currentTimeStamp.toString())
                if (!binding.editTextGlucose.text.toString().equals("")) {
                    item.setDate(selectDateTimeStamp!!.toInt())
                    item.setValue(listOf(binding.editTextGlucose.text.toString()))
                    item.setVitalId(7)
                    item.setType(0)
                    item.setCurrentDate(GlucoseDate)
                    item.setGlucoseId(mSpinnerPosition)
                }

                if (!binding.editTextHeartRate.text.toString().equals("")) {

                    item.setDate(selectDateTimeStamp!!.toInt())
                    item.setValue(listOf(binding.editTextHeartRate.text.toString()))
                    item.setVitalId(2)
                    item.setType(0)
                    item.setCurrentDate(0)
                    item.setGlucoseId(0)
                }

                if (!binding.editTextBodyTemperature.text.toString().equals("")) {
                    //val temperatures = VitalsInputModel()
                    item.setDate(selectDateTimeStamp!!.toInt())
                    item.setValue(listOf(binding.editTextBodyTemperature.text.toString()))
                    item.setVitalId(3)
                    item.setType(0)
                    item.setCurrentDate(0)
                    item.setGlucoseId(0)
                    //selectItems.add(temperatures)
                    //data.setData(selectItems)
                }

                if (!binding.editTextOxygenLevel.text.toString().equals("")) {
                    //val breathRate = VitalsInputModel()
                    item.setDate(selectDateTimeStamp)
                    item.setValue(listOf(binding.editTextOxygenLevel.text.toString()))
                    item.setVitalId(4)
                    item.setType(0)
                    item.setCurrentDate(0)
                    item.setGlucoseId(0)
                    //selectItems.add(breathRate)
                    //data.setData(selectItems)
                }

                if ((!binding.editTextSystolic.text.toString()
                        .equals("")) && (!binding.editTextDiastolic.text.toString().equals(""))
                ) {
                    //val bloodPressure = VitalsInputModel()
                    item.setDate(selectDateTimeStamp!!.toInt())
                    item.setValue(listOf(binding.editTextSystolic.text.toString() + "/" + binding.editTextDiastolic.text.toString()))
                    item.setVitalId(5)
                    item.setType(0)
                    item.setCurrentDate(0)
                    item.setGlucoseId(0)
                    //selectItems.add(bloodPressure)
                    //data.setData(selectItems)
                }

                if (!binding.editTextWeight.text.toString().equals("")) {
                    //val weight = VitalsInputModel()
                    item.setDate(selectDateTimeStamp!!.toInt())
                    item.setValue(listOf(binding.editTextWeight.text.toString()))
                    item.setVitalId(6)
                    item.setType(0)
                    item.setCurrentDate(0)
                    item.setGlucoseId(0)
                    //selectItems.add(weight)
                    //data.setData(selectItems)
                }


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

                    viewModel.Response1!!.observe(this, androidx.lifecycle.Observer {
                        when (it) {
                            is Resource.Success -> {
                                Log.e("ooomg", it.toString())
                                if (progressDoalog != null && progressDoalog!!.isShowing) {
                                    progressDoalog!!.dismiss()
                                }
                                if (it.value.getStatusCode() == 200) {

                                    if (it.value.getValue()!!.status == 1) {
                                        /*if (vitalID!!.toInt() == 7) {
                                            intent = Intent(
                                                this@AddVitalsActivity,
                                                BloodGlucoseActivity::class.java
                                            )
                                        } else if (vitalID!!.toInt() == 5) {
                                            intent = Intent(
                                                this@AddVitalsActivity,
                                                BloodPressureDetailActivity::class.java
                                            )
                                        } else if (vitalID!!.toInt() == 4) {
                                            intent = Intent(
                                                this@AddVitalsActivity,
                                                OxygenLevelDetailActivity::class.java
                                            )
                                        } else {
                                            intent = Intent(
                                                this@AddVitalsActivity,
                                                TemperatureDetailActivity::class.java
                                            )
                                        }

                                        intent.putExtra(Constants.VITALS_ID, vitalID)
                                        intent.putExtra(Constants.VITALS_NAME, vitalName)
                                        if (vitalID!!.toInt() == 7) {
                                            intent.putExtra(
                                                Constants.VITALS_DATE,
                                                GlucoseDate.toString()
                                            )
                                        } else {
                                            intent.putExtra(
                                                Constants.VITALS_DATE,
                                                selectDateTimeStamp.toString()
                                            )
                                        }

                                        intent.putExtra(Constants.VITALS_IMAGE, image)
                                        startActivity(intent)*/

                                        val returnIntent = Intent()
                                        returnIntent.putExtra(Constants.VITALS_DATE, selectDateTimeStamp.toString())
                                        setResult(
                                            123,
                                            returnIntent
                                        )

                                        finish()
                                    }
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
            } else {
                Utils.showToast(this, resources.getString(R.string.toast_enter_valid_data), true)
            }
        } else {
            Utils.showToast(this, resources.getString(R.string.toast_empty_data), true)
        }
    }

    private fun datePickerData() {
        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        mHours = c[Calendar.HOUR]
        mMins = c[Calendar.MINUTE]
        c.add(Calendar.MONTH, -1)

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
                    var selectdate: String = month + "/" + day + "/" + year
                    selectDate = selectdate
                    if (mCurrentDate.equals(selectdate)) {
                        val sdf2 = SimpleDateFormat(Constants.TIMESTAMP_VITAL_TIME)
                        val currentTime = sdf2.format(Date())
                        binding.textViewTime.text = currentTime
                    } else {

                    }

                    val df: DateFormat =
                        SimpleDateFormat("MM/dd/yyy")
                    try {
                        val date1 = df.parse(selectdate)
                        val outputFormatter1: DateFormat =
                            SimpleDateFormat(Constants.TIMESTAMP_VITALS_DETAILS)
                        val output1 = outputFormatter1.format(date1)
                        binding.textViewDate.text = output1
                        Log.e("Time", mHours.toString() + " - " + mMins)

                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }
            },
            mYear!!,
            mMonth!!,
            mDay!!
        )
        datePickerDialog.datePicker.minDate = c.timeInMillis
        datePickerDialog.show()
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000)
        datePickerDialog.show()
        datePickerDialog.setButton(
            DialogInterface.BUTTON_NEGATIVE,
            getString(R.string.cancel),
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                    }
                }
            })
    }

    private fun alertPopup(mSystolic: Int) {
        val alertDialog = Dialog(this)
        val lp = WindowManager.LayoutParams()
        val window = alertDialog.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.alert_dialog_view)
        val textView: AppCompatTextView = alertDialog.findViewById(R.id.textview_dialog_heading)
        textView.text = "You entered " + mSystolic.toString() + " mg/dL" + " is this correct?"
        val posTv: AppCompatButton = alertDialog.findViewById(R.id.button_proceed)
        posTv.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
            AddVitalsAPI()
        })
        val negTv: AppCompatButton = alertDialog.findViewById(R.id.button_cancel)
        negTv.text = resources.getString(R.string.button_cancel)
        negTv.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
        })
        alertDialog.show()
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

}