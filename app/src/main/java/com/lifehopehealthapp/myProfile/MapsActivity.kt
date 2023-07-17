package com.lifehopehealthapp.myProfile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.SettingsResponse
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.saveSettingsAPI
import org.greenrobot.eventbus.EventBus
import java.util.*

class MapsActivity : BaseActivity<MapViewViewModel, MapViewModel>(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {
    private var permissionPopup: Boolean = true
    private var locationPermission: Boolean = false
    private var service: LocationManager? = null
    private var enabled: Boolean? = null
    private var mLocationRequest: LocationRequest? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLastLocation: Location? = null
    private var mCurrLocationMarker: Marker? = null
    private lateinit var mMap: GoogleMap
    private var REQUEST_LOCATION_CODE = 101
    var autoLatLng: LatLng? = null
    var DynamicmidLatLng: LatLng? = null
    lateinit var geocoder: Geocoder
    private val DEFAULT_ZOOM = 15f
    lateinit var currentAddress: TextView
    lateinit var searchicon: ImageView
    var layoutsearchicon: ImageView? = null
    lateinit var layoutsearch: ConstraintLayout
    lateinit var layoutautosearch: ConstraintLayout
    var searchplace: AppCompatEditText? = null
    var changelocation: AppCompatTextView? = null
    var autosearchplace: ConstraintLayout? = null
    var backarrow: ImageView? = null
    var mapFragment: SupportMapFragment? = null
    private val AUTOCOMPLETE_REQUEST_CODE = 101
    var mapSearchDetails: Boolean? = false
    private var changeAddress: AppCompatButton? = null
    private var landmark: AppCompatEditText? = null
    private var apiResponse: String? = ""
    var mPrefs: SharedPreferences? = null
    private var mLastClickTime: Long = 0




    private fun checkRunTimePermission() {
        val permissionArrays = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, REQUEST_LOCATION_CODE)
        } else {
            // if already permition granted
            // PUT YOUR ACTION (Like Open cemara etc..)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        mPrefs = PreferenceHelper.defaultPreference(this)
        landmark = findViewById<View>(R.id.edittext_land_mark) as AppCompatEditText
        currentAddress = findViewById<View>(R.id.textview_current_address) as TextView
        searchicon = findViewById<View>(R.id.imageview_search_place) as ImageView
        layoutsearchicon = findViewById<View>(R.id.imageview_search) as ImageView
        layoutsearch = findViewById<View>(R.id.layout_search_icon) as ConstraintLayout
        layoutautosearch = findViewById<View>(R.id.layout_autosearch) as ConstraintLayout
        autosearchplace = findViewById<View>(R.id.layout_autosearchplace) as ConstraintLayout
        backarrow = findViewById<View>(R.id.imageview_back_arrow) as ImageView
        searchplace = findViewById<View>(R.id.edittext_search_place) as AppCompatEditText
        changelocation = findViewById<View>(R.id.textview_change_location) as AppCompatTextView
        changeAddress = findViewById<View>(R.id.button_address) as AppCompatButton

        backarrow?.setOnClickListener(this)
        layoutsearchicon?.setOnClickListener(this)
        searchplace?.setOnClickListener(this)
        searchicon.setOnClickListener(this)
        changelocation?.setOnClickListener(this)
        changeAddress?.setOnClickListener(this)


        checkRunTimePermission()
        var landMark: String = intent.getStringExtra("LandMark").toString()
        if (landMark.equals("null")) {
            landmark!!.setText("")
        } else {
            landmark!!.setText(landMark)
        }

        //apiResponse = runBlocking { viewModel.getAPIResponse() }
        apiResponse = mPrefs!!.saveSettingsAPI
        val aGson = Gson()
        val aSettingConfig: SettingsResponse =
            aGson.fromJson(apiResponse!!, SettingsResponse::class.java)

        val apiKey = getString(R.string.googleID)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        val placesClient: PlacesClient = Places.createClient(this)

        service = this.getSystemService(LOCATION_SERVICE) as LocationManager
        enabled = service!!.isProviderEnabled(LocationManager.GPS_PROVIDER)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment!!.getMapAsync(this)
    }

    fun moveAutoSearch() {
        val fields = Arrays.asList(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
        // Start the autocomplete intent.
        // Start the autocomplete intent.
        val intent: Intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields
        ).setCountry("US") //NIGERIA
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode === Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                layoutautosearch.visibility = View.VISIBLE
                searchplace?.setText(place.address)
                mapSearchDetails = true
                mMap.clear()
                mMap.addMarker(
                    MarkerOptions()
                        .position(place.latLng!!)
                )
                landmark!!.setText("")
                currentAddress.setText(place.address)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, DEFAULT_ZOOM))

                val address = place.address
                // do query with address
            } else if (resultCode === AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status =
                    Autocomplete.getStatusFromIntent(data!!)
                Toast.makeText(
                    this,
                    "Error: " + status.statusMessage,
                    Toast.LENGTH_LONG
                ).show()
            } else if (resultCode === Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    @SuppressLint("UseRequireInsteadOfGet")
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Location Permission already granted
                buildGoogleApiClient()
                mMap.isMyLocationEnabled = true
                val locationButton = (mapFragment!!.getView()!!.findViewById<View>("1".toInt())
                    .getParent() as View).findViewById<View>("2".toInt())
                val rlp =
                    locationButton.layoutParams as RelativeLayout.LayoutParams
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                rlp.setMargins(0, 0, 30, 30)
            } else {
                //Request Location Permission
                //checkLocationPermission()
            }
        } else {
            buildGoogleApiClient()
            mMap.isMyLocationEnabled = true
            val locationButton = (mapFragment!!.getView()!!.findViewById<View>("1".toInt())
                .getParent() as View).findViewById<View>("2".toInt())
            val rlp =
                locationButton.layoutParams as RelativeLayout.LayoutParams
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            rlp.setMargins(0, 0, 30, 30)
        }
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location?) {
        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }

        //Place current location marker
        val latLng = LatLng(location!!.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        //mCurrLocationMarker = mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))


        val addresses: List<Address>
        geocoder = Geocoder(this, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            location.latitude,
            location.longitude,
            1
        )
        if (addresses != null && addresses.size > 0) {
            val address = addresses[0].getAddressLine(0)
            currentAddress.setText(address)
        } else {
            getAddressGoogle(location.latitude, location.longitude)
        }

        mMap.setOnCameraChangeListener { cameraPosition ->
            mMap.clear()
            //mMap.addMarker(MarkerOptions().position(cameraPosition.target))
        }

        mMap.setOnCameraIdleListener {
            DynamicmidLatLng = mMap.cameraPosition.target
            val dynamicLatLngValue: String =
                DynamicmidLatLng!!.latitude.toString() + "," + DynamicmidLatLng!!.longitude

            val addresses1: List<Address>
            geocoder = Geocoder(this, Locale.getDefault())

            addresses1 = geocoder.getFromLocation(
                DynamicmidLatLng!!.latitude,
                DynamicmidLatLng!!.longitude,
                1
            )
            if (addresses1 != null && addresses1.size > 0) {
                val address = addresses1[0].getAddressLine(0)
                currentAddress.setText(address)
            } else {
                getAddressGoogle(DynamicmidLatLng!!.latitude, DynamicmidLatLng!!.longitude)
            }
            if (mapSearchDetails!!) {
                mapSearchDetails = false
            } else {
                searchplace?.setText("")
            }

        }
        /*lifecycleScope.launch {
            viewModel.saveLatLng(token = location!!.latitude.toString() + " , " + location.longitude)
        }*/
    }

    private fun getAddressGoogle(latitude: Double, longitude: Double) {
        val key = getString(R.string.googleID)
        val mLatLnge = latitude.toString() + "," + longitude
        viewModel.callGoogleAddress(mLatLnge, key)

        viewModel.Response.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    currentAddress.setText(it.value.getResults()!![0]!!.formattedAddress)
                }

                is Resource.GenericError -> {
                    /*SnackBarToast.snackBarError(
                        this, "Canont get Address!"
                    )*/
                }
            }
        })
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 100000
        mLocationRequest!!.fastestInterval = 100000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        // Check if enabled and if not send user to the GPS settings
        if (!enabled!!) {
            //newly hided
           /* val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)*/
            showGPSNotEnabledDialog(this)
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

    fun showGPSNotEnabledDialog(context: Context) {

            android.app.AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.enable_gps))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.enable_now)) { _, _ ->
                    resultLauncherForUserLocation.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton(context.getString(R.string.text_cancel)) { dialogInterface: DialogInterface, i: Int ->
                    finish()
                    dialogInterface.dismiss()

                }
                .show()

    }


    //newly added
    var resultLauncherForUserLocation =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (!isLocationEnabled(this)) {
                showGPSNotEnabledDialog(this)
            }



        }

    override fun onConnectionSuspended(p0: Int) {
        Toast.makeText(applicationContext, "connection suspended", Toast.LENGTH_SHORT).show()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(applicationContext, "connection Failed", Toast.LENGTH_SHORT).show()
    }





    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
                        mMap.isMyLocationEnabled = true
                    }
                } else {
                    checkLocationPermission()
                }
                return
            }
        }
    }

    private fun openSettings() {
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
        finish()
    }

    private fun showSettingsDialog() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
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

    private fun checkLocationPermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                @SuppressLint("MissingPermission")
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient()
                        }
                        mMap.isMyLocationEnabled = true
                        locationPermission = true
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        locationPermission = false
                        if (permissionPopup) {
                            permissionPopup = false
                            mapSearchDetails = true
                            showSettingsDialog()
                        }
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
                    locationPermission = false
                }
            })
            .onSameThread()
            .check()
    }

    private fun checkLocationPermissionNew() : Boolean{
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                @SuppressLint("MissingPermission")
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        locationPermission = true
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        locationPermission = false

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
                    locationPermission = false
                }
            })
            .onSameThread()
            .check()

        return  locationPermission
    }

    override fun onClick(p0: View?) {
        when (p0) {
            backarrow -> {
                finish()
            }
            searchplace -> {
                moveAutoSearch()
            }
            searchicon -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                moveAutoSearch()
            }
            layoutsearchicon -> {
                moveAutoSearch()
            }
            changelocation -> {
                moveAutoSearch()
            }
            changeAddress -> {
                var address = currentAddress.text.toString()
                var mData: String = landmark!!.text.toString()

                if (checkLocationPermissionNew()) {
                    if (!address.contentEquals("")) {
                        if (mData.trim().isEmpty()) {
                            EventBus.getDefault().postSticky(address + "." + mData)
                            finish()
                        } else {
                            EventBus.getDefault().postSticky(address + "." + "\nLandMark : " + mData)
                            finish()
                        }
                    } else {

                        Toast.makeText(applicationContext, "Select your location", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Allow your location permission", Toast.LENGTH_SHORT).show()
                }



            }
        }
    }

    override fun getViewModel() = MapViewViewModel::class.java

    override fun getActivityRepository() =
        MapViewModel(remoteDataSource.BuildGoogleAPI(APIManager::class.java), PreferenceHelper)


}