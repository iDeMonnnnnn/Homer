package com.demon.apport.util

import android.annotation.SuppressLint
import android.content.Context
import com.base.log.Log
import java.io.File


/**
 * @author DeMon
 * Created on 2022/7/11.
 * E-mail idemon_liu@qq.com
 * Desc:
 */
@SuppressLint("SimpleDateFormat")
object LogUtils {


    fun wtf(tag: String, msg: String, error: Throwable) {
        Log.e(tag, msg, error)
    }

    fun wtf(tag: String, msg: String) {
        Log.i(tag, msg)
    }

    /**
     * 获取错误报告文件路径
     *
     * @param ctx
     * @return
     */
    fun getLogFiles(): List<String> {
        val filesDir = File(Log.getLogFolderPath())
        val fileNames = filesDir.list() ?: arrayOf()
        return fileNames.toMutableList().map {
            "${filesDir.absolutePath}/$it"
        }
    }
}