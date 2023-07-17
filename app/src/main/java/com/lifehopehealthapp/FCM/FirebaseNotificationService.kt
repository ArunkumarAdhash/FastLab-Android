package com.lifehopehealthapp.FCM

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lifehopehealthapp.Calls.HangUpReceiver
import com.lifehopehealthapp.Calls.IncomingCallNotificationBuilder
import com.lifehopehealthapp.R
import com.lifehopehealthapp.dashBoard.DashBoardActivity
import com.lifehopehealthapp.splash.SplashActivity
import com.lifehopehealthapp.trackTestOrders.TrackOrderActivity
import com.lifehopehealthapp.utils.PreferenceHelper
import com.lifehopehealthapp.utils.PreferenceHelper.authToken
import com.lifehopehealthapp.utils.Utils
import org.json.JSONObject
import java.lang.Exception
import java.util.*


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseNotificationService : FirebaseMessagingService() {
    private lateinit var context: Context
    var mPrefs: SharedPreferences? = null
    private var userID: String? = ""
    private var pendingIntent: PendingIntent? = null
    val min = 5
    val max = 100
    val aR = Random()
    private var notificationManager: NotificationManager? = null
    private val hangUpReceiver = HangUpReceiver()
    private var incomingCallNotificationBuilder: IncomingCallNotificationBuilder? = null

    override fun onMessageReceived(p0: RemoteMessage) {

        try {
            mPrefs = PreferenceHelper.defaultPreference(this)
            userID = mPrefs!!.authToken
            Log.e("getKey", userID.toString())
            Log.e("messageResponse", p0.data.toString())
            val aMsgJOSNObject = JSONObject(p0.data as Map<*, *>)

            if (aMsgJOSNObject.getString("type").equals("2")) {
                incomingCallNotificationBuilder = IncomingCallNotificationBuilder(this,p0)
                notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                hangUpReceiver.setAnsweredCallHangUpHandler { this.handleAnsweredCallHangUp() }
                hangUpReceiver.setIncomingCallHangUpHandler { this.handleIncomingCallHangUp() }
                notificationManager!!.notify(11, incomingCallNotificationBuilder!!.build())
            }else{
                testProgressNotification(p0)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }


    }

    private fun setImageShape(imageURL: String): Bitmap? {
        return Utils.createBitmap(imageURL)!!
    }

    private fun testProgressNotification(p0: RemoteMessage){
        val mNotificationId = aR.nextInt(max - min + 1) + min
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "Lab_Tests_Track_Notifications_Channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Lab Test Status Track Notifications", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "Check the Ordered Test Status"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // to display notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)
            channel.canBypassDnd()
        }
        var intent: Intent? = null

        val aMsgJOSNObject = JSONObject(p0.data as Map<*, *>)
        if (userID!!.contains("Bearer")) {
            Log.e("userID->", userID.toString())
            val aBundle = Bundle()
            Log.e("aMsgJOSNObject", aMsgJOSNObject.toString())
            if (aMsgJOSNObject.getString("testStatus").equals("Bulk Test Report Generated")) {
                intent = Intent(this, DashBoardActivity::class.java)
                pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else if (aMsgJOSNObject.getString("orderId").equals("") || aMsgJOSNObject.getString("orderId") == null)
            {
                intent = Intent(this, DashBoardActivity::class.java)
                pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                intent = Intent(this, TrackOrderActivity::class.java)
                intent.putExtra("orderId", aMsgJOSNObject.getString("orderId"))
                intent.putExtras(aBundle)
                pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        } else {
            Log.e("userID1->", userID.toString())
            val intentValue = Intent(this, SplashActivity::class.java)
            intentValue.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intentValue,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)

        notificationBuilder
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(this, R.color.purple_500))
            .setContentTitle(aMsgJOSNObject.getString("title"))
            .setContentText(aMsgJOSNObject.getString("testStatus"))
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            //.setLargeIcon(setImageShape(aMsgJOSNObject.getString("imageUrl")))
            .setSmallIcon(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) R.drawable.pushnotification_logo else R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(mNotificationId, notificationBuilder.build())

    }
    private fun handleAnsweredCallHangUp() {

    }

    private fun handleIncomingCallHangUp() {
        notificationManager!!.cancel(11)
    }

}