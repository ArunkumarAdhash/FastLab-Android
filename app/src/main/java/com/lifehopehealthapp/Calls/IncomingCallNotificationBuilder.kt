package com.lifehopehealthapp.Calls

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import com.lifehopehealthapp.R
import org.json.JSONObject

class IncomingCallNotificationBuilder(private val context: Context, p0: RemoteMessage) {
    private val vibrationPattern = longArrayOf(1000, 1000)
    private val soundUri: Uri
    private var getData:RemoteMessage? =null
    private fun createNotificationChannel() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Incoming calls", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "Calls"
            notificationChannel.vibrationPattern = vibrationPattern
            notificationChannel.enableVibration(true)
            notificationChannel.setSound(soundUri, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build())
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun build(): Notification {
        val aMsgJOSNObject = JSONObject(getData as Map<*, *>)
        val incomingCallIntent = Intent(context, IncomingCallActivity::class.java)
        val incomingCallPendingIntent = PendingIntent.getActivity(context, INCOMING_CALL_REQUEST_ID, incomingCallIntent, PendingIntent.FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) R.drawable.pushnotification_logo else R.mipmap.ic_launcher)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setContentIntent(incomingCallPendingIntent)
            .setFullScreenIntent(incomingCallPendingIntent, true)
            .setSound(soundUri)
            .setVibrate(vibrationPattern)
            .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
            .setCustomContentView(incomingCallNotificationView)
            .build()
        notification.flags = notification.flags or NotificationCompat.FLAG_INSISTENT
        return notification
    }

    private val incomingCallNotificationView: RemoteViews
        get() {
            val incomingCallNotificationView = RemoteViews(context.packageName, R.layout.notification_incoming_call)
            val answerCallIntent = Intent(context, AnsweredCallActivity::class.java)
            val hangUpIntent = Intent(HangUpReceiver.ACTION_HANG_UP_INCOMING_CALL)
            val hangUpPendingIntent = PendingIntent.getBroadcast(context, INCOMING_CALL_HANG_UP_REQUEST_ID, hangUpIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val answerCallPendingIntent = PendingIntent.getActivity(context, ANSWER_CALL_REQUEST_ID, answerCallIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            hangUpIntent.action = HangUpReceiver.ACTION_HANG_UP_INCOMING_CALL

            incomingCallNotificationView.setOnClickPendingIntent(R.id.answer_call_button, answerCallPendingIntent)
            incomingCallNotificationView.setOnClickPendingIntent(R.id.hang_up_button, hangUpPendingIntent)
            return incomingCallNotificationView
        }

    companion object {
        private const val INCOMING_CALL_HANG_UP_REQUEST_ID = 1100
        private const val NOTIFICATION_CHANNEL_ID = "INCOMING_CALL_NOTIFICATION_CHANNEL_ID"
        const val INCOMING_CALL_REQUEST_ID = 11
        const val ANSWER_CALL_REQUEST_ID = 22
    }

    init {
        getData = p0
        soundUri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(context.packageName)
            .path(Integer.toString(R.raw.incoming_call_ringtone))
            .build()
        createNotificationChannel()
    }
}