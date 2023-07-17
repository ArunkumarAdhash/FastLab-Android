package com.lifehopehealthapp.scheduleDateTime

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivitySelectDateTimeBinding
import com.lifehopehealthapp.orderSummary.OrderSummaryActivity
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.RecyclerItemClickListenr
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.widgets.TransitionHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ScheduleDateTimeActivity : BaseActivity<ScheduleDateTimeViewModel, ScheduleDateTimeModel>() {

    private lateinit var binding: ActivitySelectDateTimeBinding

    private var BookingType: Int = 0
    private var token: String? = ""
    private var selectSession: String? = ""
    private var selectDate: String? = ""
    private var selectTime: String? = ""
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
    private var name: String? = ""
    private var mobile: String? = ""
    private var address: String? = ""
    private var iosImage: String? = ""
    private var profilePic: String? = ""
    private var extraInfo: String? = ""

    private var SELECTPOSITION: Int = 0

    private var progressDoalog: Dialog? = null
    private var mPrefs: SharedPreferences? = null
    private var mAdapter: ScheduleTimeAdapter? = null
    private var mData: ArrayList<ListData>? = ArrayList<ListData>()
    private var mSelectMrngTime: ArrayList<Morning>? = ArrayList<Morning>()

    override fun getViewModel() = ScheduleDateTimeViewModel::class.java

    override fun getActivityRepository() =
        ScheduleDateTimeModel(
            remoteDataSource.buildApi(APIManager::class.java),
            PreferenceHelper
        )

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectDateTimeBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        setContentView(binding.root)

        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken

        val extras = intent.extras
        BookTest = extras!!.getString("BookTest").toString()
        TestType = extras.getString("TestType").toString()
        title = extras.getString("title").toString()
        description = extras.getString("des").toString()
        image = extras.getString("pic").toString()
        price = extras.getString("price").toString()
        iosImage = extras.getString("iosImage").toString()
        testTypeId = extras.getString("testTypeId").toString()
        testCategoryId = extras.getString("testCategoryId").toString()
        tax = extras.getString("tax").toString()
        shippingCharge = extras.getString("shippingCharge").toString()
        name = extras.getString("name").toString()
        address = extras.getString("address").toString()
        mobile = extras.getString("mobile").toString()
        profilePic = extras.getString("ProfilePic").toString()
        extraInfo = extras.getString("extraInfo")
        BookingType = extras.getInt("BookingType")

        binding.imageviewPicture.loadSvg(image!!)
        binding.textviewTestHeading.setText(title)
        binding.textviewTestDescription.setText(description)
        binding.recyclerviewTime.layoutManager = GridLayoutManager(this, 3)

        val sdf =
            SimpleDateFormat("MM/dd/yyyy")
        selectDate = sdf.format(Date())
        Log.e("currentDate", selectDate.toString())

        binding.layoutMorning.setOnClickListener {
            selectSession = "Morning"
            mSelectMrngTime!!.clear()
            for (l in mData!!.indices) {
                if (l == SELECTPOSITION) {
                    if (selectSession.equals("Morning")) {
                        for (k in mData!![l].morning!!.indices) {
                            Log.e("SelectDay", mData!![l].morning!![k].time!!)
                            var mrng: Morning? =
                                Morning()
                            mrng!!.time = mData!![l].morning!![k].time!!
                            mrng.isAvailable = mData!![l].morning!![k].isAvailable!!
                            mSelectMrngTime!!.add(mrng)
                        }
                        mAdapter =
                            ScheduleTimeAdapter(
                                mSelectMrngTime!!,
                                /*mSelectEvenTime!!,*/
                                applicationContext,

                                )
                        binding.recyclerviewTime.adapter = mAdapter
                        mAdapter!!.notifyDataSetChanged()
                        Log.e("Pos", l.toString())
                    }
                }
            }
            binding.layoutMorning.setBackgroundResource(R.drawable.divider_select_session)
            binding.textviewMorningLabel.setTextColor(resources.getColor(R.color.white))
            binding.layoutEvening.setBackgroundResource(R.drawable.alert_bg)
            binding.textviewEveningLabel.setTextColor(resources.getColor(R.color.black))

        }

        binding.layoutEvening.setOnClickListener {
            selectSession = "Evening"
            mSelectMrngTime!!.clear()
            for (l in mData!!.indices) {
                if (l == SELECTPOSITION) {
                    if (selectSession.equals("Evening")) {
                        for (k in mData!![l].evening!!.indices) {
                            Log.e("SelectDay", mData!![l].evening!![k].time!!)
                            var eve: Morning? =
                                Morning()
                            eve!!.time = mData!![l].evening!![k].time!!
                            eve.isAvailable = mData!![l].evening!![k].isAvailable!!
                            mSelectMrngTime!!.add(eve)
                        }
                        Log.e("Pos", l.toString())
                        mAdapter =
                            ScheduleTimeAdapter(
                                mSelectMrngTime!!, applicationContext,
                                /*mSelectEvenTime!!,*/

                            )
                        binding.recyclerviewTime.adapter = mAdapter
                        mAdapter!!.notifyDataSetChanged()
                    }

                }
            }
            binding.layoutEvening.setBackgroundResource(R.drawable.divider_select_session)
            binding.textviewEveningLabel.setTextColor(resources.getColor(R.color.white))
            binding.textviewMorningLabel.setTextColor(resources.getColor(R.color.black))
            binding.layoutMorning.setBackgroundResource(R.drawable.alert_bg)

        }


        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()


            val tz = TimeZone.getDefault()
            val now = Date()
            val offsetFromUtc = tz.getOffset(now.time) / 1000

            val tsLong = System.currentTimeMillis() / 1000
            val timeStamp = tsLong + offsetFromUtc
            Log.e("Timestm", tsLong.toString())
            Log.e("offset", offsetFromUtc.toString())
            val data = ScheduleDateTimeAPIRequest()
            data.type=timeStamp.toInt()

            val gson = Gson()
            var json: String? = ""
            json = gson.toJson(data)
            Log.e("Model---->", json.toString())

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(json) as JsonObject


            viewModel.getDateTime(token!!, aJsonObject)

            viewModel.Response.observe(this, androidx.lifecycle.Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            for (k in it.value.getValue()!!.data!!.indices) {
                                for (l in it.value.getValue()!!.data!![k].list!!.indices) {
                                    Log.e("date", it.value.getValue()!!.data!![k]!!.list!![l].day!!)
                                    var data: ListData? =
                                        ListData()
                                    data!!.day = it.value.getValue()!!.data!![k]!!.list!![l].day!!
                                    data.date = it.value.getValue()!!.data!![k]!!.list!![l].date!!
                                    data.dayText = it.value.getValue()!!.data!![k]!!.list!![l].dayText!!
                                    data.morning = it.value.getValue()!!.data!![k]!!.list!![l].morning
                                    data.evening = it.value.getValue()!!.data!![k]!!.list!![l].evening
                                    mData!!.add(data)
                                }
                            }

                            binding.recyclerviewDate.layoutManager =
                                LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

                            val adapter = ScheduleDateAdapter(this, mData)
                            binding.recyclerviewDate.adapter = adapter

                            binding.layoutMorning.performClick()

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

        binding.recyclerviewDate.addOnItemTouchListener(
            RecyclerItemClickListenr(
                this,
                binding.recyclerviewDate,
                object : RecyclerItemClickListenr.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        selectDate = mData!![position].date
                        SELECTPOSITION = position
                        mSelectMrngTime!!.clear()
                        //mSelectEvenTime!!.clear()
                        for (l in mData!!.indices) {
                            if (l == SELECTPOSITION) {
                                if (selectSession.equals("Morning")) {
                                    for (k in mData!![l].morning!!.indices) {
                                        Log.e("SelectDay", mData!![l].morning!![k].time!!)
                                        var mrng: Morning? =
                                            Morning()
                                        mrng!!.time = mData!![l].morning!![k].time!!
                                        mrng.isAvailable = mData!![l].morning!![k].isAvailable!!
                                        mSelectMrngTime!!.add(mrng)
                                    }
                                    mAdapter =
                                        ScheduleTimeAdapter(
                                            mSelectMrngTime!!,
                                            applicationContext,

                                            )
                                    binding.recyclerviewTime.adapter = mAdapter
                                    mAdapter!!.notifyDataSetChanged()
                                    Log.e("Pos", l.toString())
                                } else if (selectSession.equals("Evening")) {
                                    for (k in mData!![l].evening!!.indices) {
                                        Log.e("SelectDay", mData!![l].evening!![k].time!!)
                                        var eve: Morning? =
                                            Morning()
                                        eve!!.time = mData!![l].evening!![k].time!!
                                        eve.isAvailable = mData!![l].evening!![k].isAvailable!!
                                        mSelectMrngTime!!.add(eve)
                                    }
                                    Log.e("Pos", l.toString())
                                    mAdapter =
                                        ScheduleTimeAdapter(
                                            mSelectMrngTime!!, applicationContext,

                                            )
                                    binding.recyclerviewTime.adapter = mAdapter
                                    mAdapter!!.notifyDataSetChanged()
                                }

                            }
                        }
                    }
                })
        )

        binding.buttonConfirmSchedule.setOnClickListener {
            var upToNCharacters: String = ""
            if (selectTime!!.contentEquals("")) {
                Utils.showToast(this, getString(R.string.select_your_schedule_time), true)
            } else {

                var result = selectDate + " " + selectTime


                var localTime: Date? = null
                try {
                    localTime =
                        SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault()).parse(result)
                    Log.e("timestamp", localTime!!.time.toString())

                    val formatter: DateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a")
                    val date = formatter.parse(result) as Date
                    System.out.println("Today is " + date.time)
                    Log.e("NEWWW", date.time.toString())
                    upToNCharacters =
                        localTime.time.toString()
                            .substring(0, Math.min(localTime.time.toString().length, 10))
                    Log.e("timestamp1", upToNCharacters)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                intent = Intent(this, OrderSummaryActivity::class.java)
                val extras = Bundle()
                extras.putString("BookTest", "BookTest")
                extras.putString("TestType", TestType)
                extras.putString("title", title)
                extras.putString("des", description)
                extras.putString("pic", image)
                extras.putString("price", price)
                extras.putString("TimeZone", upToNCharacters)
                extras.putString("testTypeId", testTypeId)
                extras.putString("testCategoryId", testCategoryId)
                extras.putString("SelectDateTime", selectDate + ", " + selectTime)
                extras.putString("tax", tax)
                extras.putString("shippingCharge", shippingCharge)
                extras.putString("name", name)
                extras.putString("mobile", mobile)
                extras.putString("address", address)
                extras.putString("iosImage", iosImage)
                extras.putString("ProfilePic", profilePic)
                extras.putString("extraInfo", extraInfo)
                extras.putInt("BookingType", BookingType)
                intent.putExtras(extras)
                if (Utils.isLollipopHigher() && it != null) {
                    val pairs: Array<Pair<View, String>> =
                        TransitionHelper.createSafeTransitionParticipants(
                            this,
                            false,
                            Pair(it, getString(R.string.trans_tool_bar_title))
                        )
                    val transitionActivityOptions =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this, *pairs)
                    startActivity(intent, transitionActivityOptions.toBundle())
                } else {
                    startActivity(intent)
                }
                finish()
            }

        }

        binding.imageviewClose.setOnClickListener {
            finish()
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
        TransitionHelper.removeActivityFromTransitionManager(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleEvent(event: ScheduleTimeModel) {
        selectTime = event.selectTime
    }

    fun ImageView.loadSvg(url: String?) {
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }

    private fun AppCompatImageView.loadSvg(url: String) {
        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(this@loadSvg.context)) }
            .build()

        val request = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(500)
            .data(url)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    }
}