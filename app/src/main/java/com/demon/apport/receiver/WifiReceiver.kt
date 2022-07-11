package com.demon.apport.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Parcelable
import android.net.NetworkInfo
import android.util.Log
import com.demon.apport.App
import com.demon.apport.data.Constants
import com.demon.apport.util.LogUtils
import com.demon.apport.util.Tag
import com.demon.apport.util.WifiUtils
import com.jeremyliao.liveeventbus.LiveEventBus

class WifiReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        LogUtils.wtf(Tag, "onReceive: ${intent.action}")
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION == intent.action) {
            val flag = WifiUtils.getNetState(App.appContext)
            LiveEventBus.get<Boolean>(Constants.WIFI_CONNECT_CHANGE_EVENT).post(flag)
        }
    }
}
