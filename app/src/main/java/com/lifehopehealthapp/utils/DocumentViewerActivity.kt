package com.lifehopehealthapp.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.net.http.SslError
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.lifehopehealthapp.R
import com.lifehopehealthapp.utils.PreferenceHelper.authToken

class DocumentViewerActivity : AppCompatActivity() {

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

            webSettings.javaScriptEnabled = true
            webView!!.settings.builtInZoomControls = true
            webView!!.settings.displayZoomControls = false
            webView!!.settings.domStorageEnabled = true

            webView!!.webChromeClient = object : WebChromeClient() {

                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    if (newProgress == 100) {
                    }
                }
            }
            webView!!.settings.javaScriptEnabled = true
            webView!!.settings.javaScriptCanOpenWindowsAutomatically = true
            webView!!.settings.allowFileAccess = true

            webView!!.webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                    view.loadUrl(url!!)
                    return true
                }

                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)
//                Toast.makeText(this@WebViewActivity,"webview",Toast.LENGTH_SHORT).show()
                    if (view!!.title.equals(""))
                        view.reload()
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    webView!!.loadUrl("javascript:(function() {document.querySelector('[class=\"ndfHFb-c4YZDc-Wrql6b\"]').remove();})()")
                    progressBar!!.visibility = View.GONE
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    progressBar!!.visibility = View.GONE

                }

                override fun onReceivedSslError(
                    view: WebView,
                    handler: SslErrorHandler,
                    error: SslError
                ) {
                    super.onReceivedSslError(view, handler, error)
                    try {
                        val builder = AlertDialog.Builder(this@DocumentViewerActivity)
                        var message: String = getString(R.string.ssl_certificate_error)
                        when (error.primaryError) {
                            SslError.SSL_UNTRUSTED -> message =
                                getString(R.string.certificate_not_trusted)
                            SslError.SSL_EXPIRED -> message =
                                getString(R.string.certificate_expired)
                            SslError.SSL_IDMISMATCH -> message =
                                getString(R.string.certificate_host_mismatch)
                            SslError.SSL_NOTYETVALID -> message =
                                getString(R.string.certificate_not_valid)
                        }
                        message += getString(R.string.do_you_want_continue)
                        //builder.setTitle(R.string.certificate_error)
                        builder.setMessage(message)
                        builder.setPositiveButton(R.string.ssl_continue) { _, _ -> handler.proceed() }
                        builder.setNegativeButton(R.string.text_cancel) { _, _ -> handler.cancel() }
                        val dialog = builder.create()
                        dialog.show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }

            webView!!.loadUrl(
                "https://docs.google.com/gview?embedded=true&url=" + mUrl
            )

        }

        backarrow!!.setOnClickListener {
            finish()
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
}