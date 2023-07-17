package com.lifehopehealthapp.orderSummary

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.amazonaws.AmazonServiceException
import com.amazonaws.HttpMethod
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.*
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.databinding.ActivityOrderSummaryBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.splash.SplashActivity
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.ErrorHandling
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.addresslatlng
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.cityName
import com.lifehopehealthapp.utils.PreferenceHelper.fcmToken
import com.lifehopehealthapp.utils.PreferenceHelper.latlng
import com.lifehopehealthapp.utils.PreferenceHelper.profile
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketName
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketRegion
import com.lifehopehealthapp.utils.PreferenceHelper.saveSettingsAPI
import com.lifehopehealthapp.utils.PreferenceHelper.stateName
import com.lifehopehealthapp.utils.PreferenceHelper.userAge
import com.lifehopehealthapp.utils.PreferenceHelper.userGender
import com.lifehopehealthapp.utils.PreferenceHelper.userID
import com.lifehopehealthapp.utils.PreferenceHelper.userlimit
import com.lifehopehealthapp.utils.PreferenceHelper.w3AccessKey
import com.lifehopehealthapp.utils.PreferenceHelper.w3SecretKey
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.utils.Utils.PerfectDecimal
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.OnBalloonClickListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderSummaryActivity : BaseActivity<OrderSummaryViewModel, OrderSummaryModel>(),
    OnBalloonClickListener {

    private var token: String? = ""
    private var name: String? = ""
    private var address: String? = ""
    private var profileImage: String? = ""
    private var BookTest: String? = ""
    private var TestType: String? = ""
    private var title: String? = ""
    private var description: String? = ""
    private var image: String? = ""
    private var BookingType: Int = 0
    private var price: String? = ""
    private var selectdateTime: String? = ""
    private var time: String? = ""
    private var age: String? = ""
    private var gender: String? = ""
    private var testTypeId: String? = ""
    private var testCategoryId: String? = ""
    private var iosImage: String? = ""
    private var latlng: String? = ""
    private var tax: String? = ""
    private var shippingCharge: String? = ""
    private var extraInfo: String? = ""
    private var mobile: String? = ""
    private var ProfilePic: String? = ""
    private var apiResponse: String? = ""
    private var countryCode: String? = ""
    private var mCity: String? = ""

    private var userLimit: Int? = 0
    private var mTime: Int? = 0
    private var count: Int? = 0
    private var countData: Int? = 0

    private var total: Double? = 0.00
    private var mTAX: Double? = 0.00
    private var mTaxData: Double? = 0.00
    private var mShippingCharge: Double? = 0.00
    private var priceamount: Double? = 0.00
    private var totalPriceAPI: Double? = 0.00

    private var progressDoalog: Dialog? = null
    private var mPrefs: SharedPreferences? = null
    private var paymentItems: ArrayList<Payment>? = ArrayList()
    private var memberItems: ArrayList<Member>? = ArrayList()
    private var userData: ArrayList<String>? = null
    private var adapter: AddMemberAdapter? = null
    var balloon: Balloon? = null
    var mLayoutManager: LinearLayoutManager? = null

    private lateinit var binding: ActivityOrderSummaryBinding

    override fun getViewModel() = OrderSummaryViewModel::class.java

    override fun getActivityRepository() =
        OrderSummaryModel(remoteDataSource.buildApi(APIManager::class.java), PreferenceHelper)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSummaryBinding.inflate(layoutInflater)
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
        time = extras.getString("TimeZone").toString()
        testTypeId = extras.getString("testTypeId").toString()
        testCategoryId = extras.getString("testCategoryId").toString()
        selectdateTime = extras.getString("SelectDateTime").toString()
        tax = extras.getString("tax").toString()
        shippingCharge = extras.getString("shippingCharge").toString()
        name = extras.getString("name").toString()
        iosImage = extras.getString("iosImage").toString()
        address = extras.getString("address").toString()
        mobile = extras.getString("mobile").toString()
        ProfilePic = extras.getString("ProfilePic").toString()
        extraInfo = extras.getString("extraInfo")
        BookingType = extras.getInt("BookingType")

        mTAX = tax!!.toDouble()
        mShippingCharge = shippingCharge!!.toDouble()

        if (selectdateTime!!.contentEquals("null")) {

        } else {
            binding.textviewSelectDate.isVisible = true
            binding.textviewSelectDate.setText(resources.getString(R.string.text_schedule_txt) + selectdateTime.toString())
        }
        binding.textviewMobileInfoLabel.loadSvg(image!!)
        profileImage = mPrefs!!.profile
        age = mPrefs!!.userAge.toString()
        gender = mPrefs!!.userGender
        latlng = mPrefs!!.latlng
        userLimit = mPrefs!!.userlimit
        mCity = mPrefs!!.cityName
        apiResponse = mPrefs!!.saveSettingsAPI

        Log.e("apiResponse", apiResponse.toString())
        /*val aGson = Gson()
        val aSettingConfig: SettingsResponse =
            aGson.fromJson(apiResponse!!, SettingsResponse::class.java)
        countryCode = aSettingConfig.getValue()!!.data!!.countryCode*/
        val obj = JSONObject(apiResponse.toString())
        countryCode = obj.get("countryCode").toString()

        binding.textviewTestHeading.text = title
        binding.textviewTestName.text = title
        binding.textviewTestDescription.text = description
        binding.textviewUserName.text = name
        binding.textviewUserMobile.setText(countryCode + " " + mobile)
        binding.textviewUserAddress.text = address

        PresignedImg(
            ProfilePic.toString(),
            mPrefs!!.s3BucketName,
            mPrefs!!.userID,
            this,
            Utils.decryption(mPrefs!!.w3SecretKey),
            Utils.decryption(mPrefs!!.w3AccessKey)
        )
        /*if (ProfilePic.equals("")) {
            Glide.with(this).load(profileImage)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_profile_no_image).error(R.drawable.ic_profile_no_image)
                .into(binding.userProfileImage)
        } else {
            Glide.with(this).load(ProfilePic)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_profile_no_image).error(R.drawable.ic_profile_no_image)
                .into(binding.userProfileImage)
        }
*/
        userData = ArrayList<String>()

        mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerviewUserData.setLayoutManager(mLayoutManager)
        mLayoutManager!!.setStackFromEnd(true);
        mLayoutManager!!.setReverseLayout(true);

        binding.imageviewBackArrow.setOnClickListener {
            finish()
        }
        binding.imageviewAddMember.setOnClickListener {
            if (userData!!.size < userLimit!!) {
                showPopup()
            }
            else
            {
                Utils.showToast(
                    this,
                    resources.getString(R.string.toast_ad_on_member),
                    true
                )
            }
        }

        Log.e("arraysize", userData!!.size.toString())


        adapter = AddMemberAdapter(userData!!, this)
        binding.recyclerviewUserData.adapter = adapter
        adapter!!.notifyDataSetChanged()

        binding.buttonBookNow.setOnClickListener {
            booknow()
        }

        binding.imageviewBackArrow.setOnClickListener {
            finish()
        }

        binding.imageviewEditProfile.setOnClickListener {
            finish()
        }

        val formatter: NumberFormat =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                DecimalFormat("##.##")
            } else {
                TODO("VERSION.SDK_INT < N")
            }
        if (TestType!!.contentEquals("1")) {
            binding.textviewTestType.text = "S"
            val dtime1 = DecimalFormat("#.##")
            val i1 = java.lang.Double.valueOf(dtime1.format(mShippingCharge!!))


           /* binding.textviewShippingCharge.setText("$" + String.format("%.2f", mShippingCharge))*/

            binding.textviewShippingCharge.setText(NumberFormat.getCurrencyInstance(
                Locale("en", "US")
            ).format(mShippingCharge))

            val userSize: Int? = userData!!.size + 1
            priceamount = price!!.toDouble()
            total = multiply(userSize!!.toDouble(), priceamount!!)
            totalPriceAPI = total

            val priceDetail: Double = mShippingCharge!! + priceamount!!
            mTaxData = ((priceDetail * mTAX!!) / 100)
            Log.e("result", mTaxData.toString())
            /*mTaxData = PerfectDecimal(mTaxData.toString(), 3, 2)!!.toDouble()*/
            total = mTaxData!! + mShippingCharge!! + priceamount!!
            val str2: String? = PerfectDecimal(mTaxData.toString(), 3, 2)



           /* binding.textviewTaxCharge.setText("$" + str2)
            binding.textviewTestAmount.setText("$" + String.format("%.2f", priceamount))
            binding.textviewTotalCharge.setText("$" + String.format("%.2f", total))*/

            binding.textviewTaxCharge.setText(NumberFormat.getCurrencyInstance(
                Locale("en", "US")
            ).format(mTaxData))
            binding.textviewTestAmount.setText(NumberFormat.getCurrencyInstance(
                Locale("en", "US")
            ).format(priceamount))
            binding.textviewTotalCharge.setText(NumberFormat.getCurrencyInstance(
                Locale("en", "US")
            ).format(total))



        } else {
            binding.textviewShippingCharge.isVisible = false
            binding.textviewShippingChargeLabel.isVisible = false
            binding.textviewTestType.text = "H"

            val userSize: Int? = userData!!.size + 1
            priceamount = price!!.toDouble()
            total = multiply(userSize!!.toDouble(), priceamount!!)
            totalPriceAPI = total
            val priceDetail = priceamount!!
            mTaxData = ((priceDetail * mTAX!!) / 100)
            //mTaxData = PerfectDecimal(mTaxData.toString(), 3, 2)!!.toDouble()
            Log.e("result", mTaxData.toString())
            //mTaxData = formatter.format(mTaxData!!).toDouble()
            total = mTaxData!! + priceamount!!
            val str2: String? = PerfectDecimal(mTaxData.toString(), 3, 2)


           /* binding.textviewTaxCharge.setText("$" + str2)
            binding.textviewTestAmount.setText("$" + String.format("%.2f", priceamount))
            binding.textviewTotalCharge.setText("$" + String.format("%.2f", total))
*/

            binding.textviewTaxCharge.setText(NumberFormat.getCurrencyInstance(
                Locale("en", "US")
            ).format(mTaxData))
            binding.textviewTestAmount.setText(NumberFormat.getCurrencyInstance(
                Locale("en", "US")
            ).format(priceamount))
            binding.textviewTotalCharge.setText(NumberFormat.getCurrencyInstance(
                Locale("en", "US")
            ).format(total))


        }

        var geocoder = Geocoder(this@OrderSummaryActivity, Locale.ENGLISH)
        try {
            val parts: List<String> = mPrefs!!.addresslatlng!!.split(",")
            val part1 = parts[0]
            val part2 = parts[1]
            val addresses =
                geocoder.getFromLocation(part1.toDouble(), part2.toDouble(), 1)
            if (addresses.size > 0) {
                val fetchedAddress = addresses[0]
                val strAddress = StringBuilder()
                mCity = addresses[0].getLocality()
                Log.e("mCity", mCity.toString())
                val state = addresses[0].adminArea
                mPrefs!!.stateName = state
                Log.e("stateName", state)
                mPrefs!!.cityName = mCity
                for (i in 0 until fetchedAddress.maxAddressLineIndex) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append(" ")
                }
            } else {
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        balloon = extraInfo?.let {
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
        }


        binding.textviewShippingChargeLabel.setOnClickListener {

            if (balloon!!.isShowing) {
                balloon!!.dismiss()
            } else {
                balloon!!.showAlignTop(binding.textviewShippingChargeLabel)
            }
        }

    }

    private fun PresignedImg(
        filePath: String,
        s3BucketName: String?,
        userID: String?,
        orderSummaryActivity: OrderSummaryActivity,
        secrerKey: String?,
        accessKey: String?
    ) {

        val policy: StrictMode.ThreadPolicy =
            StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        val credentials = BasicAWSCredentials(
            accessKey, secrerKey
        )
        val s3 = AmazonS3Client(credentials)
        java.security.Security.setProperty("networkaddress.cache.ttl", "60")
        if (mPrefs!!.s3BucketRegion!!.contains("us-west-1")) {
            s3.setRegion(Region.getRegion(Regions.US_WEST_1))
        } else if (mPrefs!!.s3BucketRegion!!.contains("us-west-2")) {
            s3.setRegion(Region.getRegion(Regions.US_WEST_2))
        } else {
            s3.setRegion(Region.getRegion(Regions.US_EAST_2))
        }


        try {

            // Set the presigned URL to expire after one hour.
            val expiration = Date()
            var expTimeMillis = expiration.time
            expTimeMillis += 1000 * 60 * 60.toLong()
            expiration.time = expTimeMillis

            // Generate the presigned URL.
            println("Generating pre-signed URL.")
            val generatePresignedUrlRequest: GeneratePresignedUrlRequest =
                GeneratePresignedUrlRequest(
                    s3BucketName,
                    Constants.Image_Bucket_Name + userID + "/" + filePath
                )
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration)
            val url: URL = s3.generatePresignedUrl(generatePresignedUrlRequest)
            Glide.with(this).load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_profile_no_image)
                .error(R.drawable.ic_profile_no_image)
                .into(binding.userProfileImage)
            println("Pre-Signed URL: $url")
        } catch (e: AmazonServiceException) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace()
        }
    }

    fun AppCompatImageView.loadSvg(url: String) {
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

    private fun booknow() {
        val tsLong = System.currentTimeMillis() / 1000
        val timeStamp = tsLong.toString()
        val orderDate = timeStamp.toInt()
        if (time!!.contentEquals("null")) {
            mTime = 0
        } else {
            mTime = time!!.toInt()
            Log.e("SelectTime", mTime.toString())
        }

        if (userData!!.size == 0) {
            count = 1
        } else {
            count = userData!!.size + 1
        }

        paymentItems!!.clear()
        memberItems!!.clear()
        var data = OrderSummaryInputModel()
        data.setAddress(address)
        data.bookingtype = 0
        data.setIOSImagePath(iosImage)
        data.setImagePath(ProfilePic)
        data.setLatLong(mPrefs!!.addresslatlng)
        data.setCurrentlatLong(latlng)
        data.setLocation(mCity)
        data.setOrderDate(orderDate)
        data.setPhoneNo(mobile)
        data.setPrimaryName(name)
        data.setScheduleDate(mTime!!)
        data.setState(mPrefs!!.stateName)
        data.setTestTypeID(testTypeId)
        data.setUserFCMToken(mPrefs!!.fcmToken)
        data.setTestCategoryId(testCategoryId)
        data.BookingType = BookingType

        var paymentValue = Payment()
        paymentValue.paymentName = title
        paymentValue.testCount = count!!
        paymentValue.amount = totalPriceAPI!!
        paymentItems!!.add(paymentValue)
        data.setPayment(paymentItems)

        var paymentValue1 = Payment()
        if (TestType!!.contentEquals("1")) {
            paymentValue1.paymentName = "Shipping Charge"
            paymentValue1.testCount = 1
            paymentValue1.amount = mShippingCharge
            paymentItems!!.add(paymentValue1)
            data.setPayment(paymentItems)
        }

        val dtime =
            DecimalFormat("0.00")
        val i2 = dtime.format(mTaxData!!)
        //val i2: String? = PerfectDecimal(mTaxData.toString(), 3, 2)
        var paymentValue2 = Payment()
        paymentValue2.paymentName = "Tax"
        paymentValue2.testCount = 1
        paymentValue2.amount = i2.toDouble()
        paymentItems!!.add(paymentValue2)
        data.setPayment(paymentItems)

        for (k in userData!!.indices) {
            var memberValue = Member()
            val separated: List<String> = userData!![k].split(",")
            try {
                memberValue.patientName = separated[0]
                memberValue.age = separated[1].toInt()
                memberValue.gender = separated[2]
                memberItems!!.add(memberValue)
            } catch (e: JSONException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

        }
        data.setMembers(memberItems)
        val gson = Gson()
        var json: String? = ""
        json = gson.toJson(data)
        Log.e("Model---->", json.toString())

        val aJsonParser = JsonParser()
        val aJsonObject = aJsonParser.parse(json) as JsonObject
        Log.e("aJsonObject", aJsonObject.toString())
        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            viewModel.getOrderBooking(token!!, aJsonObject)
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }

        viewModel.Response1.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    if (progressDoalog != null && progressDoalog!!.isShowing) {
                        progressDoalog!!.dismiss()
                    }
                    if (it.value.getStatusCode() == 200) {
                        val url: String = it.value.getValue()!!.data!!.link!!
                        val order_id: String = it.value.getValue()!!.data!!.orderID!!
                        Log.e("orderID", it.value.getValue()!!.data!!.orderID!!)
                        Utils.webViewData("null", url, order_id, this)
                        finish()
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
                    if (it.code == 400) {
                        Utils.clearSharedPreferences(this)
                        val intent = Intent(this, SplashActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val extras = Bundle()
                        extras.putString("LogOut", "LogOut")
                        intent.putExtras(extras)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        Utils.showToast(this, "User doesn't exist", true)
                    } else if (it.code == 401) {
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

    private fun showPopup() {
        var passData: String? = ""
        var genderName: ArrayList<String>? = null
        var genderData: String? = ""
        var userNameData: String? = ""
        var userAgeData: String? = ""
        val alertDialog = Dialog(this)
        val lp = WindowManager.LayoutParams()
        val window = alertDialog.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.dialog_add_members)
        val userName: TextInputEditText = alertDialog.findViewById(R.id.edittext_name)
        val userAge: TextInputEditText = alertDialog.findViewById(R.id.edittext_age)
        val gender: Spinner = alertDialog.findViewById(R.id.spinner_gender)

        genderName = ArrayList<String>()
        genderName.add("Select Gender")
        genderName.add("Male")
        genderName.add("Female")

        val adapter1 = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, genderName
        )
        gender.adapter = adapter1

        gender.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                genderData = genderName[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val posTv: AppCompatButton = alertDialog.findViewById(R.id.button_proceed)
        posTv.setOnClickListener(View.OnClickListener {
            userNameData = userName.text.toString().trim()
            userAgeData = userAge.text.toString().trim()
            if (!userNameData!!.contentEquals("")) {
                if (!userAgeData!!.contentEquals("")) {
                    val limiAge: Int = userAgeData!!.toInt()
                    if (limiAge <= 120) {
                        if (!genderData!!.equals("Select Gender")) {
                            binding.cardPersonTax.isVisible = true
                            passData = userNameData + "," + userAgeData + "," + genderData
                            if (userData!!.size < userLimit!!) {
                                userData!!.add(passData!!)
                                binding.cardPersonTax.isVisible = true
                                alertDialog.dismiss()
                                adapter!!.notifyDataSetChanged()
                                val userSize: Int? = userData!!.size + 1
                                val priceamount = price!!.toDouble()
                                total = multiply(userSize!!.toDouble(), priceamount)
                                //total = total!! + mShippingCharge!!


                               /* binding.textviewTestAmount.text =
                                    "$" + String.format("%.2f", total)*/


                                binding.textviewTestAmount.text =
                                    NumberFormat.getCurrencyInstance(
                                        Locale("en", "US")
                                    ).format(total)



                                totalPriceAPI = total

                                Log.e("result_total", ""+total)
                                if (TestType!!.contentEquals("1")) {
                                    val withShipping: Double = total!! + mShippingCharge!!
                                    var data = withShipping * mTAX!!
                                    mTaxData = (data / 100)


                                    Log.e("withShipping", withShipping.toString())
                                    Log.e("result", mTaxData.toString())

                                    val dtime =
                                        DecimalFormat("0.00")
                                    val i2 = dtime.format(mTaxData!!)


                                    Log.e("Tax", i2.toString())



                                   // val str2: String? = PerfectDecimal(mTaxData.toString(), 3, 2)
                                  //  binding.textviewTaxCharge.setText("$" + str2)

                                   /* binding.textviewTaxCharge.setText("$" + i2)*/


                                    binding.textviewTaxCharge.setText(NumberFormat.getCurrencyInstance(
                                        Locale("en", "US")
                                    ).format(mTaxData))



                                    //newly hided
                                  //  total = total!! + mShippingCharge!! + mTaxData!!

                                    //newly added
                                    total = total!! + mShippingCharge!! + i2.toDouble()


                                    val dtime1 =
                                        DecimalFormat("0.00")
                                    val i1 = dtime1.format(total!!)


                                   /* binding.textviewTotalCharge.setText("$" + i1)*/

                                    binding.textviewTotalCharge.setText(NumberFormat.getCurrencyInstance(
                                        Locale("en", "US")
                                    ).format(i1.toDouble()))



                                    countData = userSize + 1
                                    binding.textviewTestName.setText(title + " x " + userSize)
                                } else {

                                    mTaxData = ((total!! * mTAX!!) / 100)
                                    Log.e("result", mTaxData.toString())
                                    //mTaxData = formatter!!.format(mTaxData!!).toDouble()
                                    val dtime =
                                        DecimalFormat("0.00")
                                    val i2 = dtime.format(mTaxData!!)


                                   // val str2: String? = PerfectDecimal(mTaxData.toString(), 3, 2)
                                   // binding.textviewTaxCharge.setText("$" + str2)

                                    //binding.textviewTaxCharge.setText("$" + i2)

                                    binding.textviewTaxCharge.setText(NumberFormat.getCurrencyInstance(
                                        Locale("en", "US")
                                    ).format(mTaxData))



                                    //newly hided
                                   // total = total!! + mTaxData!!

                                    //newly added
                                    total = total!! + i2.toDouble()


                                    val dtime1 =
                                        DecimalFormat("0.00")
                                    val i1 = dtime1.format(total!!)

                                  /*  binding.textviewTotalCharge.setText("$" + i1)*/

                                    binding.textviewTotalCharge.setText(NumberFormat.getCurrencyInstance(
                                        Locale("en", "US")
                                    ).format(i1.toDouble()))



                                    countData = userSize + 1
                                    binding.textviewTestName.setText(title + " x " + userSize)
                                }

                            } else {
                                Utils.showToast(
                                    this,
                                    resources.getString(R.string.toast_ad_on_member),
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
                        Utils.showToast(this, getString(R.string.age_greater_than_120), true)
                    }
                } else {
                    Utils.showToast(this, getString(R.string.toast_enter_valid_age), true)
                }
            } else {
                Utils.showToast(this, getString(R.string.enter_the_name), true)
            }
        })
        val negTv: AppCompatButton = alertDialog.findViewById(R.id.button_cancel)
        negTv.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
        })
        alertDialog.show()
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

    fun multiply(x: Double, y: Double): Double? = x * y

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: AddOnMemberModel) {
        if (event.count == 0) {
            binding.cardPersonTax.isVisible = false
            binding.textviewTestName.setText(title)
            val userSize: Int? = userData!!.size + 1
            val priceamount = price!!.toDouble()
            total = multiply(userSize!!.toDouble(), priceamount)
            //total = total!! + mShippingCharge!!


           /* binding.textviewTestAmount.text =
                "$" + String.format("%.2f", total)*/


            binding.textviewTestAmount.text =
                NumberFormat.getCurrencyInstance(
                    Locale("en", "US")
                ).format(total)



            totalPriceAPI = total
            if (TestType!!.contentEquals("1")) {
                val withShipping: Double = total!! + mShippingCharge!!
                var data = withShipping * mTAX!!
                mTaxData = (data / 100)
                Log.e("withShipping", withShipping.toString())
                Log.e("result", mTaxData.toString())
                val dtime =
                    DecimalFormat("0.00")
                val i2 = dtime.format(mTaxData!!)
                Log.e("Tax", i2.toString())
               /* val str2: String? = PerfectDecimal(mTaxData.toString(), 3, 2)
                binding.textviewTaxCharge.setText("$" + str2)*/

               /* binding.textviewTaxCharge.setText("$" + i2)*/


                binding.textviewTaxCharge.setText(NumberFormat.getCurrencyInstance(
                    Locale("en", "US")
                ).format(mTaxData))



                //newly hided
               // total = total!! + mShippingCharge!! + mTaxData!!

                total = total!! + mShippingCharge!! + i2.toDouble()


                val dtime1 =
                    DecimalFormat("0.00")
                val i1 = dtime1.format(total!!)

               /* binding.textviewTotalCharge.setText("$" + i1)*/


                binding.textviewTotalCharge.setText(NumberFormat.getCurrencyInstance(
                    Locale("en", "US")
                ).format(i1.toDouble()))


                countData = userSize + 1
                binding.textviewTestName.setText(title + " x " + userSize)
            } else {

                mTaxData = ((total!! * mTAX!!) / 100)
                Log.e("result", mTaxData.toString())
                //mTaxData = formatter!!.format(mTaxData!!).toDouble()
                val dtime =
                    DecimalFormat("0.00")
                val i2 = dtime.format(mTaxData!!)

                /*val str2: String? = PerfectDecimal(mTaxData.toString(), 3, 2)
                binding.textviewTaxCharge.setText("$" + str2)*/

               /* binding.textviewTaxCharge.setText("$" + i2)*/

                binding.textviewTaxCharge.setText(NumberFormat.getCurrencyInstance(
                    Locale("en", "US")
                ).format(mTaxData))


                //newly hided
               // total = total!! + mTaxData!!
                total = total!! + i2.toDouble()

                val dtime1 =
                    DecimalFormat("0.00")
                val i1 = dtime1.format(total!!)

               /* binding.textviewTotalCharge.setText("$" + i1)*/

                binding.textviewTotalCharge.setText(NumberFormat.getCurrencyInstance(
                    Locale("en", "US")
                ).format(i1.toDouble()))


                countData = userSize + 1
                binding.textviewTestName.setText(title + " x " + userSize)
            }

            /*if (TestType!!.contentEquals("1")) {
                val priceDetail: Double = mShippingCharge!! + priceamount!!
                mTaxData = ((priceDetail * mTAX!!) / 100)
                Log.e(
                    "result",
                    mTaxData.toString() + "   priceamount " + priceamount + "  mTAX " + mTAX + "  mShippingCharge " + mShippingCharge
                )
                val dtime =
                    DecimalFormat("#.##")
                val i2 = java.lang.Double.valueOf(dtime.format(mTaxData!!))
                val str2: String? = PerfectDecimal(mTaxData.toString(), 3, 2)
                binding.textviewTaxCharge.setText("$ " + str2)

                total = mTaxData!! + mShippingCharge!! + priceamount!!
                val dtime1 =
                    DecimalFormat("#.##")
                val i1 = java.lang.Double.valueOf(dtime1.format(total!!))
                binding.textviewTotalCharge.setText("$ " + i1)
                binding.textviewTestAmount.text = "$ " + String.format("%.2f", priceamount)
            } else {
                mTaxData = ((priceamount!! * mTAX!!) / 100)
                Log.e("result", mTaxData.toString() + "priceamount " + priceamount + "mTAX" + mTAX)
                val dtime =
                    DecimalFormat("#.##")
                val i2 = java.lang.Double.valueOf(dtime.format(mTaxData!!))
                val str2: String? = PerfectDecimal(mTaxData.toString(), 3, 2)
                binding.textviewTaxCharge.setText("$ " + str2)

                total = mTaxData!! + priceamount!!

                val dtime1 =
                    DecimalFormat("#.##")
                val i1 = java.lang.Double.valueOf(dtime1.format(total!!))
                binding.textviewTotalCharge.setText("$ " + i1)
                binding.textviewTestAmount.text = "$ " + String.format("%.2f", priceamount)
            }*/

        } else {
            val userSize: Int? = userData!!.size + 1
            val priceamount = price!!.toDouble()
            total = multiply(userSize!!.toDouble(), priceamount)
            //total = total!! + mShippingCharge!!


            /*binding.textviewTestAmount.text =
                "$" + String.format("%.2f", total)*/


            binding.textviewTestAmount.text =NumberFormat.getCurrencyInstance(
                Locale("en", "US")
            ).format(total)



            totalPriceAPI = total
            if (TestType!!.contentEquals("1")) {
                val withShipping: Double = total!! + mShippingCharge!!
                var data = withShipping * mTAX!!
                mTaxData = (data / 100)
                Log.e("withShipping", withShipping.toString())
                Log.e("result", mTaxData.toString())

                val dtime =
                    DecimalFormat("0.00")
                val i2 = dtime.format(mTaxData!!)
                Log.e("Tax", i2.toString())

               /* val str2: String? = PerfectDecimal(mTaxData.toString(), 3, 2)
                binding.textviewTaxCharge.setText("$" + str2)*/


               /* binding.textviewTaxCharge.setText("$" + i2)*/

                binding.textviewTaxCharge.setText(NumberFormat.getCurrencyInstance(
                    Locale("en", "US")
                ).format(mTaxData))



                //newly hided
               // total = total!! + mShippingCharge!! + mTaxData!!

                total = total!! + mShippingCharge!! +i2.toDouble()



                val dtime1 =
                    DecimalFormat("0.00")
                val i1 = dtime1.format(total!!)

               /* binding.textviewTotalCharge.setText("$" + i1)*/

                binding.textviewTotalCharge.setText(NumberFormat.getCurrencyInstance(
                    Locale("en", "US")
                ).format(i1.toDouble()))

                countData = userSize + 1
                binding.textviewTestName.setText(title + " x " + userSize)
            } else {

                mTaxData = ((total!! * mTAX!!) / 100)
                Log.e("result", mTaxData.toString())
                //mTaxData = formatter!!.format(mTaxData!!).toDouble()
                val dtime =
                    DecimalFormat("0.00")
                val i2 = dtime.format(mTaxData!!)

                /*val str2: String? = PerfectDecimal(mTaxData.toString(), 3, 2)
                binding.textviewTaxCharge.setText("$" + str2)*/

               /* binding.textviewTaxCharge.setText("$" + i2)*/


                binding.textviewTaxCharge.setText(NumberFormat.getCurrencyInstance(
                    Locale("en", "US")
                ).format(mTaxData))


                //newly hided
               // total = total!! + mTaxData!!

                total = total!! +i2.toDouble()



                val dtime1 =
                    DecimalFormat("0.00")
                val i1 = dtime1.format(total!!)

               /* binding.textviewTotalCharge.setText("$" + i1)*/

                binding.textviewTotalCharge.setText(NumberFormat.getCurrencyInstance(
                    Locale("en", "US")
                ).format(i1.toDouble()))


                countData = userSize + 1
                binding.textviewTestName.setText(title + " x " + userSize)
            }
            /*val userSize: Int? = event.count + 1
            val priceamount = price!!.toDouble()
            total = priceamount * userSize!!.toDouble()
            binding.textviewTestAmount.text = "$ " + String.format("%.2f", total)
            totalPriceAPI = total
            if (TestType!!.contentEquals("1")) {
                val data = total!! * mTAX!!
                mTaxData = (data / 100)
                Log.e("result", mTaxData.toString())
                val dtime =
                    DecimalFormat("#.##")
                val i2 = java.lang.Double.valueOf(dtime.format(mTaxData!!))
                val str2: String? = PerfectDecimal(mTaxData.toString(), 3, 2)
                binding.textviewTaxCharge.setText("$ " + str2)

                total = total!! + mShippingCharge!! + mTaxData!!
                val dtime1 =
                    DecimalFormat("#.##")
                val i1 = java.lang.Double.valueOf(dtime1.format(total!!))
                binding.textviewTotalCharge.setText("$ " + i1)
                countData = userSize + 1
                binding.textviewTestName.setText(title + " x " + userSize)
            } else {
                mTaxData = ((total!! * mTAX!!) / 100)
                Log.e("result", mTaxData.toString())
                val dtime =
                    DecimalFormat("#.##")
                val i2 = java.lang.Double.valueOf(dtime.format(mTaxData!!))
                val str2: String? = PerfectDecimal(mTaxData.toString(), 3, 2)
                binding.textviewTaxCharge.setText("$ " + str2)

                total = total!! + mTaxData!!
                val dtime1 =
                    DecimalFormat("#.##")
                val i1 = java.lang.Double.valueOf(dtime1.format(total!!))
                binding.textviewTotalCharge.setText("$ " + i1)
                countData = userSize + 1
                binding.textviewTestName.setText(title + " x " + userSize)
            }*/
        }
    }

    override fun onBalloonClick(view: View) {
        TODO("Not yet implemented")
        if (balloon!!.isShowing) {
            balloon!!.dismiss()
        }
    }
}