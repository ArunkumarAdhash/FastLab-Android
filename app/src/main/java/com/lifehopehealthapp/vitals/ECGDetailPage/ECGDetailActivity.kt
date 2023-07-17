package com.lifehopehealthapp.vitals.ECGDetailPage

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.core.view.isVisible
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityEcgDetailBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.*
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import org.greenrobot.eventbus.Subscribe
import java.util.*
import kotlin.collections.ArrayList


class ECGDetailActivity : BaseActivity<ECGDetailViewModel, ECGDetailModel>() {

    private var lastDate: String? = ""
    private var mPrefs: SharedPreferences? = null
    private var vitalID: String? = ""
    private var name: String? = ""
    private var lastValue: String? = ""
    private var token: String? = ""
    private var image: String? = ""
    private var progressDoalog: Dialog? = null
    private var graphMin: Int? = 0
    private var graphMax: Int? = 0
    private var mColor: String? = ""

    private var mXValue: ArrayList<Int> = ArrayList<Int>()
    private var mYValue: ArrayList<Int> = ArrayList<Int>()

    override fun getViewModel() = ECGDetailViewModel::class.java

    override fun getActivityRepository() =
        ECGDetailModel(
            remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
        )

    private lateinit var binding: ActivityEcgDetailBinding


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
        //item.setValue(separated[0])
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


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEcgDetailBinding.inflate(layoutInflater)
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
//        binding.layoutChartView.imageViewHistory.isVisible = false
        binding.buttonShare.isVisible = false
        binding.layoutHeader.textViewLabel.text = name

        binding.layoutHeader.imageviewBackArrow.setOnClickListener {
            finish()
        }

        binding.layoutHeader.imageViewHistory.setOnClickListener {
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

        if (lastDate.equals("0")) {

        } else {
            getGraphView()
            binding.imageViewVitalsImage.loadSvg(image)
        }


        binding.buttonShare.setOnClickListener {
            ShareVitals()
        }

    }

    private fun getGraphView() {
        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            val data = BloodPressureInputModel()
            data.setVitalId(vitalID!!.toInt())
            data.setStartdate(lastDate!!.toInt())
            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(data)
            Log.e("Model---->", json.toString())

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(json) as JsonObject

            viewModel.getECGList(token!!, aJsonObject)

            viewModel.Response.observe(this, androidx.lifecycle.Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        val typeValue: String =
                            it.value.getValue()!!.data!!.graphType!!.graphTypeData!!
                        binding.textViewHeading.text = Utils.getDateCurrentTimeZone(
                            lastDate!!.toLong(),
                            Constants.TIMESTAMP_VITALS_DETAILS!!
                        ) +
                                " ~ " + typeValue
                        //graphMin = it.value.getValue()!!.data!!.graphMinVal
                        //graphMax = it.value.getValue()!!.data!!.graphMaxVal
                        /*if (it.value.getValue()!!.data!!.ecgXAxisBarData != null) {
                            mXValue = it.value.getValue()!!.data!!.ecgXAxisBarData as ArrayList<Int>
                        }*/
                        if (it.value.getValue()!!.data!!.barData != null) {
                            mYValue = it.value.getValue()!!.data!!.barData as ArrayList<Int>
                        }

                        Log.e("mXValue", mXValue.size.toString())
                        mColor = it.value.getValue()!!.data!!.colorHexcode
/*
                        if (mXValue.size == 0 || it.value.getValue()!!.data!!.ecgYAxisBarData == null) {

                        } else*/
                        if (mYValue.size > 0) {
                            binding.textViewContent.text = it.value.getValue()!!.data!!.graphDesc
                            initGraphValue(mXValue, mYValue)
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

    private fun initGraphValue(
        mXValue: ArrayList<Int>,
        mYValue: ArrayList<Int>
    ) {
        binding.chart1.getDescription().setEnabled(false)
        binding.chart1.setTouchEnabled(true)
        binding.chart1.setDragDecelerationFrictionCoef(0.9f)
        binding.chart1.setDragEnabled(true)
        binding.chart1.setScaleEnabled(true)
        binding.chart1.setDrawGridBackground(false)
        binding.chart1.setHighlightPerDragEnabled(true)
        binding.chart1.setBackgroundColor(Color.WHITE)
        binding.chart1.setViewPortOffsets(0f, 0f, 0f, 0f)
        val l: Legend = binding.chart1.getLegend()
        l.isEnabled = false


        binding.chart1.setBackgroundColor(Color.BLACK)
        val custom: IAxisValueFormatter = ECGValueFormatter()
        val xAxis: XAxis = binding.chart1.getXAxis()
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.textSize = 10f
        xAxis.textColor = Color.BLACK
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(true)
        xAxis.textColor = Color.rgb(255, 192, 56)
        xAxis.setCenterAxisLabels(true)
        xAxis.granularity = 1f
        xAxis.valueFormatter = custom

        val leftAxis: YAxis = binding.chart1.getAxisLeft()
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        leftAxis.textColor = ColorTemplate.getHoloBlue()
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = true
        leftAxis.yOffset = -9f
        leftAxis.textColor = Color.rgb(255, 192, 56)
//        leftAxis.axisMaximum = graphMin!!.toFloat()
//        leftAxis.axisMaximum = graphMax!!.toFloat()

        val rightAxis: YAxis = binding.chart1.getAxisRight()
        rightAxis.isEnabled = false

        setData(mYValue)
        binding.chart1.invalidate()
        xAxis.valueFormatter = object : IAxisValueFormatter {

            override fun getFormattedValue(value: Float, axis: AxisBase): String {
                val intValue = value.toInt()

                return if (mXValue.size > intValue && intValue >= 0) mXValue.get(intValue)
                    .toString() else ""
                return ""
            }
        }
        binding.chart1.animateX(2500)

    }

    private fun setData(mYValue: ArrayList<Int>) {
        val values = ArrayList<Entry>()
        for (i in 0 until mYValue.size) {
            values.add(BarEntry(i.toFloat(), mYValue[i].toFloat()))
        }

        val set1 = LineDataSet(values, "")
        set1.axisDependency = YAxis.AxisDependency.LEFT
        set1.color = Color.RED
        set1.valueTextColor = ColorTemplate.getHoloBlue()
        set1.lineWidth = 1.5f
        set1.setDrawCircles(false)
        set1.setDrawValues(false)
        set1.fillAlpha = 65
        set1.fillColor = ColorTemplate.getHoloBlue()
        set1.highLightColor = Color.RED
        set1.setDrawCircleHole(false)
        val data = LineData(set1)
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(9f)
        binding.chart1.setData(data)
    }

    fun ImageView.loadSvg(url: String?) {
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }

    private fun ShareVitals() {
        if (Utils.isNetworkAvailable(this)) {

            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
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

            viewModel.Response1.observe(this, androidx.lifecycle.Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, it.value.getValue()!!.data!!)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        startActivity(shareIntent)
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
}