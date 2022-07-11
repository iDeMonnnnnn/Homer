package com.demon.apport.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.util.Log
import com.demon.apport.App
import com.demon.apport.BuildConfig
import kotlinx.coroutines.launch
import java.io.File
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author DeMon
 * Created on 2022/7/11.
 * E-mail idemon_liu@qq.com
 * Desc:
 */
@SuppressLint("SimpleDateFormat")
object LogUtils {

    private var sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    private var nowDay = ""

    init {
        nowDay = SimpleDateFormat("yyyy-MM-dd").format(Date())
    }

    fun wtf(tag: String, msg: String) {
        Log.i(tag, "wtf: $msg")
        scopeIO.launch {
            val mContext = App.appContext
            val logFile = File(getTodayLog())
            val sb = StringBuilder()
            if (!logFile.exists()) {
                logFile.createNewFile()
                val pi: PackageInfo = mContext.packageManager.getPackageInfo(mContext.packageName, 0)
                //程序信息
                sb.append("APPLICATION_ID:").append(mContext.packageName) //软件APPLICATION_ID
                sb.append("\nVERSION_CODE:").append(pi.versionCode.toString() + "") //软件版本号
                sb.append("\nVERSION_NAME:").append(pi.versionName) //VERSION_NAME
                sb.append("\nBUILD_TYPE:").append(BuildConfig.DEBUG) //是否是DEBUG版本
                //设备信息
                sb.append("\nMODEL:").append(Build.MODEL)
                sb.append("\nMANUFACTURER:").append(Build.MANUFACTURER)
                sb.append("\nRELEASE:").append(Build.VERSION.RELEASE)
                sb.append("\nSDK:").append(Build.VERSION.SDK_INT)
                sb.append("\n")
            }
            val nowTime = sdf.format(Date())
            sb.append("[$nowTime] ")
            sb.append("$tag ")
            sb.append("$msg\n")
            FileUtils.writeTxt(logFile.absolutePath, sb.toString(), true)
        }
    }


    fun getTodayLog(): String = getLogFilePath(App.appContext) + nowDay + ".log"

    /**
     * 获取文件夹路径
     *
     * @param context
     * @return
     */
    fun getLogFilePath(context: Context): String {
        val path: String = context.getExternalFilesDir(null).toString() + "/Log/"
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return path
    }

    /**
     * 获取错误报告文件路径
     *
     * @param ctx
     * @return
     */
    fun getLogFiles(ctx: Context): MutableList<String> {
        val filesDir = File(getLogFilePath(ctx))
        val fileNames = filesDir.list() ?: arrayOf()
        return fileNames.toMutableList()
    }
}