package com.demon.apport.data

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.demon.apport.App
import com.demon.apport.R
import com.demon.qfsolution.utils.getMimeTypeByFileName

/**
 * Created by cretin on 2018/1/12.
 */
class InfoModel {
    var type: Int = 0 //0.普通文件，1.apk
    var path: String = ""
    var version: String? = null
    var size: String? = null
    var name: String? = null
    var icon: Drawable? = null

    constructor() {

    }

    constructor(type: Int) {
        this.type = type
    }

    fun isApk() = type == 1

    override fun toString(): String {
        return "InfoModel(type=$type, path='$path', version=$version, size=$size, name=$name)"
    }


}