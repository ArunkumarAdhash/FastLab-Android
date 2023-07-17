package com.lifehopehealthapp.Appointments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.lifehopehealthapp.R
import com.lifehopehealthapp.doctorList.DoctorListActivity

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val buttonStart: AppCompatButton = findViewById(R.id.buttonStart)
        buttonStart.setOnClickListener {
            startActivity(Intent(this, DoctorListActivity::class.java))
            finish()
        }
    }
}