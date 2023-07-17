package com.lifehopehealthapp.bookingList

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
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
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.ResponseModel.TestListResponse
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.bulkBooking.organizationType.OrganizationTypeActivity
import com.lifehopehealthapp.databinding.ActivityTestBookigBinding
import com.lifehopehealthapp.myProfile.MyProfileActivity
import com.lifehopehealthapp.myTestOrderList.MyTestOrderListActivity
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.scanner.ScannerActivity
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
import java.text.DecimalFormat
import kotlin.collections.ArrayList


class TestBookingActivity : BaseActivity<TestBookingViewModel, TestBookingModel>() {

    private var progressDoalog: Dialog? = null
    private var adapter: TestBookingAdapter? = null
    private var mPrefs: SharedPreferences? = null

    private var selfTestNotes: String? = ""
    private var homeTestNotes: String? = ""
    private var mShippingCharge: String? = ""
    private var testType: String? = "1"
    private var selection: String? = "self"
    private var token: String? = ""
    private var mTax: String? = ""
    private var extraInfo: String? = ""
    private var mHome: ArrayList<TestListResponse.TestList> = ArrayList()
    private var mSelf: ArrayList<TestListResponse.TestList> = ArrayList()
    private var holedata: List<TestListResponse.Datum>? = ArrayList<TestListResponse.Datum>()
    private var tvTitle: String? = ""

    private lateinit var binding: ActivityTestBookigBinding

    var decimalWithTwoDigit: DecimalFormat = DecimalFormat("#,###")

    override fun getViewModel() = TestBookingViewModel::class.java

    override fun getActivityRepository() =
        TestBookingModel(remoteDataSource.buildApi(APIManager::class.java), PreferenceHelper)

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBookigBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)

        token = mPrefs!!.authToken
        binding.recyclerviewTestList.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)


        //tvTitle = intent.getStringExtra("title")
        //binding.tvHeading.text = tvTitle


        binding.imageViewOrderList.setOnClickListener {
            startActivity(Intent(this, MyTestOrderListActivity::class.java))
        }

        binding.imageViewScanner.setOnClickListener {
            checkPermissions()
        }

        binding.imageViewBulkBooking.setOnClickListener {
            intent = Intent(this@TestBookingActivity, OrganizationTypeActivity::class.java)
            if (Utils.isLollipopHigher() && it != null) {
                val pairs: Array<Pair<View, String>> =
                    TransitionHelper.createSafeTransitionParticipants(
                        this@TestBookingActivity,
                        false,
                        Pair(it, getString(R.string.trans_tool_bar_title))
                    )
                val transitionActivityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@TestBookingActivity,
                        *pairs
                    )
                startActivity(intent, transitionActivityOptions.toBundle())
            } else {
                startActivity(intent)
            }
        }
        binding.layoutSelfTest.setOnClickListener {
            selection = "self"
            if (mHome.size != 0) {
                binding.layoutSelfTest.setBackground(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.divider_left_select
                    )
                )
            } else {
                binding.layoutSelfTest.setBackground(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.divider_corner
                    )
                )
            }

            binding.layoutHomeTest.setBackground(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_layout_right_select
                )
            )
            binding.textViewSelfTest.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.textViewHomeTest.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )
            binding.textViewInfoText.setText(selfTestNotes)

            if (mSelf.size > 0) {
                adapter!!.notifyDataSetChanged()
                adapter!!.checkdata(mSelf)

                adapter!!.notifyDataSetChanged()
            }

            testType = "1"
        }
        binding.layoutHomeTest.setOnClickListener {
            selection = "home"
            binding.layoutSelfTest.setBackground(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_layout_left_select
                )
            )
            binding.layoutHomeTest.setBackground(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_right_select
                )
            )
            binding.textViewSelfTest.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.purple_500
                )
            )
            binding.textViewHomeTest.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.textViewInfoText.setText(homeTestNotes)

            if (mHome.size > 0) {
                adapter!!.notifyDataSetChanged()
                adapter!!.checkdata(mHome)
                adapter!!.notifyDataSetChanged()
            }

            testType = "2"
        }

        binding.recyclerviewTestList.addOnItemTouchListener(
            RecyclerItemClickListenr(
                this,
                binding.recyclerviewTestList,
                object : RecyclerItemClickListenr.OnItemClickListener {

                    override fun onItemClick(view: View, position: Int) {
                        if (selection!!.contentEquals("home")) {
                            if (mHome[position].isBookingType == 1) {
                                pricePopup(
                                    mHome[position].price,
                                    mHome[position].expeditedPricing,
                                    mHome[position].actualDescription,
                                    mHome[position].expeditedDescription,
                                    position
                                )
                            } else {

                                intent =
                                    Intent(this@TestBookingActivity, MyProfileActivity::class.java)
                                val extras = Bundle()
                                extras.putString("BookTest", "BookTest")
                                extras.putString("TestType", testType)
                                extras.putInt("BookingType", 0)
                                extras.putString("title", mHome[position].getCategoryName()!!)
                                extras.putString("des", mHome[position].getCategoryDescription()!!)
                                extras.putString("pic", mHome[position].getImagePath()!!)
                                extras.putString("price", mHome[position].price.toString())
                                extras.putString("testTypeId", mHome[position].getTestTypeId()!!)
                                extras.putString("iosImage", mHome[position].getIosImagePath()!!)
                                extras.putString("testNotes", homeTestNotes)
                                extras.putString("extraInfo", extraInfo)
                                extras.putString(
                                    "testCategoryId",
                                    mHome[position].getTestCategoryId()!!
                                )
                                extras.putString("tax", mTax)
                                extras.putString(
                                    "shippingCharge",
                                    mShippingCharge
                                )
                                intent.putExtras(extras)
                                /*if (Utils.isLollipopHigher() && view != null) {
                                    val pairs: Array<Pair<View, String>> =
                                        TransitionHelper.createSafeTransitionParticipants(
                                            this@TestBookingActivity,
                                            false,
                                            Pair(view, getString(R.string.trans_tool_bar_title))
                                        )
                                    val transitionActivityOptions =
                                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                            this@TestBookingActivity,
                                            *pairs
                                        )
                                    startActivity(intent, transitionActivityOptions.toBundle())
                                } else {

                                }*/
                                startActivity(intent)
                            }
                        } else if (selection!!.contentEquals("self")) {
                            if (mSelf[position].isBookingType == 1) {
                                pricePopup(
                                    mSelf[position].price,
                                    mSelf[position].expeditedPricing,
                                    mSelf[position].actualDescription,
                                    mSelf[position].expeditedDescription,
                                    position
                                )
                            } else {
                                intent =
                                    Intent(this@TestBookingActivity, MyProfileActivity::class.java)
                                val extras = Bundle()
                                extras.putString("BookTest", "BookTest")
                                extras.putString("TestType", testType)
                                extras.putString("title", mSelf[position].getCategoryName()!!)
                                extras.putString("des", mSelf[position].getCategoryDescription()!!)
                                extras.putString("pic", mSelf[position].getImagePath()!!)
                                extras.putString("price", mSelf[position].price.toString())
                                extras.putString("iosImage", mSelf[position].getIosImagePath()!!)
                                extras.putString("testTypeId", mSelf[position].getTestTypeId()!!)
                                extras.putString("testNotes", selfTestNotes)
                                extras.putString("extraInfo", extraInfo)
                                extras.putInt("BookingType", 0)
                                extras.putString(
                                    "testCategoryId",
                                    mSelf[position].getTestCategoryId()!!
                                )
                                extras.putString("tax", mTax)
                                extras.putString(
                                    "shippingCharge",
                                    mShippingCharge
                                )
                                intent.putExtras(extras)
                                /*if (Utils.isLollipopHigher() && view != null) {
                                    val pairs: Array<Pair<View, String>> =
                                        TransitionHelper.createSafeTransitionParticipants(
                                            this@TestBookingActivity,
                                            false,
                                            Pair(view, getString(R.string.trans_tool_bar_title))
                                        )
                                    val transitionActivityOptions =
                                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                            this@TestBookingActivity,
                                            *pairs
                                        )
                                    startActivity(intent, transitionActivityOptions.toBundle())
                                } else {

                                }*/
                                startActivity(intent)
                            }
                        }
                    }
                })
        )

        binding.imageViewBackArrow.setOnClickListener {
            finish()
        }

    }

    private fun checkPermissions() {

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        startActivity(Intent(this@TestBookingActivity, ScannerActivity::class.java))
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


    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
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
                        if (it.value.getStatusCode() == 200) {
                            val imagepath = it.value.getValue()!!.data!!.get(0).imagePath

                            if (imagepath.equals("")) {
                                binding.layoutBulkBooking.isVisible = false
                            } else {
                                binding.layoutBulkBooking.isVisible = true
                                GlideToVectorYou
                                    .init()
                                    .with(this)
                                    .apply { requestBuilder.fitCenter() }
                                    .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
                                    .load(Uri.parse(imagepath), binding.imageViewBulkBooking)
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

                        holedata = (it.value.getValue()!!.data as List<TestListResponse.Datum>?)!!
                        mTax = holedata!![0].getTax().toString()
                        mShippingCharge = holedata!![0].shippingCharge.toString()
                        selfTestNotes = holedata!![0].type1Notes
                        homeTestNotes = holedata!![0].type2Notes
                        extraInfo = holedata!![0].extraInfo
                        binding.imageViewInfo.isVisible = true
                        binding.textViewInfoText.text = selfTestNotes
                        mSelf.clear()
                        mHome.clear()

                        for (item in holedata!!.indices) {
                            for (l in holedata!![item].list!!.indices) {
                                var data: String =
                                    holedata!![item].list!!.get(l)!!.price!!.toString()
                                var type: Int = holedata!![item].list!!.get(l)!!.type!!
                                val testValue: TestListResponse.TestList =
                                    TestListResponse.TestList()
                                if (type == 1) {
                                    val data = TestListResponse.TestList()
                                    data.setType(it.value.getValue()!!.data?.get(0)!!.list?.get(l)!!.type)
                                    data.setCategoryDescription(
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(
                                            l
                                        )!!.getCategoryDescription()
                                    )
                                    data.setCategoryName(
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(
                                            l
                                        )!!.getCategoryName()
                                    )
                                    data.setImagePath(
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(
                                            l
                                        )!!.imagePath
                                    )
                                    data.setOrderBy(it.value.getValue()!!.data?.get(0)!!.list?.get(l)!!.orderBy)
                                    data.setPrice(it.value.getValue()!!.data?.get(0)!!.list?.get(l)!!.price)
                                    data.isBookingType =
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(l)!!.isBookingType
                                    data.setTestCategoryId(
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(
                                            l
                                        )!!.getTestCategoryId()
                                    )
                                    data.setTestTypeId(
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(
                                            l
                                        )!!.testTypeId
                                    )
                                    data.expeditedPricing =
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(l)!!.expeditedPricing
                                    data.setIosImagePath(
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(
                                            l
                                        )!!.getIosImagePath()
                                    )
                                    data.actualDescription =
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(l)!!.actualDescription
                                    data.expeditedDescription =
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(l)!!.expeditedDescription
                                    mSelf.add(data)
                                } else {
                                    val data = TestListResponse.TestList()
                                    data.setType(it.value.getValue()!!.data?.get(0)!!.list?.get(l)!!.type)
                                    data.setCategoryDescription(
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(
                                            l
                                        )!!.getCategoryDescription()
                                    )
                                    data.setCategoryName(
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(
                                            l
                                        )!!.getCategoryName()
                                    )
                                    data.setImagePath(
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(
                                            l
                                        )!!.imagePath
                                    )
                                    data.setOrderBy(it.value.getValue()!!.data?.get(0)!!.list?.get(l)!!.orderBy)
                                    data.setPrice(it.value.getValue()!!.data?.get(0)!!.list?.get(l)!!.price)
                                    data.setIosImagePath(
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(
                                            l
                                        )!!.getIosImagePath()
                                    )
                                    data.isBookingType =
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(l)!!.isBookingType
                                    data.expeditedPricing =
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(l)!!.expeditedPricing
                                    data.setTestCategoryId(
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(
                                            l
                                        )!!.getTestCategoryId()
                                    )
                                    data.setTestTypeId(
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(
                                            l
                                        )!!.testTypeId
                                    )
                                    data.actualDescription =
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(l)!!.actualDescription
                                    data.expeditedDescription =
                                        it.value.getValue()!!.data?.get(0)!!.list?.get(l)!!.expeditedDescription
                                    mHome.add(data)
                                }
                            }
                        }
                        if (mSelf.size > 0) {
                            binding.layoutSelfTest.isVisible = true
                            binding.layoutSelfTest.setBackground(resources.getDrawable(R.drawable.divider_corner))
                        } else {
                            binding.layoutSelfTest.isVisible = false
                        }
                        if (mHome.size > 0) {
                            binding.layoutHomeTest.isVisible = true
                            binding.layoutHomeTest.setBackground(resources.getDrawable(R.drawable.alert_bg))
                        } else {
                            binding.layoutHomeTest.isVisible = false
                        }
                        adapter = TestBookingAdapter(mSelf, this)
                        binding.recyclerviewTestList.adapter = adapter
                        adapter!!.notifyDataSetChanged()
                        binding.layoutSelfTest.performClick()
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

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        TransitionHelper.removeActivityFromTransitionManager(this)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun handleEvent(event: String) {
        Log.e("event", event)
    }

    private fun pricePopup(
        price: Int?,
        expeditedPricing: Int?,
        actualDescription: String?,
        expeditedDescription: String?,
        position: Int
    ) {
        var selectedPrice: String = ""
        val alertDialog = Dialog(this)
        val lp = WindowManager.LayoutParams()
        val window = alertDialog.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.alert_dialog_price_list)
        val posTv: AppCompatButton = alertDialog.findViewById(R.id.buttonBookNow)
        val layoutActualPrice: ConstraintLayout =
            alertDialog.findViewById(R.id.layoutActualPrice)
        val textViewInfoText: AppCompatTextView =
            alertDialog.findViewById(R.id.textViewInfoText)
        val layoutExpeditedPricing: ConstraintLayout =
            alertDialog.findViewById(R.id.layoutExpeditedPricing)
        val textViewPrice: AppCompatTextView =
            alertDialog.findViewById(R.id.textViewPrice)
        val textViewSelfTest: AppCompatTextView =
            alertDialog.findViewById(R.id.textViewSelfTest)
        val textViewHomeTest: AppCompatTextView =
            alertDialog.findViewById(R.id.textViewHomeTest)
        selectedPrice = price.toString()
       /* textViewPrice.setText("$" + price.toString())*/
        textViewPrice.setText("$" +decimalWithTwoDigit.format(price))
        textViewInfoText.setText(actualDescription)
        layoutActualPrice.setOnClickListener {
            layoutActualPrice.setBackground(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_left_select
                )
            )
            layoutExpeditedPricing.setBackground(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_layout_right_select
                )
            )
            textViewInfoText.setText(actualDescription)
            textViewSelfTest.setTextColor(resources.getColor(R.color.white))
            textViewHomeTest.setTextColor(resources.getColor(R.color.purple_500))
            //textViewPrice.setText("$" + price.toString())
            textViewPrice.setText("$"+decimalWithTwoDigit.format(price))

            selectedPrice = price.toString()
        }

        layoutExpeditedPricing.setOnClickListener {
            layoutActualPrice.setBackground(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_layout_left_select
                )
            )
            layoutExpeditedPricing.setBackground(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider_right_select
                )
            )
            textViewInfoText.setText(expeditedDescription)
            textViewSelfTest.setTextColor(resources.getColor(R.color.purple_500))
            textViewHomeTest.setTextColor(resources.getColor(R.color.white))
            /*textViewPrice.setText("$" + expeditedPricing.toString())*/

            textViewPrice.setText("$"+decimalWithTwoDigit.format(expeditedPricing))

            selectedPrice = expeditedPricing.toString()
        }
        posTv.setOnClickListener {
            if (selection!!.contentEquals("self")) {
                intent =
                    Intent(this@TestBookingActivity, MyProfileActivity::class.java)
                val extras = Bundle()
                extras.putString("BookTest", "BookTest")
                extras.putString("TestType", testType)
                extras.putInt("BookingType", 1)
                extras.putString("title", mSelf[position].getCategoryName()!!)
                extras.putString("des", mSelf[position].getCategoryDescription()!!)
                extras.putString("pic", mSelf[position].getImagePath()!!)
                extras.putString("price", selectedPrice)
                extras.putString("iosImage", mSelf[position].getIosImagePath()!!)
                extras.putString("testTypeId", mSelf[position].getTestTypeId()!!)
                extras.putString("testNotes", selfTestNotes)
                extras.putString("extraInfo", extraInfo)
                extras.putString(
                    "testCategoryId",
                    mSelf[position].getTestCategoryId()!!
                )
                extras.putString("tax", mTax)
                extras.putString(
                    "shippingCharge",
                    mShippingCharge
                )
                intent.putExtras(extras)
                startActivity(intent)
                alertDialog.dismiss()
            } else {
                intent =
                    Intent(this@TestBookingActivity, MyProfileActivity::class.java)
                val extras = Bundle()
                extras.putString("BookTest", "BookTest")
                extras.putString("TestType", testType)
                extras.putInt("BookingType", 1)
                extras.putString("title", mHome[position].getCategoryName()!!)
                extras.putString("des", mHome[position].getCategoryDescription()!!)
                extras.putString("pic", mHome[position].getImagePath()!!)
                extras.putString("price", selectedPrice)
                extras.putString("iosImage", mHome[position].getIosImagePath()!!)
                extras.putString("testTypeId", mHome[position].getTestTypeId()!!)
                extras.putString("testNotes", selfTestNotes)
                extras.putString("extraInfo", extraInfo)
                extras.putString(
                    "testCategoryId",
                    mHome[position].getTestCategoryId()!!
                )
                extras.putString("tax", mTax)
                extras.putString(
                    "shippingCharge",
                    mShippingCharge
                )
                intent.putExtras(extras)
                startActivity(intent)
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }
}