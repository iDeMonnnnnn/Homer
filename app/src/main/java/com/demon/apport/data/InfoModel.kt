package com.demon.apport.data

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.demon.apport.App
import com.demon.apport.R

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
        get() =
            if (isApk()) field
            else {
                ContextCompat.getDrawable(App.appContext, R.mipmap.icon_logo)
            }


    constructor() {

    }

    constructor(type: Int) {
        this.type = type
    }

    fun isApk() = type == 1
}