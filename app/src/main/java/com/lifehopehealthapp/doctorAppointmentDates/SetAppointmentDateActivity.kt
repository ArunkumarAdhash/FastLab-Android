package com.lifehopehealthapp.doctorAppointmentDates

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lifehopehealthapp.Calls.Video.VideoChatViewActivity
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivitySetAppointmentBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketName
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketRegion
import com.lifehopehealthapp.utils.PreferenceHelper.userID
import com.lifehopehealthapp.utils.PreferenceHelper.w3AccessKey
import com.lifehopehealthapp.utils.PreferenceHelper.w3SecretKey
import com.lifehopehealthapp.utils.RecyclerItemClickListenr
import com.lifehopehealthapp.utils.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SetAppointmentDateActivity :
    BaseActivity<SetAppointmentDateViewModel, SetAppointmentDateModel>() {

    private var mAdapter: SetAvailabilityTimeAdapter? = null

    private var progressDoalog: Dialog? = null

    private var mStartTimeStamp: Long = 0
    private var mEndTimeStamp: Long = 0
    private var mSlotID: String = ""
    private var mSelectDate: Int = 0
    private var mAmount: Double = 0.0

    private var selectDate: String? = ""

    private var selectSession: String? = ""

    private var SELECTPOSITION: Int = 0

    private lateinit var binding: ActivitySetAppointmentBinding

    private var mPrefs: SharedPreferences? = null

    private var token: String? = ""

    private var mDoctorID: String? = ""

    private var mSelectMrngTime: ArrayList<MorningSlot>? = ArrayList<MorningSlot>()

    override fun getViewModel() = SetAppointmentDateViewModel::class.java

    private var mAvailabilityTime: ArrayList<ListOfDate>? = null

    override fun getActivityRepository() =
        SetAppointmentDateModel(
            remoteDataSource.buildApi(
                APIManager::class.java
            ), PreferenceHelper
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySetAppointmentBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        mDoctorID = intent.getStringExtra(Constants.DOCTOR_ID)
        binding.recyclerviewDate.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        binding.recyclerviewTime.layoutManager = GridLayoutManager(this, 4)

        callAPI()

        binding.imageViewVideoCall.setOnClickListener {
            val intent = Intent(this, VideoChatViewActivity::class.java)
            startActivity(intent)
        }

        binding.layoutMorning.setOnClickListener {
            selectSession = "morningSlot"
            mSelectMrngTime!!.clear()
            if (mAvailabilityTime!!.size != 0) {
                for (l in mAvailabilityTime!!.indices) {
                    if (l == SELECTPOSITION) {
                        if (selectSession.equals("morningSlot")) {
                            for (k in mAvailabilityTime!!.get(l).morningSlot!!.indices) {
                                val mrng =
                                    MorningSlot()
                                mrng.startTime = mAvailabilityTime!![l].morningSlot!![k].startTime!!
                                mrng.endTime = mAvailabilityTime!![l].morningSlot!![k].endTime!!
                                mrng.isActive = mAvailabilityTime!![l].morningSlot!![k].isActive!!
                                mrng.slotId = mAvailabilityTime!![l].morningSlot!![k].slotId!!
                                mSelectMrngTime!!.add(mrng)
                            }

                            if (mSelectMrngTime!!.size==0){
                                binding.recyclerviewTime.isVisible = false
                                binding.textViewNoData.isVisible = true
                            }else{
                                binding.recyclerviewTime.isVisible = true
                                binding.textViewNoData.isVisible = false
                                mAdapter =
                                    SetAvailabilityTimeAdapter(
                                        mSelectMrngTime!!,
                                        applicationContext,
                                        mAvailabilityTime!!,
                                        selectSession!!,
                                        SELECTPOSITION,

                                        )
                                binding.recyclerviewTime.adapter = mAdapter
                                mAdapter!!.notifyDataSetChanged()
                                Log.e("Pos", l.toString())
                            }

                        }
                    }
                }
                binding.layoutMorning.setBackgroundResource(R.drawable.divider_select_session)
                binding.textviewMorningLabel.setTextColor(resources.getColor(R.color.white))
                binding.layoutEvening.setBackgroundResource(R.drawable.alert_bg)
                binding.textviewEveningLabel.setTextColor(resources.getColor(R.color.black))
            }
        }

        binding.layoutEvening.setOnClickListener {
            selectSession = "eveningSlot"
            mSelectMrngTime!!.clear()
            for (l in mAvailabilityTime!!.indices) {
                if (l == SELECTPOSITION) {
                    if (selectSession.equals("eveningSlot")) {
                        for (k in mAvailabilityTime!![l].eveningSlot!!.indices) {
                            Log.e(
                                "SelectDay",
                                mAvailabilityTime!![l].eveningSlot!![k].startTime!!.toString()
                            )
                            var eve: MorningSlot? =
                                MorningSlot()
                            eve!!.startTime = mAvailabilityTime!![l].eveningSlot!![k].startTime!!
                            eve.endTime = mAvailabilityTime!![l].eveningSlot!![k].endTime!!
                            eve.isActive = mAvailabilityTime!![l].eveningSlot!![k].isActive!!
                            eve.slotId = mAvailabilityTime!![l].eveningSlot!![k].slotId!!
                            mSelectMrngTime!!.add(eve)
                        }
                        if (mSelectMrngTime!!.size==0){
                            binding.recyclerviewTime.isVisible = false
                            binding.textViewNoData.isVisible = true
                        }else{
                            binding.recyclerviewTime.isVisible = true
                            binding.textViewNoData.isVisible = false
                            Log.e("Pos", l.toString())
                            mAdapter =
                                SetAvailabilityTimeAdapter(
                                    mSelectMrngTime!!,
                                    applicationContext,
                                    mAvailabilityTime!!,
                                    selectSession!!,
                                    SELECTPOSITION,
                                    /*mSelectEvenTime!!,*/

                                )
                            binding.recyclerviewTime.adapter = mAdapter
                            mAdapter!!.notifyDataSetChanged()
                        }
                    }

                }
            }
            binding.layoutEvening.setBackgroundResource(R.drawable.divider_select_session)
            binding.textviewEveningLabel.setTextColor(resources.getColor(R.color.white))
            binding.textviewMorningLabel.setTextColor(resources.getColor(R.color.black))
            binding.layoutMorning.setBackgroundResource(R.drawable.alert_bg)

        }

        binding.imageviewBackArrow.setOnClickListener {
            finish()
        }

        binding.recyclerviewDate.addOnItemTouchListener(
            RecyclerItemClickListenr(
                this,
                binding.recyclerviewDate,
                object : RecyclerItemClickListenr.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        selectDate = mAvailabilityTime!![position].date
                        mSelectDate = mAvailabilityTime!![position].date!!.toInt()
                        SELECTPOSITION = position
                        mSelectMrngTime!!.clear()
                        binding.layoutEvening.setBackgroundResource(R.drawable.alert_bg)
                        binding.textviewEveningLabel.setTextColor(resources.getColor(R.color.black))
                        binding.layoutMorning.setBackgroundResource(R.drawable.alert_bg)
                        binding.textviewMorningLabel.setTextColor(resources.getColor(R.color.black))
                        binding.recyclerviewTime.adapter = mAdapter
                        binding.layoutMorning.performClick()
                    }
                })
        )

        binding.buttonBookAppointment.setOnClickListener {
            if (mSlotID.equals("")) {
                Utils.showToast(this, "Please Select your Time.", true)
            } else if (binding.editTextAboutMe.text.toString().equals("")) {
                Utils.showToast(this, "Please Describe your health issues.", true)
            } else {
                saveAPI()
            }

        }

    }

    private fun saveAPI() {

        if (progressDoalog == null) {
            progressDoalog = Utils.getDialog(this)
        }
        progressDoalog!!.show()
        val startValue = mStartTimeStamp / 1000
        val endValue = mEndTimeStamp / 1000
        val data = SaveAppointmentRequest()
        data.setStartTime(startValue)
        data.setEndTime(endValue)
        data.setDoctorId(mDoctorID)
        data.setSlotId(mSlotID)
        data.setAmount(mAmount)
        data.setDate(mSelectDate)
        data.setPatientId(mPrefs!!.userID)
        data.setDescription(binding.editTextAboutMe.text.toString())


        viewModel.saveAppointment(token!!, data)

        viewModel.Response1!!.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    if (progressDoalog != null && progressDoalog!!.isShowing) {
                        progressDoalog!!.dismiss()
                    }
                    Log.e(
                        "Booking->",
                        it.value.getValue()!!.getLink()!! + "" + it.value.getValue()!!
                            .getAppointmentId()!!
                    )
                    Utils.webViewData(
                        "doctor", it.value.getValue()!!.getLink()!!,
                        it.value.getValue()!!.getAppointmentId()!!, this
                    )
                }

                is Resource.GenericError -> {
                    if (progressDoalog != null && progressDoalog!!.isShowing) {
                        progressDoalog!!.dismiss()
                    }
                    Utils.showToast(this, it.error!!.getMessage().toString(), true)
                }
            }
        })
    }

    private fun callAPI() {

        if (progressDoalog == null) {
            progressDoalog = Utils.getDialog(this)
        }
        progressDoalog!!.show()

        val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa")
        //sdf.timeZone = TimeZone.getTimeZone("UTC")
        val currentDate = sdf.format(Date())
        Log.e("currentDate", currentDate)

        val data = SetDateRequest()
        data.setDate(currentDate)
        data.setDoctorId(mDoctorID)

        val gson = Gson()
        var json: String? = ""
        json = gson.toJson(data)
        Log.e("Model-->", json)

        viewModel.getDetails(token!!, data)

        viewModel.dataResponse.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    if (progressDoalog != null && progressDoalog!!.isShowing) {
                        progressDoalog!!.dismiss()
                    }
                    mAmount = it.value.getValue()!!.getPrice()!!
                    binding.textViewDoctorLocation.setText(it.value.getValue()!!.specialisation)
                    binding.textViewDoctorName.setText(it.value.getValue()!!.getDoctorName())
                    binding.textViewDoctorDepartment.setText(it.value.getValue()!!.getEducation())
                    binding.textViewExperience.setText(
                        it.value.getValue()!!.getExperience()
                    )

                    for (i in it.value.getValue()!!.getListOfDates()!!.indices) {
                        if (i == 0) {
                            mSelectDate =
                                it.value.getValue()!!.getListOfDates()!![i]!!.date!!.toInt()
                            Log.e(
                                "CurrentDate->",
                                it.value.getValue()!!.getListOfDates()!![i]!!.date.toString()
                            )
                        }
                    }
                    if (it.value.getValue()!!.getIsAvailable()!!) {
                        binding.textViewDoctorAvailability.setText(
                            "Available Now"
                        )
                    } else {
                        binding.textViewDoctorAvailability.setText(
                            "UnAvailable Now"
                        )
                    }
                    Utils.Presigned(
                        it.value.getValue()!!.getProfilePic()!!,
                        mPrefs!!.s3BucketName,
                        it.value.getValue()!!.getDoctorId(),
                        this,
                        Utils.decryption(mPrefs!!.w3SecretKey),
                        Utils.decryption(mPrefs!!.w3AccessKey),
                        mPrefs!!.s3BucketRegion,
                        "doctor"
                    )

                    mAvailabilityTime =
                        it.value.getValue()!!.getListOfDates()!! as ArrayList<ListOfDate>
                    val adapter = SetAvailabilityDateAdapter(this, mAvailabilityTime!!)
                    binding.recyclerviewDate.adapter = adapter
                    binding.layoutMorning.performClick()
                }

                is Resource.GenericError -> {
                    if (progressDoalog != null && progressDoalog!!.isShowing) {
                        progressDoalog!!.dismiss()
                    }
                    Utils.showToast(this, it.error!!.message.toString(), true)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: DownloadImageViewModel) {
        Glide.with(this).load(event.mUrl)
            .placeholder(R.drawable.ic_profile_no_image)
            .error(R.drawable.ic_profile_no_image)
            .into(binding.ivDoctorProfilePicture)
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: SaveDoctorAppointment) {

        mSlotID = event.mSlotID

        val inputFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a", Locale.ENGLISH)

        val start = event.mStartTime.replace("+00:00", ".000Z");
        val end = event.mEndTime.replace("+00:00", ".000Z");

        val startTime: LocalDateTime =
            LocalDateTime.parse(start, inputFormatter)
        val StartTiming: String = outputFormatter.format(startTime)

        val endTime: LocalDateTime =
            LocalDateTime.parse(end, inputFormatter)
        val EndTiming: String = outputFormatter.format(endTime)


        val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss a")
        //formatter.timeZone = TimeZone.getTimeZone("UTC")
        val date = formatter.parse(StartTiming) as Date

        System.out.println("Today is " + date.time)

        val formatter1: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss a")
        //formatter1.timeZone = TimeZone.getTimeZone("UTC")
        val date1 = formatter1.parse(EndTiming) as Date
        System.out.println("Today is " + date1.time)

        mStartTimeStamp = date.time
        mEndTimeStamp = date1.time
    }
}

