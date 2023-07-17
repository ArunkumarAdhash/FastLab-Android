package com.lifehopehealthapp.webview

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lifehopehealthapp.R
import com.lifehopehealthapp.ResponseModel.DoctorPaymentRequest
import com.lifehopehealthapp.ResponseModel.RefreshAPIRequest
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.base.Resource
import com.lifehopehealthapp.bulkBooking.mailScreen.ResendMailActivity
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.utils.*
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.PreferenceHelper.refreshToken
import com.lifehopehealthapp.widgets.TransitionHelper
import org.json.JSONObject


class WebViewPaymentActivity : BaseActivity<WebViewViewModel, WebViewModel>() {

    private lateinit var uploadMessageAboveL: ValueCallback<Array<Uri?>?>
    private var mUM: ValueCallback<Uri>? = null
    private var mUMA: ValueCallback<Array<Uri>>? = null
    private val mCM: String? = null

    private val FILE_CHOOSER_RESULT_CODE: Int? = 111
    private var titleval: String = ""
    private var webView: WebView? = null
    private var title: String? = null
    private var mUrl: String? = null
    private var layTitle: TextView? = null
    private var backarrow: ImageView? = null
    private var orderID: String? = ""
    private var progressBar: ProgressBar? = null
    private var noImage: AppCompatImageView? = null
    private var mStatus: Int? = 0
    private var token: String? = ""
    private var progressDoalog: Dialog? = null
    private var framLay: ConstraintLayout? = null
    var mPrefs: SharedPreferences? = null

    var checkOutBool : Boolean = false

    companion object {
        fun startActivity(context: Activity, aBundle: Bundle?, view: View?) {
            val starter = Intent(context, WebViewPaymentActivity::class.java)
            starter.putExtras(aBundle!!)
            if (Utils.isLollipopHigher() && view != null) {
                val pairs: Array<Pair<View, String>> =
                    TransitionHelper.createSafeTransitionParticipants(
                        context,
                        false,
                        Pair(view, context.getString(R.string.trans_tool_bar_title))
                    )
                val transitionActivityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(context, *pairs)
                context.startActivity(starter, transitionActivityOptions.toBundle())
            } else {
                context.startActivity(starter)
            }
        }

        fun startActivity(context: Activity, aBundle: Bundle?) {
            val starter = Intent(context, WebViewPaymentActivity::class.java)
            starter.putExtras(aBundle!!)
            context.startActivity(starter)
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            Utils.setupWindowAnimations(window, this)
            setContentView(R.layout.activity_webview)
            noImage = findViewById(R.id.no_image)
            webView = findViewById(R.id.web_view)
            layTitle = findViewById(R.id.lay_title)
            progressBar = findViewById(R.id.loading)
            backarrow = findViewById(R.id.back_img)
            progressBar!!.setVisibility(View.VISIBLE)
            framLay = findViewById(R.id.framLay)

            mPrefs = PreferenceHelper.defaultPreference(this)
            token = mPrefs!!.authToken
            getIntentResult()

            if (!TextUtils.isEmpty(mUrl) && !TextUtils.isEmpty(title)) {

                if (title!!.equals("null")) {
                    framLay!!.isVisible = false
                } else if (title.equals("bulk")) {
                    framLay!!.isVisible = false
                } else if (title.equals("doctor")) {
                    framLay!!.isVisible = false
                } else {
                    layTitle!!.text = title
                }
                val webSettings: WebSettings = webView!!.getSettings()


                webView!!.webViewClient = LoadWebClient()
              // webView!!.webChromeClient = WebChromeClient()
                webView!!.getSettings().javaScriptEnabled = true
                webView!!.getSettings().setSupportMultipleWindows(true)
                webView!!.getSettings().javaScriptCanOpenWindowsAutomatically = true
                webView!!.getSettings().allowFileAccess = true
                webView!!.settings.setMixedContentMode(0);
                webView!!.settings.allowContentAccess = true
                webView!!.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                webView!!.loadUrl(mUrl!!)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        backarrow!!.setOnClickListener {
            finish()
        }

//        webView!!.setWebChromeClient(object : WebChromeClient() {
//            override fun onProgressChanged(view: WebView?, newProgress: Int) {
//                super.onProgressChanged(view, newProgress)
//            }
//
//            //For Android API >= 21 (5.0 OS)
//            override fun onShowFileChooser(
//                webView: WebView?,
//                filePathCallback: ValueCallback<Array<Uri>>?,
//                fileChooserParams: FileChooserParams?
//            ): Boolean {
//                /*uploadMessageAboveL = filePathCallback
//                openImageChooserActivity()
//                return true*/
//                if (mUMA != null) {
//                    mUMA!!.onReceiveValue(null)
//                }
//                mUMA = filePathCallback
//                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
//                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
//                contentSelectionIntent.type = "image/*"
//                val chooserIntent = Intent(Intent.ACTION_CHOOSER)
//                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
//                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
//                startActivityForResult(chooserIntent, 123)
//                //showFileChooser()
//                return true
//            }
//        })
    }

    private fun getIntentResult() {
        val aBundle = intent.extras
        if (aBundle != null) {
            title = aBundle.getString(Constants.PAGE_TITLE)
            mUrl = aBundle.getString(Constants.PAGE_URL)
            orderID = aBundle.getString(Constants.ORDER_ID)
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                  onBackPressed()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    inner class LoadWebClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith("mailto:")) {
                val body = "Body of message."
                val mail =
                    Intent(Intent.ACTION_SEND)
                mail.type = "application/octet-stream"
                val separated =
                    url.split("mailto:%20".toRegex()).toTypedArray()
                Log.e("Mail", separated[1])
                mail.putExtra(Intent.EXTRA_EMAIL, arrayOf(separated[1]))
                mail.putExtra(Intent.EXTRA_SUBJECT, "Subject")
                mail.putExtra(Intent.EXTRA_TEXT, body)
                startActivity(mail)
                //view.loadUrl(url)
            } else {
                view.loadUrl(url)
            }
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            progressBar!!.visibility = View.GONE

            if(url.contains("checkout"))
            {
                checkOutBool =true
            }else{
                checkOutBool=false
            }


            if (url.contains("Status=0")) {//User Cancel
                mStatus = 0
                if (title.equals("doctor")) {
                    DoctorPaymentCancel(mStatus!!, orderID, token, false)
                } else if (title.equals("null")) {
                    confirmOrderCancel(mStatus!!, orderID, token)
                } else {
                    bulkBookingConfirmOrderCancel(mStatus!!, orderID, title)
                   // finish()
                }
                // finish()
                //Utils.showToast(this@WebViewActivity, "Payment Cancel", true)
            } else if (url.contains("Status=1")) {//Success
                mStatus = 1
                if (title.equals("doctor")) {
                    DoctorPayment(mStatus!!, orderID, token, false)
                } else if (title.equals("null")) {
                    confirmOrder(mStatus!!, orderID, token)
                } else {
                    bulkBookingConfirmOrder(mStatus!!, orderID, title)
                }
            } else if (url.contains("Status=2")) { //Something went wrong
                mStatus = 2
                if (title.equals("doctor")) {
                    DoctorPayment(mStatus!!, orderID, token, false)
                } else if (title.equals("null")) {
                    confirmOrder(mStatus!!, orderID, token)
                } else {
                    bulkBookingConfirmOrder(mStatus!!, orderID, title)
                }
                //finish()
                //Utils.showToast(this@WebViewActivity, "Something went Wrong", true)
            } else if (url.contains("Status=3")) {//Failure
                mStatus = 3
                if (title.equals("doctor")) {
                    DoctorPayment(mStatus!!, orderID, token, false)
                } else if (title.equals("null")) {
                    confirmOrder(mStatus!!, orderID, token)
                } else {
                    bulkBookingConfirmOrder(mStatus!!, orderID, title)
                }
                //finish()
                //Utils.showToast(this@WebViewActivity, "Payment Failure", true)
            }
            else {
                Log.e("url", url)
            }
        }

        override fun onLoadResource(view: WebView, url: String) {
            super.onLoadResource(view, url)
        }

        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {
            // TODO Auto-generated method stub
            super.onReceivedError(view, errorCode, description, failingUrl)
            progressBar!!.visibility = View.GONE
//            noImage!!.visibility = View.VISIBLE
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            super.onReceivedSslError(view, handler, error)
            try {
                val builder = AlertDialog.Builder(this@WebViewPaymentActivity)
                var message = getString(R.string.ssl_certificate_error)
                when (error.primaryError) {
                    SslError.SSL_UNTRUSTED -> message = getString(R.string.certificate_not_trusted)
                    SslError.SSL_EXPIRED -> message = getString(R.string.certificate_expired)
                    SslError.SSL_IDMISMATCH -> message =
                        getString(R.string.certificate_host_mismatch)
                    SslError.SSL_NOTYETVALID -> message = getString(R.string.certificate_not_valid)
                }
                message += getString(R.string.do_you_want_continue)
                //builder.setTitle(R.string.certificate_error)
                builder.setMessage(message)
                builder.setPositiveButton(R.string.ssl_continue) { dialog, which -> handler.proceed() }
                builder.setNegativeButton(R.string.cancel) { dialog, which -> handler.cancel() }
                val dialog = builder.create()
                dialog.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun DoctorPayment(mStatus: Int, orderID: String?, token: String?, BackPress: Boolean) {
        if (Utils.isNetworkAvailable(this)) {

            val data = DoctorPaymentRequest()
            data.setAppointmentId(orderID)
            data.setPaymentStatus(mStatus)

            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            val token = mPrefs!!.authToken
            progressDoalog!!.show()
            viewModel.getPaymentConfirm(token!!, data)

            viewModel.Response.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (BackPress) {
                            finish()
                        } else {
                            if (it.value.getValue()!!.getStatus() == 0) {
                                intent = Intent(
                                    this@WebViewPaymentActivity,
                                    PaymentCompleteActivity::class.java
                                )
                                intent.putExtra(
                                    "Status",
                                    it.value.getValue()!!.description!!
                                )
                                startActivity(intent)
                                finish()

                            } else if (it.value.getValue()!!.getStatus() == 1) {
                                intent = Intent(
                                    this@WebViewPaymentActivity,
                                    PaymentCompleteActivity::class.java
                                )
                                intent.putExtra(
                                    "Status",
                                    it.value.getValue()!!.description!!
                                )
                                startActivity(intent)
                                finish()

                            } else if (it.value.getValue()!!.getStatus() == 2) {
                                intent = Intent(
                                    this@WebViewPaymentActivity,
                                    PaymentCompleteActivity::class.java
                                )
                                intent.putExtra(
                                    "Status",
                                    it.value.getValue()!!.description!!
                                )
                                startActivity(intent)
                                finish()

                            } else if (it.value.getValue()!!.getStatus() == 3) {
                                intent = Intent(
                                    this@WebViewPaymentActivity,
                                    PaymentCompleteActivity::class.java
                                )
                                intent.putExtra(
                                    "Status",
                                    it.value.getValue()!!.description!!
                                )
                                startActivity(intent)
                                finish()

                            } else if (it.value.getValue()!!.getStatus() == 4) {
                                intent = Intent(
                                    this@WebViewPaymentActivity,
                                    ResendMailActivity::class.java
                                )
                                intent.putExtra("orderID", orderID)
                                intent.putExtra("paymentStatus", mStatus)
                                intent.putExtra("bookingType", "1")
                                intent.putExtra("bulkbooking", "2")
                                intent.putExtra(
                                    "emailContent",
                                    it.value.getValue()!!.getDescription()!!
                                )
                                startActivity(intent)
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

    }


    private fun DoctorPaymentCancel(mStatus: Int, orderID: String?, token: String?, BackPress: Boolean) {
        if (Utils.isNetworkAvailable(this)) {

            val data = DoctorPaymentRequest()
            data.setAppointmentId(orderID)
            data.setPaymentStatus(mStatus)

            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            val token = mPrefs!!.authToken
            progressDoalog!!.show()
            viewModel.getPaymentConfirm(token!!, data)

            viewModel.Response.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (BackPress) {
                            finish()
                        } else {
                            if (it.value.getValue()!!.getStatus() == 0 || it.value.getValue()!!.getStatus() == 1 ||
                                it.value.getValue()!!.getStatus() == 2 || it.value.getValue()!!.getStatus() == 3
                                || it.value.getValue()!!.getStatus() == 4)
                                {
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

    }

    private fun bulkBookingConfirmOrder(
        mStatus: Int,
        orderID: String?,
        title: String?
    ) {
        if (Utils.isNetworkAvailable(this)) {

            val studentsObj = JSONObject()
            studentsObj.put("orderID", orderID)
            studentsObj.put("paymentStatus", mStatus)
            studentsObj.put("bookingType", mStatus)
            val jsonStr = studentsObj.toString()
            Log.e("req", jsonStr + "   " + token)

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(jsonStr) as JsonObject

            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            viewModel.getBulkOrderConfirm(token!!, aJsonObject)

            viewModel.Response2.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            if (it.value.getValue()!!.data!!.getStatus() == 0) {
                                intent = Intent(
                                    this@WebViewPaymentActivity,
                                    PaymentCompleteActivity::class.java
                                )
                                intent.putExtra(
                                    "Status",
                                    it.value.getValue()!!.data!!.description!!
                                )
                                startActivity(intent)
                                finish()

                            } else if (it.value.getValue()!!.data!!.getStatus() == 1) {
                                intent = Intent(
                                    this@WebViewPaymentActivity,
                                    PaymentCompleteActivity::class.java
                                )
                                intent.putExtra(
                                    "Status",
                                    it.value.getValue()!!.data!!.description!!
                                )
                                startActivity(intent)
                                finish()

                            } else if (it.value.getValue()!!.data!!.getStatus() == 2) {
                                intent = Intent(
                                    this@WebViewPaymentActivity,
                                    PaymentCompleteActivity::class.java
                                )
                                intent.putExtra(
                                    "Status",
                                    it.value.getValue()!!.data!!.description!!
                                )
                                startActivity(intent)
                                finish()

                            } else if (it.value.getValue()!!.data!!.getStatus() == 3) {
                                intent = Intent(
                                    this@WebViewPaymentActivity,
                                    PaymentCompleteActivity::class.java
                                )
                                intent.putExtra(
                                    "Status",
                                    it.value.getValue()!!.data!!.description!!
                                )
                                startActivity(intent)
                                finish()

                            } else if (it.value.getValue()!!.data!!.getStatus() == 4) {
                                intent = Intent(
                                    this@WebViewPaymentActivity,
                                    ResendMailActivity::class.java
                                )
                                intent.putExtra("orderID", orderID)
                                intent.putExtra("paymentStatus", mStatus)
                                intent.putExtra("bookingType", "1")
                                intent.putExtra("bulkbooking", "1")
                                intent.putExtra(
                                    "emailContent",
                                    it.value.getValue()!!.data!!.getEmailContent()!!
                                )
                                startActivity(intent)
                                finish()
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


    private fun bulkBookingConfirmOrderCancel(
        mStatus: Int,
        orderID: String?,
        title: String?
    ) {
        if (Utils.isNetworkAvailable(this)) {

            val studentsObj = JSONObject()
            studentsObj.put("orderID", orderID)
            studentsObj.put("paymentStatus", mStatus)
            studentsObj.put("bookingType", mStatus)
            val jsonStr = studentsObj.toString()
            Log.e("req", jsonStr + "   " + token)

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(jsonStr) as JsonObject

            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            viewModel.getBulkOrderConfirm(token!!, aJsonObject)

            viewModel.Response2.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if(it.value.getStatusCode() == 200) {

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

   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Build.VERSION.SDK_INT >= 21) {
            var results: Array<Uri>? = null
            //Check if response is positive
            if (resultCode === RESULT_OK) {
                if (requestCode === 123) {
                    if (null == mUMA) {
                        return
                    }
                    if (intent == null) {
                        //Capture Photo if no image available
                        if (mCM != null) {
                            results = arrayOf(Uri.parse(mCM))
                        }
                    } else {
                        val dataString = intent.dataString
                        if (dataString != null) {
                            results = arrayOf(Uri.parse(dataString))
                        }
                    }
                }
            }
            mUMA!!.onReceiveValue(results)
            mUMA = null
        } else {
            if (requestCode === 123) {
                if (null == mUM) return
                val result = if (intent == null || resultCode !== RESULT_OK) null else intent.data
                mUM!!.onReceiveValue(result)
                mUM = null
            }
        }
        *//*if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (uploadMessageAboveL != null) {
                uploadMessageAboveL?.onReceiveValue(
                    WebChromeClient.FileChooserParams.parseResult(
                        resultCode,
                        data
                    )
                )
            }
        }*//*

    }*/

    private fun confirmOrder(mStatus: Int, orderID: String?, token: String?) {
        if (Utils.isNetworkAvailable(this)) {

            val studentsObj = JSONObject()
            studentsObj.put("orderID", orderID)
            studentsObj.put("paymentStatus", mStatus)
            val jsonStr = studentsObj.toString()
            Log.e("req", jsonStr + "   " + token)

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(jsonStr) as JsonObject

            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            viewModel.getOrderConfirm(token!!, aJsonObject)

            viewModel.Response1.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
                            intent = Intent(
                                this@WebViewPaymentActivity,
                                PaymentCompleteActivity::class.java
                            )
                            intent.putExtra("Status", it.value.getValue()!!.data!!.description!!)
                            startActivity(intent)
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

    private fun confirmOrderCancel(mStatus: Int, orderID: String?, token: String?) {
        if (Utils.isNetworkAvailable(this)) {

            val studentsObj = JSONObject()
            studentsObj.put("orderID", orderID)
            studentsObj.put("paymentStatus", mStatus)
            val jsonStr = studentsObj.toString()
            Log.e("req", jsonStr + "   " + token)

            val aJsonParser = JsonParser()
            val aJsonObject = aJsonParser.parse(jsonStr) as JsonObject

            if (progressDoalog == null) {
                progressDoalog = Utils.getDialog(this)
            }
            progressDoalog!!.show()
            viewModel.getOrderConfirm(token!!, aJsonObject)

            viewModel.Response1.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        if (progressDoalog != null && progressDoalog!!.isShowing) {
                            progressDoalog!!.dismiss()
                        }
                        if (it.value.getStatusCode() == 200) {
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


    override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            if(checkOutBool)
            {
              //  finish()
                mStatus = 0
                if(title.equals("doctor")) {
                    DoctorPaymentCancel(mStatus!!, orderID, token, false)
                } else if (title.equals("null")) {
                    confirmOrderCancel(mStatus!!, orderID, token)
                } else {
                    bulkBookingConfirmOrderCancel(mStatus!!, orderID, title)

                }

            }
            else
            {
                webView!!.goBack()
            }

        } else {
            if (title.equals("doctor")) {
                DoctorPayment(0, orderID, title, true)
            } else {
                webView!!.clearHistory(); // clear history
                finish()
            }
            //super.onBackPressed()

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        TransitionHelper.removeActivityFromTransitionManager(this)
        finish()
    }

    override fun getViewModel() = WebViewViewModel::class.java

    override fun getActivityRepository() =
        WebViewModel(remoteDataSource.buildApi(APIManager::class.java), PreferenceHelper)
}