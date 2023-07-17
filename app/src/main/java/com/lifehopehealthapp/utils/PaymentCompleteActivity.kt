package com.lifehopehealthapp.utils

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lifehopehealthapp.R
import com.lifehopehealthapp.dashBoard.DashBoardActivity

class PaymentCompleteActivity : AppCompatActivity() {
    private val TIME_OUT = 3000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_completed)
        val statusvalue: String? = intent.getStringExtra("Status")
        val status: TextView = findViewById(R.id.textview_payment_text) as TextView
        status.setText(statusvalue)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if (statusvalue.equals("Payment Gateway Error")) {
                    finish()
                } else {
                    val i = Intent(this, DashBoardActivity::class.java)
                    startActivity(i)
                    finish()
                }

            }, TIME_OUT
        )
    }

    override fun onBackPressed() {
        val i = Intent(this, DashBoardActivity::class.java)
        startActivity(i)
        finish()
    }
}