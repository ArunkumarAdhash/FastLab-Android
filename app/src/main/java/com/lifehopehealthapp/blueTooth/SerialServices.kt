package com.lifehopehealthapp.blueTooth

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import com.lifehopehealthapp.R
import com.lifehopehealthapp.utils.SerialSocket
import java.io.IOException
import java.util.*
import kotlin.jvm.Throws

class SerialServices : Service(), SerialListener {


    internal class SerialBinder : Binder() {
        val service: SerialBinder
            get() = this
    }

    internal enum class QueueType {
        Connect, ConnectError, Read, IoError
    }

    class QueueItem internal constructor(
        var type: QueueType,
        var data: ByteArray?,
        var e: java.lang.Exception?
    )

    private var mainLooper: Handler? = null
    private var binder: IBinder? = null
    private var queue1: Queue<QueueItem>? = null
    var queue2: Queue<QueueItem>? = null

    private var socket: SerialSocket? = null
    private var listener: SerialListener? = null
    private var connected = false


    /**
     * Lifecylce
     */
    fun SerialServices() {
        mainLooper = Handler(Looper.getMainLooper())
        binder = SerialServices.SerialBinder()
        queue1 = LinkedList<SerialServices.QueueItem>()
        queue2 = LinkedList<SerialServices.QueueItem>()
    }

    override fun onDestroy() {
        cancelNotification()
        disconnect()
        super.onDestroy()
    }

    fun disconnect() {
        connected = false // ignore data,errors while disconnecting
        cancelNotification()
        if (socket != null) {
            socket!!.disconnect()
            socket = null
        }
    }

    private fun cancelNotification() {
        stopForeground(true)
    }

    /**
     * Api
     */
    @Throws(IOException::class)
    fun connect(socket: SerialSocket) {
        socket.connect(this)
        this.socket = socket
        connected = true
    }


    @Throws(IOException::class)
    fun write(data: ByteArray?) {
        if (!connected) throw IOException("not connected")
        socket!!.write(data)
    }

    fun attach(listener: SerialListener) {
        require(
            !(Looper.getMainLooper()
                .thread !== Thread.currentThread())
        ) { "not in main thread" }
        cancelNotification()
        // use synchronized() to prevent new items in queue2
        // new items will not be added to queue1 because mainLooper.post and attach() run in main thread
        synchronized(this) { this.listener = listener }
        for (item in queue1!!) {
            when (item.type) {
                SerialServices.QueueType.Connect -> listener.onSerialConnect()
                SerialServices.QueueType.ConnectError -> listener.onSerialConnectError(item.e)
                SerialServices.QueueType.Read -> listener.onSerialRead(item.data)
                SerialServices.QueueType.IoError -> listener.onSerialIoError(item.e)
            }
        }
        for (item in queue2!!) {
            when (item.type) {
                SerialServices.QueueType.Connect -> listener.onSerialConnect()
                SerialServices.QueueType.ConnectError -> listener.onSerialConnectError(item.e)
                SerialServices.QueueType.Read -> listener.onSerialRead(item.data)
                SerialServices.QueueType.IoError -> listener.onSerialIoError(item.e)
            }
        }
        queue1!!.clear()
        queue2!!.clear()
    }

    fun detach() {
        if (connected) createNotification()
        // items already in event queue (posted before detach() to mainLooper) will end up in queue1
        // items occurring later, will be moved directly to queue2
        // detach() and mainLooper.post run in the main thread, so all items are caught
        listener = null
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nc = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL,
                "Background service",
                NotificationManager.IMPORTANCE_LOW
            )
            nc.setShowBadge(false)
            val nm =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(nc)
        }
        val disconnectIntent = Intent()
            .setAction(Constants.INTENT_ACTION_DISCONNECT)
        val restartIntent = Intent()
            .setClassName(this, Constants.INTENT_CLASS_MAIN_ACTIVITY)
            .setAction(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
        val disconnectPendingIntent =
            PendingIntent.getBroadcast(
                this,
                1,
                disconnectIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        val restartPendingIntent = PendingIntent.getActivity(
            this,
            1,
            restartIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder =
            NotificationCompat.Builder(
                this,
                Constants.NOTIFICATION_CHANNEL
            )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(resources.getColor(R.color.purple_500))
                .setContentTitle(resources.getString(R.string.app_name))
                .setContentText(if (socket != null) "Connected to " + socket!!.name else "Background Service")
                .setContentIntent(restartPendingIntent)
                .setOngoing(true)
                .addAction(
                    NotificationCompat.Action(
                        R.mipmap.ic_launcher,
                        "Disconnect",
                        disconnectPendingIntent
                    )
                )
        // @drawable/ic_notification created with Android Studio -> New -> Image Asset using @color/colorPrimaryDark as background color
        // Android < API 21 does not support vectorDrawables in notifications, so both drawables used here, are created as .png instead of .xml
        val notification = builder.build()
        startForeground(
            Constants.NOTIFY_MANAGER_START_FOREGROUND_SERVICE,
            notification
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onSerialConnect() {
        if (connected) {
            synchronized(this) {
                if (listener != null) {
                    mainLooper!!.post {
                        if (listener != null) {
                            listener!!.onSerialConnect()
                        } else {
                            queue1!!.add(
                                QueueItem(
                                    QueueType.Connect,
                                    null,
                                    null
                                )
                            )
                        }
                    }
                } else {
                    queue2!!.add(
                        QueueItem(
                            QueueType.Connect,
                            null,
                            null
                        )
                    )
                }
            }
        }
    }

    override fun onSerialConnectError(e: Exception?) {
        if (connected) {
            synchronized(this) {
                if (listener != null) {
                    mainLooper!!.post {
                        if (listener != null) {
                            listener!!.onSerialConnectError(e)
                        } else {
                            queue1!!.add(
                                QueueItem(
                                    QueueType.ConnectError,
                                    null,
                                    e
                                )
                            )
                            cancelNotification()
                            disconnect()
                        }
                    }
                } else {
                    queue2!!.add(
                        QueueItem(
                            QueueType.ConnectError,
                            null,
                            e
                        )
                    )
                    cancelNotification()
                    disconnect()
                }
            }
        }
    }

    override fun onSerialRead(data: ByteArray?) {
        if (connected) {
            synchronized(this) {
                if (listener != null) {
                    mainLooper!!.post {
                        if (listener != null) {
                            listener!!.onSerialRead(data)
                        } else {
                            queue1!!.add(
                                QueueItem(
                                    QueueType.Read,
                                    data,
                                    null
                                )
                            )
                        }
                    }
                } else {
                    queue2!!.add(QueueItem(QueueType.Read, data, null))
                }
            }
        }
    }

    override fun onSerialIoError(e: Exception?) {
        if (connected) {
            synchronized(this) {
                if (listener != null) {
                    mainLooper!!.post {
                        if (listener != null) {
                            listener!!.onSerialIoError(e)
                        } else {
                            queue1!!.add(
                                QueueItem(
                                    QueueType.IoError,
                                    null,
                                    e
                                )
                            )
                            cancelNotification()
                            disconnect()
                        }
                    }
                } else {
                    queue2!!.add(QueueItem(QueueType.IoError, null, e))
                    cancelNotification()
                    disconnect()
                }
            }
        }
    }
}