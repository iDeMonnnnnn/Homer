package com.demon.apport.util

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.WifiInfo
import com.demon.apport.util.WifiUtils
import android.net.NetworkInfo
import android.net.ConnectivityManager

/**
 * Created by masel on 2016/10/10.
 */
object WifiUtils {

    fun getWifiIp(context: Context): String? {
        val wifimanager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifimanager.connectionInfo
        return if (wifiInfo != null) {
            intToIp(wifiInfo.ipAddress)
        } else null
    }

    private fun intToIp(i: Int): String {
        return (i and 0xFF).toString() + "." + (i shr 8 and 0xFF) + "." + (i shr 16 and 0xFF) + "." + (i shr 24 and 0xFF)
    }

    /**
     * 有可用的网络
     */
    fun getNetState(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = manager.allNetworkInfo
        for (info in netInfo) {
            LogUtils.wtf(Tag, "getNetState: ${info.typeName}=${info.isConnected}")
            if (info.isConnected) {
                return true
            }
        }
        return false
    }
}