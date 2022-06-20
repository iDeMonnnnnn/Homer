package com.demon.apport

import android.app.Application
import android.content.Context
import com.demon.qfsolution.QFHelper

/**
 * @author DeMonnnnnn
 * @date 2022/6/20
 * @email liu_demon@qq.com
 * @desc
 */
class App : Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this.applicationContext

        QFHelper.init(this, "fileProvider")
    }
}