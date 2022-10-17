package com.demon.apport.service

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.demon.apport.service.WebService
import com.demon.apport.util.LogUtils
import com.demon.apport.util.NotificationUtils
import com.demon.apport.util.Tag
import com.demon.apport.util.WifiUtils

class WebService : Service() {


    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()

        buildNotification()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action
        LogUtils.wtf(Tag, "onStartCommand: $action")
        if (ACTION_START_WEB_SERVICE == action) {
            val ipAddr = WifiUtils.getIp()
            LogUtils.wtf(Tag, "device ip=$ipAddr")
            WebHelper.instance.startServer(this)
        } else if (ACTION_STOP_WEB_SERVICE == action) {
            WebHelper.instance.stopService()

            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        WebHelper.instance.stopService()
    }


    private fun buildNotification() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForeground(GRAY_SERVICE_ID, NotificationUtils.createNotificationByChannel(this))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startForeground(GRAY_SERVICE_ID, NotificationUtils.buildNotification(this))
            } else {
                val innerIntent = Intent(this, GrayInnerService::class.java)
                startService(innerIntent)
                startForeground(GRAY_SERVICE_ID, Notification())
            }
        } catch (e: Exception) {
            Log.d(TAG, Log.getStackTraceString(e))
        }
    }

    class GrayInnerService : Service() {
        override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
            startForeground(GRAY_SERVICE_ID, Notification())
            stopForeground(true)
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }

        override fun onBind(intent: Intent): IBinder? {
            return null
        }
    }

    companion object {
        private const val TAG = "WebService"
        private val GRAY_SERVICE_ID = 1001
        const val ACTION_START_WEB_SERVICE = "START_WEB_SERVICE"
        const val ACTION_STOP_WEB_SERVICE = "STOP_WEB_SERVICE"

        fun start(context: Context) {
            val intent = Intent(context, WebService::class.java)
            intent.action = ACTION_START_WEB_SERVICE
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, WebService::class.java)
            intent.action = ACTION_STOP_WEB_SERVICE
            context.startService(intent)
        }
    }
}