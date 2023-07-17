package com.lifehopehealthapp

import `in`.myinnos.customfontlibrary.TypefaceUtil
import android.app.Application
import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.lifehopehealthapp.retrofitService.mainModule
import org.koin.android.ext.android.startKoin
import org.koin.android.logger.AndroidLogger


class LifeHopeApplication : Application() {
    private var typeface: Typeface? = null
    private var INSTANCE: LifeHopeApplication? = null

    companion object {
        private var instance: LifeHopeApplication? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        /*fun getTypeface(): Typeface? {
            return getTypeface()
        }

        fun setTypeface(typeface: Typeface?) {
            instance!!.typeface = typeface
        }*/
    }


    init {
        instance = this
    }


    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val context: Context = LifeHopeApplication.applicationContext()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Poppins-Regular.ttf")
        startKoin(
            this,
            listOf(mainModule),
            logger = AndroidLogger(showDebug = BuildConfig.DEBUG)
        )
    }
}