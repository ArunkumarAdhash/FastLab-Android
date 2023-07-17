package com.lifehopehealthapp.PDFView

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lifehopehealthapp.R
import com.lifehopehealthapp.utils.Constants
import kotlinx.android.synthetic.main.activity_pdfview.*

class PDFWebView : AppCompatActivity() {
    var mURL: String? = ""
    val SAMPLE_FILE = "angela1wisdom.pdf"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdfview)

        val extras = intent.extras
        if (extras != null) {
            mURL = extras.getString(Constants.PAGE_URL)
        }
        val uri: Uri = Uri.parse(mURL)
        pdfView.fromUri(uri)
            .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
            .enableSwipe(true) // allows to block changing pages using swipe
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .defaultPage(0)
            .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
            .scrollHandle(null)
            .enableAntialiasing(true) // improve rendering a little bit on low-res screens
            .spacing(0)
            .invalidPageColor(Color.WHITE) // color of page that is invalid and cannot be loaded
            .load()
    }
}