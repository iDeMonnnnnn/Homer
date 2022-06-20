package com.demon.apport.util

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.demon.apport.App
import com.demon.apport.data.Constants
import com.demon.apport.data.InfoModel
import com.demon.qfsolution.utils.getExternalOrFilesDir
import java.io.File
import java.text.DecimalFormat

/**
 * @author DeMonnnnnn
 * @date 2022/6/20
 * @email liu_demon@qq.com
 * @desc
 */
object FileUtils {
    private const val TAG = "FileUtils"

    //获取文件信息
    fun getAllFiles(context: Context): MutableList<InfoModel> {
        val list = mutableListOf<InfoModel>()
        val dir = context.getExternalOrFilesDir(Environment.DIRECTORY_DCIM)
        Log.i(TAG, "handleApk: ${dir.absoluteFile}")
        if (dir.exists() && dir.isDirectory) {
            val files = dir.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.absolutePath.endsWith(".apk", true)) {
                        getApkInfo(context, file)?.run {
                            list.add(this)
                        }
                    } else {
                        val infoModel = InfoModel()
                        infoModel.name = file.name
                        infoModel.path = file.absolutePath
                        infoModel.size = getFileSize(file.length())
                        list.add(infoModel)
                    }
                }
            }
        }
        return list
    }


    fun getApkInfo(context: Context, apk: File): InfoModel? {
        var infoModel: InfoModel? = null
        val pm = context.packageManager
        val archiveFilePath = apk.absolutePath
        val info = pm.getPackageArchiveInfo(archiveFilePath, 0)
        if (info != null) {
            val appInfo = info.applicationInfo
            appInfo.sourceDir = archiveFilePath
            appInfo.publicSourceDir = archiveFilePath
            val packageName = appInfo.packageName //得到安装包名称
            val version = info.versionName //得到版本信息
            var icon: Drawable? = pm.getApplicationIcon(appInfo)
            var appName = pm.getApplicationLabel(appInfo).toString()
            if (TextUtils.isEmpty(appName)) {
                appName = getApplicationName(packageName)
            }
            if (icon == null) {
                icon = getIconFromPackageName(packageName, context) // 获得应用程序图标
            }
            infoModel = InfoModel()
            infoModel.name = appName
            infoModel.packageName = packageName
            infoModel.path = archiveFilePath
            infoModel.size = getFileSize(apk.length())
            infoModel.version = version
            infoModel.icon = icon
            infoModel.isInstalled = isAvilible(context, packageName)
        }
        return infoModel
    }

    fun getApplicationName(packageName: String): String {
        var packageManager: PackageManager? = null
        var applicationInfo: ApplicationInfo? = null
        try {
            packageManager = App.appContext.packageManager
            applicationInfo = packageManager.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            applicationInfo = null
        }
        return if (packageManager != null && applicationInfo != null) {
            packageManager.getApplicationLabel(applicationInfo) as String
        } else packageName
    }

    fun getFileSize(length: Long): String {
        val df = DecimalFormat("######0.00")
        val d1 = 3.23456
        val d2 = 0.0
        val d3 = 2.0
        df.format(d1)
        df.format(d2)
        df.format(d3)
        val l = length / 1000 //KB
        if (l < 1024) {
            return df.format(l) + "KB"
        } else if (l < 1024 * 1024f) {
            return df.format((l / 1024f).toDouble()) + "MB"
        }
        return df.format((l / 1024f / 1024f).toDouble()) + "GB"
    }

    /**
     * 判断相对应的APP是否存在
     *
     * @param context
     * @param packageName(包名)(若想判断QQ，则改为com.tencent.mobileqq，若想判断微信，则改为com.tencent.mm)
     * @return
     */
    fun isAvilible(context: Context, packageName: String?): Boolean {
        val packageManager = context.packageManager

        //获取手机系统的所有APP包名，然后进行一一比较
        val pinfo = packageManager.getInstalledPackages(0)
        for (i in pinfo.indices) {
            if ((pinfo[i] as PackageInfo).packageName
                    .equals(packageName, ignoreCase = true)
            ) return true
        }
        return false
    }

    @Synchronized
    fun getIconFromPackageName(packageName: String, context: Context): Drawable? {
        val pm = context.packageManager
        try {
            val pi = pm.getPackageInfo(packageName, 0)
            val otherAppCtx = context.createPackageContext(packageName, AppCompatActivity.CONTEXT_IGNORE_SECURITY)
            val displayMetrics = intArrayOf(
                DisplayMetrics.DENSITY_XXXHIGH,
                DisplayMetrics.DENSITY_XXHIGH,
                DisplayMetrics.DENSITY_XHIGH,
                DisplayMetrics.DENSITY_HIGH,
                DisplayMetrics.DENSITY_TV
            )
            for (displayMetric in displayMetrics) {
                try {
                    val d = otherAppCtx.resources.getDrawableForDensity(pi.applicationInfo.icon, displayMetric)
                    if (d != null) {
                        return d
                    }
                } catch (e: Resources.NotFoundException) {
                    continue
                }
            }
        } catch (e: Exception) {
            // Handle Error here
        }
        var appInfo: ApplicationInfo = pm.getApplicationInfo(packageName, 0)
        return appInfo.loadIcon(pm)
    }

    //安装
    fun installApkFile(context: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        //兼容7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (context.packageManager.queryIntentActivities(intent, 0).size > 0) {
            context.startActivity(intent)
        }
    }

    //卸载
    fun delete(context: Context, packageName: String?) {
        val uri = Uri.fromParts("package", packageName, null)
        val intent = Intent(Intent.ACTION_DELETE, uri)
        context.startActivity(intent)
    }
}