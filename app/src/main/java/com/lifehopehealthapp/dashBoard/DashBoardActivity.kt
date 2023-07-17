package com.lifehopehealthapp.dashBoard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextClock
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agrawalsuneet.dotsloader.loaders.AllianceLoader
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.Appointments.IntroActivity
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.DownloadImageViewModel
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.ResponseModel.SettingsResponse
import com.lifehopehealthapp.WebViewActivity
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.bookingList.TestBookingActivity
import com.lifehopehealthapp.covidstatus.CovidStatusActivity
import com.lifehopehealthapp.dashboard.DashBoardModel
import com.lifehopehealthapp.dashboard.DashBoardViewModel
import com.lifehopehealthapp.dashboard.Home
import com.lifehopehealthapp.editProfile.EditProfileActivity
import com.lifehopehealthapp.labResultList.LabResultListActivity
import com.lifehopehealthapp.myTestOrderList.MyTestOrderListActivity
import com.lifehopehealthapp.notificationList.NotificationListActivity
import com.lifehopehealthapp.productlist.ProductListActivity
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.retrofitService.RemoteDataSource
import com.lifehopehealthapp.splash.SplashActivity
import com.lifehopehealthapp.symptomSearch.DiagnosisList.DiagnosisListActivity
import com.lifehopehealthapp.utils.*
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.cityName
import com.lifehopehealthapp.utils.PreferenceHelper.clearValues
import com.lifehopehealthapp.utils.PreferenceHelper.contactSupport
import com.lifehopehealthapp.utils.PreferenceHelper.firstname
import com.lifehopehealthapp.utils.PreferenceHelper.lastname
import com.lifehopehealthapp.utils.PreferenceHelper.latlng
import com.lifehopehealthapp.utils.PreferenceHelper.localOTPcheck
import com.lifehopehealthapp.utils.PreferenceHelper.notificationCount
import com.lifehopehealthapp.utils.PreferenceHelper.privacypolicy
import com.lifehopehealthapp.utils.PreferenceHelper.profile
import com.lifehopehealthapp.utils.PreferenceHelper.profileOldImg
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketName
import com.lifehopehealthapp.utils.PreferenceHelper.s3BucketRegion
import com.lifehopehealthapp.utils.PreferenceHelper.saveMobileNo
import com.lifehopehealthapp.utils.PreferenceHelper.saveSettingsAPI
import com.lifehopehealthapp.utils.PreferenceHelper.stateName
import com.lifehopehealthapp.utils.PreferenceHelper.terms
import com.lifehopehealthapp.utils.PreferenceHelper.termsandcondition
import com.lifehopehealthapp.utils.PreferenceHelper.userID
import com.lifehopehealthapp.utils.PreferenceHelper.w3AccessKey
import com.lifehopehealthapp.utils.PreferenceHelper.w3SecretKey
import com.lifehopehealthapp.vaccineReport.VaccineReportActivity
import com.lifehopehealthapp.vitals.VitalsCategoryList.VitalsCategoryListActivity
import com.lifehopehealthapp.widgets.TransitionHelper
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class DashBoardActivity : BaseActivity<DashBoardViewModel, DashBoardModel>(),
    NavigationView.OnNavigationItemSelectedListener,
    LocationListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    private var awsRegion: String? = ""
    private var mobile: String? = ""
    private var firstname: String? = ""
    private var lastname: String? = ""
    private var profilePicture: String? = ""
    private var token: String? = ""
    private var privacyUrl: String? = ""
    private var Terms: String? = ""
    private var contactSupport: String? = ""
    private var apiResponse: String? = ""
    private var updateUrl: String? = ""
    private var updateMsg: String? = ""
    private var mCity: String? = ""
    private var countryCode: String? = ""
    private var AppId: String = ""
    private var updateTitle: String? = ""

    private var enabled: Boolean? = null
    private var forceUpdate: Boolean? = false

    var locationManager: LocationManager? = null
    var locationListener: android.location.LocationListener? = null
    private var service: LocationManager? = null

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageViewBell: AppCompatImageView
    private var mLocationRequest: LocationRequest? = null
    private var mGoogleApiClient: GoogleApiClient? = null

    private var REQUEST_LOCATION_CODE = 101

    private var currentWeather: TextView? = null
    private var loaderView: AllianceLoader? = null
    private var currentDate: TextView? = null
    private var weatherImage: TextView? = null
    private var currentTime: TextClock? = null
    private var sliderView: SliderView? = null
    private var navView: NavigationView? = null
    private var progressDoalog: Dialog? = null
    private var username: AppCompatTextView? = null
    private var usermobile: AppCompatTextView? = null
    private var userPicture: CircleImageView? = null
    private var appUpdateDialog: Dialog? = null
    private var mAlertDialog: Dialog? = null
    private var layoutHeader: LinearLayout? = null

    private var mPrefs: SharedPreferences? = null
    private var adapter: DashBoardMenusAdapter? = null
    private var mData: ArrayList<Home>? = null

    private var notificationCount: Int? = 0


    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("WrongViewCast", "SimpleDateFormat", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setupWindowAnimations(window, this)
        setContentView(R.layout.activity_dash_board)
        mPrefs = PreferenceHelper.defaultPreference(this)
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(object : OnCompleteListener<String?> {
                override fun onComplete(@NonNull task: Task<String?>) {
                    if (!task.isSuccessful()) {
                        return
                    }

                    // Get new FCM registration token
                    val token: String = task.result!!
                    Log.e("newToken", token)
                }
            })

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar?.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_menu)
        setSupportActionBar(toolbar)
        toolbar?.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)

        }
        val notification: ImageView = findViewById(R.id.imageViewBell)
        imageViewBell = findViewById(R.id.imageViewBell_no)
        recyclerView = findViewById(R.id.recyclerViewMenus)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        notification.setOnClickListener {
            intent = Intent(
                this@DashBoardActivity,
                NotificationListActivity::class.java
            )
            if (Utils.isLollipopHigher() && it != null) {
                val pairs: Array<Pair<View, String>> =
                    TransitionHelper.createSafeTransitionParticipants(
                        this@DashBoardActivity,
                        false,
                        Pair(it, getString(R.string.trans_tool_bar_title))
                    )
                val transitionActivityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@DashBoardActivity,
                        *pairs
                    )
                startActivity(intent, transitionActivityOptions.toBundle())
            } else {
                startActivity(intent)
            }
        }
        sliderView = findViewById(R.id.imageSlider)
        sliderView!!.setSliderAdapter(ImageSliderAdapter(this))
        sliderView!!.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
        sliderView!!.setIndicatorAnimation(IndicatorAnimationType.WORM)
        sliderView!!.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        sliderView!!.scrollTimeInSec = 3
        sliderView!!.startAutoCycle()

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val hView: View = navView!!.getHeaderView(0)
        val editUser =
            hView.findViewById<View>(R.id.edit) as AppCompatImageView

        username = hView.findViewById<View>(R.id.textview_header_name) as AppCompatTextView
        layoutHeader = hView.findViewById<View>(R.id.layout_header) as LinearLayout
        usermobile = hView.findViewById<View>(R.id.textView_header_mobile) as AppCompatTextView
        userPicture = hView.findViewById<View>(R.id.iv_profile) as CircleImageView
        //editImage = navView.findViewById<View>(R.id.edit) as ImageView
        currentWeather = findViewById<View>(R.id.textview_current_weather) as TextView
        weatherImage = findViewById<View>(R.id.textview_current_weather) as TextView
        loaderView = findViewById<View>(R.id.loaderView) as AllianceLoader
        currentDate = findViewById<View>(R.id.textview_Curret_Date) as TextView
        currentTime = findViewById<View>(R.id.clock_txt) as TextClock

        checkRunTimePermission()

        apiResponse = mPrefs!!.saveSettingsAPI/*runBlocking { viewModel.getAPIResponse() }*/
        Log.e("token", mPrefs!!.authToken.toString())
        Log.e("apiResponse", apiResponse.toString())
        val aGson = Gson()
        val aSettingConfig: SettingsResponse =
            aGson.fromJson(apiResponse!!, SettingsResponse::class.java)
        val obj = JSONObject(apiResponse.toString())
        Log.e("dash", apiResponse.toString())
        val objects = obj.getJSONObject("versionInfo")
        val mForceUpdate = objects.get("forceUpdate")
        forceUpdate = mForceUpdate.toString().toBoolean()
        updateMsg = objects.get("updateMsg").toString()
        updateTitle = objects.get("updateTitle").toString()
        updateUrl = objects.get("playStoreUrl").toString()
        AppId = obj.get("weatherApiKey").toString()
        countryCode = obj.get("countryCode").toString()
        awsRegion = obj.get("region").toString()
        val userAgree = obj.getString("userAgree")
        mPrefs!!.terms = userAgree
        mPrefs!!.s3BucketRegion = awsRegion
        mPrefs!!.localOTPcheck = ""
        val latestVersion: Double = objects.get("latestVersion") as Double
        //val latestVersion = mLatestVersion.toString().toInt()
        Log.e("AppId", AppId)
        if (latestVersion > Utils.getAppVersionCode()
        ) {
            notifyAlert(Constants.APP_UPDATE_ALERT)
        }

        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val currentdate = sdf.format(Date())
        currentDate!!.setText(currentdate)
        layoutHeader!!.setOnClickListener {
            intent = Intent(this, EditProfileActivity::class.java)
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
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        // token = runBlocking { viewModel.getKey() }
        token = mPrefs!!.authToken

        recyclerView.addOnItemTouchListener(
            RecyclerItemClickListenr(
                this,
                recyclerView,
                object : RecyclerItemClickListenr.OnItemClickListener {

                    override fun onItemClick(view: View, position: Int) {

                        if (mData!![position].id == 1) {
                            intent = Intent(
                                this@DashBoardActivity,
                                DiagnosisListActivity::class.java
                            )
                            intent.putExtra("TitleName", mData!![position].name)
                            if (Utils.isLollipopHigher() && view != null) {
                                val pairs: Array<Pair<View, String>> =
                                    TransitionHelper.createSafeTransitionParticipants(
                                        this@DashBoardActivity,
                                        false,
                                        Pair(view, getString(R.string.trans_tool_bar_title))
                                    )
                                val transitionActivityOptions =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@DashBoardActivity,
                                        *pairs
                                    )
                                startActivity(intent, transitionActivityOptions.toBundle())
                            } else {
                                startActivity(intent)
                            }

                           /* intent = Intent(
                                this@DashBoardActivity,
                                ProductListActivity::class.java
                            )
                            intent.putExtra("TitleName", mData!![position].name)
                            if (Utils.isLollipopHigher() && view != null) {
                                val pairs: Array<Pair<View, String>> =
                                    TransitionHelper.createSafeTransitionParticipants(
                                        this@DashBoardActivity,
                                        false,
                                        Pair(view, getString(R.string.trans_tool_bar_title))
                                    )
                                val transitionActivityOptions =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@DashBoardActivity,
                                        *pairs
                                    )
                                startActivity(intent, transitionActivityOptions.toBundle())
                            } else {
                                startActivity(intent)
                            }*/


                        } else if (mData!![position].id == 2) {
                            intent = Intent(
                                this@DashBoardActivity,
                                TestBookingActivity::class.java
                            )
                            intent.putExtra("title", mData!![position].name)

                            if (Utils.isLollipopHigher() && view != null) {
                                val pairs: Array<Pair<View, String>> =
                                    TransitionHelper.createSafeTransitionParticipants(
                                        this@DashBoardActivity,
                                        false,
                                        Pair(view, getString(R.string.trans_tool_bar_title))
                                    )
                                val transitionActivityOptions =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@DashBoardActivity,
                                        *pairs
                                    )
                                startActivity(intent, transitionActivityOptions.toBundle())
                            } else {
                                startActivity(intent)
                            }
                        } else if (mData!![position].id == 3) {
                            intent = Intent(
                                this@DashBoardActivity,
                                LabResultListActivity::class.java
                            )
                            if (Utils.isLollipopHigher() && view != null) {
                                val pairs: Array<Pair<View, String>> =
                                    TransitionHelper.createSafeTransitionParticipants(
                                        this@DashBoardActivity,
                                        false,
                                        Pair(view, getString(R.string.trans_tool_bar_title))
                                    )
                                val transitionActivityOptions =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@DashBoardActivity,
                                        *pairs
                                    )
                                startActivity(intent, transitionActivityOptions.toBundle())
                            } else {
                                startActivity(intent)
                            }
                        } else if (mData!![position].id == 4) {
                            intent = Intent(
                                this@DashBoardActivity,
                                VitalsCategoryListActivity::class.java
                            )
                            if (Utils.isLollipopHigher() && view != null) {
                                val pairs: Array<Pair<View, String>> =
                                    TransitionHelper.createSafeTransitionParticipants(
                                        this@DashBoardActivity,
                                        false,
                                        Pair(view, getString(R.string.trans_tool_bar_title))
                                    )
                                val transitionActivityOptions =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@DashBoardActivity,
                                        *pairs
                                    )
                                startActivity(intent, transitionActivityOptions.toBundle())
                            } else {
                                startActivity(intent)
                            }
                        } else if (mData!![position].id == 6) {
                            intent = Intent(
                                this@DashBoardActivity,
                                IntroActivity::class.java
                            )
                            if (Utils.isLollipopHigher() && view != null) {
                                val pairs: Array<Pair<View, String>> =
                                    TransitionHelper.createSafeTransitionParticipants(
                                        this@DashBoardActivity,
                                        false,
                                        Pair(view, getString(R.string.trans_tool_bar_title))
                                    )
                                val transitionActivityOptions =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@DashBoardActivity,
                                        *pairs
                                    )
                                startActivity(intent, transitionActivityOptions.toBundle())
                            } else {
                                startActivity(intent)
                            }
                        } else if (mData!![position].id == 7) {
                            intent = Intent(
                                this@DashBoardActivity,
                                VaccineReportActivity::class.java
                            )
                            if (Utils.isLollipopHigher() && view != null) {
                                val pairs: Array<Pair<View, String>> =
                                    TransitionHelper.createSafeTransitionParticipants(
                                        this@DashBoardActivity,
                                        false,
                                        Pair(view, getString(R.string.trans_tool_bar_title))
                                    )
                                val transitionActivityOptions =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@DashBoardActivity,
                                        *pairs
                                    )
                                startActivity(intent, transitionActivityOptions.toBundle())
                            } else {
                                startActivity(intent)
                            }
                        } else if (mData!![position].id == 8) {
                            intent = Intent(
                                this@DashBoardActivity,
                                CovidStatusActivity::class.java
                            )
                            if (Utils.isLollipopHigher() && view != null) {
                                val pairs: Array<Pair<View, String>> =
                                    TransitionHelper.createSafeTransitionParticipants(
                                        this@DashBoardActivity,
                                        false,
                                        Pair(view, getString(R.string.trans_tool_bar_title))
                                    )
                                val transitionActivityOptions =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@DashBoardActivity,
                                        *pairs
                                    )

                                intent.putExtra("toolBarTitle", mData!![position].name)
                                startActivity(intent, transitionActivityOptions.toBundle())
                            } else {
                                intent.putExtra("toolBarTitle", mData!![position].name)
                                startActivity(intent)
                            }
                        }
                    }
                })
        )
//        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /* appBarConfiguration = AppBarConfiguration(
             setOf(
                 R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
             ), drawerLayout
         )*/
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
        navView!!.setNavigationItemSelectedListener(this)
        navView!!.setItemIconTintList(null)


        service = this.getSystemService(LOCATION_SERVICE) as LocationManager
        enabled = service!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

        /*mobile = runBlocking { viewModel.getMobileNo() }
        firstname = runBlocking { viewModel.getUserFirstName() }
        lastname = runBlocking { viewModel.getUserLastName() }
        profilePicture = runBlocking { viewModel.getUserProfilePicture() }*/
        profilePicture = mPrefs!!.profile

        /*Glide.with(this).load(profilePicture)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.ic_profile_no_image)
            .error(R.drawable.ic_profile_no_image)
            .into(userPicture!!)*/
        locationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : android.location.LocationListener {
            override fun onLocationChanged(location: Location) {
                mPrefs!!.latlng = location.latitude.toString() + " , " + location.longitude
                /*lifecycleScope.launch {
                    viewModel.saveLatLng(token = location.latitude.toString() + " , " + location.longitude)
                }*/
                Log.i("LOCATION", location.toString())
                Log.e(
                    "LOCATION",
                    location.latitude.toString() + " , " + location.longitude
                )

                val geocoder = Geocoder(this@DashBoardActivity, Locale.ENGLISH)
                try {
                    val addresses =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (addresses.size > 0) {
                        val fetchedAddress = addresses[0]
                        val strAddress = StringBuilder()
                        mCity = addresses[0].getLocality()
                        Log.e("mCity", mCity.toString())
                        val state = addresses[0].adminArea
                        if (state.equals("Tamil Nadu")) {

                        } else {
                            mPrefs!!.cityName = mCity
                            mPrefs!!.stateName = state
                        }
                        Log.e("stateName", state)
                        for (i in 0 until fetchedAddress.maxAddressLineIndex) {
                            strAddress.append(fetchedAddress.getAddressLine(i)).append(" ")
                        }
                    } else {
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
/*
                lifecycleScope.launch {
                    viewModel.getCityName(token = mCity!!)
                }
*/

                // Toast.makeText(getApplicationContext(), location.toString(), Toast.LENGTH_SHORT).show();
                getWeatherData(location.latitude, location.longitude)
                //getWeatherData(37.926868, -78.024902)
                Log.e("AppId", AppId + "API")
            }

            override fun onStatusChanged(
                provider: String,
                status: Int,
                extras: Bundle
            ) {
            }

            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                10f,
                locationListener as android.location.LocationListener
            )
        }
    }

    override fun onResume() {
        super.onResume()
        mobile = mPrefs!!.saveMobileNo
        firstname = mPrefs!!.firstname
        lastname = mPrefs!!.lastname

        username!!.setText(firstname + " " + lastname)
        usermobile!!.setText(countryCode + " " + Utils.USFormatMobile(mobile!!.toLong()))

        if (mPrefs!!.profile!!.contains("ProfilePic")) {
            val separated: List<String> = mPrefs!!.profile!!.split("/")
            Log.e(
                "onResume",
                separated[5]
            )
            Utils.Presigned(
                separated[5],
                mPrefs!!.s3BucketName,
                mPrefs!!.userID,
                this,
                Utils.decryption(mPrefs!!.w3SecretKey),
                Utils.decryption(mPrefs!!.w3AccessKey),
                mPrefs!!.s3BucketRegion,
                ""
            )
        } else {
            Utils.Presigned(
                mPrefs!!.profile.toString(),
                mPrefs!!.s3BucketName,
                mPrefs!!.userID,
                this,
                Utils.decryption(mPrefs!!.w3SecretKey),
                Utils.decryption(mPrefs!!.w3AccessKey),
                mPrefs!!.s3BucketRegion,
                ""
            )
        }


        if (Utils.isNetworkAvailable(this)) {
            /*val extras = intent.extras
            if (extras != null) {
                val authToken = extras!!.getString(Constants.NEW_TOKEN)
                val refreshToken = extras.getString(Constants.REFRESH_TOKEN)
                mPrefs!!.authToken = "Bearer " + authToken
                mPrefs!!.refreshToken = refreshToken
            }*/
            getCategoriesList()
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }
    }

    private fun getCategoriesList() {
        if (progressDoalog == null) {
            progressDoalog = Utils.getDialog(this)
        }
        progressDoalog!!.show()
        viewModel.getHomeCategories(mPrefs!!.authToken!!)

        viewModel.Response.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    if (progressDoalog != null && progressDoalog!!.isShowing) {
                        progressDoalog!!.dismiss()
                    }
                    if (it.value.getStatusCode() == 200) {
                        mData =
                            it.value.getValue()!!.data!!.getHomeList() as ArrayList<Home>?

                        adapter = DashBoardMenusAdapter(this, mData)
                        recyclerView.adapter = adapter
                        adapter!!.notifyDataSetChanged()
                        notificationCount = it.value.getValue()!!.data!!.getBatchCount()!!
                        val savedCount = mPrefs!!.notificationCount
                        Log.e(
                            "notificationCount",
                            savedCount.toString() + "  -->" + it.value.getValue()!!.data!!.getBatchCount()!!
                        )
                        if (notificationCount != 0) {
                            /*mPrefs!!.notificationCount =
                                it.value.getValue()!!.data!!.getBatchCount()!!*/
                            imageViewBell.isVisible = true
                        } else {
                            imageViewBell.isVisible = false
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

    private fun checkRunTimePermission() {
        val permissionArrays = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, REQUEST_LOCATION_CODE)
        } else {
            // if already permition granted
            // PUT YOUR ACTION (Like Open cemara etc..)
        }
    }

    fun KtoF(k: Double): Double? {
        return k * 9.0 / 5 - 459.67
    }

    @SuppressLint("SetTextI18n")
    private fun getWeatherData(latitude: Double, longitude: Double) {
        if (Utils.isNetworkAvailable(this)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(RemoteDataSource.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(APIManager::class.java)
            val call = service.getCurrentWeatherData(
                latitude,
                longitude, AppId
            )
            call!!.enqueue(object : Callback<WeatherResponse?> {
                override fun onResponse(
                    @NonNull call: Call<WeatherResponse?>,
                    @NonNull response: Response<WeatherResponse?>
                ) {
                    if (response.code() == 200) {
                        val weatherResponse = response.body()!!
                        KtoF(weatherResponse.main!!.temp.toDouble())
                        val formatter: NumberFormat =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                DecimalFormat("##.#")
                            } else {
                                TODO("VERSION.SDK_INT < N")
                            }
                        val doubleVal: Double =
                            KtoF(weatherResponse.main!!.temp.toDouble())!!.toDouble()
                        loaderView!!.isVisible = false
                        currentWeather!!.isVisible = true
                        currentWeather!!.setText(formatter.format(doubleVal) + "°F")
                        Log.e("CurrentWeather", weatherResponse.main!!.temp.toString())
                        for (k in weatherResponse.weather.indices) {
                            var data = weatherResponse.weather[k].description
                            Log.e("data", data.toString())
                            if (data.equals("mist") || data.equals("Smoke") || data.equals("Haze") || data.equals(
                                    "fog"
                                )
                            ) {
                                weatherImage!!.setCompoundDrawablesWithIntrinsicBounds(
                                    0, R.drawable.ic_windy_cloud, 0, 0
                                )
                            } else if (data.equals("sand")) {
                                weatherImage!!.setCompoundDrawablesWithIntrinsicBounds(
                                    0, R.drawable.ic_sun, 0, 0
                                )
                            } else if (data.equals("dust")) {
                                weatherImage!!.setCompoundDrawablesWithIntrinsicBounds(
                                    0, R.drawable.ic_sun, 0, 0
                                )
                            } else if (data.equals("tornado")) {
                                weatherImage!!.setCompoundDrawablesWithIntrinsicBounds(
                                    0, R.drawable.ic_sun, 0, 0
                                )
                            } else {
                                weatherImage!!.setCompoundDrawablesWithIntrinsicBounds(
                                    0, R.drawable.ic_sun, 0, 0
                                )
                            }
                        }
                    }
                }

                override fun onFailure(
                    @NonNull call: Call<WeatherResponse?>,
                    @NonNull t: Throwable
                ) {
                    Log.e("error", "error weather")
                }
            })
        } else {
            Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
        }

    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {

            R.id.nav_my_test_orders -> {
                navView?.let {
                    intent = Intent(this, MyTestOrderListActivity::class.java)
                    if (Utils.isLollipopHigher() && it != null) {
                        val pairs: Array<Pair<View, String>> =
                            TransitionHelper.createSafeTransitionParticipants(
                                this@DashBoardActivity,
                                false,
                                Pair(it, getString(R.string.trans_tool_bar_title))
                            )
                        val transitionActivityOptions =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                this@DashBoardActivity,
                                *pairs
                            )
                        startActivity(intent, transitionActivityOptions.toBundle())
                    } else {
                        startActivity(intent)
                    }


                }
            }
            R.id.nav_contact_support -> {
                contactSupport = mPrefs!!.contactSupport
                //contactSupport = runBlocking { viewModel.getContactSupport() }
                // var userId = runBlocking { viewModel.getUserId() }
                var userId = mPrefs!!.userID
                Log.e("userID", userId.toString())
                navView?.let {
                    contactSupport?.let { it1 ->
                        val intent = Intent(this, WebViewActivity::class.java)
                        val aBundle = Bundle()
                        aBundle.putString(
                            Constants.PAGE_TITLE,
                            resources.getString(R.string.contact_support)
                        )
                        aBundle.putString(Constants.PAGE_URL, it1 + "?id=" + userId)
                        intent.putExtras(aBundle)
                        startActivity(intent)
                    }
                }
            }
            R.id.nav_term_cons -> {

                Terms = mPrefs!!.termsandcondition
                //Terms = runBlocking { viewModel.getTandC() }
                navView?.let {
                    Terms?.let { it1 ->
                        val intent = Intent(this, WebViewActivity::class.java)
                        val aBundle = Bundle()
                        aBundle.putString(
                            Constants.PAGE_TITLE,
                            resources.getString(R.string.terms_conditions)
                        )
                        aBundle.putString(Constants.PAGE_URL, it1)
                        intent.putExtras(aBundle)
                        startActivity(intent)
                    }
                }
            }
            R.id.nav_privacy_policy -> {

                //privacyUrl = runBlocking { viewModel.getPrivicy() }
                privacyUrl = mPrefs!!.privacypolicy

                navView?.let {
                    privacyUrl?.let { it1 ->
                        val intent = Intent(this, WebViewActivity::class.java)
                        val aBundle = Bundle()
                        aBundle.putString(
                            Constants.PAGE_TITLE,
                            resources.getString(R.string.privacy_policy)
                        )
                        aBundle.putString(Constants.PAGE_URL, it1)
                        intent.putExtras(aBundle)
                        startActivity(intent)
                    }
                }
            }
            R.id.nav_logout -> {
                logoutPopup()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logoutPopup() {
        val alertDialog = Dialog(this)
        val lp = WindowManager.LayoutParams()
        val window = alertDialog.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.alert_dialog_view)
        val layout: TextView = alertDialog.findViewById(R.id.textview_dialog_heading)
        layout.setText(resources.getString(R.string.logout_content))

        val posTv: AppCompatButton = alertDialog.findViewById(R.id.button_proceed)

        posTv.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
            val logout = LogOut(this)
            logout.calLogOut(token)
            //runBlocking { viewModel.logout() }
            mPrefs!!.clearValues
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

    var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (doubleBackToExitPressedOnce) {
                val intent =
                    Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
                super.onBackPressed()
                return
            }
            doubleBackToExitPressedOnce = true
            Snackbar.make(
                this.window.decorView.findViewById(android.R.id.content),
                resources.getString(R.string.Snackbar_content),
                Snackbar.LENGTH_SHORT
            ).show()
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    override fun onLocationChanged(location: Location?) {
        getWeatherData(location!!.latitude, location.longitude)
        //  getWeatherData(37.926868, -78.024902)
        mPrefs!!.latlng = location.latitude.toString() + " , " + location.longitude
        Log.e("AppId", AppId + "API2")


    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient()
                        }
                    }
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton(
                            "OK",
                            DialogInterface.OnClickListener { dialog, which ->
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts(
                                        "package",
                                        getPackageName(),
                                        null
                                    )
                                )
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            })
                        .create()
                        .show()
                }
                return
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000000
        mLocationRequest!!.fastestInterval = 1000000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        // Check if enabled and if not send user to the GPS settings
        if (!enabled!!) {
            buildAlertMessageNoGps();
        }
        // Check if permission is granted or not
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
            )
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder =
            AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, Want to enable it to proceed.")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
        /*.setNegativeButton(
            "No"
        ) { dialog, id -> dialog.cancel() }*/
        val alert = builder.create()
        alert.show()
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.e("onConnectionSuspended", p0.toString())
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e("onConnectionFailed", p0.toString())
    }

    private fun f2c(f: Float): Float {
        return (f - 32) * 5 / 9
    }

    /**
     * shown notify Alert popup
     *
     * @param alertType based on type view are updated
     */
    fun notifyAlert(alertType: Int) {
        val view1 = View.inflate(
            this,
            R.layout.alert_update,
            null
        )
        if (appUpdateDialog == null) appUpdateDialog =
            Dialog(this, R.style.dialogwinddow)
        appUpdateDialog!!.setContentView(view1)
        appUpdateDialog!!.setCancelable(false)
        if (appUpdateDialog!!.isShowing()) {
            appUpdateDialog!!.dismiss()
            appUpdateDialog!!.show()
        } else {
            appUpdateDialog!!.show()
        }
        val title: AppCompatTextView = appUpdateDialog!!.findViewById(R.id.title)
        val desc: AppCompatTextView = appUpdateDialog!!.findViewById(R.id.description)
        val button_success: AppCompatTextView =
            appUpdateDialog!!.findViewById(R.id.button_success)
        val button_failure: AppCompatTextView =
            appUpdateDialog!!.findViewById(R.id.button_failure)
        if (alertType == Constants.APP_UPDATE_ALERT) {
            title.setText(updateMsg)
            desc.setText(updateTitle)
            desc.setVisibility(View.VISIBLE)
            button_failure.setText(this.getResources().getString(R.string.no_thanks))
            button_failure.setVisibility(if (forceUpdate!!) View.GONE else View.VISIBLE)
            button_success.setText(this.getResources().getString(R.string.update))
            button_success.setOnClickListener({ view ->
                if (Utils.isNetworkAvailable(this)) {
                    val viewIntent = Intent(
                        android.content.Intent.ACTION_VIEW,
                        Uri.parse(updateUrl)
                    )
                    startActivity(viewIntent)
                    appUpdateDialog!!.dismiss()
                } else {
                    mAlertDialog = Utils.alert_view_dialogContxt(
                        this,
                        "" + getResources().getString(R.string.message),
                        "" + getResources().getString(R.string.dia_newtwork_des),
                        "" + getResources().getString(R.string.ok),
                        "",
                        true,
                        null,
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() },
                        ""
                    )
                }
            })
            button_failure.setOnClickListener({ v -> appUpdateDialog!!.dismiss() })
        } else {
            button_success.setOnClickListener({ view ->
                if (Utils.isNetworkAvailable(this)) {
                    appUpdateDialog!!.dismiss()
                } else {
                    mAlertDialog = Utils.alert_view_dialogContxt(
                        this,
                        "" + getResources().getString(R.string.message),
                        "" + getResources().getString(R.string.dia_newtwork_des),
                        "" + getResources().getString(R.string.ok),
                        "",
                        true,
                        null,
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() },
                        ""
                    )
                }
            })
            button_failure.setOnClickListener({ v -> appUpdateDialog!!.dismiss() })
        }
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TransitionHelper.removeActivityFromTransitionManager(this)
        EventBus.getDefault().unregister(this)
    }

    override fun getViewModel() = DashBoardViewModel::class.java


    override fun getActivityRepository() = DashBoardModel(
        remoteDataSource.buildApi(APIManager::class.java), PreferenceHelper
    )

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: DownloadImageViewModel) {
        Log.e("imageData1", mPrefs!!.profile.toString() + "       " + event.mUrl)
        val separated: List<String> = event.mUrl!!.split("/")
        Log.e(
            "separated",
            separated[5]
        )
        val result = separated[5]
        val namesLists = result.split(".jpg")
        Log.e("finial", separated[5] + "     " + namesLists[0] + ".jpg")
        mPrefs!!.profileOldImg = event.mUrl
        Glide.with(this).load(event.mUrl)
            //.diskCacheStrategy(DiskCacheStrategy.NONE)
            //.skipMemoryCache(true)
            .placeholder(R.drawable.ic_profile_no_image)
            .error(R.drawable.ic_profile_no_image)
            .into(userPicture!!)
    }
}