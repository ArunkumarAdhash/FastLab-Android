package com.lifehopehealthapp

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Display
import android.view.View
import android.webkit.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.lifehopehealthapp.utils.Constants
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken

class WebViewActivity : AppCompatActivity() {

    private lateinit var uploadMessageAboveL: ValueCallback<Array<Uri>>
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

            val display: Display = windowManager.defaultDisplay
            val width: Int = display.getWidth()



            webView!!.webViewClient = LoadWebClient()
            webView!!.getSettings().setBuiltInZoomControls(true);
            webView!!.getSettings().javaScriptEnabled = true
            webView!!.getSettings().setSupportMultipleWindows(true)
            webView!!.getSettings().javaScriptCanOpenWindowsAutomatically = true
            webView!!.getSettings().allowFileAccess = true
            webView!!.settings.setMixedContentMode(0);
            webView!!.settings.allowContentAccess = true
            webView!!.setLayerType(View.LAYER_TYPE_HARDWARE, null);

            //newly added 24_05-22 - document blur issue
            webView!!.settings.loadWithOverviewMode = true
            webView!!.settings.useWideViewPort = true

           /* if (title.equals("Vaccination Report")) {
               *//* val html =
                    "<html><body><img src=\"" + mUrl.toString() + "\" width=\"100%\" height=\"100%\"\"/></body></html>"
                webView!!.loadData(html, "text/html", null)*//*
                webView!!.loadUrl(mUrl!!)

            } else {
                webView!!.loadUrl(mUrl!!)
            }*/
            webView!!.loadUrl(mUrl!!)
        }

        backarrow!!.setOnClickListener {
            finish()
        }

        webView!!.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }

            //For Android API >= 21 (5.0 OS)
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                uploadMessageAboveL = filePathCallback!!
                openImageChooserActivity()
                return true
                return true
            }
        })
    }

    private fun openImageChooserActivity() {
        val i =
            Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "image/*"
        if (FILE_CHOOSER_RESULT_CODE != null) {
            startActivityForResult(
                Intent.createChooser(i, "Image Chooser"),
                FILE_CHOOSER_RESULT_CODE
            )
        }
    }

    private fun getIntentResult() {
        val aBundle = intent.extras
        if (aBundle != null) {
            title = aBundle.getString(Constants.PAGE_TITLE)
            mUrl = aBundle.getString(Constants.PAGE_URL)
            orderID = aBundle.getString(Constants.ORDER_ID)
        }
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
                val builder = AlertDialog.Builder(this@WebViewActivity)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (uploadMessageAboveL != null) {
                uploadMessageAboveL?.onReceiveValue(
                    WebChromeClient.FileChooserParams.parseResult(
                        resultCode,
                        data
                    )
                )
            }
        }

    }

    private fun getHtmlData(bodyHTML: String): String {
        val head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>"
        return "<html>$head<body>$bodyHTML</body></html>"
    }
}