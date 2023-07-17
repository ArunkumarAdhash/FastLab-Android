package com.lifehopehealthapp.splash

import android.animation.Animator
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.lifehopehealthapp.R
import com.lifehopehealthapp.base.BaseActivity
import com.lifehopehealthapp.dashBoard.DashBoardActivity
import com.lifehopehealthapp.myProfile.MyProfileActivity
import com.lifehopehealthapp.retrofitService.APIManager
import com.lifehopehealthapp.signUp.SignUpActivity
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.checkProfileStatus
import com.lifehopehealthapp.utils.PreferenceHelper.checkuserstatus
import com.lifehopehealthapp.utils.PreferenceHelper.clearValues
import com.lifehopehealthapp.utils.Utils
import com.lifehopehealthapp.widgets.LifeHopeButton
import com.lifehopehealthapp.widgets.LifeHopenTextView


class SplashActivity : BaseActivity<SplashViewModel, SplashModel>() {

    var button: AppCompatButton? = null
    private var profileStatus = ""
    private var userStatus = ""
    var desc: TextView? = null
    var mPrefs: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mPrefs = PreferenceHelper.defaultPreference(this)
        button = findViewById<View>(R.id.clickme) as AppCompatButton
        val logo = findViewById<View>(R.id.logo) as AppCompatImageView
        val bgImage = findViewById<View>(R.id.bg_image) as AppCompatImageView

        val extras = intent.extras
        if (extras != null) {
            val logoutdata: String = extras.getString("LogOut").toString()
            if (logoutdata.contentEquals("LogOut")) {
                mPrefs!!.clearValues
            }
        }

        desc = findViewById<View>(R.id.txt_splash_desc) as TextView

        YoYo.with(Techniques.ZoomIn).duration(1000)
            .withListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    desc!!.visibility = View.VISIBLE
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).playOn(logo)


        YoYo.with(Techniques.FadeInUp).duration(2500).delay(500)
            .withListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {


                    if (Utils.isNetworkAvailable(this@SplashActivity)) {
                        profileStatus = mPrefs!!.checkProfileStatus.toString()
                        userStatus = mPrefs!!.checkuserstatus.toString()
                        if (profileStatus.equals("false")) {
                            startActivity(Intent(this@SplashActivity, MyProfileActivity::class.java))
                            Animatoo.animateCard(this@SplashActivity)
                            finish()
                        } else if (profileStatus.equals("true") || profileStatus.equals("Completed")) {
                            startActivity(Intent(this@SplashActivity, DashBoardActivity::class.java))
                            Animatoo.animateCard(this@SplashActivity)
                            finish()
                        } else if (userStatus.equals("true")) {
                            startActivity(Intent(this@SplashActivity, DashBoardActivity::class.java))
                            Animatoo.animateCard(this@SplashActivity)
                            finish()
                        } else if (profileStatus.equals("false") && userStatus.equals("false")) {
                            startActivity(Intent(this@SplashActivity, MyProfileActivity::class.java))
                            Animatoo.animateCard(this@SplashActivity)
                            finish()
                        } else {
                            startActivity(Intent(this@SplashActivity, SignUpActivity::class.java))
                            Animatoo.animateCard(this@SplashActivity)
                            finish()
                        }
                    }
                    else {
                        noInternetAlert(resources.getString(R.string.no_internet_msg), this@SplashActivity)
                    }



                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).playOn(desc)


        val clk_rotate = AnimationUtils.loadAnimation(
            this,
            R.anim.circle_anim
        )
    }


    override fun getViewModel() = SplashViewModel::class.java

    override fun getActivityRepository() =
        SplashModel(remoteDataSource.buildApi(APIManager::class.java), PreferenceHelper)

    override fun onBackPressed() {
        super.onBackPressed()
        val intent =
            Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun noInternetAlert(msg: String?, context: Context) {


        if(!this@SplashActivity.isFinishing)
        {
            val view1 =
                View.inflate(context, R.layout.alert_no_internet, null)
            val internetDialog =
                Dialog(context, R.style.dialogwinddow)
            internetDialog.setContentView(view1)
            internetDialog.setCancelable(false)
            internetDialog?.dismiss()
            internetDialog.show()
            val viewGap =
                internetDialog.findViewById<View>(R.id.view_gap)
            viewGap.visibility = View.VISIBLE
            val title: LifeHopenTextView = internetDialog.findViewById(R.id.tv_title)
            title.setText(msg)
            val button_success: LifeHopeButton = internetDialog.findViewById(R.id.button_success)
            button_success.setText(context.getResources().getString(R.string.retry))
            val button_failure: LifeHopeButton = internetDialog.findViewById(R.id.button_failure)
            button_failure.setVisibility(View.VISIBLE)
            button_success.setOnClickListener({ view ->
                internetDialog.dismiss()
                finish()
                startActivity(intent)
            })
            button_failure.setOnClickListener({ view ->
                internetDialog.dismiss()
                finish()
            })
        }


    }
}


