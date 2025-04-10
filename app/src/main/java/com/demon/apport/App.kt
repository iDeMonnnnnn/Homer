package com.demon.apport

import android.app.Application
import android.content.Context
import com.demon.apport.util.LogUtils
import com.demon.qfsolution.QFHelper
import com.tencent.mmkv.MMKV

/**
 * @author DeMonnnnnn
 * @date 2022/6/20
 * @email liu_demon@qq.com
 * @desc
 */
class App : Application() {
    companion object {
        private const val TAG = "App"
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this.applicationContext

        val rootDir = MMKV.initialize(this)
        LogUtils.wtf(TAG, "onCreate:  $rootDir")
        QFHelper.init(this)
    }
}