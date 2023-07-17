package com.lifehopehealthapp.vitals.VitalsCategoryList

import android.annotation.SuppressLint
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.ResponseModel.VitalBandData
import com.lifehopehealthapp.ResponseModel.VitalsCategoryListResponse
import com.lifehopehealthapp.ResponseModel.VitalsInputModel
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.blueTooth.SerialListener
import com.lifehopehealthapp.blueTooth.SerialService
import com.lifehopehealthapp.blueTooth.SerialServices
import com.lifehopehealthapp.databinding.ActivityVitalsCategoryListBinding
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.*
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.vitals.BloodGlucose.BloodGlucoseActivity
import com.lifehopehealthapp.vitals.BloodPressureDetailPage.BloodPressureDetailActivity
import com.lifehopehealthapp.vitals.ECGDetailPage.ECGDetailActivity
import com.lifehopehealthapp.vitals.OxygenLevelDetailPage.OxygenLevelDetailActivity
import com.lifehopehealthapp.vitals.TemperatureDetailPage.TemperatureDetailActivity
import com.lifehopehealthapp.widgets.TransitionHelper
import org.greenrobot.eventbus.EventBus
import java.util.*

@Suppress("INACCESSIBLE_TYPE")
class VitalsCategoryListActivity :
    BaseActivity<VitalsCategoryListViewModel, VitalsCategoryListModel>,
    ServiceConnection, SerialListener {

    private enum class Connected {
        False, Pending, True
    }

    private var deviceAddress: String? = null
    private var services: SerialService? = null

    private var hexWatcher: TextUtil.HexWatcher? = null

    private var connected = Connected.False
    private var initialStart = true
    private val hexEnabled = false
    private var pendingNewline = false
    private val newline = "\r\n"

    private var data: List<VitalsCategoryListResponse.VitalMenu>? = null
    private var progressDoalog: Dialog? = null
    private var mPrefs: SharedPreferences? = null
    private var adapter: VitalsCategoryListAdapter? = null
    private var token: String? = ""
    private var tempBoolen: Boolean = false
    private var bloodValue: Boolean = false
    private var bloodValueSYS: Boolean = false
    private var weightValue: Boolean = false
    private var spoBoolen: Boolean = false

    private var visitCount: Int? = 0
    private var bpmBooleam: Boolean = false
    private var mTextValue: String? = null
    private var mLastChar = '\u0000' // init with empty character
    private var slashValidation: Boolean = true
    private var mKeyDel = 0
    private var imageURL: String? = ""
    private var textDescription: String? = ""
    private var textHeading: String? = ""
    private var bandValue: String? = ""
    private var mVitalID: Int? = 0

    var bluetoothAdapter =
        BluetoothAdapter.getDefaultAdapter()
    private val REQUEST_CONNECT_DEVICE = 1


    private enum class ScanState {
        NONE, LE_SCAN, DISCOVERY, DISCOVERY_FINISHED
    }

    private var scanState = ScanState.NONE
    private val LE_SCAN_PERIOD: Long = 10000

    private val leScanStopHandler = Handler()
    private var leScanCallback: BluetoothAdapter.LeScanCallback? = null
    private var discoveryBroadcastReceiver: BroadcastReceiver? = null
    private var discoveryIntentFilter: IntentFilter? = null
    private val listItems =
        ArrayList<BluetoothDevice>()


    private lateinit var binding: ActivityVitalsCategoryListBinding

    override fun getViewModel() = VitalsCategoryListViewModel::class.java

    override fun getActivityRepository() =
        VitalsCategoryListModel(
            remoteDataSource.buildApi(APIManager::class.java), preferences = PreferenceHelper
        )

    constructor() {

        leScanCallback =
            BluetoothAdapter.LeScanCallback { device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray? ->
                if (device != null) {
                    runOnUiThread(Runnable { updateScan(device) })
                }
            }
        discoveryBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                if (BluetoothDevice.ACTION_FOUND == intent.action) {
                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (device!!.type != BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                        runOnUiThread(Runnable { updateScan(device) })
                    }
                }
                if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == intent.action) {
                    scanState = ScanState.DISCOVERY_FINISHED // don't cancel again
                    stopScan()
                }
            }
        }
        discoveryIntentFilter = IntentFilter()
        discoveryIntentFilter!!.addAction(BluetoothDevice.ACTION_FOUND)
        discoveryIntentFilter!!.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
    }

    private fun stopScan() {
        if (scanState == ScanState.NONE) return
        when (scanState) {
            ScanState.LE_SCAN -> {
                leScanStopHandler.removeCallbacks { stopScan() }
                bluetoothAdapter.stopLeScan(leScanCallback)
            }
            ScanState.DISCOVERY -> bluetoothAdapter.cancelDiscovery()
            else -> {
            }
        }
        scanState = ScanState.NONE
    }


    @SuppressLint("NewApi")
    private fun updateScan(device: BluetoothDevice) {
        if (scanState == ScanState.NONE) return
        if (!listItems.contains(device)) {
            listItems.add(device)
            Collections.sort(
                listItems
            ) { a: BluetoothDevice?, b: BluetoothDevice? ->
                compareTo(
                    a!!,
                    b!!
                )
            }
            val device = listItems
            for (i in listItems.indices) {
                if (device.get(i).address == "D5:84:D0:C6:6F:24") {
                    deviceAddress = device.get(i).address
                    connect()
                } else {
                    Utils.showToast(this, "Try Again", true)
                }
                Log.e(
                    "listItems value-->",
                    device.get(i).address + "   alias->" + device.get(i).alias + "   bondState->" + device.get(
                        i
                    ).bondState + "   name->" + device.get(
                        i
                    ).name + "    type->" + device.get(i).type + "   uuids->" + device.get(i).uuids
                )
            }

//            listAdapter!!.notifyDataSetChanged()
        }
    }

    fun compareTo(
        a: BluetoothDevice,
        b: BluetoothDevice
    ): Int {
        val aValid = a.name != null && !a.name.isEmpty()
        val bValid = b.name != null && !b.name.isEmpty()
        if (aValid && bValid) {
            val ret = a.name.compareTo(b.name)
            return if (ret != 0) ret else a.address.compareTo(b.address)
        }
        if (aValid) return -1
        return if (bValid) +1 else a.address.compareTo(b.address)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVitalsCategoryListBinding.inflate(layoutInflater)
        Utils.setupWindowAnimations(window, this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mPrefs = PreferenceHelper.defaultPreference(this)
        token = mPrefs!!.authToken
        binding.recyclerviewVitalList.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        //getAPICall()
        hexWatcher = TextUtil.HexWatcher(binding.sendtxt)
        hexWatcher!!.enable(hexEnabled)
        binding.sendtxt.addTextChangedListener(hexWatcher)
        binding.imageViewBackArrow.setOnClickListener {
            onBackPressed()
        }

        binding.buttonValueAdd.setOnClickListener {
            //VitalSelectionPopup()
            //VitalsPopup()
        }
        getItemClick()

        Log.e("listItems", listItems.size.toString())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onPause() {
        super.onPause()
        stopScan()
        unregisterReceiver(discoveryBroadcastReceiver)
    }

    fun ImageView.loadSvg(url: String?) {
        GlideToVectorYou
            .init()
            .with(this.context)
            .setPlaceHolder(R.drawable.ic_no_image, R.drawable.ic_no_image)
            .load(Uri.parse(url), this)
    }

    private fun VitalSelectionPopup() {

        val alertDialog = Dialog(this)
        val lp = WindowManager.LayoutParams()
        val window = alertDialog.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.dialog_select_vitals_options)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        val manualButton: AppCompatButton = alertDialog.findViewById(R.id.buttonManualFeed)
        val fitBandButton: AppCompatButton = alertDialog.findViewById(R.id.buttonPairFitBand)
        val closeImageButton: AppCompatImageButton = alertDialog.findViewById(R.id.imageButtonClose)
        val imageViewVitals: AppCompatImageView =
            alertDialog.findViewById(R.id.imageViewVitalImage)
        val textViewVitalsHeading: AppCompatTextView =
            alertDialog.findViewById(R.id.textViewHeading)
        val textViewFitBandText: AppCompatTextView =
            alertDialog.findViewById(R.id.textViewFitBandText)

        textViewVitalsHeading.text = textHeading
        textViewFitBandText.text = textDescription
        Glide.with(this).load(imageURL)
            /*.diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)*/
            .placeholder(R.drawable.ic_no_image)
            .error(R.drawable.ic_no_image)
            .into(imageViewVitals)
        //imageViewVitals.loadSvg(imageURL)
        closeImageButton.setOnClickListener {
            alertDialog.dismiss()
        }
        manualButton.setOnClickListener {
            alertDialog.dismiss()
            //VitalsPopup()
        }
        fitBandButton.setOnClickListener {
            if (!bluetoothAdapter.isEnabled()) {
                val enableBtIntent =
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_CONNECT_DEVICE)
            } else {
                scanState = ScanState.LE_SCAN
                if (scanState == ScanState.LE_SCAN) {
                    leScanStopHandler.postDelayed(
                        { stopScan() },
                        LE_SCAN_PERIOD
                    )
                    object :
                        AsyncTask<Void?, Void?, Void?>() {

                        override fun doInBackground(vararg params: Void?): Void? {
                            bluetoothAdapter.startLeScan(null, leScanCallback)
                            return null
                        }
                    }.execute()
                } else {
                    bluetoothAdapter.startDiscovery()
                }
            }
        }
        alertDialog.show()
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

    override fun onResume() {
        super.onResume()
        val extras = intent.extras
        if (extras != null) {
            deviceAddress = extras.getString("device")
        }
        registerReceiver(discoveryBroadcastReceiver, discoveryIntentFilter);
        bindService(
            Intent(this@VitalsCategoryListActivity, SerialService::class.java),
            this,
            Context.BIND_AUTO_CREATE
        )
        send("")
        getAPICall()
        if (initialStart && services != null) {
            initialStart = false
            runOnUiThread(Runnable { connect() })
        }
    }

    private fun showKeyboard(temperature: TextInputEditText) {
        val imm =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(temperature, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun VitalsPopup() {
        var usertemperatureData: String? = ""
        var userbreathData: String? = ""
        var userWeight: String? = ""
        var userBloodPressure: String? = ""
        var userbpm: String? = ""
        var apiCalling: Boolean = false

        val alertDialog = Dialog(this)
        val lp = WindowManager.LayoutParams()
        val window = alertDialog.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(R.layout.dialog_add_vitals_data)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        val temperature: TextInputEditText = alertDialog.findViewById(R.id.edittext_temperature)
        val breathData: TextInputEditText = alertDialog.findViewById(R.id.edittext_breathData)
        val bloodpressureData: TextInputEditText =
            alertDialog.findViewById(R.id.edittextBPM)
        val weightData: TextInputEditText = alertDialog.findViewById(R.id.editTextWeight)
        val bpmData: TextInputEditText = alertDialog.findViewById(R.id.edittextBP)
        val imageviewClose: AppCompatImageButton = alertDialog.findViewById(R.id.imageviewClose)


        temperature.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                temperature.hint = resources.getString(R.string._94_f_to_115_7_f)
                showKeyboard(temperature)
            } else {
                temperature.hint = ""
            }
        }

        bpmData.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                //bpmData.hint = resources.getString(R.string._80_to_100)
                showKeyboard(bpmData)
            } else {
                bpmData.hint = ""
            }
        }

        breathData.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                breathData.hint = resources.getString(R.string._90_to_100)
                showKeyboard(breathData)
            } else {
                breathData.hint = ""
            }
        }

        weightData.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                weightData.hint = resources.getString(R.string._66_to_999)
                showKeyboard(weightData)
            } else {
                weightData.hint = ""
            }
        }

        bloodpressureData.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                //bloodpressureData.hint = resources.getString(R.string.sys_dia)
                showKeyboard(bloodpressureData)
            } else {
                bloodpressureData.hint = ""
            }
        }

        imageviewClose.setOnClickListener {
            alertDialog.dismiss()
        }
        val posTv: AppCompatButton = alertDialog.findViewById(R.id.buttonDone)
        posTv.text = resources.getString(R.string.text_done)

        temperature.addTextChangedListener(object : TextWatcher {
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
                val str: String = temperature.text.toString()
                if (str.isEmpty()) return
                if (str.contentEquals("0")) {
                    temperature.setText("")
                } else {
                    val str2: String? = PerfectDecimal(str, 3, 1)
                    if (str2 != str) {
                        temperature.setText(str2)
                        temperature.setSelection(str2!!.length)
                    }
                    val TempData: Double = str2.toDouble()
                    if (str.length == 2) {
                        if (TempData >= 94) {
                            tempBoolen = true
                        } else {
                            tempBoolen = false
                            temperature.setError(resources.getString(R.string.toast_enter_valid_temp))
                        }
                    } else if (str.length == 5) {
                        if (TempData <= 115.7) {
                            tempBoolen = true
                        } else {
                            tempBoolen = false
                            temperature.setError(resources.getString(R.string.toast_enter_valid_temp))
                        }
                    } else if (str.length == 3) {
                        if (TempData <= 115.7) {
                            tempBoolen = true
                        } else {
                            tempBoolen = false
                            temperature.setError(resources.getString(R.string.toast_enter_valid_temp))
                        }
                    } else if (str.length == 4) {
                        if (TempData <= 94.0) {
                            tempBoolen = false
                            temperature.setError(resources.getString(R.string.toast_enter_valid_temp))
                        } else {
                            tempBoolen = true
                        }
                    }
                }
            }
        })

        bloodpressureData.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                var str: String = bloodpressureData.text.toString()
                /*if (str.length == 1 && str.startsWith("0")) {
                    s!!.clear()
                }*/
                if (str.contains("/")) {
                    val parts: List<String> = str.split("/")
                    val part = parts[0]
                    val part1 = parts[1]
                    Log.e("part1", part + " " + part1)
                    if (!part.equals("")) {
                        val bloodpressure1: Int = part.toInt()
                        if (bloodpressure1 >= 100) {
                            bloodValueSYS = true
                            if (bloodpressure1 <= 180) {
                                bloodValueSYS = true
                            } else {
                                bloodpressureData.setError(resources.getString(R.string.toast_enter_valid_bp))
                                bloodValueSYS = false
                            }

                        } else {
                            bloodpressureData.setError(resources.getString(R.string.toast_enter_valid_bp))
                            bloodValueSYS = false
                        }
                        if (part.toInt() == 0) {
                            bloodpressureData.setError(resources.getString(R.string.toast_enter_valid_bp))
                            bloodValueSYS = false
                        }
                    }

                    if (!part1.equals("")) {
                        val bloodpressure1: Int = part1.toInt()
                        if (bloodpressure1 >= 60) {
                            bloodValue = true
                            if (bloodpressure1 <= 110) {
                                bloodValue = true
                            } else {
                                bloodpressureData.setError(resources.getString(R.string.toast_enter_valid_bp))
                                bloodValue = false
                            }
                            if (part1.equals("000")) {
                                bloodpressureData.setError(resources.getString(R.string.toast_enter_valid_bp))
                                bloodValue = false
                            }
                        } else {
                            bloodpressureData.setError(resources.getString(R.string.toast_enter_valid_bp))
                            bloodValue = false
                        }
                    }


                    if (part.length <= 3) {

                    } else {
                        bloodValueSYS = false
                        bloodpressureData.setError(resources.getString(R.string.toast_enter_valid_bp))
                    }


                } else {
                    str = str.replace("/", "")
                    if (str.equals("")) {

                    } else {
                        val bloodpressure: Int = str.toInt()
                        if (bloodpressure >= 100) {
                            bloodValue = true
                        } else {
                            bloodValue = false
                            bloodpressureData.setError(resources.getString(R.string.toast_enter_valid_bp))
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s!!.length > 0) { // save the last char value
                    mLastChar = s[s.length - 1]
                } else {
                    mLastChar = '\u0000'
                }
            }

            private val charIndex: Int? = 0
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var flag = true
                val eachBlock: List<String> =
                    bloodpressureData.getText().toString().split("/")
                for (i in eachBlock.indices) {
                    if (eachBlock[i].length > 4) {
                        flag = false
                    }
                }
                if (flag) {
                    bloodpressureData.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                        if (keyCode == KeyEvent.KEYCODE_DEL) mKeyDel = 1
                        false
                    })
                    if (mKeyDel == 0) {
                        /* if ((bloodpressureData.getText()!!.length + 1) % 4 == 0) {

                         }*/
                        if (s!!.length == 3) {
                            bloodpressureData.setText(bloodpressureData.getText().toString() + "/")
                            bloodpressureData.setSelection(bloodpressureData.getText()!!.length)
                            bloodValue = false
                        }
                        mTextValue = bloodpressureData.getText().toString()
                    } else {
                        mTextValue = bloodpressureData.getText().toString()
                        if (mLastChar.equals('/')) {
                            mTextValue = mTextValue!!.substring(0, mTextValue!!.length - 1)
                            bloodpressureData.setText(mTextValue)
                            bloodpressureData.setSelection(mTextValue!!.length)
                        }
                        mKeyDel = 0
                    }
                } else {
                    bloodpressureData.setText(mTextValue)
                }
            }
        })

        breathData.addTextChangedListener(object : TextWatcher {
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
                val str: String = breathData.text.toString()
                if (str.isEmpty()) return

                if (str.length == 1 && str.startsWith("0")) {
                    s.clear()
                }
                if (str.length == 1) {
                    spoBoolen = false
                    breathData.setError(resources.getString(R.string.toast_enter_valid_spo))
                }
                if (str.length == 2) {
                    val BreathData: Int = str.toInt()
                    if (BreathData >= 90) {
                        spoBoolen = true
                    } else {
                        spoBoolen = false
                        breathData.setError(resources.getString(R.string.toast_enter_valid_spo))
                    }
                }
                if (str.length == 3) {
                    val BreathData: Int = str.toInt()
                    if (BreathData <= 100) {
                        spoBoolen = true
                    } else {
                        spoBoolen = false
                        breathData.setError(resources.getString(R.string.toast_enter_valid_spo))
                    }
                }
                if (str.length >= 4) {
                    spoBoolen = false
                    breathData.setError(resources.getString(R.string.toast_enter_valid_spo))
                }
            }
        })

        weightData.addTextChangedListener(object : TextWatcher {

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
                val str: String = weightData.text.toString()
                if (str.isEmpty()) return
                if (str.contentEquals("0")) {
                    weightData.setText("")
                } else {
                    val str2: String? = PerfectDecimal(str, 3, 2)

                    if (str2 != str) {
                        weightData.setText(str2)
                        weightData.setSelection(str2!!.length)
                    }
                    val weightdata: Double = str2.toDouble()
                    if (str.length == 2) {
                        if (weightdata >= 66) {
                            weightValue = true
                        } else {
                            weightValue = false
                            weightData.setError(resources.getString(R.string.toast_enter_valid_weight))
                        }
                    } else if (str.length == 3) {
                        weightValue = true
                    } else if (str.length == 4) {
                        if (weightdata <= 66.0) {
                            weightValue = false
                            weightData.setError(resources.getString(R.string.toast_enter_valid_weight))
                        } else {
                            weightValue = true
                        }
                    } else if (str.length <= 5 || str.length == 6) {
                        if (weightdata <= 999) {
                            weightValue = true
                        } else {
                            weightValue = false
                            weightData.setError(resources.getString(R.string.toast_enter_valid_weight))
                        }
                    } else {
                        weightValue = false
                        weightData.setError(resources.getString(R.string.toast_enter_valid_weight))
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        bpmData.addTextChangedListener(object : TextWatcher {
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
                val str: String = bpmData.text.toString()
                if (str.isEmpty()) return

                if (str.length == 1 && str.startsWith("0")) {
                    s.clear()
                }
                if (str.length >= 4) {
                    bpmBooleam = false
                    bpmData.setError(resources.getString(R.string.toast_enter_valid_bpm))
                }
                if (str.length == 2) {
                    val BreathData: Int = str.toInt()
                    if (BreathData >= 60) {
                        bpmBooleam = true
                        //breathData.setText(str)
                        //breathData.setSelection(str.length)
                    } else {
                        bpmBooleam = false
                        bpmData.setError(resources.getString(R.string.toast_enter_valid_bpm))
                        /*Utils.showToast(
                            this@VitalsCategoryListActivity,
                            resources.getString(R.string.toast_enter_valid_spo),
                            true
                        )*/
                    }
                } else if (str.length == 3) {
                    val BreathData: Int = str.toInt()
                    if (BreathData <= 170) {
                        bpmBooleam = true
                        //breathData.setText(str)
                        //breathData.setSelection(str.length)
                    } else {
                        bpmBooleam = false
                        bpmData.setError(resources.getString(R.string.toast_enter_valid_bpm))
                        /*Utils.showToast(
                            this@VitalsCategoryListActivity,
                            resources.getString(R.string.toast_enter_valid_spo),
                            true
                        )*/
                    }
                } else {
                    bpmBooleam = false
                    bpmData.setError(resources.getString(R.string.toast_enter_valid_bpm))
                }
            }
        })

        posTv.setOnClickListener(View.OnClickListener {
            usertemperatureData = temperature.text.toString().trim()
            userbreathData = breathData.text.toString().trim()
            userWeight = weightData.text.toString().trim()
            userBloodPressure = bloodpressureData.text.toString().trim()
            userbpm = bpmData.text.toString().trim()

            //var selectItems: ArrayList<VitalsInput>? = ArrayList()
            if ((!userbreathData.equals("")) || (!usertemperatureData.equals("")) || (!userBloodPressure.equals(
                    ""
                )) || (!userWeight.equals("")) || (!userbpm.equals(""))
            ) {

                var brathdata: String? = ""
                var tempData: String? = ""
                var bloodData: String? = ""
                var weightdata: String? = ""
                var bpmData: String? = ""
                if (!userbreathData.equals("")) {
                    if (spoBoolen) {
                        apiCalling = true
                    } else {
                        brathdata = "Invalid"
                        apiCalling = false
                    }
                }
                if (!userbpm.equals("")) {
                    if (bpmBooleam) {
                        apiCalling = true
                    } else {
                        bpmData = "Invalid"
                        apiCalling = false
                    }
                }
                if (!usertemperatureData.equals("")) {
                    if (tempBoolen) {
                        apiCalling = true
                    } else {
                        tempData = "Invalid"
                        apiCalling = false
                    }
                }
                if (!userBloodPressure.equals("")) {
                    if (bloodValue && bloodValueSYS) {
                        apiCalling = true
                    } else {
                        bloodData = "Invalid"
                        apiCalling = false
                    }
                }
                if (!userWeight.equals("")) {
                    if (weightValue) {
                        apiCalling = true
                    } else {
                        weightdata = "Invalid"
                        apiCalling = false
                    }

                }

                if (weightdata.equals("Invalid") || bloodData.equals("Invalid") || tempData.equals("Invalid") || brathdata.equals(
                        "Invalid"
                    ) || bpmData.equals("Invalid")
                ) {
                    Utils.showToast(
                        this,
                        resources.getString(R.string.toast_enter_valid_data),
                        true
                    )
                } else {
                    val tsLong = System.currentTimeMillis() / 1000
                    //selectItems!!.clear()
                    val data = VitalsInputModel()

                    /*if (!userbpm.equals("")) {
                        val bpm = VitalsInput()
                        bpm.setDate(tsLong.toInt())
                        bpm.setValue(userbpm)
                        bpm.setVitalId(2)
                        bpm.setType(0)
                        selectItems.add(bpm)
                        data.setData(selectItems)
                    }

                    if (!usertemperatureData.equals("")) {
                        val temperatures = VitalsInput()
                        temperatures.setDate(tsLong.toInt())
                        temperatures.setValue(usertemperatureData)
                        temperatures.setVitalId(3)
                        temperatures.setType(0)
                        selectItems.add(temperatures)
                        data.setData(selectItems)
                    }

                    if (!userbreathData.equals("")) {
                        val breathRate = VitalsInput()
                        breathRate.setDate(tsLong.toInt())
                        breathRate.setValue(userbreathData)
                        breathRate.setVitalId(4)
                        breathRate.setType(0)
                        selectItems.add(breathRate)
                        data.setData(selectItems)
                    }

                    if (!userBloodPressure.equals("")) {
                        val bloodPressure = VitalsInput()
                        bloodPressure.setDate(tsLong.toInt())
                        bloodPressure.setValue(userBloodPressure)
                        bloodPressure.setVitalId(5)
                        bloodPressure.setType(0)
                        selectItems.add(bloodPressure)
                        data.setData(selectItems)
                    }

                    if (!userWeight.equals("")) {
                        val weight = VitalsInput()
                        weight.setDate(tsLong.toInt())
                        weight.setValue(userWeight)
                        weight.setVitalId(6)
                        weight.setType(0)
                        selectItems.add(weight)
                        data.setData(selectItems)
                    }
*/

                    val gson = Gson()
                    val json: String = gson.toJson(data)
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
                        val aJsonObject = aJsonParser.parse(apiRequest) as JsonObject
                        Log.e("aJsonObject", aJsonObject.toString())
                        viewModel.saveVitals(token!!, aJsonObject)

                        viewModel.Response1.observe(this, Observer {
                            when (it) {
                                is Resource.Success -> {
                                    if (progressDoalog != null && progressDoalog!!.isShowing) {
                                        progressDoalog!!.dismiss()
                                    }
                                    apiCalling = false
                                    getAPICall()
                                    alertDialog.dismiss()
                                }
                                is Resource.GenericError -> {
                                    if (progressDoalog != null && progressDoalog!!.isShowing) {
                                        progressDoalog!!.dismiss()
                                    }
                                    alertDialog.dismiss()
                                }
                            }
                        })
                    } else {
                        Utils.noInternetAlert(resources.getString(R.string.no_internet_msg), this)
                    }
                }
            } else {
                Utils.showToast(this, resources.getString(R.string.toast_empty_data), true)
            }
        })
        alertDialog.show()
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

    fun PerfectDecimal(
        str: String,
        MAX_BEFORE_POINT: Int,
        MAX_DECIMAL: Int
    ): String? {
        var str = str
        if (str[0] == '.') str = "0$str"
        val max = str.length
        var rFinal = ""
        var after = false
        var i = 0
        var up = 0
        var decimal = 0
        var t: Char
        while (i < max) {
            t = str[i]
            if (t != '.' && after == false) {
                up++
                if (up > MAX_BEFORE_POINT) return rFinal
            } else if (t == '.') {
                after = true
            } else {
                decimal++
                if (decimal > MAX_DECIMAL) return rFinal
            }
            rFinal = rFinal + t
            i++
        }
        return rFinal
    }

    private fun getAPICall() {
        if (Utils.isNetworkAvailable(this)) {
            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            viewModel.getVitalsList(token!!)

            viewModel.Response.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            data = (it.value.getValue()!!.data!!
                                .getVitalMenu() as List<VitalsCategoryListResponse.VitalMenu>?)
                            imageURL = it.value.getValue()!!.data!!.getIAppleImagePath()
                            Log.e("imageURL", imageURL.toString())
                            textHeading = it.value.getValue()!!.data!!.getHeader()
                            textDescription = it.value.getValue()!!.data!!.getDescription()
                            if (visitCount == 0) {
                                //VitalSelectionPopup()
                                visitCount = visitCount!! + 1
                            }
                            Log.e("data", data!!.size.toString())
                            if (data!!.size == 0) {
                                binding.recyclerviewVitalList.isVisible = false
                                binding.textviewNoData.isVisible = true
                                binding.textviewNoData.text =
                                    resources.getString(R.string.text_no_vitals_data)
                            } else {
                                adapter =
                                    VitalsCategoryListAdapter(it.value.getValue()!!.data!!.getVitalMenu())
                                binding.recyclerviewVitalList.adapter = adapter
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

    private fun getItemClick() {
        binding.recyclerviewVitalList.addOnItemTouchListener(
            RecyclerItemClickListenr(
                this,
                binding.recyclerviewVitalList,
                object : RecyclerItemClickListenr.OnItemClickListener {

                    override fun onItemClick(view: View, position: Int) {
                        mVitalID = data!!.get(position).id
                        if (mVitalID == 2 || mVitalID == 3 || mVitalID == 6 || mVitalID == 1) {
                            if (mVitalID == 2) {
                                send("hrt")
                            } else if (mVitalID == 3) {
                                send("tem")
                            } else if (mVitalID == 1) {
                                send("ec#")
                            }
                            Log.e("testId", mVitalID.toString())

                            if (mVitalID == 1) {
                                Handler().postDelayed({

                                }, 3000)
                                send("")
                                intent = Intent(
                                    this@VitalsCategoryListActivity,
                                    ECGDetailActivity::class.java
                                )
                                intent.putExtra(Constants.VITALS_ID, mVitalID.toString())
                                intent.putExtra(
                                    Constants.VITALS_NAME,
                                    data!!.get(position).shortName
                                )
                                intent.putExtra(
                                    Constants.VITALS_DATE,
                                    data!!.get(position).lastRecordDate.toString()
                                )
                                intent.putExtra(
                                    Constants.VITALS_LAST_DATA,
                                    data!!.get(position).lastData
                                )
                                intent.putExtra(Constants.VITALS_IMAGE, data!![position].imagePath)
                                if (Utils.isLollipopHigher() && view != null) {
                                    val pairs: Array<Pair<View, String>> =
                                        TransitionHelper.createSafeTransitionParticipants(
                                            this@VitalsCategoryListActivity,
                                            false,
                                            Pair(view, getString(R.string.trans_tool_bar_title))
                                        )
                                    val transitionActivityOptions =
                                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                            this@VitalsCategoryListActivity,
                                            *pairs
                                        )
                                    startActivity(intent, transitionActivityOptions.toBundle())
                                } else {
                                    startActivity(intent)
                                }
                            } else {
                                intent = Intent(
                                    this@VitalsCategoryListActivity,
                                    TemperatureDetailActivity::class.java
                                )
                                intent.putExtra(Constants.VITALS_ID, mVitalID.toString())
                                intent.putExtra(
                                    Constants.VITALS_NAME,
                                    data!!.get(position).shortName
                                )
                                intent.putExtra(
                                    Constants.VITALS_LAST_DATA,
                                    data!!.get(position).lastData
                                )
                                intent.putExtra(
                                    Constants.VITALS_DATE,
                                    data!!.get(position).lastRecordDate.toString()
                                )
                                intent.putExtra(Constants.VITALS_IMAGE, data!![position].imagePath)
                                if (Utils.isLollipopHigher() && view != null) {
                                    val pairs: Array<Pair<View, String>> =
                                        TransitionHelper.createSafeTransitionParticipants(
                                            this@VitalsCategoryListActivity,
                                            false,
                                            Pair(view, getString(R.string.trans_tool_bar_title))
                                        )
                                    val transitionActivityOptions =
                                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                            this@VitalsCategoryListActivity,
                                            *pairs
                                        )
                                    startActivity(intent, transitionActivityOptions.toBundle())
                                } else {
                                    startActivity(intent)
                                }
                                send("")
                            }
                        } else if (mVitalID == 4) {
                            //send("spo")
                            intent = Intent(
                                this@VitalsCategoryListActivity,
                                OxygenLevelDetailActivity::class.java
                            )
                            intent.putExtra(Constants.VITALS_ID, mVitalID.toString())
                            intent.putExtra(Constants.VITALS_NAME, data!!.get(position).shortName)
                            intent.putExtra(
                                Constants.VITALS_DATE,
                                data!!.get(position).lastRecordDate.toString()
                            )
                            intent.putExtra(
                                Constants.VITALS_LAST_DATA,
                                data!!.get(position).lastData
                            )
                            intent.putExtra(Constants.VITALS_IMAGE, data!![position].imagePath)
                            if (Utils.isLollipopHigher() && view != null) {
                                val pairs: Array<Pair<View, String>> =
                                    TransitionHelper.createSafeTransitionParticipants(
                                        this@VitalsCategoryListActivity,
                                        false,
                                        Pair(view, getString(R.string.trans_tool_bar_title))
                                    )
                                val transitionActivityOptions =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@VitalsCategoryListActivity,
                                        *pairs
                                    )
                                startActivity(intent, transitionActivityOptions.toBundle())
                            } else {
                                startActivity(intent)
                            }
                            send("")
                        } else if (mVitalID == 5) {
                            intent = Intent(
                                this@VitalsCategoryListActivity,
                                BloodPressureDetailActivity::class.java
                            )
                            intent.putExtra(Constants.VITALS_ID, mVitalID.toString())
                            intent.putExtra(Constants.VITALS_NAME, data!!.get(position).shortName)
                            intent.putExtra(
                                Constants.VITALS_DATE,
                                data!!.get(position).lastRecordDate.toString()
                            )
                            intent.putExtra(
                                Constants.VITALS_LAST_DATA,
                                data!!.get(position).lastData
                            )
                            intent.putExtra(Constants.VITALS_IMAGE, data!![position].imagePath)
                            if (Utils.isLollipopHigher() && view != null) {
                                val pairs: Array<Pair<View, String>> =
                                    TransitionHelper.createSafeTransitionParticipants(
                                        this@VitalsCategoryListActivity,
                                        false,
                                        Pair(view, getString(R.string.trans_tool_bar_title))
                                    )
                                val transitionActivityOptions =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@VitalsCategoryListActivity,
                                        *pairs
                                    )
                                startActivity(intent, transitionActivityOptions.toBundle())
                            } else {
                                startActivity(intent)
                            }
                        } else if (mVitalID == 7) {
                            intent = Intent(
                                this@VitalsCategoryListActivity,
                                BloodGlucoseActivity::class.java
                            )
                            intent.putExtra(Constants.VITALS_ID, mVitalID.toString())
                            intent.putExtra(Constants.VITALS_NAME, data!!.get(position).shortName)
                            intent.putExtra(
                                Constants.VITALS_DATE,
                                data!!.get(position).lastRecordDate.toString()
                            )
                            intent.putExtra(
                                Constants.VITALS_LAST_DATA,
                                data!!.get(position).lastData
                            )
                            intent.putExtra(Constants.VITALS_IMAGE, data!![position].imagePath)
                            if (Utils.isLollipopHigher() && view != null) {
                                val pairs: Array<Pair<View, String>> =
                                    TransitionHelper.createSafeTransitionParticipants(
                                        this@VitalsCategoryListActivity,
                                        false,
                                        Pair(view, getString(R.string.trans_tool_bar_title))
                                    )
                                val transitionActivityOptions =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@VitalsCategoryListActivity,
                                        *pairs
                                    )
                                startActivity(intent, transitionActivityOptions.toBundle())
                            } else {
                                startActivity(intent)
                            }
                        } else if (mVitalID == 1) {
                            send("ec#")
                            intent = Intent(
                                this@VitalsCategoryListActivity,
                                ECGDetailActivity::class.java
                            )
                            intent.putExtra(Constants.VITALS_ID, mVitalID.toString())
                            intent.putExtra(Constants.VITALS_NAME, data!!.get(position).shortName)
                            intent.putExtra(
                                Constants.VITALS_DATE,
                                data!!.get(position).lastRecordDate.toString()
                            )
                            intent.putExtra(
                                Constants.VITALS_LAST_DATA,
                                data!!.get(position).lastData
                            )
                            intent.putExtra(Constants.VITALS_IMAGE, data!![position].imagePath)
                            if (Utils.isLollipopHigher() && view != null) {
                                val pairs: Array<Pair<View, String>> =
                                    TransitionHelper.createSafeTransitionParticipants(
                                        this@VitalsCategoryListActivity,
                                        false,
                                        Pair(view, getString(R.string.trans_tool_bar_title))
                                    )
                                val transitionActivityOptions =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        this@VitalsCategoryListActivity,
                                        *pairs
                                    )
                                startActivity(intent, transitionActivityOptions.toBundle())
                            } else {
                                startActivity(intent)
                            }
                        }
                    }
                })
        )
    }

    private fun send(str: String) {
        if (connected != Connected.True) {
            return
        }
        try {
            val msg: String
            val data: ByteArray

            if (hexEnabled) {
                val sb = StringBuilder()
                TextUtil.toHexString(sb, TextUtil.fromHexString(str))
                TextUtil.toHexString(sb, newline.toByteArray())
                msg = sb.toString()
                data = TextUtil.fromHexString(msg)
            } else {
                msg = str
                data = (str + newline).toByteArray()
            }
            val spn =
                SpannableStringBuilder(
                    """
                        $msg
                        
                        """.trimIndent()
                )
            spn.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.purple_500)),
                0,
                spn.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            Log.e("spn", spn.toString())
            binding.recivetxt.append(spn)
            services!!.write(data)
        } catch (e: java.lang.Exception) {
            onSerialIoError(e)
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        services = null
    }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        services = (binder as SerialService.SerialBinder).getService()
        services!!.attach(this)
        if (initialStart) {
            initialStart = false
            runOnUiThread(Runnable { this.connect() })
        }
    }

    private fun connect() = try {
        val bluetoothAdapter =
            BluetoothAdapter.getDefaultAdapter()
        val device =
            bluetoothAdapter.getRemoteDevice(deviceAddress)
        status("connecting...")
        connected = Connected.Pending
        val socket = SerialSocket(getApplicationContext(), device)
        services!!.connect(socket)
    } catch (e: java.lang.Exception) {
        onSerialConnectError(e)
    }

    private fun status(str: String) {
        val spn =
            SpannableStringBuilder(
                """
                    $str
                    
                    """.trimIndent()
            )
        spn.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.purple_500)),
            0,
            spn.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.recivetxt.append(spn)
    }

    override fun onSerialConnect() {
        status("connected")
        connected = Connected.True
        Utils.showToast(this, "connected", true)
    }

    override fun onSerialConnectError(e: Exception?) {
        status("connection failed: " + e!!.message)
        disconnect()
    }

    override fun onSerialRead(data: ByteArray?) {
        if (data != null) {
            receive(data)
        }
    }


    private fun receive(data: ByteArray) {
        if (hexEnabled) {
            binding.recivetxt.append(
                """
                    ${TextUtil.toHexString(data)}
                    
                    """.trimIndent()
            )
        } else {
            var msg = String(data)
            if (newline == TextUtil.newline_crlf && msg.length > 0) {
                // don't show CR as ^M if directly before LF
                msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf)
                // special handling if CR and LF come in separate fragments
                if (pendingNewline && msg[0] == '\n') {
                    //val edt: Editable = receiveText.getEditableText()
                    //if (edt != null && edt.length > 1) edt.replace(edt.length - 2, edt.length, "")
                }
                pendingNewline = msg[msg.length - 1] == '\r'
            }
            //binding.recivetxt.append(TextUtil.toCaretString(msg, newline.length != 0))
            bandValue = TextUtil.toCaretString(msg, newline.length != 0).toString()
            val data = VitalBandData()
            data.mBandData = TextUtil.toCaretString(msg, newline.length != 0).toString()
            data.mVitalId = mVitalID
            EventBus.getDefault().post(data)
            Log.e("temp", TextUtil.toCaretString(msg, newline.length != 0).toString())
        }
    }

    override fun onSerialIoError(e: Exception?) {
        status("connection lost: " + e!!.message)
        disconnect()
    }

    override fun onDestroy() {
        if (connected != Connected.False) disconnect()
        stopService(Intent(this, SerialServices::class.java))
        try {
            unbindService(this)
        } catch (ignored: java.lang.Exception) {
        }
        super.onDestroy()
    }

    private fun disconnect() {
        connected = Connected.False
        services!!.disconnect()
    }

    override fun onStart() {
        super.onStart()
        if (services != null) services!!.attach(this) else startService(
            Intent(
                this,
                SerialServices::class.java
            )
        )

    }

    override fun onStop() {
        if (services != null && !isChangingConfigurations()) services!!.detach()
        super.onStop()
    }


}