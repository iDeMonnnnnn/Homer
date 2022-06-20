package com.demon.apport.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Parcelable
import android.net.NetworkInfo
import android.util.Log
import com.demon.apport.data.Constants
import com.jeremyliao.liveeventbus.LiveEventBus

class WifiReceiver : BroadcastReceiver() {
    private val TAG = "WifiReceiver"

    companion object {
        var state: Int = -1
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive: ${intent.action}")
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION == intent.action) {
            val parcelableExtra = intent
                .getParcelableExtra<Parcelable>(WifiManager.EXTRA_NETWORK_INFO)
            if (null != parcelableExtra) {
                val networkInfo = parcelableExtra as NetworkInfo
                //1.已连接，4断开连接，0连接中
                state = networkInfo.state.ordinal
                LiveEventBus.get<NetworkInfo.State>(Constants.WIFI_CONNECT_CHANGE_EVENT).post(networkInfo.state)
            }
        }
    }
}